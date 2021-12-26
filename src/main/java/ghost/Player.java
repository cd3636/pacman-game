package ghost;

public class Player extends Mover {
  private Dir nextDir;
  //lives

  public Dir nextDir() {
    return nextDir;
  }

  public void setNextDir(Dir d) {
    nextDir = d;
  }

  public void goHome() {
    super.goHome();
    nextDir = curDir();
  }

  public Player(MoverCoord homePos) {
    super(homePos);
    nextDir = curDir();
  }
}
