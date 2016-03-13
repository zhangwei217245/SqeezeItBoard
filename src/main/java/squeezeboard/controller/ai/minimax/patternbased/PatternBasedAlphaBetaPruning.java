package squeezeboard.controller.ai.minimax.patternbased;

import squeezeboard.controller.ai.AIUtils;
import squeezeboard.controller.ai.SqueezeAI;
import squeezeboard.controller.pattern.SqueezePatternType;
import squeezeboard.model.*;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by zhangwei on 3/4/16.
 */
public class PatternBasedAlphaBetaPruning implements SqueezeAI {


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
                AIUtils.selectBestMoves(allPossibleMoves, currentBoardConfiguration, computerColor);
        bestMoves.parallelStream().forEach(pairIntegerPair -> {
            BoardConfiguration newBoard = currentBoardConfiguration.clone();
            Pair<CellData, CellData> move = pairIntegerPair.getFirst();
            newBoard.setPiece(move);
            int removal = GameUtils.tryRemovePattern(pairIntegerPair.getFirst().getSecond(), newBoard, computerColor);

            int estmateScore = removal > 0 ? (int)(removal + SqueezePatternType.FULFILLED_GAP.baseScore) * 100
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
                    , newBoard, newColor);
        if (depth >= GameUtils.SEARCH_DEPTH) {
            
            Optional<Pair<Pair<CellData, CellData>, Integer>> bestMove = null;
            if ((depth & 1) == 0) {
                bestMove = bestMoves.stream().min((o1, o2) -> Integer.compare(o1.getSecond(), o2.getSecond()));
            } else {
                bestMove = bestMoves.stream().max((o1, o2) -> Integer.compare(o1.getSecond(), o2.getSecond()));
            }
            
            if (bestMove.isPresent()) {
                return bestMove.get().getSecond();
            }
            return AIUtils.globalEstimate(newBoard, newColor);
        } else {
            final int[] result = {0};
            final int[] alpha = {lowerBound};
            final int[] beta = {upperBound};
            
            for (Pair<Pair<CellData, CellData>, Integer> pairIntegerPair : bestMoves) {
                if (pairIntegerPair.getFirst() == null) {
                    break;
                }
                newBoard.setPiece(pairIntegerPair.getFirst());
                int removal = GameUtils.tryRemovePattern(pairIntegerPair.getFirst().getSecond(), newBoard, newColor);
                int threateningScore = (int)(removal + SqueezePatternType.FULFILLED_GAP.baseScore) * 100;

                if ((depth & 1) == 0) { // MIN
                    if (removal > 0) {
                        return -threateningScore;
                    }
                    Pair<Integer, Integer> blue_orange = GameUtils.calculateLeftPiecesCount(newBoard);
                    int moveCounter = GameUtils.currentCursor.get() + depth;
                    if (moveCounter >= GameUtils.MAXIMUM_MOVES * 2) {
                        beta[0] = -MAX_SCORE;
                        break;
                    }
                    PromptableException.ExceptFactor gameResult = GameUtils
                            .determineGameResult(moveCounter, blue_orange.getFirst(), blue_orange.getSecond());
                    if (gameResult != null) {
                        beta[0] = -MAX_SCORE;
                    } else {
                        beta[0] = Math.min(beta[0], this.alphaBetaPruning(depth+1, alpha[0], beta[0], newBoard.clone(),
                              pairIntegerPair.getFirst(), newColor.getOpponentColor()));
                        // undo set Chess.
                        if (beta[0] <= alpha[0]){
                            return beta[0];
                        }
                    }
                } else { // MAX
                    if (removal > 0) {
                        return threateningScore;
                    }
                    Pair<Integer, Integer> blue_orange = GameUtils.calculateLeftPiecesCount(newBoard);
                    int moveCounter = GameUtils.currentCursor.get() + depth;
                    if (moveCounter >= GameUtils.MAXIMUM_MOVES * 2) {
                        alpha[0] = MAX_SCORE;
                        break;
                    }
                    PromptableException.ExceptFactor gameResult = GameUtils
                            .determineGameResult(moveCounter, blue_orange.getFirst(), blue_orange.getSecond());
                    if (gameResult != null) {
                        alpha[0] = MAX_SCORE;
                    } else {
                        alpha[0] = Math.max(alpha[0], this.alphaBetaPruning(depth+1, alpha[0], beta[0], newBoard,
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
