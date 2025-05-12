package com.betmotion.agentsmanagement.platform.queue;

import com.betmotion.agentsmanagement.platform.api.dto.DepositUserRequestDto;
import com.betmotion.agentsmanagement.platform.api.dto.RegisterUserRequestDto;
import com.betmotion.agentsmanagement.platform.api.dto.WithdrawalUserRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformQueueAdapter {

    @Value("${queue.name.createDeposit}")
    private String createDepositQueue;

    @Value("${queue.name.createWithdraw}")
    private String createWithdrawQueue;

    @Value("${queue.name.createUser}")
    private String createUserQueue;

    private final RabbitTemplate rabbitTemplate;

    public void sendDeposit(DepositUserRequestDto content) {
        log.info("Sending deposit to deposit queue, content: {}", content);
        rabbitTemplate.convertAndSend(createDepositQueue, content);
        log.info("Successfully sent deposit to deposit queue");
    }

    public void sendWithdraw(WithdrawalUserRequestDto content) {
        log.info("Sending withdrawal to deposit queue, content: {}", content);
        rabbitTemplate.convertAndSend(createWithdrawQueue, content);
        log.info("Successfully sent withdrawal to deposit queue");
    }

    public void createUser(RegisterUserRequestDto content) {
        log.info("Sending createUser to queue, content: {}", content);
        rabbitTemplate.convertAndSend(createUserQueue, content);
        log.info("Successfully sent createUser to queue");
    }
}
