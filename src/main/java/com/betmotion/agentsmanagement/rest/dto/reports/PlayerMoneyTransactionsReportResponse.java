package com.betmotion.agentsmanagement.rest.dto.reports;

import com.betmotion.agentsmanagement.platform.api.dto.UserTransactionDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerMoneyTransactionsReportResponse {

  List<UserTransactionDTO> items;

  Long count;

  public static PlayerMoneyTransactionsReportResponse Empty(){
    PlayerMoneyTransactionsReportResponse r = new PlayerMoneyTransactionsReportResponse();
    r.setCount(0L);
    r.setItems(new ArrayList<>());
    return r;
  }
}
