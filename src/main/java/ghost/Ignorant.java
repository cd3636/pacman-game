package ghost;

public class Ignorant extends Ghost {
  public Ignorant(MoverCoord homePos, Player player) {
    super(homePos, player);
  }
  /*
   * look up @override in javadoc
   */
  public GhostType type() {
    return GhostType.IGNORANT;
  }

  public GridCoord scatterCoord() {
    return new GridCoord(0, Grid.GRID_ROWS - 1);
  }
  public GridCoord chaseCoord() {
    GridCoord ghostPos = new GridCoord(curPos());
    GridCoord playerPos = new GridCoord(player.curPos());
    if (ghostPos.distanceTo(playerPos) > 8) {
      return new GridCoord(player.curPos());
    } else {
      return scatterCoord();
    }
  }
}
