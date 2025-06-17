package ru.motorinsurance.kasko.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.motorinsurance.kasko.enums.PolicyStatus;
import ru.motorinsurance.kasko.model.Policy;

import java.time.LocalDate;

public interface PolicyCustomRepository {
    Page<Policy> findPoliciesByFilters(
            String vin,
            String policyHolderName,
            PolicyStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}

//@Repository
//public class PolicyCustomRepositoryImpl implements PolicyCustomRepository {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    public Page<Policy> findPoliciesByFilters(String vin,
//                                              String policyHolderName,
//                                              PolicyStatus status,
//                                              LocalDate startDate,
//                                              LocalDate endDate,
//                                              Pageable pageable) {
//        //TODO: Реализация сложного запроса с критериями
//    }
//}
