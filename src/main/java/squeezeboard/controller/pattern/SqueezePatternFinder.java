package squeezeboard.controller.pattern;

import squeezeboard.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 *
 * @author zhangwei
 */
public class SqueezePatternFinder {

    /**
     * API for you to find the pattern in the specified boardConfiguration based on the cell
     * to which you want to move the piece of currentColor.
     * @param boardConfiguration
     * @param piece
     * @param currentColor
     * @return
     */
    public static Map<SqueezePatternType, List<SqueezePattern>> findPattern(
            BoardConfiguration boardConfiguration,
            CellData piece, PlayerColor currentColor) {
        Map<SqueezePatternType, List<SqueezePattern>> result = new HashMap<>();
        CellData[][] board = boardConfiguration.getBoard();
        int dimension = boardConfiguration.getDimension();
        // extracting string from current board.
        for (SqueezePatternType patternType : SqueezePatternType.values()) {
            List<SqueezePattern> patternsInType = getSqueezePatterns(patternType, currentColor, piece, board, dimension);
            result.put(patternType, patternsInType);
        }
        return result;
    }

    public static List<SqueezePattern> getSqueezePatterns(SqueezePatternType patternType, PlayerColor patternColor, CellData piece,
                                                          CellData[][] board, int dimension) {
        List<SqueezePattern>  patternsInType = getSqueezePatterns(patternType, patternColor,
                piece, board, dimension, false);
        patternsInType.addAll(getSqueezePatterns(patternType, patternColor,
                piece, board, dimension, true));
        return patternsInType;
    }


    public static List<SqueezePattern> getAllSqueezePatternsOnBoard(PlayerColor patternColor, CellData[][] board) {
        List<SqueezePattern> squeezePatterns = new ArrayList<>();
        for (PatternDirection direction : PatternDirection.values()) {
            direction.getGroupStrList(board).forEach(pair -> {
                for (SqueezePatternType patternType : SqueezePatternType.values()) {
                    Matcher matcher = patternType.getPattern(patternColor).matcher(pair.getFirst());
                    int start = 0;
                    int end = 0;
                    while (matcher.find(start)) {
                        start = matcher.toMatchResult().start();
                        end = matcher.toMatchResult().end() - 1;
                        CellData startCell = direction.getCellInAGroup(start, pair.getSecond(), board);
                        CellData endCell = direction.getCellInAGroup(end, pair.getSecond(), board);
                        //System.out.println(String.format("%s, %s", start, end));
                        Pair<CellData, CellData> bothEnds = new Pair<>(startCell, endCell);
                        String patternStr = getPatternStr(bothEnds, board, direction);
                        SqueezePattern squeezePattern = new SqueezePattern(bothEnds, patternStr, patternType, patternColor,
                                direction);
                        squeezePatterns.add(squeezePattern);
                        start = end;
                    }
                }

            });
        }
        return squeezePatterns;
    }

    public static List<SqueezePattern> getSqueezePatterns(SqueezePatternType patternType, PlayerColor patternColor, CellData piece,
                                                           CellData[][] board, int dimension, boolean isConsecutive) {
        List<SqueezePattern> squeezePatterns = new ArrayList<>();
        PlayerColor gapColor = isConsecutive? patternColor.getOpponentColor(): patternColor;
        for (PatternDirection direction : PatternDirection.values()) {
            Matcher matcher = patternType.getPattern(gapColor).matcher(getGroupStr(piece, board, dimension, direction));
            int start = 0;
            int end = 0;
            while (matcher.find(start)) {
                start = matcher.toMatchResult().start();
                end = matcher.toMatchResult().end() - 1;
                CellData startCell = direction.getCellInAGroup(start, piece, board);
                CellData endCell = direction.getCellInAGroup(end, piece, board);
                //System.out.println(String.format("%s, %s", start, end));
                Pair<CellData, CellData> bothEnds = new Pair<>(startCell, endCell);
                String patternStr = getPatternStr(bothEnds, board, direction);
                SqueezePattern squeezePattern = new SqueezePattern(bothEnds, patternStr, patternType, patternColor,
                        direction);
                squeezePatterns.add(squeezePattern);
                start = end;
            }
        }
        return squeezePatterns;
    }

    private static String getPatternStr(Pair<CellData, CellData> bothEnds, CellData[][] board, PatternDirection direction) {
        StringBuilder sb = new StringBuilder();
        int start = direction.getIndexInAGroup(bothEnds.getFirst());
        int end = direction.getIndexInAGroup(bothEnds.getSecond());
        for (int i = start; i <= end; i++) {
            sb.append(direction.getCellInAGroup(i, bothEnds.getFirst(), board).getCellChar());
        }
        return sb.toString();
    }

    private static String getGroupStr (CellData piece, CellData[][] board, int dimension, PatternDirection direction){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dimension; i++) {
            CellData cell = direction.getCellInAGroup(i,piece,board);
            sb.append(cell.getCellChar());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Matcher matcherB = PlayerColor.blue.getGapPattern().matcher("EBEBBOB");

        int start = 0;
        int end = 0;
        while (matcherB.find(start)) {
            start = matcherB.toMatchResult().start();
            end = matcherB.toMatchResult().end();
            System.out.println(start + "," + end);
            start = end - 1;
        }
        
        Matcher matcherO = PlayerColor.orange.getGapPattern().matcher("OEOBEOBO");
        start = 0;
        end = 0;
        while (matcherO.find(start)) {
            start = matcherO.toMatchResult().start();
            end = matcherO.toMatchResult().end();
            System.out.println(start + "," + end);
            start = end - 1;
        }

        matcherB = PlayerColor.blue.getGapPattern().matcher("EBOOOBE");

        start = 0;
        end = 0;
        while (matcherB.find(start)) {
            start = matcherB.toMatchResult().start();
            end = matcherB.toMatchResult().end();
            System.out.println(start + "," + end);
            start = end;
        }

        matcherO = PlayerColor.orange.getGapPattern().matcher("EOBBBBOE");
        start = 0;
        end = 0;
        while (matcherO.find(start)) {
            start = matcherO.toMatchResult().start();
            end = matcherO.toMatchResult().end();
            System.out.println(start + "," + end);
            start = end;
        }

//        Matcher consecB = PlayerColor.blue.getIncompleteGapPattern().matcher("BEBBOBB");
//        start = 0;
//        end = 0;
//        while (consecB.find(start)) {
//            start = consecB.toMatchResult().start();
//            end = consecB.toMatchResult().end();
//            System.out.println(start + "," + end);
//            start = end;
//        }
//
//
//        Matcher consecO = PlayerColor.orange.getIncompleteGapPattern().matcher("BEBBOBB");
//        start = 0;
//        end = 0;
//        while (consecO.find(start)) {
//            start = consecO.toMatchResult().start();
//            end = consecO.toMatchResult().end();
//            System.out.println(start + "," + end);
//            start = end;
//        }
    }
}
