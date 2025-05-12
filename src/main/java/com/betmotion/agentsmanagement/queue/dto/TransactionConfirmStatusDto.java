package com.betmotion.agentsmanagement.queue.dto;

import com.betmotion.agentsmanagement.domain.UserTransactionStatus;
import lombok.Data;

@Data
public class TransactionConfirmStatusDto {

    private Long remoteId;
    private UserTransactionStatus status;
    private Long chargedRemoteId;
}
