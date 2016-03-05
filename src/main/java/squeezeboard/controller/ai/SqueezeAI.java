package squeezeboard.controller.ai;

import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import squeezeboard.model.Pair;
import squeezeboard.model.PlayerColor;

/**
 * Created by zhangwei on 3/4/16.
 */
public interface SqueezeAI {

    public Pair<CellData, CellData> findOptimalMove(PlayerColor computerColor, BoardConfiguration boardConfiguration);
}
