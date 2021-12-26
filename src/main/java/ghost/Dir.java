package ghost;

public enum Dir {
  UP(0, -1, false),
  DOWN(0, 1, true),
  LEFT(-1, 0, false),
  RIGHT(1, 0, true);

  private int xD;
  private int yD;
  private boolean isPositive;

  private Dir(int xD, int yD, boolean isPositive) {
    this.xD = xD;
    this.yD = yD;
    this.isPositive = isPositive;
  }

  public int xD() {
    return this.xD;
  }

  public int yD() {
    return this.yD;
  }

  public boolean isPositive() {
    return this.isPositive;
  }

  public Dir opposite() {
    switch (this) {
      case UP:
        return DOWN;
      case DOWN:
        return UP;
      case LEFT:
        return RIGHT;
      case RIGHT:
        return LEFT;
    }
    return null;
  }
}
