
package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.HashMap;
import java.lang.AssertionError;

class GridTest {
    @Test
    public void gridLoadsCorrectly() {
        // Check that the Grid methods reflect the data read from the map file
        Grid grid = new Grid("src/test/resources/map-grid-loads-ok.txt");
        assertNotNull(grid);
        assertEquals(303, grid.fruitLeft());
        MoverCoord playerHome = grid.playerHome();
        assertEquals(13, playerHome.x);
        assertEquals(20, playerHome.y);
        Map<MoverCoord,GhostType> ghostInfo = grid.ghostInfo();
        assertEquals(4, ghostInfo.size());
        /*
         * TODO - having issues with equality of coordinate objects in a map
         *
        Map<MoverCoord, GhostType> expected = new HashMap<MoverCoord, GhostType>();
        expected.put(new MoverCoord(10,14), GhostType.Ambusher);
        expected.put(new MoverCoord(14,14), GhostType.Ignorant);
        expected.put(new MoverCoord(16,14), GhostType.Whim);
        expected.put(new MoverCoord(12,14), GhostType.Whim);
        for (MoverCoord m : expected.keySet()) {
          assertEquals("Expected ghost cooordinate " + m + " to be present, but was missing", ghostInfo.containsKey(m));
          assertEquals(expected.get(m), ghostInfo.get(m));
        }
         */
    }

    @Test
    public void gridFruitEatenCorrectly() {
      // Check that eaten fruit is replaced by an AIR cell and that the fruit count goes down by 1.
      Grid grid = new Grid("src/test/resources/map-grid-loads-ok.txt");
      assertNotNull(grid);
      assertEquals(303, grid.fruitLeft());
      for (int x = 0; x < grid.GRID_COLS; x++) {
        for (int y = 0; y < grid.GRID_ROWS; y++) {
          if (grid.cellAt(x, y) == Cell.FRUIT) {
            grid.eatFruit(x, y);
            assertEquals(grid.cellAt(x,y), Cell.AIR);
            assertEquals(302, grid.fruitLeft());
            return; // Have tested 1 fruit cell
          }
        }
      }
    }


    @Test
    public void gridDetectsInvalidMapCharacters() {
      // Check that the Grid object detects invalid map characters
      assertThrows(AssertionError.class, () -> {
        Grid grid = new Grid("src/test/resources/map-grid-invalid-chars.txt");
        assertNotNull(grid);
      });
    }
}
