package com.betmotion.agentsmanagement.dao.projection;

import java.util.Date;
import lombok.Value;

@Value
public class UserDetailInfo {
  Long id;
  String userName;
  String fullName;
  String email;
  String phone;

  String role;
  Date createdOn;
  String parentUserName;
  Long numberOfDirectSubAgents;
  Long numberOfDirectPlayers;

  String commissionType;
  String commission;
  String commissionSports;
  String commissionSlots;
  String commissionCasino;
}
