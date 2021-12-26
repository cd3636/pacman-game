package ghost;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Grid {
  public final static int GRID_ROWS = 36;
  public final static int GRID_COLS = 28;

  public static int fixRow(int row) {
    if (row < 0) {
      return 0;
    } else if (row >= GRID_ROWS) {
      return GRID_ROWS - 1;
    } else {
      return row;
    }
  }
  public static int fixCol(int col) {
    if (col < 0) {
      return 0;
    } else if (col >= GRID_COLS) {
      return GRID_COLS - 1;
    } else {
      return col;
    }
  }

  private Cell[][] cells = new Cell[GRID_COLS][GRID_ROWS];
  private boolean[][] eatenFruit = new boolean[GRID_COLS][GRID_ROWS];
  private Map<Character,Cell> cellConvert = new HashMap<Character,Cell>();
  private Map<Character,GhostType> ghostConvert = new HashMap<Character,GhostType>();
  private int fruitLeft;

  public Cell cellAt(int x, int y) {
    if (eatenFruit[x][y]) {
      return Cell.AIR;
    }
    return cells[x][y];
  }

  public int fruitLeft() {
    return fruitLeft;
  }

  public void resetFruit() {
    fruitLeft = 0;
    for (int row = 0; row < GRID_ROWS; row++) {
      for (int col = 0; col < GRID_COLS; col++) {
        Cell cell = cells[col][row];
        if (cell == Cell.FRUIT || cell == Cell.SUPERFRUIT) {
          fruitLeft++;
        }
        eatenFruit[col][row] = false;
      }
    }
  }

  public void eatFruit(int col, int row) {
    eatenFruit[col][row] = true;
    fruitLeft--;
  }

  private MoverCoord playerHome;
  private Map<MoverCoord,GhostType> ghostInfo = new HashMap<MoverCoord,GhostType>();
  public MoverCoord playerHome() {
    return playerHome;
  }
  public Map<MoverCoord,GhostType> ghostInfo() {
    return ghostInfo;
  }

  private void initialise(String[] mapRows) {
    fruitLeft = 0;
    cellConvert.put('0', Cell.AIR);
    cellConvert.put('1', Cell.WALLHORIZONTAL);
    cellConvert.put('2', Cell.WALLVERTICAL);
    cellConvert.put('3', Cell.WALLUPLEFT);
    cellConvert.put('4', Cell.WALLUPRIGHT);
    cellConvert.put('5', Cell.WALLDOWNLEFT);
    cellConvert.put('6', Cell.WALLDOWNRIGHT);
    cellConvert.put('7', Cell.FRUIT);
    cellConvert.put('8', Cell.SUPERFRUIT);

    ghostConvert.put('a', GhostType.AMBUSHER);
    ghostConvert.put('c', GhostType.CHASER);
    ghostConvert.put('g', GhostType.CHASER);
    ghostConvert.put('i', GhostType.IGNORANT);
    ghostConvert.put('w', GhostType.WHIM);

    assert(mapRows.length == GRID_ROWS);
    for (int row = 0; row < GRID_ROWS; row++) {
      String line = mapRows[row];
      assert(line.length() == GRID_COLS);
      for (int col = 0; col < GRID_COLS; col++) {
        Cell cell = Cell.AIR;
        char ch = line.charAt(col);
        if (ch == 'p') {
          cell = Cell.AIR;
          playerHome = new MoverCoord(col, row);
        } else if (ghostConvert.containsKey(ch)) {
          cell = Cell.AIR;
          ghostInfo.put(new MoverCoord(col, row),ghostConvert.get(ch));
          // ghostTypes.add(GhostType.Classic);
        } else {
          assert(cellConvert.containsKey(ch));
          cell = cellConvert.get(ch);
        }
        cells[col][row] = cell;
      }
    }
    resetFruit();
  }

  public Grid(String mapFilename) {
    String[] mapRows = new String[GRID_ROWS];
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(mapFilename)))
    {
      for (int row = 0; row < GRID_ROWS; row++) {
        String line = bufferedReader.readLine();
        assert(line != null);
        assert(line.length() == GRID_COLS);
        mapRows[row] = line;
      }
      assert(bufferedReader.readLine() == null);  // Make sure there are no more lines
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    initialise(mapRows);
  }

}
