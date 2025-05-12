package com.betmotion.agentsmanagement.rest.dto.player;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordPlayerDto {

  @NotNull
  Integer playerId;

  @NotEmpty
  String password;

}
