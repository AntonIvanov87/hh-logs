package ru.hh;

import java.util.Map;

import static java.util.Map.entry;

public class IntbalPortToService {

  private static final Map<String, String> portToService = Map.ofEntries(
      entry("7001", "tut-by"),
      entry("7003", "day-az"),
      entry("8003", "hhid"),
      entry("8004", "autosearch"),
      entry("8006", "auth-proxy"),
      entry("8009", "session"),
      entry("8014", "makeup"),
      entry("8015", "logic"),
      entry("8016", "hh-correktor"),
      entry("8017", "as"),
      entry("8018", "negotiations"),
      entry("8019", "resume-export"),
      entry("8021", "cms"),
      entry("8022", "career-services"),
      entry("8027", "billing"),
      entry("8028", "bbo"),
      entry("8029", "hh-salary-stat"),
      entry("8032", "banner"),
      entry("8033", "jlogic"),
      entry("8034", "assessments"),
      entry("8035", "kardinal"),
      entry("8036", "hh-tests-manager"),
      entry("8037", "kardinal-ranking"),
      entry("8103", "hhid"),
      entry("8669", "xmlback"),
      entry("9099", "search"),
      entry("9171", "hh-rs"),
      entry("9185", "soc-service"),
      entry("9199", "search"),
      entry("9292", "mob-notifier"),
      entry("9393", "hh-relations"),
      entry("9494", "hh-similar-query"),
      entry("9496", "hh-suggest")
  );

  public static String get(String port) {
    String service = portToService.get(port);
    if (service == null) {
      throw new RuntimeException("unknown service by intbal port " + port);
    }
    return service;
  }

  private IntbalPortToService() {
  }
}
