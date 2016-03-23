package squeezeboard.controller.ai.minimax.global;

import squeezeboard.controller.ai.AIUtils;
import squeezeboard.controller.ai.SqueezeAI;
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

/**
 * Search through all possible moves globally, and try to simulate the removal, to see which one can
 * help you to eliminate the most number of opponent's pieces
 *
 *
 * Created by zhangwei on 3/4/16.
 */
public class GlobalMoveGenerator implements SqueezeAI {


    private static final Random RANDOM = new SecureRandom();

    @Override
    public List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> findOptimalMove(PlayerColor computerColor, BoardConfiguration currentBoardConfiguration) {
        final List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> result = new ArrayList<>();

        List<CellData> allComputerPieces = AIUtils.findAllComputerPieces(computerColor, currentBoardConfiguration);
        if (allComputerPieces.size() <= 0) {
            return result;
        }
        List<Pair<CellData, CellData>> allPossibleMoves = AIUtils.getAllPossibleMoves(allComputerPieces, currentBoardConfiguration);
        List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> bestMoves =
                AIUtils.selectBestMoves(allPossibleMoves, currentBoardConfiguration, computerColor,
                        (p1, p2) -> Double.compare(p1.score(currentBoardConfiguration), p2.score(currentBoardConfiguration)));
        if (!bestMoves.isEmpty()) {
            bestMoves.parallelStream().forEach(m -> {
                BoardConfiguration newBoard = currentBoardConfiguration.clone();
                Tuple<CellData, CellData, Integer> move = m.getFirst();
                newBoard.setPiece(move);
                int removal = GameUtils.tryRemovePattern(move.getSecond(), newBoard, computerColor);
                int estmateScore = AIUtils.alphaBeta(0, Integer.MIN_VALUE, Integer.MAX_VALUE, newBoard,
                        new Function<Pair<BoardConfiguration, PlayerColor>, List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>>>() {
                            @Override
                            public List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> apply(Pair<BoardConfiguration, PlayerColor> pair) {
                                List<CellData> allComputerPieces = AIUtils.findAllComputerPieces(pair.getSecond(), pair.getFirst());
                                if (allComputerPieces.size() <= 0) {
                                    return Collections.emptyList();
                                }
                                List<Pair<CellData, CellData>> allPossibleMoves = AIUtils.getAllPossibleMoves(allComputerPieces, pair.getFirst());
                                return AIUtils.selectBestMoves(allPossibleMoves, pair.getFirst(), pair.getSecond(),
                                        (p1, p2) -> Double.compare(p1.score(pair.getFirst()), p2.score(pair.getFirst())));
                            }
                        },
                        computerColor.getOpponentColor());
                m.setSecond(removal);
                m.setThird(estmateScore);
            });
            Optional<Integer> maxScore = bestMoves.stream().map(move -> move.getThird())
                    .max((o1, o2) -> Integer.compare(o1, o2));
            int bestEstimate = maxScore.isPresent() ? maxScore.get() : Integer.MIN_VALUE;
            bestMoves.stream().filter(move -> move.getThird() >= bestEstimate)
                    .sorted((a, b) -> Integer.compare(b.getSecond(), a.getSecond()))
                    .limit(GameUtils.SEARCH_WIDTH / 2).forEach(each -> result.add(each));
        } else {
            Pair<CellData, CellData> move = allPossibleMoves.get(RANDOM.nextInt(allPossibleMoves.size()));
            result.add(new Tuple<>(new Tuple<>(move.getFirst(), move.getSecond(), 0), 0,0));
        }
        System.out.println(this.getClass().getSimpleName()+" got result = " + result.size());
        System.out.println("==================================================");
        return result;
    }

}
