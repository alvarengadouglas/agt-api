package com.betmotion.agentsmanagement.service;

import com.betmotion.agentsmanagement.dao.MfaCodeRepository;
import com.betmotion.agentsmanagement.dao.UserRepository;
import com.betmotion.agentsmanagement.domain.MfaCode;
import com.betmotion.agentsmanagement.domain.User;
import com.betmotion.agentsmanagement.platform.api.PlatformApi;
import com.betmotion.agentsmanagement.service.exceptions.ServiceException;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Calendar;
import java.util.Map;
import java.util.Random;

import static lombok.AccessLevel.PRIVATE;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class MfaService {

    MfaCodeRepository mfaCodeRepository;
    UserRepository userRepository;
    PlatformApi platformApi;

    private static final String ALLOWED_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int CODE_LENGTH = 5;
    private Random random = new Random();
    private final Docket api;

    @Transactional
    public MfaCode generateMfaChangePassword(String username) {

        User user = userRepository.findByUserName(username);

        if (user == null) {
            throw new ServiceException("OP02", new Object[] { username });
        }

        MfaCode mfaCode = generateMfaCodeToUser(user);

        sendEmailChangePassword(user, mfaCode.getCode());

        return mfaCode;
    }

    @Transactional
    public MfaCode generateMfaAddCredits(String username) {


        User user = userRepository.findByUserName(username);

        if (user == null) {
            throw new ServiceException("OP02", new Object[] { username });
        }

        MfaCode mfaCode = generateMfaCodeToUser(user);

        sendEmailAddCredits(user, mfaCode.getCode());

        return mfaCode;
    }

    @Transactional
    public MfaCode generateMfaCodeToUser(User user) {
        String code = generateRandomCode();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);

        MfaCode mfaCode = MfaCode.builder()
                .code(code)
                .user(user)
                .expiresAt(calendar.getTime())
                .createdAt(Calendar.getInstance().getTime())
                .build();

        mfaCodeRepository.deleteByUserId(user.getId());
        mfaCodeRepository.save(mfaCode);

        log.info("Generated MFA code: {} user: {}", code, user.getId());

        return mfaCode;
    }

    private String generateRandomCode() {
        StringBuilder codeBuilder = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            codeBuilder.append(ALLOWED_CHARACTERS.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    private void sendEmailChangePassword(User user, String code) {
        Map<String, Object>  body = Map.of("email", user.getEmail(), "code", code, "userName", user.getUserName());
        log.info("Sending email TFA to change password: {}", body);
        platformApi.mfaSendEmailCodeChangePassword(body);
    }

    private void sendEmailAddCredits(User user, String code) {
        Map<String, Object>  body = Map.of("email", user.getEmail(), "code", code, "userName", user.getUserName());
        platformApi.mfaSendEmailCodeAddCredits(body);
    }

}
