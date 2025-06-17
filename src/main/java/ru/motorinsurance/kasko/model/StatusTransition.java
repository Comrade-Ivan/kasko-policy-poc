package ru.motorinsurance.kasko.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "status_transitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transition_id")
    private Long transitionId;

    @Column(name = "from_status", length = 50)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 50)
    private String toStatus;

    @Column(name = "transition_time", nullable = false)
    private LocalDateTime transitionTime;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", referencedColumnName = "policy_id")
    private Policy policy;
}
