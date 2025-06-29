package ru.motorinsurance.kasko.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.motorinsurance.kasko.dto.PolicyChangeStatusRequest;
import ru.motorinsurance.kasko.dto.PolicyCreateRequest;
import ru.motorinsurance.kasko.dto.PolicyResponse;
import ru.motorinsurance.kasko.dto.PolicyUpdateDto;
import ru.motorinsurance.kasko.service.PolicyService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
@Validated
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping("/create")
    public ResponseEntity<PolicyResponse> createPolicy(
            @RequestBody @Valid PolicyCreateRequest request) {
        PolicyResponse response = policyService.createPolicy(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/api/v1/policies/{id}")
                .buildAndExpand(response.getPolicyId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{policyId}/status")
    public ResponseEntity<PolicyResponse> changePolicyStatus(
            @PathVariable String policyId,
            @RequestBody @Valid PolicyChangeStatusRequest request) {

        request.setPolicyId(policyId); // Убедимся, что ID в пути и теле совпадают FIXME: Убрать policyId из DTO
        PolicyResponse response = policyService.changePolicyStatusAndReturnResponse(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{policyId}")
    public ResponseEntity<PolicyResponse> getPolicy(@PathVariable String policyId) {
        PolicyResponse response = policyService.getPolicyById(policyId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{policyId}")
    public ResponseEntity<PolicyResponse> updatePolicy(
            @PathVariable String policyId,
            @RequestBody @Valid PolicyUpdateDto policyUpdateDto
    ) {
        PolicyResponse response = policyService.updatePolicyAndReturnResponse(policyId, policyUpdateDto);
        return ResponseEntity.ok(response);
    }
}


