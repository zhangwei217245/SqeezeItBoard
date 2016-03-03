/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.model;

import javafx.scene.control.Alert;

/**
 *
 * @author zhangwei
 */
public class PromptableException extends Exception {

    private static final long serialVersionUID = -2449009277878470480L;
    
    private ExceptFactor factor;

    /**
     * Creates a new instance of <code>PromptableException</code> without detail
     * message.
     */
    public PromptableException() {
    }

    @Override
    public String getLocalizedMessage() {
        return this.factor.getMsg(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getMessage() {
        return this.factor.getMsg(); //To change body of generated methods, choose Tools | Templates.
    }

    
    /**
     * Constructs an instance of <code>PromptableException</code> with the
     * specified detail message.
     *
     * @param factor
     */
    public PromptableException(ExceptFactor factor) {
        this.factor = factor;
    }
    
    
    public enum ExceptFactor {
    
        YOU_WIN("Congratulations!!! You Win!!!", null,"\"Baby, you're a firework!\nCome on, let your colours burst!\"", Alert.AlertType.NONE),
        COMPUTER_WIN("Oops! I win~~", null,"\"Oops, I did it again~~ \nI played with you heart, got lost in the game~~\"", Alert.AlertType.NONE),
        DRAW_GAME("You've done a good job.", null, "\"Just as good as what I did!\"", Alert.AlertType.NONE),
        PIECE_ON_PIECE("Invalid Piece Drop!", null,"You cannot drop a piece onto opponent's piece!", Alert.AlertType.WARNING),
        INVALID_MOVE("Invalid Piece Drop!", null, "You cannot make an invalid move!!", Alert.AlertType.WARNING),
        PICKED_UP_DATA_MESS("Internal Data Error", null, "PickedUp data is already messed-up!!!", Alert.AlertType.ERROR),
        NOT_YOUR_TURN("NOT YOUR TURN! ", null, "It's Not Your Turn!!!", Alert.AlertType.ERROR),
        WAIT_FOR_COMPUTER("Please Wait for Computer! ", null, "It's Not Your Turn!!!", Alert.AlertType.INFORMATION),
        NO_MOVES_TO_BE_UNDONE("No More Undo!", null, "There are no more moves that can be undone!", Alert.AlertType.WARNING);

        private String titleText;
        private String headerText;
        private String msg;
        private Alert.AlertType alertType;

        private ExceptFactor(String titleText, String headerText, String msg, Alert.AlertType alertType) {
            this.titleText = titleText;
            this.headerText = headerText==null?titleText:headerText;
            this.msg = msg;
            this.alertType = alertType;
        }
        public String getMsg() {
            return msg;
        }

        public Alert.AlertType getAlertType() {
            return alertType;
        }

        public String getTitleText() {
            return titleText;
        }

        public String getHeaderText() {
            return headerText;
        }
        
        
    }
}
