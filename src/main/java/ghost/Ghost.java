package ghost;

/**
 * Base class for different types of ghosts.
 */
public abstract class Ghost extends Mover {
  public final Player player;
  private GhostMode mode;
  private boolean removed;

  public GhostMode mode() {
    return mode;
  }
  public void setMode(GhostMode mode) {
    this.mode = mode;
  }
  public abstract GhostType type();
  public abstract GridCoord scatterCoord();
  public abstract GridCoord chaseCoord();

  public boolean removed() {
    return removed;
  }
  public void setRemoved() {
    removed = true;
  }
  public void goHome() {
    super.goHome();
    removed = false;
  }
  public GridCoord targetCoord() {
    // Return this ghost's current target coordinate.
    if (mode == GhostMode.SCATTER) {
      return scatterCoord();
    } else {
      return chaseCoord();
    }
  }
  public Ghost(MoverCoord homePos, Player player) {
    super(homePos);
    this.mode = GhostMode.SCATTER;
    this.player = player;
    this.removed = false;
  }
}
