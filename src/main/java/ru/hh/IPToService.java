package ru.hh;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class IPToService {

  private static Map<String, String> ipToService;

  static {
    List<Map.Entry<String, String[]>> serviceToIPs = List.of(
        entry("advdream", new String[]{"192.168.2.132", "192.168.2.133"}),
        entry("auth-proxy", new String[]{"192.168.1.73", "192.168.1.229", "192.168.1.133"}),
        entry("autosearch", new String[]{"192.168.1.54", "192.168.1.55"}),
        entry("as", new String[]{"192.168.1.14", "192.168.1.15", "192.168.1.27", "192.168.1.28", "192.168.1.60", "192.168.1.62", "192.168.1.71"}),
        entry("badclients", new String[]{"192.168.1.249"}),
        entry("bbo", new String[]{"192.168.2.3", "192.168.2.4", "192.168.2.5"}),
        entry("banneradm", new String[]{"192.168.2.87"}),
        entry("bannerb", new String[]{"192.168.1.112"}),
        entry("billing", new String[]{"192.168.1.46", "192.168.1.48", "192.168.1.201"}),
        entry("cms", new String[]{"192.168.1.48", "192.168.1.147", "192.168.1.149", "192.168.2.138"}),
        entry("crm", new String[]{"192.168.1.181", "192.168.1.182", "192.168.1.183"}),
        entry("front", new String[]{"192.168.1.3", "192.168.1.4", "192.168.1.70", "192.168.1.79", "192.168.1.84", "192.168.1.189"}),
        entry("hrspace", new String[]{"192.168.1.110"}),
        entry("jlogic", new String[]{"192.168.1.142", "192.168.2.198", "192.168.2.199", "192.168.2.200"}),
        entry("makeup", new String[]{"192.168.2.234"}),
        entry("meta", new String[]{"192.168.1.143", "192.168.1.145"}),
        entry("mob-notifier", new String[]{"192.168.1.225", "192.168.1.217"}),
        entry("resizer", new String[]{"192.168.1.191", "192.168.2.43"}),
        entry("rexport", new String[]{"192.168.1.24", "192.168.1.29"}),
        entry("tms", new String[]{"192.168.2.188", "192.168.2.189", "192.168.2.208"}),
        entry("xmlback", new String[]{"192.168.1.22", "192.168.1.56", "192.168.1.98", "192.168.1.202", "192.168.1.205", "192.168.1.218", "192.168.1.219", "192.168.1.221", "192.168.2.56"}),
        entry("docker", new String[]{"192.168.1.91", "192.168.1.146", "192.168.1.208", "192.168.1.209", "192.168.1.212", "192.168.1.213", "192.168.1.222",
            "192.168.2.30", "192.168.2.231", "192.168.2.244", "192.168.2.250", "192.168.2.251", "192.168.2.252", "192.168.2.253", "192.168.2.254"})
    );
    ipToService = new HashMap<>();
    for (Map.Entry<String, String[]> serviceAndIPs : serviceToIPs) {
      String service = serviceAndIPs.getKey();
      for (String ip : serviceAndIPs.getValue()) {
        ipToService.put(ip, service);
      }
    }
  }

  public static String get(String ip) {
    String service = ipToService.get(ip);
    if (service == null) {
      throw new RuntimeException("unknown service by ip " + ip);
    }
    return service;
  }

  private IPToService() {
  }
}
