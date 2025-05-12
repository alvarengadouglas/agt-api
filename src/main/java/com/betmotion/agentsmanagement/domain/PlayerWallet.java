package com.betmotion.agentsmanagement.domain;

import static com.betmotion.agentsmanagement.utils.Constants.DEFAULT_BATCH_SIZE;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "player_wallet")
@DynamicUpdate
@Getter
@Setter
@BatchSize(size = DEFAULT_BATCH_SIZE)
public class PlayerWallet {


  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "player_id", nullable = false)
  private Integer playerId;

  //Positive value - player has to pay to agent
  //Negative value - agent has to pay to player
  @Column(name = "balance", nullable = false)
  private Long balance;

  @Column(name = "platform_balance", nullable = false)
  private Long platformBalance;

}
