package ghost;

public class Ambusher extends Ghost {


  public Ambusher(MoverCoord homePos, Player player) {
    super(homePos, player);
  }
  /*
   * look up @override in javadoc
   */
  public GhostType type() {
    return GhostType.AMBUSHER;
  }

  public GridCoord scatterCoord() {
    return new GridCoord(Grid.GRID_COLS - 1, 0);
  }
  public GridCoord chaseCoord() {
    GridCoord playerCoord = new GridCoord(player.curPos());
    int x = Grid.fixCol(playerCoord.x + 4 * player.curDir().xD());
    int y = Grid.fixRow(playerCoord.y + 4 * player.curDir().yD());
    return new GridCoord(x, y);
  }
}
