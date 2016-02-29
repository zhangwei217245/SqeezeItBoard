package squeezeboard.controller.pattern;

import squeezeboard.model.CellData;
import squeezeboard.model.PlayerColor;

/**
 *
 * @author zhangwei
 */
public class SqueezePattern {
    
    private CellData[] pattern;
    
    private PlayerColor patternCreator;
    
    

    public CellData[] getPattern() {
        return pattern;
    }

    public void setPattern(CellData[] pattern) {
        this.pattern = pattern;
    }

    public PlayerColor getPatternCreator() {
        return patternCreator;
    }

    public void setPatternCreator(PlayerColor patternCreator) {
        this.patternCreator = patternCreator;
    }
    
    public SqueezePattern(CellData[] pattern, PlayerColor patternCreator) {
        this.pattern = pattern;
        this.patternCreator = patternCreator;
    }
    
    public PatternDirection getDirection(){
        if (pattern[0].getRowCord() == pattern[1].getRowCord()) {
            return PatternDirection.HORIZONTAL;
        } else if (pattern[0].getColCord()== pattern[1].getColCord()) {
            return PatternDirection.VERTICAL;
        }
        return null;
    }
    
    public enum PatternDirection {
        VERTICAL,
        HORIZONTAL
    }
    
    
}
