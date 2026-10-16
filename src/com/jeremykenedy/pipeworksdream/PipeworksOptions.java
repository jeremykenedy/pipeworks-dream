package com.jeremykenedy.pipeworksdream;

import java.util.Random;

public final class PipeworksOptions {
  public final int pipeCount;
  public final int stepDelay;
  public final int palette;
  public final int background;
  public final int lighting;
  public final boolean glow;
  public final int shape;
  public final float radius;
  public final boolean followingTail;
  public final int tailDelaySeconds;

  private PipeworksOptions(
      int pipeCount,
      int stepDelay,
      int palette,
      int background,
      int lighting,
      boolean glow,
      int shape,
      float radius,
      boolean followingTail,
      int tailDelaySeconds) {
    this.pipeCount = pipeCount;
    this.stepDelay = stepDelay;
    this.palette = palette;
    this.background = background;
    this.lighting = lighting;
    this.glow = glow;
    this.shape = shape;
    this.radius = radius;
    this.followingTail = followingTail;
    this.tailDelaySeconds = tailDelaySeconds;
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
    return resolve(
        density,
        speed,
        palette,
        background,
        lighting,
        glow,
        "round",
        "medium",
        "keep",
        "15",
        randomizeAll,
        random);
  }

  public static PipeworksOptions resolve(
      String density,
      String speed,
      String palette,
      String background,
      String lighting,
      String glow,
      String shape,
      String size,
      String tail,
      String tailDelay,
      boolean randomizeAll,
      Random random) {
    String selectedDensity =
        choose(density, randomizeAll, random, "handful", "few", "many", "ton", "schools");
    String selectedSpeed = choose(speed, randomizeAll, random, "natural", "slow", "quick");
    String selectedPalette =
        choose(
            palette,
            randomizeAll,
            random,
            "copper",
            "cool",
            "neon",
            "monochrome",
            "red",
            "green",
            "blue",
            "purple",
            "gold");
    String selectedBackground =
        choose(background, randomizeAll, random, "midnight", "ocean", "graphite");
    String selectedLighting =
        choose(lighting, randomizeAll, random, "subtle", "cinematic", "bright");
    String selectedGlow = choose(glow, randomizeAll, random, "on", "off");
    String selectedShape = choose(shape, randomizeAll, random, "round", "square", "hexagonal");
    String selectedSize = choose(size, randomizeAll, random, "medium", "thin", "thick");
    String selectedTail = choose(tail, randomizeAll, random, "keep", "follow");
    String selectedDelay = choose(tailDelay, randomizeAll, random, "15", "5", "30", "60");
    return new PipeworksOptions(
        countFor(selectedDensity),
        delayFor(selectedSpeed),
        paletteFor(selectedPalette),
        backgroundFor(selectedBackground),
        lightingFor(selectedLighting),
        "on".equals(selectedGlow),
        shapeFor(selectedShape),
        radiusFor(selectedSize),
        "follow".equals(selectedTail),
        tailDelayFor(selectedDelay));
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
    if ("red".equals(palette)) return 4;
    if ("green".equals(palette)) return 5;
    if ("blue".equals(palette)) return 6;
    if ("purple".equals(palette)) return 7;
    if ("gold".equals(palette)) return 8;
    return 0;
  }

  public int tailPoints() {
    return followingTail ? (int) Math.ceil(tailDelaySeconds * 1000.0 / stepDelay) + 2 : 0;
  }

  static int shapeFor(String shape) {
    if ("square".equals(shape)) return 1;
    if ("hexagonal".equals(shape)) return 2;
    return 0;
  }

  static float radiusFor(String size) {
    if ("thin".equals(size)) return .10f;
    if ("thick".equals(size)) return .24f;
    return .17f;
  }

  static int tailDelayFor(String delay) {
    if ("5".equals(delay)) return 5;
    if ("30".equals(delay)) return 30;
    if ("60".equals(delay)) return 60;
    return 15;
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
