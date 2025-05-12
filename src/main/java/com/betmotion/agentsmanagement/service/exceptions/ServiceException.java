package com.betmotion.agentsmanagement.service.exceptions;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ServiceException extends RuntimeException {

  String errorCode;

  transient Object[] args;
}
