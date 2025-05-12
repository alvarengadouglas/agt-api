package com.betmotion.agentsmanagement.dao.projection;

import lombok.Value;

@Value
public class UserInfo {
  String userName;
  String role;
  Long balance;
  Long credits;
  String status;
  Long id;
  Long platformId;
  Long parentId;
}
