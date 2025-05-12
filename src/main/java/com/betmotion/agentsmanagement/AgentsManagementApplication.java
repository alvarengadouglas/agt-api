package com.betmotion.agentsmanagement;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgentsManagementApplication {

  static {
//    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  public static void main(String[] args) {
    SpringApplication.run(AgentsManagementApplication.class, args);
  }


}
