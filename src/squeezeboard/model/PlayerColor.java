package squeezeboard.model;

import javafx.scene.paint.Color;

/**
 *
 * @author zhangwei
 */
public enum PlayerColor {
    
    orange('O', Color.web("0xff9900")),
    
    blue('B', Color.web("0x0099ff"));
   
    private char CHAR;
    
    private Color color;

    private PlayerColor(char CHAR, Color color) {
        this.CHAR = CHAR;
        this.color = color;
    }
    
    public char CHAR(){
        return this.CHAR;
    }

    public Color getColor() {
        return color;
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
