package com.betmotion.agentsmanagement.domain;


import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@FieldDefaults(makeFinal = true, level = PRIVATE)
@AllArgsConstructor
public enum Currency {
  USD("840", "US$"), BRL("986", "R$"), EUR("978", "$"), MXN("484", "$"), CLP("152", "$"), PEN("604",
      "$"), UYU("858", "$"), COP("170", "$"), ARS("032", "$"), PYG("600", "$");

  String numericCode;

  String sign;


}
