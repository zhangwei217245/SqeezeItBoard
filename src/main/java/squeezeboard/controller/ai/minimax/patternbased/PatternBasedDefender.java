package squeezeboard.controller.ai.minimax.patternbased;

import squeezeboard.controller.ai.AIUtils;
import squeezeboard.controller.ai.SqueezeAI;
import squeezeboard.controller.pattern.SqueezePattern;
import squeezeboard.controller.pattern.SqueezePatternFinder;
import squeezeboard.controller.pattern.SqueezePatternType;
import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import squeezeboard.model.GameUtils;
import squeezeboard.model.PlayerColor;
import squeezeboard.model.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static squeezeboard.model.GameUtils.SEARCH_WIDTH;
import static squeezeboard.model.GameUtils.tryRemovePattern;

/**
 * Created by zhangwei on 3/13/16.
 */
public class PatternBasedDefender implements SqueezeAI {


    @Override
    public List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> findOptimalMove(PlayerColor computerColor, BoardConfiguration boardConfiguration) {

        final List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> result = new ArrayList<>();

        //find optimal defensive move here.
        List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> defensiveMoves = getDefensiveMoves(boardConfiguration, computerColor);

        if (!defensiveMoves.isEmpty()) {
            List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> movesWithRank = defensiveMoves.parallelStream().map(tuple -> {
                BoardConfiguration newBoard = boardConfiguration.clone();
                //Virtually carry out attack, and see what's going to happen.
                Tuple<CellData, CellData, Integer> move = tuple.getFirst();
                newBoard.setPiece(move);
                int removal = GameUtils.tryRemovePattern(move.getSecond(), newBoard, computerColor);
                int estimateScore = AIUtils.alphaBeta(0, Integer.MIN_VALUE, Integer.MAX_VALUE, newBoard,
                        pair -> {
                            return getDefensiveMoves(pair.getFirst(), pair.getSecond());
                        },
                        computerColor.getOpponentColor());
                return new Tuple<>(move, removal, estimateScore);
            }).collect(Collectors.toList());

//            int mostRemoval = movesWithRank.stream()
//                    .map(move -> move.getSecond()).max((a, b) -> Integer.compare(a, b)).get();
            int bestEstimate = movesWithRank.stream()
                    .map(pair -> pair.getThird()).max((a, b) -> Integer.compare(a, b)).get();

            //get best defensive move among all that are with the same defensive score.
            System.out.println("Most defensive move found!");
            movesWithRank.stream().filter(pair -> pair.getFirst().getThird() >= 3)
                    .filter(pair -> bestEstimate == pair.getThird())
                    .sorted((a, b) -> Integer.compare(b.getFirst().getThird(), a.getFirst().getThird()))
                    .limit(SEARCH_WIDTH/2).forEach(move -> result.add(move));
        }
        System.out.println(this.getClass().getSimpleName()+" got result = " + result.size());
        System.out.println("==================================================");
        return result;
    }


    private List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> getDefensiveMoves(BoardConfiguration boardConfiguration, PlayerColor attackingColor) {
        List<SqueezePattern> allSqueezePatternsOnBoard =
                SqueezePatternFinder.getAllSqueezePatternsOnBoard(attackingColor, boardConfiguration.getBoard());

        List<Tuple<CellData, CellData, Integer>> possibleDefensiveMoves =
                getPossibleDefensiveMovesFromPattern(allSqueezePatternsOnBoard, boardConfiguration, attackingColor);
        if (!possibleDefensiveMoves.isEmpty()) {
            return getDefensiveMoves(possibleDefensiveMoves, boardConfiguration, attackingColor);
        }
        return Collections.emptyList();
    }

    private List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> getDefensiveMoves(List<Tuple<CellData, CellData, Integer>> possibleDefensiveMoves, BoardConfiguration boardConfiguration, PlayerColor attackingColor) {
        List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> defensiveMoves = possibleDefensiveMoves.parallelStream().map(move -> {
            // Find all defensive moves among bestMoves that won't cause any elimination
            BoardConfiguration newBoard = boardConfiguration.clone();
            newBoard.setPiece(move);
            int removal = tryRemovePattern(move.getSecond(), newBoard, attackingColor);
            int estimateScore = AIUtils.getGlobalEstimate(boardConfiguration, attackingColor);
            return new Tuple<>(move, removal, estimateScore);
        }).filter(tuple -> tuple.getSecond() <= 0)/*FIXME:test this .limit(GameUtils.SEARCH_WIDTH)*/.collect(Collectors.toList());
        return defensiveMoves;
    }

    private List<Tuple<CellData, CellData, Integer>> getPossibleDefensiveMovesFromPattern(List<SqueezePattern> allSqueezePatternsOnBoard,
                                                                                BoardConfiguration boardConfiguration, PlayerColor computerColor) {
        List<Tuple<CellData, CellData, Integer>> result = new ArrayList<>();
        allSqueezePatternsOnBoard.forEach(squeezePattern -> {
            SqueezePatternType patternType = squeezePattern.getPatternType();
            if (patternType.equals(SqueezePatternType.GAP) ||
                    patternType.equals(SqueezePatternType.INCOMPLETE_GAP)) {
                result.addAll(squeezePattern.findPossibleDefensiveMoves(boardConfiguration.getBoard(), computerColor));
            }
        });
        return result;
    }
}
