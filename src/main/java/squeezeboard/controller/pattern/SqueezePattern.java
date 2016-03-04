package squeezeboard.controller.pattern;

import squeezeboard.model.CellData;
import squeezeboard.model.Pair;
import squeezeboard.model.PatternDirection;
import squeezeboard.model.PlayerColor;

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

    public boolean isEliminatable(){
        return SqueezePatternType.FULFILLED_GAP.getPattern(this.getPatternCreator()).matcher(this.patternStr).matches();
    }
    

    
}
