package com.betmotion.agentsmanagement.rest.dto.errors;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FieldError {

  String fieldName;

  String message;

}
