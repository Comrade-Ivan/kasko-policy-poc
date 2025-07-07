package ru.motorinsurance.kasko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.motorinsurance.common.core.enums.PolicyStatus;
import ru.motorinsurance.kasko.model.StatusTransition;

import java.util.List;

@Repository
public interface StatusTransitionRepository extends JpaRepository<StatusTransition, Long> {

    @Query("SELECT st FROM StatusTransition st WHERE st.policy.policyId = :policyId ORDER BY st.transitionTime DESC")
    List<StatusTransition> findByPolicyId(String policyId);

    @Query("SELECT st FROM StatusTransition st WHERE st.toStatus = :status")
    List<StatusTransition> findAllByStatus(PolicyStatus status);
}
