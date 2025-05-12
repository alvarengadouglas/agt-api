package com.betmotion.agentsmanagement.domain;

import static com.betmotion.agentsmanagement.utils.Constants.DEFAULT_BATCH_SIZE;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.SqlResultSetMapping;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import com.betmotion.agentsmanagement.dao.projection.PlayerInfo;

@SqlResultSetMapping(name = "Mapping.playerInfo",
    classes = @ConstructorResult(targetClass = PlayerInfo.class,
        columns = {@ColumnResult(name = "balance", type = Long.class),
            @ColumnResult(name = "id", type = Integer.class)}))

@Entity
@Table(name = "players")
@DynamicUpdate
@Getter
@Setter
@BatchSize(size = DEFAULT_BATCH_SIZE)
public class Player {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "user_name", nullable = false, length = 100)
  private String userName;

  @Column(name = "platform_id", nullable = false)
  private Integer platformId;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @Column(name = "user_id", nullable = false)
  private Integer userId;
}
