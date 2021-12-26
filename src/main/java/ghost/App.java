package ghost;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import processing.event.KeyEvent;
import java.util.Map;
import java.util.HashMap;

public class App extends PApplet {

  public static final int WIDTH = 448;
  public static final int HEIGHT = 576;
  public static final int SPRITE_SIZE = 16;
  public static final int FRAME_RATE = 60;
  public static final double SECS_PER_FRAME = 1.0 / FRAME_RATE;

  private Grid grid;
  private Game game;
  private Player player;
  private Map<Cell,PImage> cellConvert = new HashMap<Cell,PImage>();
  private Map<Dir,PImage> playerConvert = new HashMap<Dir,PImage>();
  private Map<GhostType,PImage> ghostConvert = new HashMap<GhostType,PImage>();
  private PImage frightened;
  private PFont font;
  private boolean mouthOpen = false;
  private boolean debugMode = false;
  private int splashTimer = 0;

  public App() {
    //Set up your objects
    game = new Game("config.json");
    grid = game.grid();
    player = game.player();
  }

  public void setup() {
    frameRate(FRAME_RATE);

    // Load images
    cellConvert.put(Cell.WALLHORIZONTAL, loadImage("horizontal.png"));
    cellConvert.put(Cell.WALLVERTICAL, loadImage("vertical.png"));
    cellConvert.put(Cell.WALLUPLEFT, loadImage("upLeft.png"));
    cellConvert.put(Cell.WALLUPRIGHT, loadImage("upRight.png"));
    cellConvert.put(Cell.WALLDOWNLEFT, loadImage("downLeft.png"));
    cellConvert.put(Cell.WALLDOWNRIGHT, loadImage("downRight.png"));
    cellConvert.put(Cell.FRUIT, loadImage("fruit.png"));

    playerConvert.put(Dir.UP, loadImage("playerUp.png"));
    playerConvert.put(Dir.DOWN, loadImage("playerDown.png"));
    playerConvert.put(Dir.LEFT, loadImage("playerLeft.png"));
    playerConvert.put(Dir.RIGHT, loadImage("playerRight.png"));
    playerConvert.put(null, loadImage("playerClosed.png"));

    ghostConvert.put(GhostType.AMBUSHER, loadImage("ambusher.png"));
    ghostConvert.put(GhostType.CHASER, loadImage("chaser.png"));
    ghostConvert.put(GhostType.IGNORANT, loadImage("ignorant.png"));
    ghostConvert.put(GhostType.WHIM, loadImage("whim.png"));
    frightened = loadImage("frightened.png");

    font = createFont("PressStart2P-Regular.ttf", 34, true);

    //ghostSprite = loadImage("ghost.png");
  }

  public void settings() {
    size(WIDTH, HEIGHT);
  }

  public int screenCoord(double gridUnit) {
    return (int) (gridUnit * SPRITE_SIZE + SPRITE_SIZE/2);
  }

  public void drawSprite(PImage sprite, double cellCol, double cellRow) {
    image(sprite, screenCoord(cellCol), screenCoord(cellRow));
  }

  public void drawSpriteDoubleSize(PImage sprite, double cellCol, double cellRow) {
    image(sprite, screenCoord(cellCol), screenCoord(cellRow), SPRITE_SIZE*2, SPRITE_SIZE*2);
//    image(sprite, (int) (cellCol * SPRITE_SIZE + SPRITE_SIZE/2), (int) (cellRow * SPRITE_SIZE + SPRITE_SIZE/2), sprite.width * 2, sprite.height*2);
  }

  public void draw() {
    background(0, 0, 0);
    imageMode(CENTER);
    if (game.mode() != GameMode.RUNNING) {
      String message = (game.mode() == GameMode.WIN) ? "\n\n\n\n\n\n   YOU WIN" : "\n\n\n\n\n\n  GAME OVER";
      textFont(font);
      text(message, 0, 0);
      splashTimer++;
      if (splashTimer == 600) {
        game.reset();
      }
      return;
    }
    splashTimer = 0;
    if (frameCount%8 == 0) {
      mouthOpen = !mouthOpen;
    }
    game.move(((double) game.speed()) / SPRITE_SIZE, SECS_PER_FRAME);
    for (int row = 0; row < grid.GRID_ROWS; row++) {
      for (int col = 0; col < grid.GRID_COLS; col++) {
        Cell cell = grid.cellAt(col, row);
        if (cell != Cell.AIR) {
          if (cell == Cell.SUPERFRUIT) {
            PImage sprite = cellConvert.get(Cell.FRUIT);
            drawSpriteDoubleSize(sprite, col, row);
          } else {
            PImage sprite = cellConvert.get(cell);
            drawSprite(sprite, col, row);
          }
            //System.out.println("Row = " + row + "  Col = " + col + "  sprite = " + sprite);
        }
      }
    }

    // Draw the player
    PImage playerSprite = playerConvert.get(null);
    if (!mouthOpen) {
      playerSprite = playerConvert.get(player.curDir());
    }
    drawSprite(playerSprite, player.curPos().x, player.curPos().y);

    // Draw the ghosts
    for (Ghost g : game.ghosts()) {
      if (g.removed()) {
        continue;
      }
      PImage ghostSprite;
      if (g.mode() == GhostMode.FRIGHTENED) {
        ghostSprite = frightened;
      } else {
        ghostSprite = ghostConvert.get(g.type());
      }
      drawSprite(ghostSprite, g.curPos().x, g.curPos().y);
      if (debugMode) {
        // Draw a line from the ghost to its target cell.
        stroke(255);   // Select white for the line colour
        GridCoord targetCoord = g.targetCoord();
        // Testing: GridCoord targetCoord = new GridCoord(player.curPos());
        GridCoord ghostCoord = new GridCoord(g.curPos());
        line(screenCoord(ghostCoord.x), screenCoord(ghostCoord.y), screenCoord(targetCoord.x), screenCoord(targetCoord.y));

      }
    }

    // Draw the lives
    PImage lifeSprite = loadImage("playerRight.png");
    for (int i = 0; i<game.livesLeft(); i++) {
      drawSprite(lifeSprite, i * 2 + 1, grid.GRID_ROWS - 2);
    }
  }

  public void keyPressed(KeyEvent event) {
    if (key == ' ') {
      //System.out.println("You hit space!");
      debugMode = !debugMode;
    } else if (keyCode == UP || key == 'w') {
      //System.out.println("You hit Up!");
      player.setNextDir(Dir.UP);
    } else if (keyCode == DOWN || key == 's') {
      //System.out.println("You hit Down!");
      player.setNextDir(Dir.DOWN);
    } else if (keyCode == LEFT || key == 'a') {
      //System.out.println("You hit Left!");
      player.setNextDir(Dir.LEFT);
    } else if (keyCode == RIGHT || key == 'd') {
      //System.out.println("You hit Right!");
      player.setNextDir(Dir.RIGHT);
    } else if (key == 'q') {
      //System.out.println("You hit q!");
      this.exit();
    }
  }

  public static void main(String[] args) {
    PApplet.main("ghost.App");
  }

}
