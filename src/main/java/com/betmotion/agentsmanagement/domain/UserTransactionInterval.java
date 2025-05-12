package com.betmotion.agentsmanagement.domain;

import com.betmotion.agentsmanagement.rest.dto.user.UserTypeIntervalEnum;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user_transaction_interval")
@DynamicUpdate
@Getter
@Setter
public class UserTransactionInterval {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;
  @Column(name = "user_player_id", nullable = false)
  private Integer userPlayerId;

  @Column(name= "user_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private UserTypeIntervalEnum userType;

  @Column(name = "operation_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private UserTransactionType operationType;

  @Column(name = "operation_date", nullable = false)
  private LocalDateTime operationDate;

}
