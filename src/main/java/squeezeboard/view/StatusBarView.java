package squeezeboard.view;

import javafx.scene.control.Label;
import squeezeboard.SqueezeBoardController;
import squeezeboard.model.GameUtils;
import squeezeboard.model.PlayerColor;
import squeezeboard.model.PromptableException;

/**
 *
 * @author zhangwei
 */
public class StatusBarView {
    
    
    private Label leftStatus;
    
    private Label rightStatus;
    
    private Label label_currPlayer;
    
    private SqueezeBoardController mainController;

    public StatusBarView(Label leftStatus, Label rightStatus, Label label_currPlayer,
            SqueezeBoardController mainController) {
        this.leftStatus = leftStatus;
        this.rightStatus = rightStatus;
        this.label_currPlayer = label_currPlayer;
        this.mainController = mainController;
    }
    
    public void update(){
        int computerLeft = GameUtils.blueLeft.get();
        int playerLeft = GameUtils.orangeLeft.get();
        
        String leftMessage = String.format("Computer left: %s", computerLeft);
        String rightMessage = String.format("Player left: %s", playerLeft);
        if (GameUtils.computerRole.equals(PlayerColor.orange)) {
            playerLeft = GameUtils.blueLeft.get();
            computerLeft = GameUtils.orangeLeft.get();
            leftMessage = String.format("Player left: %s", playerLeft);
            rightMessage = String.format("Computer left: %s", computerLeft);
        }
        leftStatus.setText(leftMessage);
        leftStatus.setTextFill(PlayerColor.blue.getColor());
        rightStatus.setText(rightMessage);
        rightStatus.setTextFill(PlayerColor.orange.getColor());
        
        String playerName = "Player";
        if (GameUtils.currentColor.equals(GameUtils.computerRole)) {
            playerName = "Computer";
        }

        label_currPlayer.setText(String.format(GameUtils.game_started.get()?
                        "Current Player : %s | Round : %s | Move : %s"
                        : "%s Will Firstly Serve.| Round : %s | Move : %s"
                , playerName, GameUtils.round.get(), GameUtils.currentCursor.get()));
        label_currPlayer.setTextFill(GameUtils.currentColor.getColor());
        determineGameResult(computerLeft,playerLeft);

    }

    public void determineGameResult(int computerLeft, int playerLeft){
        if (GameUtils.currentCursor.get() >= GameUtils.MAXIMUM_MOVES) {
            // GAME MUST COME TO AN END HERE, which one has the most pieces on the board will win
            showDifferentGameResult(computerLeft,playerLeft);
        } else {
            // if anyone has only one piece left on the board, he will lost.
            if (computerLeft <= 1 || playerLeft <= 1) {
                showDifferentGameResult(computerLeft,playerLeft);
            }
        }
    }
    private void showDifferentGameResult(int computerLeft, int playerLeft){
        if (computerLeft > playerLeft) {
            GameUtils.showAlertBox(PromptableException.ExceptFactor.COMPUTER_WIN);
        } else if (playerLeft > computerLeft) {
            GameUtils.showAlertBox(PromptableException.ExceptFactor.YOU_WIN);
        } else if (computerLeft == playerLeft) {
            GameUtils.showAlertBox(PromptableException.ExceptFactor.DRAW_GAME);
        }
        mainController.endGame();
    }
 
    public Label getLeftStatus() {
        return leftStatus;
    }

    public void setLeftStatus(Label leftStatus) {
        this.leftStatus = leftStatus;
    }

    public Label getRightStatus() {
        return rightStatus;
    }

    public void setRightStatus(Label rightStatus) {
        this.rightStatus = rightStatus;
    }

    public Label getLabel_currPlayer() {
        return label_currPlayer;
    }

    public void setLabel_currPlayer(Label label_currPlayer) {
        this.label_currPlayer = label_currPlayer;
    }

    public SqueezeBoardController getMainController() {
        return mainController;
    }

    public void setMainController(SqueezeBoardController mainController) {
        this.mainController = mainController;
    }
    
    
    
    
}
