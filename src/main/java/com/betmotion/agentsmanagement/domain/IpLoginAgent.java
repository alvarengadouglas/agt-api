package com.betmotion.agentsmanagement.domain;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.*;

import com.betmotion.agentsmanagement.rest.dto.reports.IPLoginsDTO;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

@SqlResultSetMapping(name = "Mapping.ipLogins",
        classes = @ConstructorResult(targetClass = IPLoginsDTO.class,
                columns = {@ColumnResult(name = "ip", type = String.class),
                        @ColumnResult(name = "username", type = String.class),
                        @ColumnResult(name = "role", type = String.class),
                        @ColumnResult(name = "loginDate", type = String.class),
                        @ColumnResult(name = "device", type = String.class)
        }))

@Entity
@DynamicUpdate
@Table(name = "ip_logins_agents")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class IpLoginAgent {
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    
    @Column(name = "ip", nullable = false)
    private Long ipAddress;
    
    @Column(name = "login_date", nullable = false)
    private Date loginDate;
    
    @Column(name = "agent_id", nullable = false)
    private Integer agentId;

    @Column(name = "device")
    private String device;

} 