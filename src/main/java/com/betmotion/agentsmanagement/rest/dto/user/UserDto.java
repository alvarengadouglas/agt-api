package com.betmotion.agentsmanagement.rest.dto.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import org.springframework.security.core.GrantedAuthority;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

  String userName;
  String agentCode;
  String fullName;
  String email;
  String parentUserName;
  UserRole role;
  List<String> authorities;
  Long balance;
  Long credits;
  BigDecimal commission;
  BigDecimal commissionCasino;
  BigDecimal commissionSlots;
  BigDecimal commissionSports;
  Integer parentId;
  Integer agentsCount;
  Integer playersCount;
}
