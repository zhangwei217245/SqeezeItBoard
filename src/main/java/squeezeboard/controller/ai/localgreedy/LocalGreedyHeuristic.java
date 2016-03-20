package squeezeboard.controller.ai.localgreedy;

import squeezeboard.controller.ai.AIUtils;
import squeezeboard.controller.ai.SqueezeAI;
import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import squeezeboard.model.Pair;
import squeezeboard.model.PlayerColor;

import java.util.List;

/**
 * Created by zhangwei on 3/13/16.
 */
public class LocalGreedyHeuristic implements SqueezeAI {


    @Override
    public Pair<CellData, CellData> findOptimalMove(PlayerColor computerColor, BoardConfiguration boardConfiguration) {
        List<CellData> allComputerPieces = AIUtils.findAllComputerPieces(computerColor, boardConfiguration);
        List<Pair<CellData, CellData>> allPossibleMoves = AIUtils.getAllPossibleMoves(allComputerPieces, boardConfiguration);

//        AIUtils.selectBestMoves(allPossibleMoves, boardConfiguration, computerColor, (p1, p2) -> {
//
//        });

        return null;
    }
}
