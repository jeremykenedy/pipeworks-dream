package com.jeremykenedy.pipeworksdream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A closed, smooth tube surface with interleaved position, normal, RGB, final-segment growth, and
 * first-segment tail attributes.
 */
public final class PipeMesh {
  public static final int FLOATS_PER_VERTEX = 11;
  public static final float RADIUS = 0.17f;
  private static final int BEND_STEPS = 6;
  private static final float BEND_RADIUS = 0.3f;

  public final float[] vertices;
  public final int vertexCount;

  private PipeMesh(float[] vertices) {
    this.vertices = vertices;
    vertexCount = vertices.length / FLOATS_PER_VERTEX;
  }

  public static PipeMesh build(int[] snapshot, int[] palette) {
    return build(snapshot, palette, 0, RADIUS);
  }

  /** Shapes are round (0), square (1), or hexagonal (2); radius reaches each section vertex. */
  public static PipeMesh build(int[] snapshot, int[] palette, int shape, float radius) {
    return build(snapshot, palette, shape, radius, 0);
  }

  /**
   * Tail attribute 10 spans 0 to 1 on the oldest step once the point limit is reached. Its sentinel
   * value 2 preserves later geometry when the shader clips against the current growth fraction.
   */
  public static PipeMesh build(
      int[] snapshot, int[] palette, int shape, float radius, int tailPoints) {
    if (snapshot == null || snapshot.length % 4 != 0 || palette == null || palette.length == 0) {
      throw new IllegalArgumentException(
          "A complete pipe snapshot and a color palette are required");
    }
    if (shape < 0 || shape > 2 || Float.isNaN(radius) || radius < 0.05f || radius > 0.25f) {
      throw new IllegalArgumentException(
          "Pipe shape must be 0 to 2 and radius must be 0.05 to 0.25");
    }
    if (tailPoints < 0) {
      throw new IllegalArgumentException("Tail point limit cannot be negative");
    }
    int sides = shape == 0 ? 16 : shape == 1 ? 4 : 6;
    Builder result = new Builder();
    List<Vec> chain = new ArrayList<>();
    int color = 0;
    for (int i = 0; i < snapshot.length; i += 4) {
      int marker = snapshot[i + 3];
      if (marker < 0) {
        appendChain(result, chain, palette[color % palette.length], sides, radius, tailPoints);
        chain.clear();
        color = (int) (-(long) marker - 1);
      }
      Vec point = new Vec(snapshot[i] - 8f, snapshot[i + 1] - 4.5f, snapshot[i + 2] - 3f);
      chain.add(point);
    }
    appendChain(result, chain, palette[color % palette.length], sides, radius, tailPoints);
    return new PipeMesh(Arrays.copyOf(result.data, result.size));
  }

  private static void appendChain(
      Builder output, List<Vec> points, int color, int sides, float radius, int tailPoints) {
    if (points.size() < 2) return;
    boolean trimming = tailPoints > 0 && points.size() >= tailPoints;
    int tailPathEnd = 1;
    List<Sample> path = new ArrayList<>();
    path.add(new Sample(points.get(0), points.get(1).minus(points.get(0)).unit()));
    int growingPathStart = Integer.MAX_VALUE;
    for (int i = 1; i < points.size() - 1; i++) {
      Vec previous = points.get(i - 1);
      Vec corner = points.get(i);
      Vec next = points.get(i + 1);
      Vec incoming = corner.minus(previous).unit();
      Vec outgoing = next.minus(corner).unit();
      if (incoming.dot(outgoing) > 0.999f) {
        if (i == points.size() - 2 || (trimming && i == 1)) {
          if (i == points.size() - 2) growingPathStart = path.size();
          path.add(new Sample(corner, incoming));
        }
        if (i == 1) tailPathEnd = path.size();
        continue;
      }
      float cut =
          Math.min(
              Math.max(BEND_RADIUS, radius * 1.5f),
              Math.min(corner.minus(previous).length(), next.minus(corner).length()) * 0.4f);
      Vec center = corner.minus(incoming.times(cut)).plus(outgoing.times(cut));
      if (i == points.size() - 2) growingPathStart = path.size();
      for (int step = 0; step <= BEND_STEPS; step++) {
        double angle = Math.PI * 0.5 * step / BEND_STEPS;
        float sine = (float) Math.sin(angle);
        float cosine = (float) Math.cos(angle);
        Vec position = center.plus(incoming.times(cut * sine)).minus(outgoing.times(cut * cosine));
        Vec tangent = incoming.times(cosine).plus(outgoing.times(sine));
        path.add(new Sample(position, tangent));
      }
      if (i == 1) tailPathEnd = path.size();
    }
    int last = points.size() - 1;
    if (growingPathStart == Integer.MAX_VALUE) growingPathStart = path.size();
    path.add(new Sample(points.get(last), points.get(last).minus(points.get(last - 1)).unit()));

    if (points.size() == 2) tailPathEnd = path.size();
    Vec firstOrigin = points.get(0);
    Vec firstStep = points.get(1).minus(firstOrigin);
    float firstLengthSquared = firstStep.dot(firstStep);
    Vec growthOrigin = points.get(last - 1);
    Vec finalStep = points.get(last).minus(growthOrigin);
    float finalLengthSquared = finalStep.dot(finalStep);
    Vec tangent = path.get(0).tangent;
    Vec reference = Math.abs(tangent.y) < 0.8f ? new Vec(0, 1, 0) : new Vec(1, 0, 0);
    Vec normal = reference.minus(tangent.times(reference.dot(tangent))).unit();
    Vec[] previousRing = null;
    Vec[] previousNormals = null;
    Vec previousTangent = tangent;
    float previousGrowth = 0;
    float previousTail = 2;
    for (int i = 0; i < path.size(); i++) {
      Sample sample = path.get(i);
      float tail = 2;
      if (trimming && i < tailPathEnd) {
        tail =
            Math.max(
                0,
                Math.min(
                    1, sample.position.minus(firstOrigin).dot(firstStep) / firstLengthSquared));
      }
      float growth =
          Math.max(
              0,
              Math.min(1, sample.position.minus(growthOrigin).dot(finalStep) / finalLengthSquared));
      // Only the final step is revealed by the shader; old segments may pass this plane.
      if (i < growingPathStart) growth = 0;
      normal = transport(normal, previousTangent, sample.tangent);
      Vec binormal = sample.tangent.cross(normal).unit();
      Vec[] ring = new Vec[sides];
      Vec[] normals = new Vec[sides];
      for (int side = 0; side < sides; side++) {
        double angle = 2 * Math.PI * side / sides + (sides == 4 ? Math.PI / 4 : 0);
        normals[side] =
            normal.times((float) Math.cos(angle)).plus(binormal.times((float) Math.sin(angle)));
        ring[side] = sample.position.plus(normals[side].times(radius));
      }
      if (previousRing == null) {
        cap(output, sample.position, ring, sample.tangent.times(-1), color, true, 0, tail);
      } else {
        for (int side = 0; side < sides; side++) {
          int next = (side + 1) % sides;
          Vec previousSide = previousNormals[side];
          Vec previousNext = previousNormals[next];
          Vec currentSide = normals[side];
          Vec currentNext = normals[next];
          if (sides != 16) {
            previousSide = previousNext = previousSide.plus(previousNext).unit();
            currentSide = currentNext = currentSide.plus(currentNext).unit();
          }
          output.vertex(previousRing[side], previousSide, color, previousGrowth, previousTail);
          output.vertex(previousRing[next], previousNext, color, previousGrowth, previousTail);
          output.vertex(ring[next], currentNext, color, growth, tail);
          output.vertex(previousRing[side], previousSide, color, previousGrowth, previousTail);
          output.vertex(ring[next], currentNext, color, growth, tail);
          output.vertex(ring[side], currentSide, color, growth, tail);
        }
      }
      if (i == path.size() - 1)
        cap(output, sample.position, ring, sample.tangent, color, false, 1, tail);
      previousRing = ring;
      previousNormals = normals;
      previousTangent = sample.tangent;
      previousGrowth = growth;
      previousTail = tail;
    }
  }

  private static Vec transport(Vec normal, Vec from, Vec to) {
    Vec axis = from.cross(to);
    float sine = axis.length();
    float cosine = Math.max(-1, Math.min(1, from.dot(to)));
    if (sine > 0.00001f) {
      axis = axis.times(1f / sine);
      normal =
          normal
              .times(cosine)
              .plus(axis.cross(normal).times(sine))
              .plus(axis.times(axis.dot(normal) * (1f - cosine)));
    }
    return normal.minus(to.times(normal.dot(to))).unit();
  }

  private static void cap(
      Builder output,
      Vec center,
      Vec[] ring,
      Vec normal,
      int color,
      boolean start,
      float growth,
      float tail) {
    for (int side = 0; side < ring.length; side++) {
      int next = (side + 1) % ring.length;
      output.vertex(center, normal, color, growth, tail);
      output.vertex(ring[start ? next : side], normal, color, growth, tail);
      output.vertex(ring[start ? side : next], normal, color, growth, tail);
    }
  }

  private static final class Sample {
    final Vec position;
    final Vec tangent;

    Sample(Vec position, Vec tangent) {
      this.position = position;
      this.tangent = tangent;
    }
  }

  private static final class Vec {
    final float x;
    final float y;
    final float z;

    Vec(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    Vec plus(Vec other) {
      return new Vec(x + other.x, y + other.y, z + other.z);
    }

    Vec minus(Vec other) {
      return new Vec(x - other.x, y - other.y, z - other.z);
    }

    Vec times(float scale) {
      return new Vec(x * scale, y * scale, z * scale);
    }

    float dot(Vec other) {
      return x * other.x + y * other.y + z * other.z;
    }

    Vec cross(Vec other) {
      return new Vec(
          y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    float length() {
      return (float) Math.sqrt(dot(this));
    }

    Vec unit() {
      return times(1f / length());
    }
  }

  private static final class Builder {
    float[] data = new float[4096];
    int size;

    void vertex(Vec position, Vec normal, int color, float growth, float tail) {
      if (size + FLOATS_PER_VERTEX > data.length) data = Arrays.copyOf(data, data.length * 2);
      data[size++] = position.x;
      data[size++] = position.y;
      data[size++] = position.z;
      data[size++] = normal.x;
      data[size++] = normal.y;
      data[size++] = normal.z;
      data[size++] = ((color >> 16) & 255) / 255f;
      data[size++] = ((color >> 8) & 255) / 255f;
      data[size++] = (color & 255) / 255f;
      data[size++] = growth;
      data[size++] = tail;
    }
  }
}
