package squeezeboard.controller.ai.minimax.patternbased;

import squeezeboard.model.*;

/**
 * Created by zhangwei on 3/4/16.
 */
public class PatternBasedAlphaBetaPruningUtils {

    public static int globalEstimate(BoardConfiguration newboard, PlayerColor color) {
        Pair<Integer, Integer> blue_orange = GameUtils.calculateLeftPiecesCount(newboard);
        int diff = color.equals(PlayerColor.blue) ? blue_orange.getFirst() - blue_orange.getSecond()
                : blue_orange.getSecond() - blue_orange.getFirst();
        return diff + 1000000;
    }

}
