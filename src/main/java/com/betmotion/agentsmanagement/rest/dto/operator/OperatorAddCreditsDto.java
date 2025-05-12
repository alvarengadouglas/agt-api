package com.betmotion.agentsmanagement.rest.dto.operator;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class OperatorAddCreditsDto {

    @NotNull
    private Long amount;

    @NotBlank
    private String code;
}
