package com.jeremykenedy.pipeworksdream;

public final class SettingsValues {
  private SettingsValues() {}

  public static boolean isSupported(String key, String value) {
    if (key == null || value == null) return false;
    if ("density".equals(key))
      return oneOf(value, "few", "handful", "many", "ton", "schools", "random");
    if ("speed".equals(key)) return oneOf(value, "slow", "natural", "quick", "random");
    if ("palette".equals(key))
      return oneOf(value, "copper", "cool", "neon", "monochrome", "random");
    if ("background".equals(key)) return oneOf(value, "midnight", "ocean", "graphite", "random");
    if ("lighting".equals(key)) return oneOf(value, "subtle", "cinematic", "bright", "random");
    if ("glow".equals(key)) return oneOf(value, "on", "off", "random");
    if ("randomize_all".equals(key)) return oneOf(value, "true", "false");
    return false;
  }

  private static boolean oneOf(String value, String... allowed) {
    for (String option : allowed) if (option.equals(value)) return true;
    return false;
  }
}
