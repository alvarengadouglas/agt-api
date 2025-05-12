package com.betmotion.agentsmanagement.rest.dto.reports;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportUserInfo {

  Integer id;

  String name;
}
