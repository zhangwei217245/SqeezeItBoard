package squeezeboard.controller.ai;

import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import squeezeboard.model.Pair;
import squeezeboard.model.PlayerColor;

/**
 * Created by zhangwei on 3/4/16.
 */
public class AlphaBetaPruningUtils {

    public static int globalEstimate(BoardConfiguration newboard, PlayerColor color) {
        Pair<Integer, Integer> blue_orange = calculateLeftPiecesCount(newboard);
        int diff = color.equals(PlayerColor.blue) ? blue_orange.getFirst() - blue_orange.getSecond()
                : blue_orange.getSecond() - blue_orange.getFirst();
        return diff + 1000000;
    }

    public static Pair<Integer,Integer> calculateLeftPiecesCount(BoardConfiguration newboard) {
        int orangeLeft = 0;
        int blueLeft = 0;
        CellData[][] boardData = newboard.getBoard();
        for (CellData[] row : boardData) {
            for (CellData cell : row) {
                if (cell.getCellChar() == PlayerColor.blue.CHAR()) {
                    blueLeft ++;
                } else if (cell.getCellChar() == PlayerColor.orange.CHAR()) {
                    orangeLeft ++;
                }
            }
        }
        return new Pair<>(blueLeft, orangeLeft);
    }
}
