package squeezeboard.controller.pattern;

import squeezeboard.model.CellData;
import squeezeboard.model.PlayerColor;

/**
 *
 * @author zhangwei
 */
public class SqueezePattern {
    
    private CellData[] pattern;
    
    private SqueezePatternType patternType;
    
    private PlayerColor patternCreator;

    public SqueezePattern(CellData[] pattern, SqueezePatternType patternType, PlayerColor patternCreator) {
        this.pattern = pattern;
        this.patternType = patternType;
        this.patternCreator = patternCreator;
    }

    public CellData[] getPattern() {
        return pattern;
    }

    public SqueezePatternType getPatternType() {
        return patternType;
    }

    public PlayerColor getPatternCreator() {
        return patternCreator;
    }
}
