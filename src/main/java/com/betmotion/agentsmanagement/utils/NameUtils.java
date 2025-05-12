package com.betmotion.agentsmanagement.utils;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;

import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

@UtilityClass
public class NameUtils {

  public static String joinNotNullStringWithSeparator(String separator, List<String> items) {
    return items.stream()
        .filter(StringUtils::hasText)
        .collect(joining(separator));
  }

  public static String joinPartForName(String... items) {
    return joinNotNullStringWithSeparator(" ", of(items).collect(toList()));
  }
}
