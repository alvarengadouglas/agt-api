package com.betmotion.agentsmanagement.rest.dto.errors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ErrorDto {

  List<String> globalErrors;

  List<FieldError> fieldErrors;

  public static ErrorDto withGlobalError(String message) {
    return new ErrorDto(singletonList(message), emptyList());
  }

}
