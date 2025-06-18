package ru.motorinsurance.kasko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.motorinsurance.kasko.model.PolicyHolder;

import java.util.Optional;

@Repository
public interface PolicyHolderRepository extends JpaRepository<PolicyHolder, Long> {

    @Query("SELECT ph FROM PolicyHolder ph WHERE ph.phone = :phone")
    Optional<PolicyHolder> findByPhone(String phone);

    @Query("SELECT ph FROM PolicyHolder ph WHERE ph.email = :email")
    Optional<PolicyHolder> findByEmail(String email);

    @Query("SELECT ph FROM PolicyHolder ph WHERE ph.name = :name AND ph.phone = :phone")
    Optional<PolicyHolder> findByNameAndPhone(String name, String phone);

    boolean existsByPhone(String phone);
}
