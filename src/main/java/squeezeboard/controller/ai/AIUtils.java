package squeezeboard.controller.ai;

import squeezeboard.controller.pattern.SqueezePattern;
import squeezeboard.controller.pattern.SqueezePatternFinder;
import squeezeboard.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by zhangwei on 3/4/16.
 */
public class AIUtils {


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
            result.addAll(getAllPossibleMovesForACell(pickedCell, currentBoardConfiguration));
        });
        return result;
    }

    public static List<Pair<CellData, CellData>> getAllPossibleMovesForACell(CellData pickedCell, BoardConfiguration currentBoardConfiguration) {
        final List<Pair<CellData, CellData>> result = new ArrayList<>();
        List<CellData> possMoves = new ArrayList<CellData>();
        //Checking vertically
        GameUtils.checkAndHighlight(pickedCell, currentBoardConfiguration.getBoard(), 1, 0, possMoves);
        //Checking Horizontally
        GameUtils.checkAndHighlight(pickedCell, currentBoardConfiguration.getBoard(), 0, 1, possMoves);
        GameUtils.removeHighlight(currentBoardConfiguration);
        List<Pair<CellData, CellData>> collect = possMoves.stream().map(
                cellData -> new Pair<CellData, CellData>(pickedCell, cellData))
                .collect(Collectors.toList());
        result.addAll(collect);
        return result;
    }

    public static List<Pair<Pair<CellData, CellData>, Integer>> selectBestMoves(List<Pair<CellData, CellData>> allPossibleMoves,
                                                                                BoardConfiguration currentBoardConfiguration,
                                                                                PlayerColor computerColor,
                                                                                Comparator<SqueezePattern> comparator){
        return allPossibleMoves.stream().map(move -> {
            BoardConfiguration clonedBoard = currentBoardConfiguration.clone();
            clonedBoard.setPiece(move);
            List<SqueezePattern> patternsToBeEvaluated = new ArrayList<SqueezePattern>();
            SqueezePatternFinder.findPattern(clonedBoard, move.getSecond(), computerColor)
                    .values().stream().forEach( list -> patternsToBeEvaluated.addAll(list));

            Optional<SqueezePattern> maxPattern = patternsToBeEvaluated.stream().max(comparator);
            //Optional<SqueezePattern> maxPattern = patternsToBeEvaluated.stream().max((o1, o2) ->
            //Double.compare(o1.score(), o2.score()));

            if (maxPattern.isPresent()) {
                return new Pair<Pair<CellData, CellData>, Integer>(move, (int)(maxPattern.get().score() * 100d));
            }
            return new Pair<Pair<CellData, CellData>, Integer>(move, Integer.MIN_VALUE);
        }).sorted((o1, o2) -> Integer.compare(o2.getSecond(), o1.getSecond()))
                .limit(GameUtils.SEARCH_WIDTH).collect(Collectors.toList());

    }

    public static int globalEstimate(BoardConfiguration newboard, PlayerColor color) {
        Pair<Integer, Integer> blue_orange = GameUtils.calculateLeftPiecesCount(newboard);
        int diff = color.equals(PlayerColor.blue) ? blue_orange.getFirst() - blue_orange.getSecond()
                : blue_orange.getSecond() - blue_orange.getFirst();
        return diff + 1000000;
    }

}
