package com.betmotion.agentsmanagement.utils;

import lombok.experimental.UtilityClass;
import org.springframework.test.util.JsonPathExpectationsHelper;

@UtilityClass
public class JsonUtils {

  public static <T> T extractValueFromJson(String jsonPath, String content) {
    return (T) new JsonPathExpectationsHelper(jsonPath).evaluateJsonPath(content);
  }
}
