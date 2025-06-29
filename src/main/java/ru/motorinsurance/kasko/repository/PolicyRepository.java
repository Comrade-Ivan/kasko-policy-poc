package ru.motorinsurance.kasko.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.motorinsurance.kasko.enums.PolicyStatus;
import ru.motorinsurance.kasko.model.Policy;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, String> {

    @EntityGraph(attributePaths = {"vehicle", "policyHolder"})
    Optional<Policy> findByPolicyId(String policyId);

    @Query("SELECT p FROM Policy p WHERE p.status = :status ORDER BY p.createdAt DESC")
    List<Policy> findAllByStatus(PolicyStatus status);

    @Query("SELECT p FROM Policy p WHERE p.policyHolder.holderId = :holderId")
    List<Policy> findByHolderId(Long holderId);

    @Modifying
    @Query("UPDATE Policy p SET p.status = :status WHERE p.id = :id")
    int updateStatus(@Param("id") String policyId, @Param("status") PolicyStatus status);

    //TODO: add updateFromDto

    boolean existsByPolicyId(String policyId);
}