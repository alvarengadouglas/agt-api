package com.betmotion.agentsmanagement.queue;

import com.betmotion.agentsmanagement.platform.api.dto.RegisterUserResponseDto;
import com.betmotion.agentsmanagement.queue.dto.TransactionConfirmStatusDto;
import com.betmotion.agentsmanagement.service.AgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQListeners {

    private final AgentService agentService;

    @RabbitListener(queues = "CreateDepositReplyQueue")
    public void listenDeposit(@Payload TransactionConfirmStatusDto transactionConfirmStatusDto){
        log.info("Received deposit confirmation: {}", transactionConfirmStatusDto);
        agentService.confirmTransactionDeposit(transactionConfirmStatusDto);
        log.info("Confirmed deposit transaction: {}", transactionConfirmStatusDto);
    }

    @RabbitListener(queues = "CreateWithdrawReplyQueue")
    public void listenWithdraw(@Payload TransactionConfirmStatusDto transactionConfirmStatusDto){
        log.info("Received Withdraw confirmation: {}", transactionConfirmStatusDto);
        agentService.confirmTransactionWithdraw(transactionConfirmStatusDto);
        log.info("Confirmed Withdraw transaction: {}", transactionConfirmStatusDto);
    }

    @RabbitListener(queues = "CreateUserReplyQueue")
    public void listenCreateUser(@Payload RegisterUserResponseDto registerUserResponseDto){
        log.info("Received listenCreateUser confirmation: {}", registerUserResponseDto);
        agentService.updateNewUserWithPlatformId(registerUserResponseDto);
        log.info("Confirmed listenCreateUser transaction: {}", registerUserResponseDto);
    }
}
