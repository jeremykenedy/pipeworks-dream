package com.jeremykenedy.pipeworksdream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class PipeEngine {
  private static final int MAX_SEGMENTS = 84;
  private static final int DIRECTIONS = 6;

  private final int width;
  private final int height;
  private final int depth;
  private final Random random;
  private final List<Chain> chains = new ArrayList<>();
  private final Set<Long> occupied = new HashSet<>();

  public PipeEngine(int width, int height, int depth, int pipeCount, Random random) {
    if (width < 2 || height < 2 || depth < 2 || pipeCount < 1 || random == null) {
      throw new IllegalArgumentException(
          "Grid dimensions, pipe count, and random source are required");
    }
    this.width = width;
    this.height = height;
    this.depth = depth;
    this.random = random;
    if ((long) width * height * depth < pipeCount) {
      throw new IllegalArgumentException("Grid must have at least one cell per pipe");
    }
    for (int i = 0; i < pipeCount; i++) {
      Chain chain = new Chain(colorIndex(i));
      restart(chain);
      chains.add(chain);
    }
  }

  public void advance() {
    for (Chain chain : chains) {
      if (chain.points.size() >= MAX_SEGMENTS || !grow(chain)) {
        restart(chain);
      }
    }
  }

  public int[] snapshot() {
    int size = 0;
    for (Chain chain : chains) {
      size += chain.points.size();
    }
    int[] result = new int[size * 4];
    int offset = 0;
    for (Chain chain : chains) {
      Point previous = null;
      for (Point point : chain.points) {
        result[offset++] = point.x;
        result[offset++] = point.y;
        result[offset++] = point.z;
        result[offset++] = previous == null ? -chain.color - 1 : chain.color;
        previous = point;
      }
    }
    return result;
  }

  private boolean grow(Chain chain) {
    Point head = chain.points.get(chain.points.size() - 1);
    int start = random.nextInt(DIRECTIONS);
    for (int attempt = 0; attempt < DIRECTIONS; attempt++) {
      int direction = (start + attempt) % DIRECTIONS;
      if (direction == (chain.direction ^ 1)) {
        continue;
      }
      Point next = move(head, direction);
      if (inside(next) && !occupied.contains(key(next))) {
        chain.points.add(next);
        occupied.add(key(next));
        chain.direction = direction;
        return true;
      }
    }
    return false;
  }

  private void restart(Chain chain) {
    clear(chain);
    int cells = width * height * depth;
    int cell = random.nextInt(cells);
    Point point = new Point(cell % width, cell / width % height, cell / (width * height));
    while (occupied.contains(key(point))) {
      cell = (cell + 1) % cells;
      point = new Point(cell % width, cell / width % height, cell / (width * height));
    }
    chain.points.add(point);
    chain.direction = random.nextInt(DIRECTIONS);
    occupied.add(key(point));
  }

  private void clear(Chain chain) {
    for (Point point : chain.points) {
      occupied.remove(key(point));
    }
    chain.points.clear();
  }

  private boolean inside(Point point) {
    return point.x >= 0
        && point.x < width
        && point.y >= 0
        && point.y < height
        && point.z >= 0
        && point.z < depth;
  }

  private Point move(Point point, int direction) {
    switch (direction) {
      case 0:
        return new Point(point.x + 1, point.y, point.z);
      case 1:
        return new Point(point.x - 1, point.y, point.z);
      case 2:
        return new Point(point.x, point.y + 1, point.z);
      case 3:
        return new Point(point.x, point.y - 1, point.z);
      case 4:
        return new Point(point.x, point.y, point.z + 1);
      default:
        return new Point(point.x, point.y, point.z - 1);
    }
  }

  private long key(Point point) {
    return ((long) point.z * height + point.y) * width + point.x;
  }

  private static int colorIndex(int index) {
    return index % 8;
  }

  private static final class Chain {
    private final int color;
    private final List<Point> points = new ArrayList<>();
    private int direction = -1;

    private Chain(int color) {
      this.color = color;
    }
  }

  private static final class Point {
    private final int x;
    private final int y;
    private final int z;

    private Point(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
  }
}
