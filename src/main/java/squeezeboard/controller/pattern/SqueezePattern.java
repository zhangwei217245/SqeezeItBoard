package squeezeboard.controller.pattern;

import squeezeboard.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhangwei
 */
public class SqueezePattern {
    
    private final Pair<CellData, CellData> patternBothEnds;

    private final String patternStr;
    
    private final SqueezePatternType patternType;
    
    private final PlayerColor patternCreator;
    
    private final PatternDirection patternDirection;

    public SqueezePattern(Pair<CellData, CellData> patternBothEnds, String patternStr, SqueezePatternType patternType,
                          PlayerColor patternCreator, PatternDirection patternDirection) {

        this.patternBothEnds = patternBothEnds;
        this.patternStr = patternStr;
        this.patternType = patternType;
        this.patternCreator = patternCreator;
        this.patternDirection = patternDirection;
    }

    public SqueezePatternType getPatternType() {
        return patternType;
    }

    public PlayerColor getPatternCreator() {
        return patternCreator;
    }

    public PatternDirection getPatternDirection() {
        return patternDirection;
    }

    public Pair<CellData, CellData> getPatternBothEnds() {
        return patternBothEnds;
    }

    public boolean isEliminatable(){
        return SqueezePatternType.FULFILLED_GAP
                .getPattern(this.getPatternCreator())
                .matcher(this.patternStr).matches();
    }

    public int validRemovalCount() {
        int removal = 0;
        PlayerColor opponentColor = this.patternCreator.getOpponentColor();
        for (char c : patternStr.toCharArray()) {
            if (c == opponentColor.CHAR()) {
                removal++;
            }
        }
        return removal;
    }

    public int emptyCount() {
        int empty = 0;
        for (char c : patternStr.toCharArray()) {
            if (c == 'E' || c == 'P') {
                empty++;
            }
        }
        return empty;
    }

    public int size() {
        return this.patternStr.length();
    }

    public int capacity() {
        if (this.getPatternType().equals(SqueezePatternType.INCOMPLETE_GAP)) {
            //FIXME: for incomplete gap, the capacity should be redefined.
            return this.size() - 1;
        } else {
            return this.size() - 2;
        }
    }

    public int tryEliminate(CellData newpiece, CellData[][] boardToCarryoutRemoval) {
        // if it is not a fulfilled gap, never try to remove anything.
        int removal = 0;
        if (!this.patternType.equals(SqueezePatternType.FULFILLED_GAP)){
            return removal;
        }
        PlayerColor opponentColor = this.patternCreator.getOpponentColor();

        int start = this.patternDirection.getIndexInAGroup(this.patternBothEnds.getFirst());
        int end = this.patternDirection.getIndexInAGroup(this.patternBothEnds.getSecond());
        for (int i = start; i <= end; i++) {
            CellData cell = this.patternDirection.getCellInAGroup(i, newpiece, boardToCarryoutRemoval);
            if (cell.getCellChar() == opponentColor.CHAR()) {
                cell.setCellChar('E');
                removal++;
            }
        }
        return removal;
    }

    public double score(BoardConfiguration boardConfiguration){
        return this.getPatternType().score(this, boardConfiguration);
    }

    public List<Pair<CellData, CellData>> findPossibleAttackingMoves(CellData[][] board, PlayerColor playerColor) {
        List<Pair<CellData, CellData>> allMoves = new ArrayList<>();
        for (PatternDirection direction : PatternDirection.values()) {
            allMoves.addAll(direction.findPossibleAttackingMoves(this, board, playerColor));
        }
        return allMoves;
    }

    public List<Pair<CellData, CellData>> findPossibleDefensiveMoves(CellData[][] board, PlayerColor playerColor) {
        List<Pair<CellData, CellData>> allMoves = new ArrayList<>();
        for (PatternDirection direction : PatternDirection.values()) {
            allMoves.addAll(direction.findPossibleDefensiveMoves(this, board, playerColor));
        }
        return allMoves;
    }

    @Override
    public String toString() {
        return "SqueezePattern{" +
                "patternBothEnds=" + patternBothEnds +
                ", patternStr='" + patternStr + '\'' +
                ", patternType=" + patternType +
                ", patternCreator=" + patternCreator +
                ", patternDirection=" + patternDirection +
                '}';
    }
}
