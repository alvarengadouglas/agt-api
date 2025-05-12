package com.betmotion.agentsmanagement.powerbi.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PowerBiErrorDto {

  @JsonProperty("error")
  private String error;

  @JsonProperty("error_description")
  private String errorDescription;

}
