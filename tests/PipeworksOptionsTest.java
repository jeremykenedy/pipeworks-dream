package com.jeremykenedy.pipeworksdream;

import java.util.Random;

public final class PipeworksOptionsTest {
  public static void main(String[] args) {
    verifyChoices();
    verifyPipeStylesAndFollowingTail();
    verifyProviderContract();
    verifyEngineMotion();
    verifyDeterministicTurnsAndOccupiedCells();
    verifyInvalidEngineInputs();
  }

  private static void verifyPipeStylesAndFollowingTail() {
    String[] colors = {"red", "green", "blue", "purple", "gold"};
    for (int i = 0; i < colors.length; i++)
      check(PipeworksOptions.paletteFor(colors[i]) == i + 4, "individual pipe color");
    check(PipeworksOptions.shapeFor("round") == 0, "round tubes");
    check(PipeworksOptions.shapeFor("square") == 1, "square ducts");
    check(PipeworksOptions.shapeFor("hexagonal") == 2, "hexagonal tubes");
    check(PipeworksOptions.shapeFor("invalid") == 0, "unknown shape defaults to round");
    check(PipeworksOptions.radiusFor("thin") == .10f, "thin radius");
    check(PipeworksOptions.radiusFor("medium") == .17f, "medium radius");
    check(PipeworksOptions.radiusFor("thick") == .24f, "thick radius");
    check(PipeworksOptions.radiusFor("invalid") == .17f, "unknown thickness uses medium");
    for (int seconds : new int[] {5, 15, 30, 60}) {
      check(
          PipeworksOptions.tailDelayFor(Integer.toString(seconds)) == seconds,
          "tail delay seconds");
      for (String speed : new String[] {"slow", "natural", "quick"}) {
        PipeworksOptions follow =
            PipeworksOptions.resolve(
                "few",
                speed,
                "blue",
                "midnight",
                "cinematic",
                "off",
                "square",
                "thick",
                "follow",
                Integer.toString(seconds),
                false,
                new Random(1));
        check(
            follow.followingTail
                && follow.shape == 1
                && follow.radius == .24f
                && follow.palette == 6,
            "explicit pipe controls apply together");
        check(
            (follow.tailPoints() - 2) * follow.stepDelay >= seconds * 1000,
            "tail never starts before selected delay");
        check(
            (follow.tailPoints() - 3) * follow.stepDelay < seconds * 1000,
            "tail starts within one growth step of selected delay");
      }
    }
    check(
        PipeworksOptions.tailDelayFor("invalid") == 15, "unknown tail delay defaults to15seconds");
    PipeworksOptions defaults =
        PipeworksOptions.resolve(
            "few", "natural", "copper", "midnight", "cinematic", "on", false, new Random(1));
    check(
        defaults.tailPoints() == 0
            && !defaults.followingTail
            && defaults.shape == 0
            && defaults.radius == .17f,
        "existing preferences retain full round pipe trails");
    PipeEngine following = new PipeEngine(400, 2, 2, 1, new ZeroRandom(), 4);
    for (int tick = 1; tick <= 1600; tick++) {
      following.advance();
      int[] snapshot = following.snapshot();
      check(
          snapshot.length == Math.min(tick + 1, 4) * 4,
          "following tail remains bounded and reuses released cells");
      if (tick == 20)
        check(snapshot[0] == 17 && snapshot[12] == 20, "oldest point follows behind growing head");
    }
    for (int length : new int[] {-1, 1, 183}) {
      try {
        new PipeEngine(5, 5, 5, 1, new Random(1), length);
        throw new AssertionError("invalid tail length accepted");
      } catch (IllegalArgumentException expected) {
        check(expected.getMessage() != null, "invalid tail length explains bounds");
      }
    }
  }

  private static final class ZeroRandom extends Random {
    @Override
    public int nextInt(int bound) {
      return 0;
    }
  }

  private static void verifyChoices() {
    check(PipeworksOptions.countFor("few") == 4, "few pipes");
    check(PipeworksOptions.countFor("handful") == 8, "handful pipes");
    check(PipeworksOptions.countFor("many") == 12, "many pipes");
    check(PipeworksOptions.countFor("ton") == 20, "ton pipes");
    check(PipeworksOptions.countFor("schools") == 28, "schools pipes");
    check(PipeworksOptions.countFor("invalid") == 8, "density fallback");
    check(PipeworksOptions.delayFor("slow") == 1050, "slow growth");
    check(PipeworksOptions.delayFor("natural") == 650, "natural growth");
    check(PipeworksOptions.delayFor("quick") == 340, "quick growth");
    check(PipeworksOptions.delayFor("invalid") == 650, "speed fallback");
    check(PipeworksOptions.paletteFor("copper") == 0, "copper palette");
    check(PipeworksOptions.paletteFor("cool") == 1, "cool palette");
    check(PipeworksOptions.paletteFor("neon") == 2, "neon palette");
    check(PipeworksOptions.paletteFor("monochrome") == 3, "monochrome palette");
    check(PipeworksOptions.paletteFor("invalid") == 0, "palette fallback");
    check(PipeworksOptions.backgroundFor("midnight") == 0, "midnight background");
    check(PipeworksOptions.backgroundFor("ocean") == 1, "ocean background");
    check(PipeworksOptions.backgroundFor("graphite") == 2, "graphite background");
    check(PipeworksOptions.backgroundFor("invalid") == 0, "background fallback");
    check(PipeworksOptions.lightingFor("subtle") == 0, "subtle lighting");
    check(PipeworksOptions.lightingFor("cinematic") == 1, "cinematic lighting");
    check(PipeworksOptions.lightingFor("bright") == 2, "bright lighting");
    check(PipeworksOptions.lightingFor("invalid") == 0, "lighting fallback");

    PipeworksOptions selected =
        PipeworksOptions.resolve(
            "few", "quick", "neon", "ocean", "bright", "off", false, new Random(1));
    check(
        selected.pipeCount == 4
            && selected.stepDelay == 340
            && selected.palette == 2
            && selected.background == 1
            && selected.lighting == 2
            && !selected.glow,
        "explicit options resolve to scene values");

    PipeworksOptions random =
        PipeworksOptions.resolve(
            "random", "random", "random", "random", "random", "random", false, new Random(7));
    check(
        random.pipeCount == 4
            || random.pipeCount == 8
            || random.pipeCount == 12
            || random.pipeCount == 20
            || random.pipeCount == 28,
        "random density range");
    check(
        random.stepDelay == 340 || random.stepDelay == 650 || random.stepDelay == 1050,
        "random speed choices");
    check(random.palette >= 0 && random.palette <= 8, "random palette range");
    check(random.background >= 0 && random.background <= 2, "random background range");
    check(random.lighting >= 0 && random.lighting <= 2, "random lighting range");

    PipeworksOptions all =
        PipeworksOptions.resolve(
            "few", "slow", "copper", "midnight", "subtle", "on", true, new Random(5));
    check(
        all.pipeCount == 4
            || all.pipeCount == 8
            || all.pipeCount == 12
            || all.pipeCount == 20
            || all.pipeCount == 28,
        "randomize all density");
    check(
        all.stepDelay == 340 || all.stepDelay == 650 || all.stepDelay == 1050,
        "randomize all speed");
    check(all.palette >= 0 && all.palette <= 8, "randomize all palette");
    check(all.background >= 0 && all.background <= 2, "randomize all background");
    check(all.lighting >= 0 && all.lighting <= 2, "randomize all lighting");

    PipeworksOptions fallback =
        PipeworksOptions.resolve(
            "invalid", "invalid", "invalid", "invalid", "invalid", "invalid", false, new Random(9));
    check(
        fallback.pipeCount == 8
            && fallback.stepDelay == 650
            && fallback.palette == 0
            && fallback.background == 0
            && fallback.lighting == 0
            && fallback.glow,
        "unsupported preferences use safe defaults");
  }

  private static void verifyProviderContract() {
    String[][] choices = {
      {"density", "few", "handful", "many", "ton", "schools", "random"},
      {"speed", "slow", "natural", "quick", "random"},
      {
        "palette",
        "copper",
        "cool",
        "neon",
        "monochrome",
        "red",
        "green",
        "blue",
        "purple",
        "gold",
        "random"
      },
      {"background", "midnight", "ocean", "graphite", "random"},
      {"lighting", "subtle", "cinematic", "bright", "random"},
      {"glow", "on", "off", "random"},
      {"shape", "round", "square", "hexagonal", "random"},
      {"size", "thin", "medium", "thick", "random"},
      {"tail", "keep", "follow", "random"},
      {"tail_delay", "5", "15", "30", "60", "random"},
      {"randomize_all", "true", "false"}
    };
    for (String[] setting : choices) {
      for (int i = 1; i < setting.length; i++) {
        check(
            SettingsValues.isSupported(setting[0], setting[i]),
            setting[0] + " accepts " + setting[i]);
      }
    }
    check(!SettingsValues.isSupported("density", "millions"), "unsupported pipe count rejected");
    check(!SettingsValues.isSupported("speed", "instant"), "unsupported speed rejected");
    check(!SettingsValues.isSupported("palette", "rainbow-foo"), "unsupported palette rejected");
    check(!SettingsValues.isSupported("background", "forest"), "unsupported background rejected");
    check(!SettingsValues.isSupported("lighting", "laser"), "unsupported lighting rejected");
    check(!SettingsValues.isSupported("glow", "maybe"), "unsupported glow choice rejected");
    check(!SettingsValues.isSupported("randomize_all", "yes"), "invalid randomize value rejected");
    check(!SettingsValues.isSupported("shape", "triangle"), "unsupported shape rejected");
    check(!SettingsValues.isSupported("size", "giant"), "unsupported thickness rejected");
    check(!SettingsValues.isSupported("tail", "maybe"), "unsupported tail mode rejected");
    check(!SettingsValues.isSupported("tail_delay", "forever"), "unsupported tail delay rejected");
    check(!SettingsValues.isSupported("unrecognized", "value"), "unknown preference rejected");
    check(!SettingsValues.isSupported(null, "copper"), "null setting key rejected");
    check(!SettingsValues.isSupported("density", null), "null setting value rejected");
  }

  private static void verifyEngineMotion() {
    PipeEngine engine = new PipeEngine(7, 6, 5, 10, new Random(2408));
    int[] first = engine.snapshot();
    check(first.length == 10 * 4, "one starting point per pipe");
    for (int tick = 0; tick < 5000; tick++) {
      engine.advance();
    }
    int[] scene = engine.snapshot();
    check(scene.length >= first.length, "growing scene keeps active pipe heads");
    check(scene.length <= 10 * 84 * 4, "pipe trails remain bounded");
    int width = 7;
    int height = 6;
    int depth = 5;
    for (int i = 0; i < scene.length; i += 4) {
      check(scene[i] >= 0 && scene[i] < width, "pipe x remains in the grid");
      check(scene[i + 1] >= 0 && scene[i + 1] < height, "pipe y remains in the grid");
      check(scene[i + 2] >= 0 && scene[i + 2] < depth, "pipe depth remains in the grid");
      check(scene[i + 3] >= -8 && scene[i + 3] < 8, "pipe color index remains valid");
    }
    PipeEngine dense = new PipeEngine(2, 2, 2, 8, new Random(11));
    for (int tick = 0; tick < 200; tick++) dense.advance();
    check(
        dense.snapshot().length == 8 * 4,
        "crowded cells restart cleanly without losing pipe heads");
  }

  private static void verifyInvalidEngineInputs() {
    expectInvalid(1, 4, 3, 1, new Random(1));
    expectInvalid(4, 1, 3, 1, new Random(1));
    expectInvalid(4, 3, 1, 1, new Random(1));
    expectInvalid(4, 3, 2, 0, new Random(1));
    expectInvalid(2, 2, 2, 9, new Random(1));
    expectInvalid(3, 3, 3, 1, null);
  }

  private static void verifyDeterministicTurnsAndOccupiedCells() {
    PipeEngine turning = new PipeEngine(3, 3, 3, 1, new ScriptedRandom(13, 0, 1));
    turning.advance();
    check(turning.snapshot().length == 2 * 4, "a pipe turns after avoiding its reverse direction");

    PipeEngine occupied = new PipeEngine(2, 2, 2, 2, new ScriptedRandom(0, 0, 0, 0, 1));
    check(occupied.snapshot().length == 2 * 4, "pipe starts skip cells occupied by another pipe");
  }

  private static final class ScriptedRandom extends Random {
    private final int[] values;
    private int index;

    private ScriptedRandom(int... values) {
      this.values = values;
    }

    @Override
    public int nextInt(int bound) {
      return values[index++] % bound;
    }
  }

  private static void expectInvalid(int width, int height, int depth, int count, Random random) {
    try {
      new PipeEngine(width, height, depth, count, random);
      throw new AssertionError("invalid pipe grid was accepted");
    } catch (IllegalArgumentException expected) {
      check(expected.getMessage() != null, "invalid input explains the problem");
    }
  }

  private static void check(boolean result, String message) {
    if (!result) throw new AssertionError(message);
  }
}
