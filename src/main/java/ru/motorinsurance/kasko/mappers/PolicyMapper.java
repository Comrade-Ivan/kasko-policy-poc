package ru.motorinsurance.kasko.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.mapstruct.*;
import ru.motorinsurance.common.core.dto.DriversDto;
import ru.motorinsurance.common.core.dto.PolicyDto;
import ru.motorinsurance.common.core.dto.PolicyHolderDto;
import ru.motorinsurance.common.core.dto.VehicleDto;
import ru.motorinsurance.common.core.enums.HolderType;
import ru.motorinsurance.common.core.enums.PolicyStatus;
import ru.motorinsurance.common.core.enums.VehicleUsagePurpose;
import ru.motorinsurance.kasko.dto.PolicyResponse;
import ru.motorinsurance.kasko.dto.PolicyUpdateDto;
import ru.motorinsurance.kasko.model.Policy;
import ru.motorinsurance.kasko.model.PolicyHolder;
import ru.motorinsurance.kasko.model.Vehicle;
import ru.motorinsurance.kasko.utils.JsonUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PolicyMapper {

    // Форматтер для дат
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Mapping(target = "startDate", source = "startDate", dateFormat = "dd.MM.yyyy")
    @Mapping(target = "endDate", source = "endDate", dateFormat = "dd.MM.yyyy")
    @Mapping(target = "policyHolder.contact.phone", source = "policyHolder.phone")
    @Mapping(target = "policyHolder.contact.email", source = "policyHolder.email")
    PolicyResponse toPolicyResponse(Policy entity);

    @Mapping(target = "startDate", source = "startDate", dateFormat = "dd.MM.yyyy")
    @Mapping(target = "endDate", source = "endDate", dateFormat = "dd.MM.yyyy")
    @Mapping(target = "policyHolder.contact.phone", source = "policyHolder.phone")
    @Mapping(target = "policyHolder.contact.email", source = "policyHolder.email")
    PolicyDto toPolicyDto(Policy entity);
    // Vehicle mapping
    @Mapping(target = "purchaseDate", source = "purchaseDate", dateFormat = "dd.MM.yyyy")
    Vehicle toVehicleEntity(VehicleDto dto);

    @InheritInverseConfiguration(name = "toVehicleEntity")
    VehicleDto toVehicleDto(Vehicle entity);

    // PolicyHolder mapping
    @Mapping(target = "phone", source = "contact.phone")
    @Mapping(target = "email", source = "contact.email")
    @Mapping(target = "policies", ignore = true)
    PolicyHolder toPolicyHolderEntity(PolicyHolderDto dto);

    //TODO: @Mapping(target = "documents", ignore = true) // Документы обрабатываются отдельно
    @InheritInverseConfiguration(name = "toPolicyHolderEntity")
    PolicyHolderDto toPolicyHolderDto(PolicyHolder entity);

    // Методы для обновления
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePolicyFromDto(PolicyUpdateDto dto, @MappingTarget Policy policy);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVehicleFromDto(VehicleDto dto, @MappingTarget Vehicle entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePolicyHolderFromDto(PolicyHolderDto dto, @MappingTarget PolicyHolder entity);

    default LocalDate map(String dateStr) {
        return dateStr != null ? LocalDate.parse(dateStr, DATE_FORMATTER) : null;
    }

    default String map(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    default VehicleUsagePurpose mapUsagePurpose(String value) {
        return value != null ? VehicleUsagePurpose.fromRussianName(value) : null;
    }

    default String mapUsagePurpose(VehicleUsagePurpose purpose) {
        return purpose != null ? purpose.getRussianName() : null;
    }

    default HolderType mapHolderType(String value) {
        return value != null ? HolderType.fromRussianName(value) : null;
    }

    default String mapHolderType(HolderType holderType) {
        return holderType != null ? holderType.getRussianName() : null;
    }

    default PolicyStatus mapPolicyStatus(String value) {
        return value != null ? PolicyStatus.fromRussianName(value) : null;
    }

    default String mapPolicyStatus(PolicyStatus policyStatus) {
        return policyStatus != null ? policyStatus.getRussianName() : null;
    }

    default String map(DriversDto driversDto) throws JsonProcessingException {
        return JsonUtils.toJson(driversDto);
    }

    default DriversDto toDriversDto(String value) throws JsonProcessingException {
        return JsonUtils.fromJson(value, DriversDto.class);
    }
}
