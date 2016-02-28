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
     * @param msg the detail message.
     */
    public PromptableException(ExceptFactor factor) {
        this.factor = factor;
    }
    
    
    public enum ExceptFactor {
    
        PIECE_ON_PIECE("You cannot drop a piece onto opponent's piece!", Alert.AlertType.WARNING),
        INVALID_MOVE("You cannot make an invalid move!!", Alert.AlertType.WARNING),
        PICKED_UP_DATA_MESS("PickedUp data is already messed-up!!!", Alert.AlertType.ERROR);

        private String msg;
        private Alert.AlertType alertType;

        private ExceptFactor(String msg, Alert.AlertType alertType) {
            this.msg = msg;
            this.alertType = alertType;
        }

        public String getMsg() {
            return msg;
        }

        public Alert.AlertType getAlertType() {
            return alertType;
        }
        
    }
}
