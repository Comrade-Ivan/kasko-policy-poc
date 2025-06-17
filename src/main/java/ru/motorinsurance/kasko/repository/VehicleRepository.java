package ru.motorinsurance.kasko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.motorinsurance.kasko.model.Vehicle;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVin(String vin);

    @Query("SELECT v FROM Vehicle v WHERE v.policy.policyId = :policyId")
    Optional<Vehicle> findByPolicyId(String policyId);

    boolean existsByVin(String vin);
}
