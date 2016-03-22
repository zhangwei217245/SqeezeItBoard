package squeezeboard.controller.ai.minimax.patternbased;

import squeezeboard.controller.ai.AIUtils;
import squeezeboard.controller.ai.SqueezeAI;
import squeezeboard.controller.pattern.SqueezePattern;
import squeezeboard.controller.pattern.SqueezePatternFinder;
import squeezeboard.controller.pattern.SqueezePatternType;
import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import squeezeboard.model.GameUtils;
import squeezeboard.model.Pair;
import squeezeboard.model.PlayerColor;
import squeezeboard.model.Tuple;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import static squeezeboard.model.GameUtils.tryRemovePattern;

/**
 * Created by zhangwei on 3/13/16.
 */
public class PatternBasedMoveGenerator implements SqueezeAI {

    private static final Random RANDOM = new SecureRandom();

    @Override
    public Pair<CellData, CellData> findOptimalMove(PlayerColor computerColor, BoardConfiguration boardConfiguration) {
        Pair<CellData, CellData> result = null;

        //get optimal attacking move here
        //First, try to get any move that gives us the most defensive attack!
        List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> attackingMoves = getAttackingMoves(boardConfiguration, computerColor, true);
        // if there are some attacking moves, try to evaluate them by minimax search, the goal is to make sure that
        // the number of residual pieces of current player is maximized while the number of residual pieces of opponent
        // is going to be minimized.
        if (!attackingMoves.isEmpty()) {
            List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> movesWithRank = attackingMoves.parallelStream().map(tuple -> {
                BoardConfiguration newBoard = boardConfiguration.clone();
                //Virtually carry out attack, and see what's going to happen.
                Tuple<CellData, CellData, Integer> move = tuple.getFirst();
                newBoard.setPiece(move);
                int removal = GameUtils.tryRemovePattern(move.getSecond(), newBoard, computerColor);
                int estimateScore = AIUtils.alphaBeta(0, Integer.MIN_VALUE, Integer.MAX_VALUE, newBoard,
                        new Function<Pair<BoardConfiguration, PlayerColor>, List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>>>() {
                            @Override
                            public List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> apply(Pair<BoardConfiguration, PlayerColor> pair) {
                                return getAttackingMoves(pair.getFirst(), pair.getSecond(), false);
                            }
                        },
                        computerColor.getOpponentColor());
                return new Tuple<>(move, removal, estimateScore);
            }).collect(Collectors.toList());
            //Get the score for the attack that is most defensive.
            int bestEstimate = movesWithRank.stream().map(pair -> pair.getThird()).max((a, b) -> Integer.compare(a, b)).get();
            int mostRemoval = movesWithRank.stream().map(pair -> pair.getSecond()).max((a, b) -> Integer.compare(a, b)).get();
            //get best attacking move among all that are with the same defensive score.
            if (mostRemoval > 0) {

            } else {

            }
            List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> aggressiveAttacks = movesWithRank.stream()
                    .filter(move -> mostRemoval == move.getSecond()).collect(Collectors.toList());

            List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> defensiveAttacks = movesWithRank.stream()
                    .filter(pair -> bestEstimate == pair.getThird()).collect(Collectors.toList());

            List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> bestMoves = null;
            if (boardConfiguration.getNumberOfPieces(computerColor) < GameUtils.GRID_DIMENSION - 1) {
                Optional<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> mostDefensive =
                        aggressiveAttacks.stream().max((a, b) -> Integer.compare(a.getThird(), b.getThird()));

                bestMoves = aggressiveAttacks.stream().filter(move -> mostDefensive.get().getThird() == move.getThird())
                        .collect(Collectors.toList());
            } else {
                Optional<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> mostAggressive
                        = defensiveAttacks.stream().max((a, b) -> Integer.compare(a.getSecond(), b.getSecond()));
                bestMoves = defensiveAttacks.stream().filter(move -> mostAggressive.get().getSecond() == move.getSecond())
                        .collect(Collectors.toList());
            }

            Tuple<CellData, CellData, Integer> tmpRst = null;
            if (bestMoves.size() >= 1) {
                tmpRst = bestMoves.get(RANDOM.nextInt(bestMoves.size())).getFirst();
                System.out.println("Most aggressive attacking move with largest defensive score found!");
            } else if (boardConfiguration.getNumberOfPieces(computerColor) >= 3) {
                aggressiveAttacks.addAll(bestMoves);
                tmpRst = aggressiveAttacks.get(RANDOM.nextInt(aggressiveAttacks.size())).getFirst();
                System.out.println("Most aggressive attacking move found!");
            } else {
                defensiveAttacks.addAll(bestMoves);
                System.out.println("Most defensive attacking move found!");
                tmpRst = defensiveAttacks.get(RANDOM.nextInt(defensiveAttacks.size())).getFirst();
            }
            if (tmpRst != null) {
                result = new Pair<>(tmpRst.getFirst(), tmpRst.getSecond());
            }
        }
        return result;
    }

    private List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> getAttackingMoves(BoardConfiguration boardConfiguration, PlayerColor attackingColor, boolean recursive) {
        List<SqueezePattern> allSqueezePatternsOnBoard =
                SqueezePatternFinder.getAllSqueezePatternsOnBoard(attackingColor, boardConfiguration.getBoard());
        allSqueezePatternsOnBoard.addAll(SqueezePatternFinder.getAllSqueezePatternsOnBoard(attackingColor.getOpponentColor(), boardConfiguration.getBoard()));
        List<Tuple<CellData, CellData,Integer>>  possibleAttackingMoves =
                getPossibleAttackingMovesFromPattern(allSqueezePatternsOnBoard, boardConfiguration, attackingColor, recursive);
        if (!possibleAttackingMoves.isEmpty()) {
            return getAttackingMoves(possibleAttackingMoves, boardConfiguration, attackingColor);
        }
        return Collections.emptyList();
    }



    private List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> getAttackingMoves(List<Tuple<CellData, CellData,Integer>>  possibleMovesFromPattern,
                                                                                      BoardConfiguration boardConfiguration, PlayerColor attackingColor) {
        List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> attackingMoves = possibleMovesFromPattern.parallelStream().map(move -> {
            // Find all attacking moves among bestMoves that give me a really attack
            BoardConfiguration newBoard = boardConfiguration.clone();
            newBoard.setPiece(move);
            int removal = tryRemovePattern(move.getSecond(), newBoard, attackingColor);
            int estimateScore = AIUtils.getGlobalEstimate(boardConfiguration, attackingColor);
            return new Tuple<>(move, removal, estimateScore);
        }).collect(Collectors.toList());
        List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> realAttackingMoves =
                attackingMoves.stream().filter(tuple -> tuple.getSecond() > 0).collect(Collectors.toList());
        if (!realAttackingMoves.isEmpty()) {
            return realAttackingMoves;
        }
        return attackingMoves;
    }

    private List<Tuple<CellData, CellData,Integer>>  getPossibleAttackingMovesFromPattern(List<SqueezePattern> allSqueezePatternsOnBoard,
                                                                                BoardConfiguration boardConfiguration, PlayerColor computerColor
            , boolean recursive) {
        List<Tuple<CellData, CellData,Integer>> result = new ArrayList<>();
        allSqueezePatternsOnBoard.forEach(squeezePattern -> {
            SqueezePatternType patternType = squeezePattern.getPatternType();
            if (patternType.equals(SqueezePatternType.GAP) ||
                    patternType.equals(SqueezePatternType.INCOMPLETE_GAP)) {
                result.addAll(squeezePattern.findPossibleAttackingMoves(boardConfiguration.getBoard(), computerColor, recursive));
            }
        });
        return result;
    }
}
