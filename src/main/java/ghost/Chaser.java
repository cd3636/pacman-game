package ghost;

public class Chaser extends Ghost {
  public Chaser(MoverCoord homePos, Player player) {
    super(homePos, player);
  }
  /*
   * look up @override in javadoc
   */
  public GhostType type() {
    return GhostType.CHASER;
  }

  public GridCoord scatterCoord() {
    return new GridCoord(0, 0);
  }
  public GridCoord chaseCoord() {
    return new GridCoord(player.curPos());
  }
}
