package com.betmotion.agentsmanagement.domain;

import static com.betmotion.agentsmanagement.utils.Constants.DEFAULT_BATCH_SIZE;
import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "user_transaction")
@DynamicUpdate
@Getter
@Setter
public class UserTransaction {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "player_id", nullable = false)
  private Integer playerid;

  @Column(name = "user_id", nullable = false)
  private Integer userid;

  @Column(name = "direct_player_agent_id", nullable = false)
  private Integer directPlayerAgentId;

  @Column(name = "amount", nullable = false)
  private Long amount;

  @Column(name = "operation_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private UserTransactionType operationType;

  @Column(name = "transaction_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private UserTransactionStatus transactionStatus;

  @Column(name = "operation_date", nullable = false)
  private LocalDateTime operationDate;

  @Column(name = "balance", nullable = false)
  private Long balance;

  @Column(name = "bonus")
  private Long bonus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player_id", updatable = false, insertable = false)
  @BatchSize(size = DEFAULT_BATCH_SIZE)
  private Player player;

  @Column(name = "note")
  private String note;

  @Formula("CASE operation_type\n"
      + "           WHEN 'DEPOSIT' THEN 4\n"
      + "           WHEN 'PAYMENT' THEN 3\n"
      + "           WHEN 'WITHDRAWAL' THEN 2\n"
      + "           WHEN 'PAYOUT' THEN 1\n"
      + "           ELSE -1\n"
      + "END ")
  private Integer transactionTypeIndex;

  //TODO Fixme: See UserTransactionType for multipliers
  @Formula("amount * CASE operation_type "
      + "           WHEN 'DEPOSIT' THEN 1 "
      + "           WHEN 'PAYMENT' THEN -1 "
      + "           WHEN 'WITHDRAWAL' THEN -1 "
      + "           WHEN 'PAYOUT' THEN 1 "
      + "           WHEN 'DEPOSIT_ROLLBACK' THEN -1 "
      + "           WHEN 'WITHDRAWAL_ROLLBACK' THEN 1 "
      + "           WHEN 'PAYMENT_ROLLBACK' THEN 1 "
      + "           WHEN 'PAYOUT_ROLLBACK' THEN -1 "
      + "           ELSE 1 "
      + "END ")
  private Long reportAmount;

  //TODO Fixme: See UserTransactionType for multipliers
  @Formula("(amount + COALESCE(bonus,0)) * CASE operation_type "
          + "           WHEN 'DEPOSIT' THEN 1 "
          + "           WHEN 'PAYMENT' THEN -1 "
          + "           WHEN 'WITHDRAWAL' THEN -1 "
          + "           WHEN 'PAYOUT' THEN 1 "
          + "           WHEN 'DEPOSIT_ROLLBACK' THEN -1 "
          + "           WHEN 'WITHDRAWAL_ROLLBACK' THEN 1 "
          + "           WHEN 'PAYMENT_ROLLBACK' THEN 1 "
          + "           WHEN 'PAYOUT_ROLLBACK' THEN -1 "
          + "           ELSE 1 "
          + "END ")
  private Long bonusReportAmount;

  //TODO Fixme: See UserTransactionType for multipliers
  @Formula("COALESCE(bonus,0)")
  private Long reportBonus;
}
