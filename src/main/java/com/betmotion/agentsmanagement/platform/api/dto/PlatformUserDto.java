package com.betmotion.agentsmanagement.platform.api.dto;

import static com.betmotion.agentsmanagement.platform.api.service.PlatformConstants.PLATFORM_DATE_FORMAT_VALUE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
public class PlatformUserDto {

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("username")
  private String userName;

  @JsonProperty("amount")
  private Long amount;

  @JsonProperty("status")
  private String status;

  @JsonProperty("email")
  private String email;

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("lastName")
  private String lastName;

  @JsonProperty("createdOn")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PLATFORM_DATE_FORMAT_VALUE)
  private Date createdOn;

  @JsonProperty("cellPhone")
  private String cellPhone;

}