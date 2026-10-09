package com.jeremykenedy.pipeworksdream;

import java.util.Random;

public final class PipeworksOptions {
  public final int pipeCount;
  public final int stepDelay;
  public final int palette;
  public final int background;
  public final int lighting;
  public final boolean glow;

  private PipeworksOptions(
      int pipeCount, int stepDelay, int palette, int background, int lighting, boolean glow) {
    this.pipeCount = pipeCount;
    this.stepDelay = stepDelay;
    this.palette = palette;
    this.background = background;
    this.lighting = lighting;
    this.glow = glow;
  }

  public static PipeworksOptions resolve(
      String density,
      String speed,
      String palette,
      String background,
      String lighting,
      String glow,
      boolean randomizeAll,
      Random random) {
    String selectedDensity =
        choose(density, randomizeAll, random, "handful", "few", "many", "ton", "schools");
    String selectedSpeed = choose(speed, randomizeAll, random, "natural", "slow", "quick");
    String selectedPalette =
        choose(palette, randomizeAll, random, "copper", "cool", "neon", "monochrome");
    String selectedBackground =
        choose(background, randomizeAll, random, "midnight", "ocean", "graphite");
    String selectedLighting =
        choose(lighting, randomizeAll, random, "subtle", "cinematic", "bright");
    String selectedGlow = choose(glow, randomizeAll, random, "on", "off");
    return new PipeworksOptions(
        countFor(selectedDensity),
        delayFor(selectedSpeed),
        paletteFor(selectedPalette),
        backgroundFor(selectedBackground),
        lightingFor(selectedLighting),
        "on".equals(selectedGlow));
  }

  private static String choose(
      String selected, boolean randomizeAll, Random random, String... choices) {
    if (randomizeAll || "random".equals(selected)) {
      return choices[random.nextInt(choices.length)];
    }
    for (String choice : choices) {
      if (choice.equals(selected)) {
        return selected;
      }
    }
    return choices[0];
  }

  static int countFor(String density) {
    if ("few".equals(density)) return 4;
    if ("many".equals(density)) return 12;
    if ("ton".equals(density)) return 20;
    if ("schools".equals(density)) return 28;
    return 8;
  }

  static int delayFor(String speed) {
    if ("slow".equals(speed)) return 1050;
    if ("quick".equals(speed)) return 340;
    return 650;
  }

  static int paletteFor(String palette) {
    if ("cool".equals(palette)) return 1;
    if ("neon".equals(palette)) return 2;
    if ("monochrome".equals(palette)) return 3;
    return 0;
  }

  static int backgroundFor(String background) {
    if ("ocean".equals(background)) return 1;
    if ("graphite".equals(background)) return 2;
    return 0;
  }

  static int lightingFor(String lighting) {
    if ("cinematic".equals(lighting)) return 1;
    if ("bright".equals(lighting)) return 2;
    return 0;
  }
}
