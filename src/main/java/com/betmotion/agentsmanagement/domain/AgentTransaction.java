package com.betmotion.agentsmanagement.domain;

import static com.betmotion.agentsmanagement.utils.Constants.DEFAULT_BATCH_SIZE;
import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "agent_transaction")
@DynamicUpdate
@Getter
@Setter
public class AgentTransaction {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "source_user_id")
  @BatchSize(size = DEFAULT_BATCH_SIZE)
  private User sourceUser;

  @ManyToOne
  @JoinColumn(name = "target_user_id")
  @BatchSize(size = DEFAULT_BATCH_SIZE)
  private User targetUser;

  @Column(name = "amount", nullable = false)
  private Long amount;

  @Column(name = "operation_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private AgentTransactionType operationType;

  @Column(name = "operation_date", nullable = false)
  private LocalDateTime operationDate;

  @Column(name = "note")
  private String note;

  @Column(name = "balance", nullable = false)
  private Long balance;

  @Column(name = "bonus")
  private Long bonus;



  //TODO Fixme: See AgentTransactionType for multipliers
  @Formula("amount * CASE operation_type\n"
      + "           WHEN 'ADD_SALDO' THEN 1 \n"
      + "           WHEN 'REMOVE_SALDO' THEN -1 \n"
      + "           WHEN 'DEPOSIT' THEN 1 \n"
      + "           WHEN 'WITHDRAWAL' THEN -1 \n"
      + "           ELSE 1\n"
      + "END ")
  private Long reportAmount;

}
