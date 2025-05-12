package com.betmotion.agentsmanagement.rest.dto.user;

import com.betmotion.agentsmanagement.domain.UserRole;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoDto {
  String userName;
  UserRole role;
  Long balance;
  String status;
  Long credits;
  Long id;
  Long platformId;

  Boolean direct;
}
