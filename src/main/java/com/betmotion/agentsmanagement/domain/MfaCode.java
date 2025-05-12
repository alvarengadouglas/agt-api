package com.betmotion.agentsmanagement.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "mfa_code")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MfaCode {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String code;

    @Column(name = "expires_at")
    private Date expiresAt;

    @Column(name = "created_at")
    private Date createdAt;
}
