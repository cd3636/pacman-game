package ghost;

public class Mover {
  private Dir curDir;
  private final MoverCoord homePos;
  private MoverCoord curPos;

  public Dir curDir() {
    return curDir;
  }

  public MoverCoord homePos() {
    return homePos;
  }

  public MoverCoord curPos() {
    return curPos;
  }

  public void setPos(MoverCoord m) {
    curPos = m;
  }
  public void move(double dist) {
    curPos = new MoverCoord(curPos.x + dist * curDir.xD(), curPos.y + dist * curDir.yD());
  }

  public void setCurDir(Dir d) {
    curDir = d;
  }

  public void goHome() {
    //System.out.printf("goHome(%f, %f)\n", homePos.x, homePos.y);
    curPos = homePos;
    curDir = Dir.LEFT;
  }

  public Mover(MoverCoord homePos) {
    //System.out.printf("Home = (%f, %f)\n", homePos.x, homePos.y);
    this.homePos = homePos;
    this.curPos = homePos;
    this.curDir = Dir.LEFT;
  }
}
