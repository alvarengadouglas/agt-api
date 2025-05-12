package com.betmotion.agentsmanagement.platform.api.service.ssl;

import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class TrustAllX509TrustManager implements X509TrustManager {

  public TrustAllX509TrustManager() {
  }

  public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
  }

  public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
  }

  public X509Certificate[] getAcceptedIssuers() {
    return new X509Certificate[0];
  }
}
