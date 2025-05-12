package com.betmotion.agentsmanagement.rest.dto.operator;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OperatorDto {

  Integer id;

  String userName;
}