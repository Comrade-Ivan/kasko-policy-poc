package ru.motorinsurance.kasko.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.motorinsurance.common.core.enums.PolicyStatus;
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
