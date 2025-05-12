package com.betmotion.agentsmanagement.domain;


import static com.betmotion.agentsmanagement.utils.Constants.DEFAULT_BATCH_SIZE;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "agents_players")
@DynamicUpdate
@Getter
@Setter
public class AgentPlayer {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "players_id", nullable = false, insertable = true)
  private Integer playerId;

  @Column(name = "agent_id", insertable = false, updatable = false)
  private Integer agentId;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "agent_id")
  @BatchSize(size = DEFAULT_BATCH_SIZE)
  private Agent agent;
}
