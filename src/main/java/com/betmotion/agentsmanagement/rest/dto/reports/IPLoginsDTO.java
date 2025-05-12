package com.betmotion.agentsmanagement.rest.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IPLoginsDTO {
    private String ip;
    private String username;
    private String role;
    private String loginDate;
    private String device;
}
