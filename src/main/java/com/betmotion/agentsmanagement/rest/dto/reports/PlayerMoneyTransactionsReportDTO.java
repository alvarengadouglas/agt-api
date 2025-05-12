package com.betmotion.agentsmanagement.rest.dto.reports;

import com.betmotion.agentsmanagement.platform.api.dto.UserTransactionDTO;
import com.betmotion.agentsmanagement.rest.dto.PageData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerMoneyTransactionsReportDTO {

  PageData<UserTransactionDTO> data;
  public PlayerMoneyTransactionsReportDTO(PlayerMoneyTransactionsReportResponse response, Integer pageSize){
    setData(new PageData<>(response.getItems(),(int)Math.ceil(response.getCount()/(float)pageSize),response.getCount()));
    data.getData().forEach(el -> el.setMoneyAmount(
            el.getType().equals("BET") ? el.getDebit() - el.getBonusAmount() :
            el.getType().equals("AWARD_WINNINGS") ? el.getCredit() - el.getBonusAmount() : 0
    ));
  }
}
