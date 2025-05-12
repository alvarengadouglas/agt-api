package com.betmotion.agentsmanagement.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "wallet_transaction")
@DynamicUpdate
@Getter
@Setter
public class WalletTransaction {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "wallet_id", nullable = false)
  private Integer walletId;

  @Column(name = "amount", nullable = false)
  private Long amount;

  @Column(name = "operation_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private WalletTransactionType operationType;

  @Column(name = "operation_date", nullable = false)
  private LocalDateTime operationDate;

  @Column(name = "note")
  private String note;

  @Column(name = "balance", nullable = false)
  private Long balance;

}
