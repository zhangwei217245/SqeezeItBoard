package squeezeboard.controller.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import squeezeboard.model.CellData;
import squeezeboard.model.GameUtils;
import squeezeboard.model.PatternDirection;
import squeezeboard.model.PlayerColor;

/**
 *
 * @author zhangwei
 */
public class SqueezePatternFinder {

    public List<SqueezePattern> findPattern(CellData piece, char currentColor, PatternDirection direction) {
        CellData[][] board = GameUtils.getCurrentBoard();
        int dimension = GameUtils.getCurrentBoardConfiguration().getDimension();
        // extracting string from current board.
        return getAllPatterns(board, piece, dimension, currentColor, direction);
    }
    
    private List<SqueezePattern> getAllPatterns(CellData[][] board, CellData piece, int dimension, 
            char currentColor, PatternDirection direction){
        int groupIndex = PatternDirection.VERTICAL.equals(direction)?
                piece.getColCord():piece.getRowCord();
        int currentPos = PatternDirection.VERTICAL.equals(direction)?
                piece.getRowCord():piece.getColCord();
        CellData[] group = new CellData[dimension];
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < dimension; i++) {
            if (i == currentPos) {
                sb.append(currentColor);
                continue;
            }
            if (PatternDirection.VERTICAL.equals(direction)) {
                group[i] = board[i][groupIndex];
                
            } else if (PatternDirection.HORIZONTAL.equals(direction)) {
                group[i] = board[groupIndex][i];
            }
            sb.append(group[i].getCellChar());
        }
        String patternStr = sb.toString();
        
        PlayerColor patternColor = PlayerColor.getColorByChar(currentColor);
        
        List<SqueezePattern> patterns = new ArrayList<>();
        // Getting Gap Pattern
        patterns.addAll(getPatterns(patternStr, patternColor, 
                group, SqueezePatternType.GAP, direction));
        // Getting Consecutive Pattern
        patterns.addAll(getPatterns(patternStr, patternColor, 
                group, SqueezePatternType.CONSECUTIVE, direction));
        
        return patterns;
    }
    
    private List<SqueezePattern> getPatterns(String patternStr, PlayerColor patternColor, 
            CellData[] group, SqueezePatternType patternType, PatternDirection direction){
        List<SqueezePattern> listPatterns = new ArrayList<>();
        Pattern pattern = patternColor.getGapPattern();
        if (SqueezePatternType.CONSECUTIVE.equals(patternType)) {
            pattern = patternColor.getConsecutivePattern();
        }
        Matcher matcher = pattern.matcher(patternStr);
        int start = 0;
        int end = 0;
        while (matcher.find(start)) {
            start = matcher.toMatchResult().start();
            end = matcher.toMatchResult().end();
            CellData[] patternBlocks = Arrays.copyOfRange(group, start, end);
            listPatterns.add(new SqueezePattern(patternBlocks, 
                    patternType, patternColor, direction));
            start = end;
        }
        return listPatterns;
    }

    public static void main(String[] args) {
        Matcher matcherB = PlayerColor.blue.getGapPattern().matcher("EBEBBOB");

        int start = 0;
        int end = 0;
        while (matcherB.find(start)) {
            start = matcherB.toMatchResult().start();
            end = matcherB.toMatchResult().end();
            System.out.println(start + "," + end);
            start = end;
        }
        
        Matcher matcherO = PlayerColor.orange.getGapPattern().matcher("OEOEOBO");
        start = 0;
        end = 0;
        while (matcherO.find(start)) {
            start = matcherO.toMatchResult().start();
            end = matcherO.toMatchResult().end();
            System.out.println(start + "," + end);
            start = end;
        }
        
        Matcher consecB = PlayerColor.blue.getConsecutivePattern().matcher("BEBBOBB");
        start = 0;
        end = 0;
        while (consecB.find(start)) {
            start = consecB.toMatchResult().start();
            end = consecB.toMatchResult().end();
            System.out.println(start + "," + end);
            start = end;
        }
        
        
        Matcher consecO = PlayerColor.orange.getConsecutivePattern().matcher("BEBBOBB");
        start = 0;
        end = 0;
        while (consecO.find(start)) {
            start = consecO.toMatchResult().start();
            end = consecO.toMatchResult().end();
            System.out.println(start + "," + end);
            start = end;
        }
    }
}
