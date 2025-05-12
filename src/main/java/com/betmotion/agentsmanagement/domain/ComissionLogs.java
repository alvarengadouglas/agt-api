package com.betmotion.agentsmanagement.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comission_logs")
@DynamicUpdate
@Getter
@Setter
public class ComissionLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "parent_agent_id", nullable = false)
    private Integer parentAgentId;

    @Column(name = "agent_id", nullable = false)
    private Integer agentId;

    @Column(name = "commission", nullable = false)
    private BigDecimal commission;

    @Column(name = "commission_slots")
    private BigDecimal commissionSlots;

    @Column(name = "commission_casino")
    private BigDecimal commissionCasino;

    @Column(name = "commission_sports")
    private BigDecimal commissionSports;

    @Column(name = "commission_type", nullable = false)
    private String commissionType;
}
