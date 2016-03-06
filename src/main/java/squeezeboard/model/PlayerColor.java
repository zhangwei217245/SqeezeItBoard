package squeezeboard.model;

import javafx.scene.paint.Color;

import java.util.regex.Pattern;

/**
 *
 * @author zhangwei
 */
public enum PlayerColor {
    
    orange('O', Color.web("0xff9900"),Pattern.compile("O[B]*[E]+[B]*O"),
            Pattern.compile("O[B]+O"), Pattern.compile("(B[O]+|[O]+B)")) {
        @Override
        public void decreaseLeftCount(int removal) {
            GameUtils.orangeLeft.getAndAdd(0-removal);
        }
    },
    
    blue('B', Color.web("0x0099ff"), Pattern.compile("B[O]*[E]+[O]*B"),
            Pattern.compile("B[O]+B"), Pattern.compile("(O[B]+|[B]+O)")) {
        @Override
        public void decreaseLeftCount(int removal) {
            GameUtils.blueLeft.getAndAdd(0-removal);
        }
    };
   
    private char CHAR;
    
    private Color color;
    
    private Pattern gapPattern;
    
    private Pattern fulfilledGapPattern;
    
    private Pattern incompleteGapPattern;

    private PlayerColor(char CHAR, Color color, Pattern gapPattern, 
            Pattern fulfilledGapPattern,
            Pattern incompleteGapPattern) {
        this.CHAR = CHAR;
        this.color = color;
        this.gapPattern = gapPattern;
        this.fulfilledGapPattern = fulfilledGapPattern;
        this.incompleteGapPattern = incompleteGapPattern;
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

    public Pattern getFulfilledGapPattern() {
        return fulfilledGapPattern;
    }
    
    public Pattern getIncompleteGapPattern() {
        return incompleteGapPattern;
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

    public abstract void decreaseLeftCount(int removal);

    public PlayerColor getOpponentColor() {
        return PlayerColor.getColorByCursor(this.ordinal() + 1);
    }
    
}
