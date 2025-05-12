package com.betmotion.agentsmanagement.domain;

import static com.betmotion.agentsmanagement.utils.Constants.DEFAULT_BATCH_SIZE;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.SqlResultSetMapping;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import com.betmotion.agentsmanagement.dao.projection.UserSumCreditsAndBalance;

@SqlResultSetMapping(name = "Mapping.directAgentSumCreditsAndBalance",
    classes = @ConstructorResult(targetClass = UserSumCreditsAndBalance.class,
        columns = {@ColumnResult(name = "balance", type = Long.class),
            @ColumnResult(name = "credits", type = Long.class)}))

@Entity
@Table(name = "agents")
@DynamicUpdate
@Getter
@Setter
public class Agent implements CreditWalletAware {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  @BatchSize(size = DEFAULT_BATCH_SIZE)
  private User user;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "wallet_id")
  @BatchSize(size = DEFAULT_BATCH_SIZE)
  private Wallet wallet;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "credit_wallet_id")
  @BatchSize(size = DEFAULT_BATCH_SIZE)
  private Wallet creditsWallet;

  @Column(name = "commission")
  private BigDecimal commission;

  @Column(name = "comission_casino")
  private BigDecimal commissionCasino;

  @Column(name = "comission_slots")
  private BigDecimal commissionSlots;

  @Column(name = "comission_sports")
  private BigDecimal commissionSports;

  @Column(name = "comission_type")
  private String commissionType;

  @Column(name = "last_comission_update", nullable = false)
  private LocalDateTime lastCommissionUpdate;

  @Column(name = "can_have_subagents")
  private boolean canHaveSubAgents;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "parent_agent_id")
  @BatchSize(size = DEFAULT_BATCH_SIZE)
  private Agent parentAgent;

  @Column(name = "parent_agent_id", insertable = false, updatable = false)
  private Integer parentId;

  @Column(name = "wallet_id", insertable = false, updatable = false)
  private Integer walletId;

  @Column(name = "credit_wallet_id", insertable = false, updatable = false)
  private Integer creditWalletId;

  @Column(name = "user_id", insertable = false, updatable = false)
  private Integer userId;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "code_id")
  @BatchSize(size = DEFAULT_BATCH_SIZE)
  private AgentCode code;

  @Column(name = "balance", nullable = false)
  private Long balance;

  @Column(name = "parent_tree")
  private String parentTree;

  @Column(name = "permission_unblock")
  private Integer permissionUnblock;
}
