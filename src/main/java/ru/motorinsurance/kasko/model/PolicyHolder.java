package ru.motorinsurance.kasko.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import ru.motorinsurance.kasko.enums.HolderType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "policy_holders")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "holder_id")
    private Long holderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private HolderType type;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone", nullable = false, length = 12)
    private String phone;

    @Column(name = "email", length = 50)
    private String email;

    @OneToMany(mappedBy = "policyHolder", fetch = FetchType.LAZY)
    private List<Policy> policies = new ArrayList<>(); // Один страхователь → много полисов
}