package com.betmotion.agentsmanagement.domain;

import static com.betmotion.agentsmanagement.utils.Constants.DEFAULT_BATCH_SIZE;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import com.betmotion.agentsmanagement.dao.projection.UserDetailInfo;
import com.betmotion.agentsmanagement.dao.projection.UserInfo;
import com.betmotion.agentsmanagement.rest.dto.agent.AgentDeactivationDto;

import lombok.Getter;
import lombok.Setter;

@SqlResultSetMapping(name = "Mapping.userInfo",
    classes = @ConstructorResult(targetClass = UserInfo.class,
        columns = {@ColumnResult(name = "userName", type = String.class),
            @ColumnResult(name = "role", type = String.class),
            @ColumnResult(name = "balance", type = Long.class),
            @ColumnResult(name = "credits", type = Long.class),
            @ColumnResult(name = "status", type = String.class),
            @ColumnResult(name = "id", type = Long.class),
            @ColumnResult(name = "platformId", type = Long.class),
            @ColumnResult(name = "parentId", type = Long.class)}))

@SqlResultSetMapping(name = "Mapping.userDetailInfo",
    classes = @ConstructorResult(targetClass = UserDetailInfo.class,
        columns = {@ColumnResult(name = "id", type = Long.class),
            @ColumnResult(name = "userName", type = String.class),
            @ColumnResult(name = "fullName", type = String.class),
            @ColumnResult(name = "email", type = String.class),
            @ColumnResult(name = "phone", type = String.class),
            @ColumnResult(name = "role", type = String.class),
            @ColumnResult(name = "createdOn", type = Date.class),
            @ColumnResult(name = "parentUserName", type = String.class),
            @ColumnResult(name = "numberOfDirectSubAgents", type = Long.class),
            @ColumnResult(name = "numberOfDirectPlayers", type = Long.class),
            @ColumnResult(name = "commissionType", type = String.class),
            @ColumnResult(name = "commission", type = String.class),
            @ColumnResult(name = "commissionSports", type = String.class),
            @ColumnResult(name = "commissionSlots", type = String.class),
            @ColumnResult(name = "commissionCasino", type = String.class)
    }))

@SqlResultSetMapping(name = "Mapping.agentDeactivation",
        classes = @ConstructorResult(targetClass = AgentDeactivationDto.class,
                columns = {
                        @ColumnResult(name = "id", type = Integer.class),
                        @ColumnResult(name = "userName", type = String.class),
                        @ColumnResult(name = "agentId", type = Integer.class)}))
@Entity
@Table(name = "users")
@DynamicUpdate
@Getter
@Setter
@BatchSize(size = DEFAULT_BATCH_SIZE)
public class User {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "user_name", nullable = false, length = 100)
  private String userName;

  @Column(name = "password", nullable = false, length = 128)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "user_role", nullable = false, length = 40)
  private UserRole role;

  @Column(name = "created_on", nullable = false)
  private Date createdOn;

  @Column(name = "bornDate", nullable = false)
  private Date bornDate;

  @Column(name = "currency", nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private Currency currency;

  @Column(name = "mail", unique = true, nullable = false, length = 100)
  private String email;

  @Column(name = "last_login", nullable = false)
  private Date lastLogin;

  @Column(name = "phone", nullable = false, length = 30)
  private String phone;

  @Column(name = "receiveEmail", nullable = false)
  private Boolean receiveEmail;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @Column(name = "test_user", nullable = false)
  private Boolean testUser;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "agent_id")
  @BatchSize(size = DEFAULT_BATCH_SIZE)
  private Agent agent;

  @Column(name = "agent_id", nullable = false, insertable = false, updatable = false)
  private Integer agentId;
}