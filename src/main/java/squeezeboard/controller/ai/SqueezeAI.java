package squeezeboard.controller.ai;

import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import squeezeboard.model.PlayerColor;
import squeezeboard.model.Tuple;

import java.util.List;

/**
 * Created by zhangwei on 3/4/16.
 */
public interface SqueezeAI {


    /**
     *
     * search depth represent the search depth for a move, the lesser the better.
     * the gain measures the number of pieces that is removed from the board,
     * if the opponent's marbles get removed, the gain is a positive number, vice versa.
     * estimate score measures the how much marbles does computer left on the board, the larger, the better.
     * @param computerColor
     * @param boardConfiguration
     * @return  return : list of Tuple<Tuple<srcCell, distCell, searchDepth>, gain, estimateScore>
     */
    public List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> findOptimalMove(PlayerColor computerColor, BoardConfiguration boardConfiguration);
}
