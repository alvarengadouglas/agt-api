package com.betmotion.agentsmanagement.rest.dto.operator;

import lombok.Data;

@Data
public class ChangePasswordDto {

    private String password;
    private String code;
}
