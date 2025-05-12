package com.betmotion.agentsmanagement.rest.dto.user;

import com.betmotion.agentsmanagement.domain.UserRole;
import com.betmotion.agentsmanagement.rest.dto.agent.ExtendedInfoAgentDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailInfoDto {
  Long id;
  String userName;
  String fullName;
  String email;

  String phone;

  UserRole role;
  String createdOn;
  ExtendedInfoAgentDto extendedInfoAgentDto;
  String parentUserName;
  Long numberOfDirectSubAgents;
  Long numberOfDirectPlayers;
  String commissionType ;
  String commission ;
  String commissionSports ;
  String commissionSlots ;
  String commissionCasino ;
}
