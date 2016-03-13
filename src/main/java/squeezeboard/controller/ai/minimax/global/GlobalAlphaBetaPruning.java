package squeezeboard.controller.ai.minimax.global;

import squeezeboard.controller.ai.SqueezeAI;
import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import squeezeboard.model.Pair;
import squeezeboard.model.PlayerColor;

/**
 * Created by zhangwei on 3/13/16.
 */
public class GlobalAlphaBetaPruning implements SqueezeAI {
    @Override
    public Pair<CellData, CellData> findOptimalMove(PlayerColor computerColor, BoardConfiguration boardConfiguration) {
        return null;
    }
}
