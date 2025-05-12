package com.betmotion.agentsmanagement.platform.api.service;

import static java.time.LocalDateTime.now;
import static java.time.ZoneOffset.UTC;
import static org.springframework.util.Base64Utils.encodeToString;

import com.betmotion.agentsmanagement.platform.api.config.PlatformApiConfiguration;
import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class TokenGenerator {

  private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
  private static final String PROVIDER = "SunJCE";
  private static final String CHARSET = "UTF-8";
  private static final String ENCODER_ALGORITHM = "AES";

  @Autowired
  PlatformApiConfiguration platformApiConfiguration;

  Cipher encript;

  public String getToken() {
    try {
      return encodeToString(encript.doFinal(now(UTC).toString().getBytes(CHARSET)));
    } catch (Exception e) {
      log.error("Error token generation", e);
      throw new RuntimeException(e);
    }
  }

  @PostConstruct
  private void afterConstruction() {
    try {
      encript = Cipher.getInstance(TRANSFORMATION, PROVIDER);
      SecretKeySpec key = new SecretKeySpec(
          platformApiConfiguration.getPrivateKey().getBytes(CHARSET), ENCODER_ALGORITHM);
      encript.init(Cipher.ENCRYPT_MODE, key,
          new IvParameterSpec(platformApiConfiguration.getVerbose().getBytes(CHARSET)));
    } catch (Exception e) {
      log.error("Error initialization of stuff for token generation", e);
      throw new RuntimeException(e);
    }
  }
}
