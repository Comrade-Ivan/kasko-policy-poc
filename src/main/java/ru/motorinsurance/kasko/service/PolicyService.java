package ru.motorinsurance.kasko.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.motorinsurance.common.core.enums.PolicyStatus;
import ru.motorinsurance.common.kafka.commandsdto.PolicyUpdateCommand;
import ru.motorinsurance.common.kafka.eventsdto.PolicyCreateEvent;
import ru.motorinsurance.common.kafka.eventsdto.PolicyUpdateEvent;
import ru.motorinsurance.common.kafka.eventsdto.PremiumCalculatedEvent;
import ru.motorinsurance.debeziumoutbox.OutboxEvent;
import ru.motorinsurance.debeziumoutbox.OutboxEventRepository;
import ru.motorinsurance.kasko.dto.PolicyChangeStatusRequest;
import ru.motorinsurance.kasko.dto.PolicyCreateRequest;
import ru.motorinsurance.kasko.dto.PolicyResponse;
import ru.motorinsurance.kasko.dto.PolicyUpdateDto;
import ru.motorinsurance.kasko.exceptions.PolicyNotFoundException;
import ru.motorinsurance.kasko.mappers.PolicyMapper;
import ru.motorinsurance.kasko.model.Policy;
import ru.motorinsurance.kasko.model.PolicyHolder;
import ru.motorinsurance.kasko.model.StatusTransition;
import ru.motorinsurance.kasko.model.Vehicle;
import ru.motorinsurance.kasko.repository.PolicyHolderRepository;
import ru.motorinsurance.kasko.repository.PolicyRepository;
import ru.motorinsurance.kasko.repository.StatusTransitionRepository;
import ru.motorinsurance.kasko.repository.VehicleRepository;
import ru.motorinsurance.kasko.service.status.PolicyStatusTransitionService;
import ru.motorinsurance.kasko.utils.JsonUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final VehicleRepository vehicleRepository;
    private final PolicyHolderRepository policyHolderRepository;
    private final PolicyMapper policyMapper;
    private final PolicyStatusTransitionService policyStatusTransitionService;
    private final StatusTransitionRepository statusTransitionRepository;
    private final OutboxEventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public PolicyResponse createPolicy(PolicyCreateRequest request) {
        String policyId = generatePolicyId();
        Vehicle vehicle = vehicleRepository.findByVin(request.getVehicle().getVin()).orElseGet(() -> {
            Vehicle newVehicle = policyMapper.toVehicleEntity(request.getVehicle());
            return vehicleRepository.save(newVehicle);
        });
        PolicyHolder policyHolder = policyHolderRepository.findByNameAndPhone(request.getPolicyHolder().getName(), request.getPolicyHolder().getContact().getPhone()).orElseGet(() -> {
            PolicyHolder newPolicyHolder = policyMapper.toPolicyHolderEntity(request.getPolicyHolder());
            return policyHolderRepository.save(newPolicyHolder);
        });

        try {
            Policy policy = Policy.builder()
                    .policyId(policyId)
                    .createdAt(LocalDateTime.now())
                    .status(PolicyStatus.PRE_CALCULATION)
                    .policyHolder(policyHolder)
                    .vehicle(vehicle)
                    .isCancelled(false)
                    .drivers(policyMapper.toDriversDto(request.getDrivers()))
                    .startDate(Optional.ofNullable(request.getStartDate()).orElseGet(LocalDate::now))
                    .build();

            vehicle.setPolicy(policy);
            policyHolder.getPolicies().add(policy);

            policyRepository.save(policy);

            PolicyCreateEvent event = new PolicyCreateEvent(UUID.randomUUID(), policyMapper.toPolicyDto(policy));
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateType("policy")
                    .aggregateId(policy.getPolicyId())
                    .eventType("CREATED")
                    .payload(JsonUtils.toJson(event))
                    .build();
            eventRepository.save(outboxEvent);

            return policyMapper.toPolicyResponse(policy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Policy updatePolicy(Policy policy, PolicyUpdateDto policyUpdateDto) {
        policyMapper.updatePolicyFromDto(policyUpdateDto, policy);
        return policy;
    }

    @Transactional
    public PolicyResponse updatePolicyAndReturnResponse(String policyId, PolicyUpdateDto policyUpdateDto) {
        Policy policy = policyRepository.findByPolicyId(policyId).orElseThrow(() -> PolicyNotFoundException.byId(policyId));
        Policy updatedPolicy = updatePolicy(policy, policyUpdateDto);
        return policyMapper.toPolicyResponse(updatedPolicy);
    }

    @Transactional
    public void changePolicyStatus(String policyId, PolicyStatus targetStatus) {
        Policy policy = policyRepository.findByPolicyId(policyId)
                .orElseThrow(() -> PolicyNotFoundException.byId(policyId));

        policyStatusTransitionService.validateTransition(policy, targetStatus);
        saveStatusTransition(policy, targetStatus);

        int updated = policyRepository.updateStatus(policyId, targetStatus);
        if (updated == 0) throw PolicyNotFoundException.byId(policyId);
    }

    @Transactional
    @KafkaListener(topics = "policy.commands", groupId = "${spring.kafka.consumer.group-id}")
    public void handleUpdatePolicyCommand(ConsumerRecord<String, String> record) {
        try {
            // Читаем JSON как общий Map/JsonNode, чтобы определить тип события
            JsonNode rootNode = objectMapper.readTree(record.value());

            // Проверяем поле, которое определяет тип события (например, "eventType")
            String eventType = new String(record.headers().lastHeader("eventType").value());

            // Десериализуем в конкретный класс на основе типа события
            Object event = switch (eventType) {
                case "INITIAL_PREMIUM_CALCULATED" -> objectMapper.readValue(rootNode.get("payload").asText(), PolicyUpdateCommand.class);
                default -> throw new IllegalArgumentException("Unknown event type: " + eventType);
            };

            // Обрабатываем событие
            if (event instanceof PolicyUpdateCommand) {
                handlePolicyPremiumCalculatedEvent((PolicyUpdateCommand) event);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse event", e);
        }
    }

    @Transactional
    private void handlePolicyPremiumCalculatedEvent(PolicyUpdateCommand event) {
        String policyId = event.getPolicy().getPolicyId();
        log.info("Adding premium to PolicyId " + policyId);
        Policy policy = policyRepository.findByPolicyId(policyId).orElseThrow(() ->PolicyNotFoundException.byId(policyId));

        BigDecimal calculatedPremium = event.getPolicy().getPremiumAmount();

        policy.setPremiumAmount(calculatedPremium);
    }

    @Transactional
    public Policy changePolicyStatusAndReturnPolicy(String policyId, PolicyStatus targetStatus) {
        changePolicyStatus(policyId, targetStatus);
        entityManager.clear();
        return policyRepository.findByPolicyId(policyId).orElseThrow(() -> PolicyNotFoundException.byId(policyId));
    }

    @Transactional
    public PolicyResponse changePolicyStatusAndReturnResponse(PolicyChangeStatusRequest request) {
        PolicyStatus targetStatus = PolicyStatus.fromRussianName(request.getTargetStatus());
        Policy policy = changePolicyStatusAndReturnPolicy(request.getPolicyId(), targetStatus);
        return policyMapper.toPolicyResponse(policy);
    }

    private void saveStatusTransition(Policy policy, PolicyStatus targetStatus) {
        StatusTransition transition = StatusTransition.builder()
                .fromStatus(policy.getStatus().getRussianName())
                .toStatus(targetStatus.getRussianName())
                .transitionTime(LocalDateTime.now())
                .policy(policy)
                .build();
        statusTransitionRepository.save(transition);
    }

    public PolicyResponse getPolicyById(String policyId) {
        Policy policy = policyRepository.findByPolicyId(policyId).orElseThrow(() -> PolicyNotFoundException.byId(policyId));
        return policyMapper.toPolicyResponse(policy);
    }
    /**
     * Generates a new policy ID in format KASKO-YYYY-XXXXXX
     *
     * @return Generated policy ID
     */
    private String generatePolicyId() {
        int year = LocalDateTime.now().getYear();
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return String.format("KASKO-%d-%s", year, randomPart);
    }

    private void updatePolicy(int updateResult, Exception ex) throws Exception {
        if (updateResult == 0) throw ex;
    }
}
