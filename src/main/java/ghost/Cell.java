package ghost;

/**
 * Represents a fixed object on the map.
 */
public enum Cell {
  AIR(false),
  FRUIT(false),
  SUPERFRUIT(false),
  WALLHORIZONTAL(true),
  WALLVERTICAL(true),
  WALLUPLEFT(true),
  WALLUPRIGHT(true),
  WALLDOWNLEFT(true),
  WALLDOWNRIGHT(true);

  private final boolean isWall;

  private Cell(boolean isWall) {
    this.isWall = isWall;
  }

  /**
   * Indicates whether this Cell is a wall Cell.
   * @return      true if the Cell is a wall Cell.
   */
  public boolean isWall() {
    return this.isWall;
  }
}
