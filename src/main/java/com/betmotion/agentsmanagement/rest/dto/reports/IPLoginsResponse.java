package com.betmotion.agentsmanagement.rest.dto.reports;

import lombok.*;

import java.util.List;

@Data
public class IPLoginsResponse {
    private List<IPLoginsDTO> logins;
}
