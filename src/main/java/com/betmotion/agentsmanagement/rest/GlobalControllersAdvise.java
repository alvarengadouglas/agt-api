package com.betmotion.agentsmanagement.rest;

import static com.betmotion.agentsmanagement.rest.dto.errors.ErrorDto.withGlobalError;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.betmotion.agentsmanagement.platform.api.config.PlatformApiConfiguration;
import com.betmotion.agentsmanagement.platform.api.dto.PlatformApiErrorDto;
import com.betmotion.agentsmanagement.powerbi.api.PowerBiErrorDto;
import com.betmotion.agentsmanagement.powerbi.config.PowerBiTokenConfiguration;
import com.betmotion.agentsmanagement.rest.dto.errors.ErrorDto;
import com.betmotion.agentsmanagement.rest.dto.errors.FieldError;
import com.betmotion.agentsmanagement.service.exceptions.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import java.util.List;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GlobalControllersAdvise {

  MessageSource messageSource;

  ObjectMapper objectMapper;

  PlatformApiConfiguration platformApiConfiguration;

  PowerBiTokenConfiguration powerBiTokenConfiguration;

  @ExceptionHandler(value = ServiceException.class)
  public ResponseEntity<ErrorDto> checkServiceException(ServiceException exception, Locale locale) {
    log.error("Service exception", exception);
    String messageCode = "errors." + exception.getErrorCode() + ".message";
    String message = messageSource.getMessage(messageCode, exception.getArgs(), locale);
    return new ResponseEntity<>(withGlobalError(message), BAD_REQUEST);
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDto> checkArgumentNotValidException(
      MethodArgumentNotValidException exception, Locale locale) {
    log.error("Argument not valid", exception);
    BindingResult bindingResult = exception.getBindingResult();
    List<FieldError> fieldErrors = bindingResult
        .getFieldErrors()
        .stream()
        .map(item -> new FieldError(item.getField(), messageSource.getMessage(item, locale)))
        .collect(toList());
    List<String> globalErrors = bindingResult
        .getGlobalErrors()
        .stream()
        .map(item -> messageSource.getMessage(item, locale))
        .collect(toList());
    return new ResponseEntity<>(new ErrorDto(globalErrors, fieldErrors), BAD_REQUEST);
  }

  @ExceptionHandler(value = FeignException.class)
  public ResponseEntity<ErrorDto> handleFeignExceptions(FeignException e) {
    log.error("Feign exception", e);
    return new ResponseEntity<>(new ErrorDto(singletonList(getMessage(e)),
        emptyList()), BAD_REQUEST);
  }

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<ErrorDto> handleAllExceptions(Exception e) {
    log.error("Uncatched exception", e);
    return new ResponseEntity<>(new ErrorDto(singletonList("Internal server error"),
        emptyList()), INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = AccessDeniedException.class)
  public ResponseEntity<ErrorDto> handleAccessDeniedExceptions(AccessDeniedException e) {
    log.error("Access denied", e);
    return new ResponseEntity<>(new ErrorDto(singletonList("Forbidden"),
        emptyList()), FORBIDDEN);
  }

  @ExceptionHandler(value = AuthenticationException.class)
  public ResponseEntity<ErrorDto> handleAuthenticationException(AuthenticationException e) {
    log.error("Authentication exception", e);
    return new ResponseEntity<>(new ErrorDto(singletonList("Unauthorized"),
        emptyList()), UNAUTHORIZED);
  }

  private String getMessage(FeignException e) {
    String text = "";
    boolean platformCall = e.request().url().startsWith(platformApiConfiguration.getUrl());
    boolean powerBiCall = e.request().url().startsWith(powerBiTokenConfiguration.getUrl());
    if (e.responseBody().isPresent()) {
      text = new String(e.responseBody().get().array(), UTF_8);
    }
    if (!isEmpty(text)) {
      try {
        if (platformCall) {
          text = objectMapper.readValue(text, PlatformApiErrorDto.class).getMessage();
        }
        if (powerBiCall) {
          text = objectMapper.readValue(text, PowerBiErrorDto.class).getErrorDescription();
        }
      } catch (Exception ex) {
        text = "";
        log.error("Error during parse", e);
      }
    }
    if (platformCall) {
      return isEmpty(text) ? "Error communication with platform" : text;
    }
    if (powerBiCall) {
      return isEmpty(text) ? "Error communication with Power BI" : text;
    }
    return "Unknown error";
  }
}
