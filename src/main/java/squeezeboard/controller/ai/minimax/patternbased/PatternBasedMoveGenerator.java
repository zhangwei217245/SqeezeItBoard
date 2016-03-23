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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static squeezeboard.model.GameUtils.SEARCH_WIDTH;
import static squeezeboard.model.GameUtils.tryRemovePattern;

/**
 * Created by zhangwei on 3/13/16.
 */
public class PatternBasedMoveGenerator implements SqueezeAI {

    private static final Random RANDOM = new SecureRandom();

    @Override
    public List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> findOptimalMove(PlayerColor computerColor, BoardConfiguration boardConfiguration) {
        final List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> result = new ArrayList<>();

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

            List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> aggressiveAttacks = null;
            if (mostRemoval > 0) {
                //if removal happens immediately, take those who gave the most number of removal.
                aggressiveAttacks = movesWithRank.stream()
                        .filter(move -> mostRemoval == move.getSecond()).collect(Collectors.toList());
            } else {
                // if removal doesn't happen immediately, take those who is from the shallowest level.
                int minDepth = movesWithRank.stream()
                        .map(m -> m.getFirst().getThird()).min((a, b) -> Integer.compare(a, b)).get();
                aggressiveAttacks = movesWithRank.stream()
                        .filter(move -> minDepth == move.getFirst().getThird())
                        //need to guarantee that the estimation is larger than the number of detour times.
                        .map(move -> {move.setSecond(move.getThird() - move.getFirst().getThird());return move;})
                        .collect(Collectors.toList());
            }

            // defensive attacks would be those who has the maximum estimating score.
            List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> defensiveAttacks = movesWithRank.stream()
                    .filter(pair -> bestEstimate == pair.getThird()).collect(Collectors.toList());

            Set<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> bestMoves = new HashSet<>();
            //bestMoves.addAll(aggressiveAttacks);
            defensiveAttacks.stream().filter(move -> move.getThird() == bestEstimate).forEach(move -> bestMoves.add(move));
            aggressiveAttacks.stream().filter(move -> move.getSecond() == mostRemoval).forEach(move -> bestMoves.add(move));

            if (bestMoves.size() >= 1) {
                result.addAll(bestMoves);
                System.out.println("Most aggressive attacking move with largest defensive score found!");
            } else if (attackingMoves.size() >= 1) {
                result.addAll(attackingMoves);
                System.out.println("Most aggressive attacking move found!");
            } else {
                result.addAll(defensiveAttacks);
                System.out.println("Most defensive attacking move found!");
            }
        }

        List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> finalResult =
                result.stream().map(item -> {
                    int danger = GameUtils.findDangerForPlayer(item.getFirst().getSecond(), boardConfiguration, computerColor);
                    if (danger > 0) {
                        item.setThird(item.getThird() - danger);
                        item.setSecond(item.getSecond() - danger);
                    }
                    return item;
                }).sorted((a, b) -> (boardConfiguration.getNumberOfPieces(computerColor.getOpponentColor()) <= 4) ?
                        (Integer.compare(b.getSecond(), a.getSecond())) // when opponent has more than 4 marbles, be more careful
                        :(Integer.compare(b.getThird(), a.getThird())) //when opponent has less than or equal to 4 marbles, become more aggressive
                )
                .limit(SEARCH_WIDTH / 2).collect(Collectors.toList());
        System.out.println(this.getClass().getSimpleName()+" got result = " + result.size());
        System.out.println("==================================================");
        return finalResult;
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
