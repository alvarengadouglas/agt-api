package com.betmotion.agentsmanagement.rest.dto.player;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePlayerDto {

  @Email
  String email;

  @NotEmpty
  String password;

  @NotEmpty
  String userName;

  String firstName;

  String lastName;

  String phoneNumber;

  Integer countryId;

  String promocode;
}
