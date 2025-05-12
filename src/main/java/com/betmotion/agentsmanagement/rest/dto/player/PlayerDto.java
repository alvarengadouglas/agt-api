package com.betmotion.agentsmanagement.rest.dto.player;

import static com.betmotion.agentsmanagement.utils.Constants.ISO_DATE_FORMAT_VALUE;

import com.betmotion.agentsmanagement.domain.ImageDocumentStatus;
import com.betmotion.agentsmanagement.domain.UserRole;
import com.betmotion.agentsmanagement.domain.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerDto {

  Integer id;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_DATE_FORMAT_VALUE)
  Date createdOn;
  String email;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_DATE_FORMAT_VALUE)
  Date lastLogin;
  String phone;
  Boolean receiveEmail;
  UserRole role;
  UserStatus status;
  Boolean testUser;
  String userName;
  Boolean allowUpdateBirthday;
  ImageDocumentStatus validStatus;
  Boolean vip;
  Integer agentId;

}
