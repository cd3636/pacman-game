package ghost;

public class Whim extends Ghost {
  private Ghost chaser;
  public Whim(MoverCoord homePos, Player player) {
    super(homePos, player);
  }
  /*
   * look up @override in javadoc
   */
  public GhostType type() {
    return GhostType.WHIM;
  }

  public GridCoord scatterCoord() {
    return new GridCoord(Grid.GRID_COLS - 1, Grid.GRID_ROWS - 1);
  }
  public GridCoord chaseCoord() {
    GridCoord chaserCoord = new GridCoord(chaser.curPos());
    GridCoord playerCoord = new GridCoord(player.curPos());
    int vectX = playerCoord.x + 2 * player.curDir().xD() - chaserCoord.x;
    int vectY = playerCoord.y + 2 * player.curDir().yD() - chaserCoord.y;
    int x = Grid.fixCol(chaserCoord.x + 2 * vectX);
    int y = Grid.fixRow(chaserCoord.y + 2 * vectY);
    return new GridCoord(x, y);
  }
  public void setChaser(Ghost chaser) {
    this.chaser = chaser;
  }
}
