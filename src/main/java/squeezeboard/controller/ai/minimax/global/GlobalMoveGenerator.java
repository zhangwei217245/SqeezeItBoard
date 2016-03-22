package squeezeboard.controller.ai.minimax.global;

import squeezeboard.controller.ai.AIUtils;
import squeezeboard.controller.ai.SqueezeAI;
import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import squeezeboard.model.GameUtils;
import squeezeboard.model.Pair;
import squeezeboard.model.PlayerColor;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Search through all possible moves globally, and try to simulate the removal, to see which one can
 * help you to eliminate the most number of opponent's pieces
 *
 *
 * Created by zhangwei on 3/4/16.
 */
public class GlobalMoveGenerator implements SqueezeAI {


    private static final Random RANDOM = new SecureRandom();

    private static final int MAX_SCORE = 1000610; //(10000 + max gap capacity)*100 + 10

    private static final int MIN_SCORE = 9990; //(100 + least incomplete gap score 0 ) * 100 - 10

    @Override
    public Pair<CellData, CellData> findOptimalMove(PlayerColor computerColor, BoardConfiguration currentBoardConfiguration) {
        List<CellData> allComputerPieces = AIUtils.findAllComputerPieces(computerColor, currentBoardConfiguration);
        if (allComputerPieces.size() <= 0) {
            return null;
        }
        List<Pair<CellData, CellData>> allPossibleMoves = AIUtils.getAllPossibleMoves(allComputerPieces, currentBoardConfiguration);
        List<Pair<Pair<CellData, CellData>, Integer>> bestMoves =
                AIUtils.selectBestMoves(allPossibleMoves, currentBoardConfiguration, computerColor,
                        (p1, p2) -> Double.compare(p1.score(currentBoardConfiguration), p2.score(currentBoardConfiguration)));
        bestMoves.parallelStream().forEach(pairIntegerPair -> {
            BoardConfiguration newBoard = currentBoardConfiguration.clone();
            Pair<CellData, CellData> move = pairIntegerPair.getFirst();
            newBoard.setPiece(move);
            int removal = GameUtils.tryRemovePattern(pairIntegerPair.getFirst().getSecond(), newBoard, computerColor);
            int estmateScore = removal > 0 ? removal
                    : this.alphaBetaPruning(0, Integer.MIN_VALUE, Integer.MAX_VALUE, newBoard, move,
                    computerColor.getOpponentColor());
            pairIntegerPair.setSecond(estmateScore);
        });
        Optional<Integer> maxScore = bestMoves.stream().map(pairIntegerPair -> pairIntegerPair.getSecond())
                .max((o1, o2) -> Integer.compare(o1, o2));
        int bestEstimate = maxScore.isPresent() ? maxScore.get() : Integer.MIN_VALUE;
        //System.out.println("bestMoves.size = " + bestMoves.size());
        List<Pair<Pair<CellData, CellData>, Integer>> resultList = bestMoves.stream()
                .filter(pairIntegerPair -> bestEstimate == pairIntegerPair.getSecond()).collect(Collectors.toList());
        if (resultList != null && resultList.size() == 0) {
            return null;
        }
        return resultList.get(RANDOM.nextInt(resultList.size())).getFirst();
    }



    private int alphaBetaPruning(int depth, int lowerBound, int upperBound,
                                 BoardConfiguration newBoard, Pair<CellData, CellData> move,
                                 PlayerColor newColor) {
        List<Pair<Pair<CellData, CellData>, Integer>> bestMoves =
                    AIUtils.selectBestMoves(AIUtils.getAllPossibleMovesForACell(move.getSecond(), newBoard)
                    , newBoard, newColor, (p1, p2) -> Double.compare(p1.score(newBoard), p2.score(newBoard)));

        newBoard.setPiece(move);
        int removal = GameUtils.tryRemovePattern(move.getSecond(), newBoard, newColor);

        if (depth >= GameUtils.SEARCH_DEPTH) {
            return removal;
        } else {
            final int[] result = {0};
            final int[] alpha = {lowerBound};
            final int[] beta = {upperBound};
            
            for (Pair<Pair<CellData, CellData>, Integer> pairIntegerPair : bestMoves) {
                if (pairIntegerPair.getFirst() == null) {
                    break;
                }

                if ((depth & 1) == 0) { // MIN
                    if (removal == 0) {
                        beta[0] = Integer.MIN_VALUE;
                    } else {
                        beta[0] = Math.min(beta[0], this.alphaBetaPruning(depth+1, alpha[0], beta[0], newBoard.clone(),
                              pairIntegerPair.getFirst(), newColor.getOpponentColor()));
                        if (beta[0] <= alpha[0]){
                            return beta[0];
                        }
                    }
                } else { // MAX
                    if (removal == 0) {
                        alpha[0] = Integer.MAX_VALUE;
                    } else {
                        alpha[0] = Math.max(alpha[0], this.alphaBetaPruning(depth+1, alpha[0], beta[0], newBoard.clone(),
                                pairIntegerPair.getFirst(), newColor.getOpponentColor()));
                        if (alpha[0] >= beta[0]){
                            return alpha[0];
                        }
                    }

                }

            };
            return (depth & 1) == 0? beta[0] : alpha[0];
        }
    }




    



}
