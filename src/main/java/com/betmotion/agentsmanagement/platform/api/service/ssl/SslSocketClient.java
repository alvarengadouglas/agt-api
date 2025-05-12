package com.betmotion.agentsmanagement.platform.api.service.ssl;

import java.security.SecureRandom;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SslSocketClient {

  public static SSLSocketFactory getSkippedSslSocketFactory() {
    try {
      SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(null, getTrustManager(), new SecureRandom());
      return sslContext.getSocketFactory();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static TrustManager[] getTrustManager() {
    return new TrustManager[]{new TrustAllX509TrustManager()};
  }

  public static HostnameVerifier allHostsAreTrustedVerifier() {
    return (s, sslSession) -> true;
  }
}
