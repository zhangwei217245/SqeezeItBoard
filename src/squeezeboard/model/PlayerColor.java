package squeezeboard.model;

import java.util.regex.Pattern;
import javafx.scene.paint.Color;

/**
 *
 * @author zhangwei
 */
public enum PlayerColor {
    
    orange('O', Color.web("0xff9900"),Pattern.compile("O[EB]+O"),
            Pattern.compile("O[B]+O"), Pattern.compile("[O]+")),
    
    blue('B', Color.web("0x0099ff"), Pattern.compile("B[EO]+B"),
            Pattern.compile("B[O]+B"), Pattern.compile("[B]+"));
   
    private char CHAR;
    
    private Color color;
    
    private Pattern gapPattern;
    
    private Pattern fullGapPattern;
    
    private Pattern consecutivePattern;

    private PlayerColor(char CHAR, Color color, Pattern gapPattern, 
            Pattern fullGapPattern,
            Pattern consecutivePattern) {
        this.CHAR = CHAR;
        this.color = color;
        this.gapPattern = gapPattern;
        this.fullGapPattern = fullGapPattern;
        this.consecutivePattern = consecutivePattern;
    }
    
    public char CHAR(){
        return this.CHAR;
    }

    public Color getColor() {
        return color;
    }

    public Pattern getGapPattern() {
        return gapPattern;
    }

    public Pattern getFullGapPattern() {
        return fullGapPattern;
    }
    
    public Pattern getConsecutivePattern() {
        return consecutivePattern;
    }
    
    public static PlayerColor getColorByChar(char colorChar) {
        for (PlayerColor color : values()) {
            if (color.CHAR() == colorChar) {
                return color;
            }
        }
        return null;
    }
    
    
    public static PlayerColor getColorByCursor(int cursor) {
        int idx = cursor % 2;
        for (PlayerColor color : values()) {
            if (color.ordinal() == idx) {
                return color;
            }
        }
        return null;
    }
    
    
}
