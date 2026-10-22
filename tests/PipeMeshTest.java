package com.jeremykenedy.pipeworksdream;

import java.util.Random;

public final class PipeMeshTest {
  private static final int[] PALETTE = {0xffcc4422, 0xff2288ee};

  public static void main(String[] args) {
    verifyInvalidInputs();
    verifyShapesAndSizes();
    verifyTailReveal();
    verifyClosedCylinder();
    verifyRoundElbow();
    verifyCircularBendClearance();
    verifySeparateChains();
    verifyGrowth();
    verifyEngineBounds();
    check(PipeMesh.build(new int[0], PALETTE).vertexCount == 0, "empty scene");
    check(
        PipeMesh.build(new int[] {0, 0, 0, -1}, PALETTE).vertexCount == 0, "singleton has no tube");
  }

  private static void verifyInvalidInputs() {
    expectInvalid(null, PALETTE);
    expectInvalid(new int[] {1}, PALETTE);
    expectInvalid(new int[0], null);
    expectInvalid(new int[0], new int[0]);
    expectInvalidShape(-1, 0.17f, 0);
    expectInvalidShape(3, 0.17f, 0);
    expectInvalidShape(0, Float.NaN, 0);
    expectInvalidShape(0, Float.NEGATIVE_INFINITY, 0);
    expectInvalidShape(0, Float.POSITIVE_INFINITY, 0);
    expectInvalidShape(0, 0.049f, 0);
    expectInvalidShape(0, 0.251f, 0);
    expectInvalidShape(0, 0.17f, -1);
  }

  private static void expectInvalid(int[] snapshot, int[] palette) {
    try {
      PipeMesh.build(snapshot, palette);
      throw new AssertionError("Incomplete geometry input was accepted");
    } catch (IllegalArgumentException expected) {
      check(expected.getMessage() != null, "Invalid geometry explains the problem");
    }
  }

  private static void expectInvalidShape(int shape, float radius, int tailPoints) {
    try {
      PipeMesh.build(new int[0], PALETTE, shape, radius, tailPoints);
      throw new AssertionError("Invalid pipe dimensions were accepted");
    } catch (IllegalArgumentException expected) {
      check(expected.getMessage() != null, "Invalid shape explains the problem");
    }
  }

  private static void verifyShapesAndSizes() {
    for (int shape = 0; shape < 3; shape++) {
      int sides = shape == 0 ? 16 : shape == 1 ? 4 : 6;
      for (float radius : new float[] {0.05f, 0.10f, 0.17f, 0.24f, 0.25f}) {
        for (int axis = 0; axis < 3; axis++) {
          for (int direction : new int[] {-1, 1}) {
            int[] snapshot = {8, 5, 3, -1, 8, 5, 3, 0};
            snapshot[4 + axis] += direction;
            PipeMesh mesh = PipeMesh.build(snapshot, PALETTE, shape, radius);
            check(mesh.vertexCount == sides * 12, "closed cross section has the expected faces");
            validate(mesh);
            verifyWinding(mesh);
            float[] maximum = new float[2];
            for (int i = 0; i < mesh.vertices.length; i += PipeMesh.FLOATS_PER_VERTEX) {
              int transverse = 0;
              for (int dimension = 0; dimension < 3; dimension++) {
                if (dimension != axis) {
                  float origin = dimension == 1 ? 0.5f : 0;
                  maximum[transverse] =
                      Math.max(
                          maximum[transverse], Math.abs(mesh.vertices[i + dimension] - origin));
                  transverse++;
                }
              }
              check(mesh.vertices[i + 10] == 2, "default mesh never trims a tail");
            }
            java.util.Arrays.sort(maximum);
            float expectedSmall =
                radius
                    * (shape == 1
                        ? (float) Math.sqrt(0.5)
                        : shape == 2 ? (float) Math.sqrt(0.75) : 1);
            float expectedLarge = shape == 1 ? expectedSmall : radius;
            check(Math.abs(maximum[0] - expectedSmall) < 0.0001f, "cross-section narrow width");
            check(Math.abs(maximum[1] - expectedLarge) < 0.0001f, "cross-section wide width");
            int stride = PipeMesh.FLOATS_PER_VERTEX;
            int surface = sides * 3 * stride;
            for (int side = 0; side < sides; side++) {
              int face = surface + side * 6 * stride;
              if (shape == 0) {
                check(
                    normalDot(mesh.vertices, face, face + stride) < 0.99f,
                    "round surface normals follow the circumference");
              } else {
                for (int vertex = 1; vertex < 6; vertex++) {
                  check(
                      normalDot(mesh.vertices, face, face + vertex * stride) > 0.9999f,
                      "each polygon face has one flat normal");
                }
                int next = surface + ((side + 1) % sides) * 6 * stride;
                check(
                    normalDot(mesh.vertices, face, next) < 0.6f,
                    "neighboring polygon faces retain their lighting crease");
                if (shape == 1) {
                  float largest = 0;
                  for (int dimension = 3; dimension < 6; dimension++) {
                    largest = Math.max(largest, Math.abs(mesh.vertices[face + dimension]));
                  }
                  check(largest > 0.9999f, "square faces align with grid axes");
                }
              }
            }
          }
        }
        PipeMesh bend =
            PipeMesh.build(
                new int[] {8, 5, 3, -1, 9, 5, 3, 0, 9, 6, 3, 0, 9, 6, 4, 0},
                PALETTE,
                shape,
                radius);
        validate(bend);
        verifyWinding(bend);
      }
    }
    PipeMesh legacy = PipeMesh.build(new int[] {8, 5, 3, -1, 9, 5, 3, 0}, PALETTE);
    PipeMesh explicit = PipeMesh.build(new int[] {8, 5, 3, -1, 9, 5, 3, 0}, PALETTE, 0, 0.17f);
    check(
        java.util.Arrays.equals(legacy.vertices, explicit.vertices),
        "legacy overload keeps round defaults");
  }

  private static float normalDot(float[] vertices, int first, int second) {
    float dot = 0;
    for (int axis = 3; axis < 6; axis++) dot += vertices[first + axis] * vertices[second + axis];
    return dot;
  }

  private static void verifyTailReveal() {
    int[] straight = {8, 5, 3, -1, 9, 5, 3, 0, 10, 5, 3, 0, 11, 5, 3, 0, 12, 5, 3, 0};
    for (int shape = 0; shape < 3; shape++) {
      PipeMesh mesh = PipeMesh.build(straight, PALETTE, shape, 0.17f, 5);
      validate(mesh);
      verifyWinding(mesh);
      boolean boundary = false;
      for (int i = 0; i < mesh.vertices.length; i += PipeMesh.FLOATS_PER_VERTEX) {
        float x = mesh.vertices[i];
        float tail = mesh.vertices[i + 10];
        if (x == 0) check(tail == 0, "tail begins at the oldest end");
        if (x == 1) {
          boundary = true;
          check(tail == 1, "straight compression preserves first-step clipping boundary");
        }
        if (x > 1) check(tail == 2, "later straight steps stay visible");
      }
      check(boundary, "the first-step endpoint is retained");
      PipeMesh waiting = PipeMesh.build(straight, PALETTE, shape, 0.17f, 6);
      for (int i = 10; i < waiting.vertices.length; i += PipeMesh.FLOATS_PER_VERTEX) {
        check(waiting.vertices[i] == 2, "tail waits until configured point limit is reached");
      }
      PipeMesh bend =
          PipeMesh.build(
              new int[] {8, 5, 3, -1, 9, 5, 3, 0, 9, 6, 3, 0, 9, 7, 3, 0},
              PALETTE,
              shape,
              0.24f,
              4);
      validate(bend);
      verifyWinding(bend);
      boolean intermediate = false;
      for (int i = 0; i < bend.vertices.length; i += PipeMesh.FLOATS_PER_VERTEX) {
        float tail = bend.vertices[i + 10];
        if (tail > 0 && tail < 1) intermediate = true;
        if (bend.vertices[i + 1] > 1.1f) check(tail == 2, "later bend sections stay visible");
      }
      check(intermediate, "tail moves smoothly through the oldest elbow");
    }
    PipeMesh firstStep = PipeMesh.build(new int[] {8, 5, 3, -1, 9, 5, 3, 0}, PALETTE, 0, 0.17f, 1);
    for (int i = 0; i < firstStep.vertices.length; i += PipeMesh.FLOATS_PER_VERTEX) {
      check(
          firstStep.vertices[i + 10] == firstStep.vertices[i],
          "single-step tail spans only that step");
    }
  }

  private static void verifyClosedCylinder() {
    PipeMesh mesh = PipeMesh.build(new int[] {8, 5, 3, -1, 9, 5, 3, 0}, PALETTE);
    check(mesh.vertexCount == 192, "16 cylinder sides and two closed caps");
    validate(mesh);
    for (int i = 0; i < mesh.vertices.length; i += PipeMesh.FLOATS_PER_VERTEX) {
      float[] v = mesh.vertices;
      check(v[i] >= 0 && v[i] <= 1, "cylinder ends stay on their planes");
      check(Math.abs(v[i + 6] - 0.8f) < 0.0001f, "palette red channel");
      if (Math.abs(v[i + 3]) < 0.001f) {
        double radius = Math.hypot(v[i + 1] - 0.5, v[i + 2]);
        check(Math.abs(radius - PipeMesh.RADIUS) < 0.0001, "uniform circular cross section");
      }
    }
    verifyWinding(mesh);
  }

  private static void verifyRoundElbow() {
    PipeMesh mesh = PipeMesh.build(new int[] {8, 5, 3, -1, 9, 5, 3, 0, 9, 6, 3, 0}, PALETTE);
    validate(mesh);
    verifyWinding(mesh);
    check(mesh.vertexCount == 864, "rounded elbow includes intermediate surface rings");
    boolean diagonalNormal = false;
    for (int i = 0; i < mesh.vertices.length; i += PipeMesh.FLOATS_PER_VERTEX) {
      float x = Math.abs(mesh.vertices[i + 3]);
      float y = Math.abs(mesh.vertices[i + 4]);
      if (x > 0.2 && x < 0.9 && y > 0.2 && y < 0.9) diagonalNormal = true;
    }
    check(diagonalNormal, "bend normals follow a smooth curved surface");
  }

  private static void verifyCircularBendClearance() {
    for (float radius : new float[] {0.17f, 0.24f, 0.25f}) {
      PipeMesh mesh =
          PipeMesh.build(new int[] {8, 5, 3, -1, 9, 5, 3, 0, 9, 6, 3, 0}, PALETTE, 0, radius);
      float bendRadius = Math.max(0.3f, radius * 1.5f);
      float centerX = 1 - bendRadius;
      float centerY = 0.5f + bendRadius;
      int stride = PipeMesh.FLOATS_PER_VERTEX;
      int cap = 16 * 3 * stride;
      int curvedVertices = 0;
      double smallestInnerRadius = Double.POSITIVE_INFINITY;
      for (int i = cap; i < mesh.vertices.length - cap; i += stride) {
        float[] vertex = mesh.vertices;
        float pathX = vertex[i] - vertex[i + 3] * radius;
        float pathY = vertex[i + 1] - vertex[i + 4] * radius;
        if (pathX <= centerX + 0.0001f || pathY <= 0.5001f || pathY >= centerY - 0.0001f) continue;
        double pathRadius = Math.hypot(pathX - centerX, pathY - centerY);
        check(Math.abs(pathRadius - bendRadius) < 0.0001, "elbow centerline has constant radius");
        double surfaceRadius = Math.hypot(vertex[i] - centerX, vertex[i + 1] - centerY);
        check(surfaceRadius >= bendRadius - radius - 0.0001, "thick elbow keeps inner clearance");
        smallestInnerRadius = Math.min(smallestInnerRadius, surfaceRadius);
        curvedVertices++;
      }
      check(curvedVertices > 0, "curved surface samples are checked");
      check(
          Math.abs(smallestInnerRadius - (bendRadius - radius)) < 0.0001,
          "inner elbow follows the same circle without pinching");
    }
  }

  private static void verifySeparateChains() {
    PipeMesh mesh =
        PipeMesh.build(new int[] {0, 0, 0, -1, 1, 0, 0, 0, 15, 9, 6, -2, 16, 9, 6, 1}, PALETTE);
    check(mesh.vertexCount == 384, "chain markers produce two independent capped cylinders");
    validate(mesh);
    for (int i = 0; i < mesh.vertices.length; i += PipeMesh.FLOATS_PER_VERTEX) {
      float x = mesh.vertices[i];
      check(x <= -7 || x >= 7, "no connector crosses the gap between chains");
      check((x < 0) == (mesh.vertices[i + 6] > 0.5), "each chain keeps its own palette color");
    }
  }

  private static void verifyGrowth() {
    PipeMesh straight =
        PipeMesh.build(new int[] {8, 5, 3, -1, 9, 5, 3, 0, 10, 5, 3, 0, 11, 5, 3, 0}, PALETTE);
    for (int i = 0; i < straight.vertices.length; i += PipeMesh.FLOATS_PER_VERTEX) {
      if (straight.vertices[i] <= 2)
        check(straight.vertices[i + 9] == 0, "old straight sections remain visible");
      if (straight.vertices[i] == 3)
        check(straight.vertices[i + 9] == 1, "final ring waits for full growth");
    }
    PipeMesh elbow = PipeMesh.build(new int[] {8, 5, 3, -1, 9, 5, 3, 0, 9, 6, 3, 0}, PALETTE);
    boolean intermediate = false;
    for (int i = 9; i < elbow.vertices.length; i += PipeMesh.FLOATS_PER_VERTEX) {
      if (elbow.vertices[i] > 0 && elbow.vertices[i] < 1) intermediate = true;
    }
    check(intermediate, "new elbow has intermediate reveal coordinates");
  }

  private static void verifyEngineBounds() {
    PipeEngine engine = new PipeEngine(17, 10, 7, 28, new Random(294));
    for (int tick = 0; tick < 200; tick++) {
      engine.advance();
      PipeMesh mesh = PipeMesh.build(engine.snapshot(), PALETTE);
      validate(mesh);
      check(
          mesh.vertexCount <= 28 * (83 * 7 * 96 + 96), "maximum engine scene has bounded geometry");
      for (int i = 0; i < mesh.vertices.length; i += PipeMesh.FLOATS_PER_VERTEX) {
        check(Math.abs(mesh.vertices[i]) <= 8 + PipeMesh.RADIUS + 0.001, "centered x bounds");
        check(Math.abs(mesh.vertices[i + 1]) <= 4.5 + PipeMesh.RADIUS + 0.001, "centered y bounds");
        check(Math.abs(mesh.vertices[i + 2]) <= 3 + PipeMesh.RADIUS + 0.001, "centered z bounds");
      }
    }
  }

  private static void validate(PipeMesh mesh) {
    check(
        mesh.vertices.length == mesh.vertexCount * PipeMesh.FLOATS_PER_VERTEX,
        "interleaved vertex count");
    check(mesh.vertexCount % 3 == 0, "complete triangles");
    for (float value : mesh.vertices)
      check(!Float.isNaN(value) && !Float.isInfinite(value), "finite vertex attributes");
    for (int i = 0; i < mesh.vertices.length; i += PipeMesh.FLOATS_PER_VERTEX) {
      double squared = 0;
      for (int axis = 3; axis < 6; axis++)
        squared += mesh.vertices[i + axis] * mesh.vertices[i + axis];
      check(Math.abs(squared - 1) < 0.0001, "unit lighting normal");
      check(mesh.vertices[i + 9] >= 0 && mesh.vertices[i + 9] <= 1, "bounded growth attribute");
      check(mesh.vertices[i + 10] >= 0 && mesh.vertices[i + 10] <= 2, "bounded tail attribute");
    }
  }

  private static void verifyWinding(PipeMesh mesh) {
    int stride = PipeMesh.FLOATS_PER_VERTEX;
    for (int i = 0; i < mesh.vertices.length; i += stride * 3) {
      float[] v = mesh.vertices;
      float ax = v[i + stride] - v[i];
      float ay = v[i + stride + 1] - v[i + 1];
      float az = v[i + stride + 2] - v[i + 2];
      float bx = v[i + stride * 2] - v[i];
      float by = v[i + stride * 2 + 1] - v[i + 1];
      float bz = v[i + stride * 2 + 2] - v[i + 2];
      float dot =
          (ay * bz - az * by) * v[i + 3]
              + (az * bx - ax * bz) * v[i + 4]
              + (ax * by - ay * bx) * v[i + 5];
      check(dot > 0, "outward triangle winding agrees with lighting normal");
    }
  }

  private static void check(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }
}
