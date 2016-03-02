package squeezeboard.controller.pattern;

import squeezeboard.model.CellData;
import squeezeboard.model.PatternDirection;
import squeezeboard.model.PlayerColor;

/**
 *
 * @author zhangwei
 */
public class SqueezePattern {
    
    private final CellData[] pattern;
    
    private final SqueezePatternType patternType;
    
    private final PlayerColor patternCreator;
    
    private final PatternDirection patternDirection;

    public SqueezePattern(CellData[] pattern, SqueezePatternType patternType, 
            PlayerColor patternCreator, PatternDirection patternDirection) {
        this.pattern = pattern;
        this.patternType = patternType;
        this.patternCreator = patternCreator;
        this.patternDirection = patternDirection;
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

    public PatternDirection getPatternDirection() {
        return patternDirection;
    }
    
    public double score(){
        return this.patternType.score(this);
    }
    
    public double eliminating_2(){
        return this.patternType.eliminating_2(this);
    }
    
    public double eliminating_consecutives() {
        return this.patternType.eliminating_consecutive(this);
    }
    
    public int tryEliminate(){
        return this.patternType.tryEliminate(this);
    }
    
}
