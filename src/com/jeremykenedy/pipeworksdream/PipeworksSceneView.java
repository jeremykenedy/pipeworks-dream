package com.jeremykenedy.pipeworksdream;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import java.util.Random;

final class PipeworksSceneView extends View {
  private static final int GRID_WIDTH = 17;
  private static final int GRID_HEIGHT = 10;
  private static final int GRID_DEPTH = 7;
  private static final long FRAME_DELAY_MS = 50L;
  private static final int[] COPPER = {
    0xffffa45b, 0xffffc77a, 0xffef754b, 0xffffdc9b, 0xffdc8e55, 0xffffb85f, 0xffe39f62, 0xffffd795
  };
  private static final int[] COOL = {
    0xff75d7ff, 0xff8ce8dc, 0xff779bff, 0xffb2e4ff, 0xff67bddb, 0xffa4d7ed, 0xff8cc7ff, 0xff79dbc9
  };
  private static final int[] NEON = {
    0xffea6cff, 0xff4ee7e0, 0xff74f18d, 0xffffd14f, 0xffff6d85, 0xff77a6ff, 0xffb9ff63, 0xffff8e54
  };
  private static final int[] MONO = {
    0xffdce7f0, 0xffb4c4d2, 0xfff3f7fa, 0xff9babb9, 0xffe3e9ee, 0xffc4d0da, 0xfff1f4f6, 0xffaab9c5
  };

  private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private PipeworksOptions options;
  private PipeEngine engine;
  private int[] pipePoints = new int[0];
  private LinearGradient background;
  private RadialGradient ambient;
  private long startedAt;
  private long lastStepAt;
  private boolean running;

  PipeworksSceneView(Context context) {
    super(context);
    setLayerType(View.LAYER_TYPE_HARDWARE, null);
    loadOptions();
  }

  void start() {
    if (!running) {
      running = true;
      startedAt = SystemClock.uptimeMillis();
      lastStepAt = startedAt;
      engine =
          new PipeEngine(
              GRID_WIDTH,
              GRID_HEIGHT,
              GRID_DEPTH,
              options.pipeCount,
              new Random(startedAt ^ 0x51f15eL));
      pipePoints = engine.snapshot();
      postInvalidateOnAnimation();
    }
  }

  void stop() {
    running = false;
    removeCallbacks(invalidator);
    engine = null;
    pipePoints = new int[0];
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (getWidth() <= 0 || getHeight() <= 0) return;
    long now = SystemClock.uptimeMillis();
    float time = (now - startedAt) / 1000f;
    drawBackdrop(canvas, time);
    drawGrid(canvas);
    if (running && engine != null) {
      if (now - lastStepAt >= options.stepDelay) {
        engine.advance();
        pipePoints = engine.snapshot();
        lastStepAt = now;
      }
      drawPipes(canvas, pipePoints);
      postDelayed(invalidator, FRAME_DELAY_MS);
    }
  }

  @Override
  protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
    super.onSizeChanged(width, height, oldWidth, oldHeight);
    int top =
        options.background == 1 ? 0xff071b24 : options.background == 2 ? 0xff11171e : 0xff090d13;
    int bottom =
        options.background == 1 ? 0xff0d2933 : options.background == 2 ? 0xff202832 : 0xff151820;
    background =
        new LinearGradient(0, 0, 0, Math.max(height, 1), top, bottom, Shader.TileMode.CLAMP);
    int lightColor =
        options.background == 1 ? 0xff1e8095 : options.background == 2 ? 0xff586577 : 0xff75442b;
    ambient =
        new RadialGradient(
            width * 0.54f,
            height * 0.42f,
            Math.max(width, height) * 0.68f,
            new int[] {withAlpha(lightColor, 38), withAlpha(lightColor, 0)},
            null,
            Shader.TileMode.CLAMP);
  }

  private final Runnable invalidator =
      new Runnable() {
        @Override
        public void run() {
          if (running) invalidate();
        }
      };

  private void loadOptions() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    options =
        PipeworksOptions.resolve(
            preferences.getString("density", "handful"),
            preferences.getString("speed", "natural"),
            preferences.getString("palette", "copper"),
            preferences.getString("background", "midnight"),
            preferences.getString("lighting", "cinematic"),
            preferences.getString("glow", "on"),
            preferences.getBoolean("randomize_all", false),
            new Random(System.currentTimeMillis()));
  }

  private void drawBackdrop(Canvas canvas, float time) {
    paint.setShader(background);
    canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    paint.setShader(ambient);
    float sweepX = (float) Math.sin(time * 0.12f) * getWidth() * 0.12f;
    canvas.save();
    canvas.translate(sweepX, 0);
    canvas.drawRect(-sweepX, 0, getWidth() - sweepX, getHeight(), paint);
    canvas.restore();
    paint.setShader(null);
  }

  private void drawGrid(Canvas canvas) {
    paint.setShader(null);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(1f);
    paint.setColor(options.background == 1 ? 0x223ca7b7 : 0x223b5065);
    float left = getWidth() * 0.12f;
    float right = getWidth() * 0.88f;
    float top = getHeight() * 0.16f;
    float bottom = getHeight() * 0.82f;
    for (int i = 0; i <= 12; i++) {
      float x = left + (right - left) * i / 12f;
      canvas.drawLine(x, top, x, bottom, paint);
    }
    for (int i = 0; i <= 7; i++) {
      float y = top + (bottom - top) * i / 7f;
      canvas.drawLine(left, y, right, y, paint);
    }
    paint.setStyle(Paint.Style.FILL);
  }

  private void drawPipes(Canvas canvas, int[] points) {
    int[] colors = paletteColors();
    float scaleX = getWidth() / 18f;
    float scaleY = getHeight() / 12f;
    float tubeWidth = Math.min(getWidth(), getHeight()) * 0.017f;
    float highlightWidth = tubeWidth * 0.27f;
    for (int i = 4; i < points.length; i += 4) {
      if (points[i + 3] < 0) continue;
      float x1 = projectX(points[i - 4], points[i - 2], scaleX);
      float y1 = projectY(points[i - 3], points[i - 2], scaleY);
      float x2 = projectX(points[i], points[i + 2], scaleX);
      float y2 = projectY(points[i + 1], points[i + 2], scaleY);
      int previousColor = points[i - 1] < 0 ? -points[i - 1] - 1 : points[i - 1];
      int color = colors[previousColor % colors.length];
      float depthScale = 0.78f + (points[i - 2] + points[i + 2]) / (2f * (GRID_DEPTH - 1)) * 0.32f;
      float segmentWidth = tubeWidth * depthScale;
      boolean hasNext = i + 7 < points.length && points[i + 7] >= 0;
      boolean elbow =
          hasNext
              && (points[i] - points[i - 4] != points[i + 4] - points[i]
                  || points[i + 1] - points[i - 3] != points[i + 5] - points[i + 1]
                  || points[i + 2] - points[i - 2] != points[i + 6] - points[i + 2]);
      if (options.glow) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(segmentWidth * (options.lighting == 2 ? 2.1f : 1.7f));
        paint.setColor(withAlpha(color, options.lighting == 0 ? 12 : 22));
        canvas.drawLine(x1, y1, x2, y2, paint);
      }
      paint.setStyle(Paint.Style.STROKE);
      paint.setStrokeCap(Paint.Cap.ROUND);
      paint.setStrokeWidth(segmentWidth * 1.34f);
      paint.setColor(withAlpha(0xff05080c, 230));
      canvas.drawLine(x1, y1 + segmentWidth * 0.08f, x2, y2 + segmentWidth * 0.08f, paint);
      paint.setStrokeWidth(segmentWidth);
      paint.setColor(withAlpha(color, options.lighting == 0 ? 205 : 245));
      canvas.drawLine(x1, y1, x2, y2, paint);
      paint.setStrokeWidth(highlightWidth * depthScale);
      paint.setColor(withAlpha(0xffffffff, options.lighting == 0 ? 78 : 142));
      canvas.drawLine(
          x1 - segmentWidth * 0.14f,
          y1 - segmentWidth * 0.24f,
          x2 - segmentWidth * 0.14f,
          y2 - segmentWidth * 0.24f,
          paint);
      if (elbow) {
        paint.setStyle(Paint.Style.FILL);
        float jointRadius = segmentWidth * 0.45f;
        paint.setColor(withAlpha(color, 236));
        canvas.drawCircle(x2, y2, jointRadius, paint);
        paint.setColor(withAlpha(0xffffffff, options.lighting == 0 ? 66 : 128));
        canvas.drawCircle(
            x2 - jointRadius * 0.28f,
            y2 - jointRadius * 0.3f,
            Math.max(1f, jointRadius * 0.25f),
            paint);
      }
    }
    paint.setStyle(Paint.Style.FILL);
  }

  private float projectX(int x, int z, float scale) {
    float center = (GRID_WIDTH - 1) / 2f;
    float depthCenter = (GRID_DEPTH - 1) / 2f;
    return getWidth() * 0.5f + (x - center) * scale * 0.9f + (z - depthCenter) * scale * 0.38f;
  }

  private float projectY(int y, int z, float scale) {
    float center = (GRID_HEIGHT - 1) / 2f;
    float depthCenter = (GRID_DEPTH - 1) / 2f;
    return getHeight() * 0.5f + (y - center) * scale * 0.86f - (z - depthCenter) * scale * 0.32f;
  }

  private int[] paletteColors() {
    if (options.palette == 1) return COOL;
    if (options.palette == 2) return NEON;
    if (options.palette == 3) return MONO;
    return COPPER;
  }

  private static int withAlpha(int color, int alpha) {
    return (color & 0x00ffffff) | (Math.max(0, Math.min(255, alpha)) << 24);
  }
}
