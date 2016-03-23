package squeezeboard.controller.ai;

import squeezeboard.controller.pattern.SqueezePattern;
import squeezeboard.controller.pattern.SqueezePatternFinder;
import squeezeboard.controller.pattern.SqueezePatternType;
import squeezeboard.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static squeezeboard.model.GameUtils.tryRemovePattern;

/**
 * Created by zhangwei on 3/4/16.
 */
public class AIUtils {


//    public static List<SqueezePattern> findAllUsefulPatterns(PlayerColor playerColor, BoardConfiguration boardConfiguration) {
//        boardConfiguration.getBoard();
//    }

    public static List<CellData> findAllComputerPieces(PlayerColor computerColor, BoardConfiguration boardConfiguration) {
        List<CellData> result = new ArrayList<>();
        CellData[][] board = boardConfiguration.getBoard();
        for (CellData[] row : board) {
            for (CellData cell : row) {
                if (cell.getCellChar() == computerColor.CHAR()) {
                    result.add(cell);
                }
            }
        }
        return result;
    }

    public static List<Pair<CellData, CellData>> getAllPossibleMoves(List<CellData> allComputerPieces
            ,BoardConfiguration currentBoardConfiguration) {
        final List<Pair<CellData, CellData>> result = new ArrayList<>();

        allComputerPieces.stream().forEach(pickedCell -> {
            result.addAll(getAllPossibleMovesForACell(pickedCell, currentBoardConfiguration.getBoard()));
        });
        return result;
    }

    public static List<Pair<CellData, CellData>> getAllPossibleMovesForACell(CellData pickedCell, CellData[][] board) {
        final List<Pair<CellData, CellData>> result = new ArrayList<>();
        List<CellData> possMoves = new ArrayList<CellData>();
        //Checking vertically
        GameUtils.checkAndHighlight(pickedCell, board, 1, 0, possMoves);
        //Checking Horizontally
        GameUtils.checkAndHighlight(pickedCell, board, 0, 1, possMoves);
        GameUtils.removeHighlight(board);
        List<Pair<CellData, CellData>> collect = possMoves.stream().map(
                cellData -> new Pair<CellData, CellData>(pickedCell, cellData))
                .collect(Collectors.toList());
        result.addAll(collect);
        return result;
    }

    public static List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> selectBestMoves(List<Pair<CellData, CellData>> allPossibleMoves,
                                                                                BoardConfiguration currentBoardConfiguration,
                                                                                PlayerColor computerColor,
                                                                                Comparator<SqueezePattern> comparator){
        return allPossibleMoves.stream().map(move -> {
            BoardConfiguration clonedBoard = currentBoardConfiguration.clone();
            clonedBoard.setPiece(move);
            Optional<SqueezePattern> max = SqueezePatternFinder.findPattern(clonedBoard, move.getSecond(), computerColor)
                    .values().stream().flatMap(pattern -> pattern.stream())
                    .max((a, b) -> Double.compare(a.score(clonedBoard), b.score(clonedBoard)));

            int removalEstimate = max.isPresent()? Double.valueOf(max.get().score(clonedBoard) + 1.0d).intValue() : 0;

            Optional<SqueezePattern> maxOpponentRemoval = SqueezePatternFinder.findPattern(clonedBoard, move.getSecond(), computerColor.getOpponentColor())
                    .values().stream().flatMap(pattern -> pattern.stream())
                    //test if we can make any full filled gap
                    .filter(pattern -> pattern.getPatternType().equals(SqueezePatternType.FULFILLED_GAP))
                    .max((a, b) -> Integer.compare(a.validRemovalCount(), b.validRemovalCount()));
            int localDanger = maxOpponentRemoval.isPresent()? maxOpponentRemoval.get().validRemovalCount() : 0;
            int estimatingScore = removalEstimate - localDanger;

            return new Tuple<>(new Tuple<>(move.getFirst(), move.getSecond(), localDanger), removalEstimate, estimatingScore);

        }).sorted((o1, o2) -> Integer.compare(o1.getFirst().getThird(), o2.getFirst().getThird()))
                .limit(GameUtils.SEARCH_WIDTH).collect(Collectors.toList());

    }

    public static int getGlobalEstimate(BoardConfiguration boardConfiguration, PlayerColor playerColor) {
        int playEstimate = boardConfiguration.getNumberOfPieces(playerColor);
        int opponentEstimate = boardConfiguration.getNumberOfPieces(playerColor.getOpponentColor());
        return playEstimate - opponentEstimate;
    }

    public static int alphaBeta(int depth, int lowerBound, int upperBound,
                          BoardConfiguration boardConfiguration,
                          Function<Pair<BoardConfiguration, PlayerColor>,
                                  List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>>> func,
                          PlayerColor playerColor) {
        if (depth >= GameUtils.SEARCH_DEPTH) {
            return getGlobalEstimate(boardConfiguration, playerColor);
        } else {
            int alpha = lowerBound;
            int beta = upperBound;

            List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> moveList =
                    func.apply(new Pair<>(boardConfiguration, playerColor));

            for (Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer> moveInfo : moveList) {
                // for each attacking move made by the virtual player, copy a new configuration.
                BoardConfiguration newBoard = boardConfiguration.clone();
                // set pieces
                Tuple<CellData, CellData, Integer> move = moveInfo.getFirst();
                newBoard.setPiece(moveInfo.getFirst());
                int removal = tryRemovePattern(move.getSecond(), newBoard, playerColor);
                Pair<Integer, Integer> blue_orange = GameUtils.calculateLeftPiecesCount(newBoard);
                int moveCounter = GameUtils.currentCursor.get() + depth;
                PromptableException.ExceptFactor gameResult = GameUtils
                        .determineGameResult(moveCounter, blue_orange.getFirst(), blue_orange.getSecond());

                if ((depth & 1) == 0) { // even depth, 0, 2, 4, 8... Human's turn, minimize it's estimate.
                    if (gameResult != null) {
                        beta = Integer.MIN_VALUE;
                    } else {
                        int estimateScore = alphaBeta(depth + 1, alpha, beta, newBoard, func,
                                playerColor.getOpponentColor());
                        beta = Math.min(beta, estimateScore);
                    }
                    //newBoard = boardConfiguration;
                    if (beta <= alpha) {
                        return beta;
                    }
                } else { //odd depth, 1, 3, 5, 7... Computer's turn again, maximize it's estimate.
                    if (gameResult != null) {
                        alpha = Integer.MAX_VALUE;
                    } else {
                        int estimateScore = alphaBeta(depth + 1, alpha, beta, newBoard, func,
                                playerColor.getOpponentColor());
                        alpha = Math.max(alpha, estimateScore);
                    }
                    //newBoard = boardConfiguration;
                    if (alpha >= beta) {
                        return alpha;
                    }
                }
            }
            return (depth & 1) == 0 ? beta : alpha;
        }
    }

}
