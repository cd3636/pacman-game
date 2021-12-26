package ghost;

import java.lang.Math;

public class GridCoord {
  public final int x;
  public final int y;
  public String toString() {
    return "(" + x + "," + y + ")";
  }
  public GridCoord(int x, int y) {
    this.x = x;
    this.y = y;
  }
  public GridCoord(MoverCoord c) {
    this.x = (int) (c.x + 0.5);
    this.y = (int) (c.y + 0.5);
  }
  /**
   * Calculate displacement from <code>c</code> to this coordinate
   * in the direction <code>d</code>.
   * @param c coordinate that the displacement is meansured from
   * @param d direction in which the displacement is measured
   * @return displacement from <code>c</code> to this coordinate
   * in the direction <code>d</code>
   */
  public double displacementTo(MoverCoord c, Dir d) {
    if (d == Dir.RIGHT) {
      return x - c.x;
    } else if (d == Dir.LEFT) {
      return c.x - x;
    } else if (d == Dir.DOWN) {
      return y - c.y;
    } else {
      return c.y - y;
    }

  }
  public double distanceTo(GridCoord c) {
    return Math.sqrt((x-c.x)*(x-c.x)+(y-c.y)*(y-c.y));
  }
}
