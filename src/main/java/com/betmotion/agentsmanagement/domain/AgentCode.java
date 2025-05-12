package com.betmotion.agentsmanagement.domain;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "agent_codes")
@DynamicUpdate
@Getter
@Setter
public class AgentCode {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;


  @Column(name = "code")
  private String code;

}
