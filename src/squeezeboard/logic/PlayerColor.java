package squeezeboard.logic;

/**
 *
 * @author zhangwei
 */
public enum PlayerColor {
    
    orange('O'),
    
    blue('B');
   
    private char CHAR;

    private PlayerColor(char CHAR) {
        this.CHAR = CHAR;
    }
    
    public char CHAR(){
        return this.CHAR;
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
