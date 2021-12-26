package ghost;

import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Game {
  private Player player;
  private Grid grid;
  private int speed;
  private int playerLives;
  private int livesLeft;
  private List<Ghost> ghosts = new ArrayList<Ghost>();
  private List<Integer> modeLengths = new ArrayList<Integer>();
  private int frightenedLength;
  // Variables for keeping track of ghost modes
  private GhostMode ghostMode;
  private double cycleTime;  // SCATTER/CHASE cycle time
  private double frightenedTime;
  private Random random = new Random();

  public Player player() {
    return player;
  }
  public List<Ghost> ghosts() {
    return ghosts;
  }
  public Grid grid() {
    return grid;
  }

  public int livesLeft() {
    return livesLeft;
  }
  public int speed() {
    return speed;
  }

  public GameMode mode() {
    if (grid.fruitLeft() == 0) {
      return GameMode.WIN;
    } else if (livesLeft == 0) {
      return GameMode.LOSE;
    } else {
      return GameMode.RUNNING;
    }
  }

  private void eatFruit(int col, int row) {
    grid.eatFruit(col, row);
    System.out.println(grid.fruitLeft());
  }
  private void ghostModeChanged() {
    for (Ghost ghost : ghosts) {
      ghost.setMode(ghostMode);
    }
  }
  private void sendHome() {
    player.goHome();
    for (Ghost ghost : ghosts) {
      ghost.goHome();
    }
    // Reset the ghost mode Variables
    ghostMode = GhostMode.SCATTER;
    cycleTime = 0;
    frightenedTime = 0;
    ghostModeChanged();
  }
  public void reset() {
    sendHome();
    livesLeft = playerLives;
    grid.resetFruit();
  }
  private void adjustGhostMode(double timePassed) {
    if (ghostMode == GhostMode.FRIGHTENED) {
      frightenedTime += timePassed;
      if (frightenedTime < frightenedLength) {
        return; // stay in frightened mode
      }
      timePassed = 0;
    }
    cycleTime += timePassed;
    int subtotal = 0;
    for (int i = 0; i < modeLengths.size(); i++) {
      subtotal += modeLengths.get(i);
      if (cycleTime < subtotal) {
        if (i%2 == 0) {
          ghostMode = GhostMode.SCATTER;
        } else {
          ghostMode = GhostMode.CHASE;
        }
        return;
      }
    }
    cycleTime -= subtotal;
    ghostMode = GhostMode.SCATTER;
  }
  public void move(double gridUnits, double timePassed) {
    movePlayer(gridUnits);
    for (Ghost ghost : ghosts) {
      moveGhost(ghost, gridUnits);
    }
    GhostMode oldMode = ghostMode;
    adjustGhostMode(timePassed);
    if (oldMode != ghostMode) {
      ghostModeChanged();
    }
  }
  private void movePlayer(double gridUnits) {
    // System.out.print(gridUnits);
    GridCoord playerCell = new GridCoord(player.curPos());

    if (player.nextDir() != null) {
      if (player.curDir().opposite() == player.nextDir()) {
        //  Immediately reverse direction.
        player.setCurDir(player.nextDir());
      }
    }
    double dispToCentre = playerCell.displacementTo(player.curPos(), player.curDir());
    if (dispToCentre >= 0 && player.nextDir() != null && player.nextDir() != player.curDir()) {
      // Human player wants to turn at the next intersection,
      // and Waka hasn't passed the centre of the current cell.
      // Check to see whether we can turn.
      //turnCell = cell at current player coords + xD & yD from setNextDir
      GridCoord turnCell = new GridCoord(playerCell.x + player.nextDir().xD(), playerCell.y + player.nextDir().yD());
      if (!grid.cellAt(turnCell.x, turnCell.y).isWall()) {
        if (gridUnits > dispToCentre) {
          // Move Waka centre of current cell, adjust remaining distance, and turnCell
          player.move(dispToCentre);
          gridUnits -= dispToCentre;
          dispToCentre = 0;
          player.setCurDir(player.nextDir());
        }
      }
    }
    GridCoord nextCell = new GridCoord(playerCell.x + player.curDir().xD(), playerCell.y + player.curDir().yD());
    // Wall detection
    if (grid.cellAt(nextCell.x, nextCell.y).isWall()) {
      if (dispToCentre >= 0) {
        //  The centre is ahead of us.  Keep moving, but not too much.
        if (gridUnits > dispToCentre) {
          gridUnits -= dispToCentre;
        }
        player.move(dispToCentre);
      } else {
        // We've gone past the centre!  Move back to dispToCentre
        player.setPos(new MoverCoord(playerCell));
      }
    } else {
      player.move(gridUnits);
    }

    Cell celltype = grid.cellAt(playerCell.x, playerCell.y);
    if (celltype == Cell.FRUIT || celltype == Cell.SUPERFRUIT) {
      eatFruit(playerCell.x, playerCell.y);
      if (celltype == Cell.SUPERFRUIT) {
        frightenedTime = 0;
        ghostMode = GhostMode.FRIGHTENED;
        ghostModeChanged();
      }
    }
  }

  private void moveGhost(Ghost ghost, double gridUnits) {
    if (ghost.removed()) {
      return;
    }
    // System.out.print(gridUnits);
    GridCoord ghostCell = new GridCoord(ghost.curPos());

    double dispToCentre = ghostCell.displacementTo(ghost.curPos(), ghost.curDir());
    if (dispToCentre >= 0 && gridUnits > dispToCentre) {
      // Ghost is near centre of the cell.
      // Move ghost to center of the cell, adjust remaining
      // distance and see whether the ghost can turn
      ghost.move(dispToCentre);
      gridUnits -= dispToCentre;
      dispToCentre = 0;

      // Find closest adjacent cell to the ghost target
      //
      GridCoord target = ghost.targetCoord();
      GridCoord closestCell = null;
      double closestDistance = -1;
      Dir closestDir = null;
      List<Dir> turnDirections = new ArrayList<Dir>();
      for (Dir d : Dir.values()) {
        if (!(d.opposite() == ghost.curDir())) {
          GridCoord turnCell = new GridCoord(ghostCell.x + d.xD(), ghostCell.y + d.yD());
          if (!grid.cellAt(turnCell.x, turnCell.y).isWall()) {
            turnDirections.add(d);
            double turnDistance = turnCell.distanceTo(target);
            if (closestCell == null || turnDistance < closestDistance) {
              closestCell = turnCell;
              closestDistance = turnDistance;
              closestDir = d;
            }
          }
        }
      }
      if (closestCell == null) {
        // Dead end - reverse direction
        ghost.setCurDir(ghost.curDir().opposite());
      } else {
        if (ghostMode == GhostMode.FRIGHTENED) {
          int randomDirIndex = random.nextInt(turnDirections.size());
          ghost.setCurDir(turnDirections.get(randomDirIndex));
        } else {
          ghost.setCurDir(closestDir);
        }
      }
    }
    //ghost.move(gridUnits);

    GridCoord newGhostCell = new GridCoord(ghost.curPos());
    GridCoord playerPos = new GridCoord(player.curPos());
    boolean collided = newGhostCell.distanceTo(playerPos) == 0 || ghostCell.distanceTo(playerPos) == 0;
    if (collided && livesLeft >= 1) {
      if (ghostMode == GhostMode.FRIGHTENED) {
        ghost.setRemoved();
        return;
      } else {
        System.out.println("Dead");
        livesLeft--;
        sendHome();
        if (livesLeft == 0) {
          System.out.println("Game over");
        }
        return;
      }
    }
    ghost.move(gridUnits);

  }

  public Game(String configFilename) {
    JSONParser jsonParser = new JSONParser();
    try (BufferedReader reader = new BufferedReader(new FileReader(configFilename))) {
      JSONObject conf = (JSONObject) jsonParser.parse(reader);
      String mapFilename = (String) conf.get("map");
      playerLives = ((Number) conf.get("lives")).intValue();
      livesLeft = playerLives;
      speed = ((Number) conf.get("speed")).intValue();
      JSONArray a = (JSONArray) conf.get("modeLengths");
      for (Object obj : a) {
        int x = ((Number) obj).intValue();
        modeLengths.add(new Integer(x));
      }
      frightenedLength = ((Number) conf.get("frightenedLength")).intValue();
      grid = new Grid(mapFilename);
      player = new Player(grid.playerHome());
      Ghost chaser = null;
      List<Whim> whims = new ArrayList<Whim>();
      for (MoverCoord m : grid.ghostInfo().keySet()) {
        Ghost ghost = null;
        switch (grid.ghostInfo().get(m)) {
          case AMBUSHER:
            ghost = new Ambusher(m, player);
            break;
          case CHASER:
            chaser = new Chaser(m, player);
            ghost = chaser;
            break;
          case IGNORANT:
            ghost = new Ignorant(m, player);
            break;
          case WHIM:
            Whim whim = new Whim(m, player);
            whims.add(whim);
            ghost = whim;
            break;
        }
        ghosts.add(ghost);
        //System.out.println(ghost.type + " " + ghost.curPos());
      }
      for (Whim whim : whims) {
        whim.setChaser(chaser);
      }
      // for each ghost home and ghost type in the grid,
      // create a ghost of the appropriate class
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
  }
}
