
package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
  @Test
  public void gameCreatedOK() {
    // Check that the game object is successfully created
    Game game = new Game("src/test/resources/config.json.game.loads.ok");
    assertNotNull(game);
  }

  @Test
  public void gameConfigDataCorrect() {
    // Check that the Game methods reflect the data read from the configuration file
    Game game = new Game("src/test/resources/config.json.game.loads.ok");
    assertNotNull(game);
    assertEquals(3, game.livesLeft());
    assertEquals(2, game.speed());
  }

  @Test
  public void gameSimpleWin() {
    // Check that the game enters a WIN state with a suitable map
    Game game = new Game("src/test/resources/config.json.game.simple.win");
    assertNotNull(game);
    for (int i = 0; i < 600; i++) {
      game.move(((double) game.speed()) / App.SPRITE_SIZE, App.SECS_PER_FRAME);
      if (game.mode() != GameMode.RUNNING) {
        break;
      }
    }
    assertEquals(GameMode.WIN, game.mode());
  }

  @Test
  public void gameSimpleLose() {
    // Check that the game enters a LOSE state with a suitable map
    Game game = new Game("src/test/resources/config.json.game.simple.lose");
    assertNotNull(game);
    for (int i = 0; i < 600; i++) {
      game.move(((double) game.speed()) / App.SPRITE_SIZE, App.SECS_PER_FRAME);
      if (game.mode() != GameMode.RUNNING) {
        break;
      }
    }
    assertEquals(GameMode.LOSE, game.mode());
  }

  @Test
  public void gameResetsAfterWin() {
    // Check that the game resets to a RUNNING state when reset() called after a WIN
    Game game = new Game("src/test/resources/config.json.game.simple.win");
    assertNotNull(game);
    for (int i = 0; i < 600; i++) {
      game.move(((double) game.speed()) / App.SPRITE_SIZE, App.SECS_PER_FRAME);
      if (game.mode() != GameMode.RUNNING) {
        break;
      }
    }
    assertEquals(GameMode.WIN, game.mode());
    game.reset();
    assertEquals(GameMode.RUNNING, game.mode());
  }

  @Test
  public void gameFrightenedMode() {
    // Check that the game enters a LOSE state with a suitable map
    Game game = new Game("src/test/resources/config.json.game.frighten.mode");
    assertNotNull(game);
    Ghost g = game.ghosts().get(0);
    assertEquals(GhostMode.SCATTER, g.mode());
    boolean wasFrightened = false;
    for (int i = 0; i < 600; i++) {
      game.move(((double) game.speed()) / App.SPRITE_SIZE, App.SECS_PER_FRAME);
      if (game.mode() != GameMode.RUNNING) {
        break;
      }
      if (g.mode() == GhostMode.FRIGHTENED) {
        wasFrightened = true;
      }
    }
    assertEquals(GameMode.WIN, game.mode());
    assertTrue(wasFrightened, "Expected ghost to be frightened but it wasn't");
  }

  @Test
  public void gameGhostModesChange() {
    // Check that the game startes in SCATTER and cycles through SCATTER and CHASE modes
    Game game = new Game("src/test/resources/config.json.game.simple.win");
    assertNotNull(game);
    assertEquals(GameMode.RUNNING, game.mode());
    Ghost g = game.ghosts().get(0);
    assertEquals(GhostMode.SCATTER, g.mode());
    GhostMode lastMode = g.mode();
    int nChangeToScatter = 0;
    int nChangeToChase = 0;
    for (int i = 0; i < 600; i++) {
      game.move(((double) game.speed()) / App.SPRITE_SIZE, App.SECS_PER_FRAME);
      if (g.mode() != lastMode) {
        lastMode = g.mode();
        System.out.println(lastMode);
        if (lastMode == GhostMode.SCATTER) {
          nChangeToScatter++;
        } else if (lastMode == GhostMode.CHASE) {
          nChangeToChase++;
        }
      }
      if (game.mode() != GameMode.RUNNING) {
        break;
      }
    }
    assertEquals(GameMode.WIN, game.mode());
    assertTrue(nChangeToScatter > 1, "Expected nChangeToScatter > 1, but it wasn't");
    assertTrue(nChangeToChase > 1, "Expected nChangeToChase > 1, but it wasn't");
  }
}
