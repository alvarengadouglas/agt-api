package com.betmotion.agentsmanagement.rest.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class PowerBiTokenDto {

  String token;
}
