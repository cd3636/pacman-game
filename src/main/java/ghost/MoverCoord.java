package ghost;

public class MoverCoord {
  public final double x;
  public final double y;
  //public String toString() {
  //  return "(" + x + ", " + y + ")";
  //}
  //public boolean equals(MoverCoord other) {
  //  return this.x == other.x && this.y == other.y;
  //}
  public MoverCoord(double x, double y) {
    this.x = x;
    this.y = y;
  }
  public MoverCoord(GridCoord coord) {
    this.x = coord.x;
    this.y = coord.y;
  }
}
