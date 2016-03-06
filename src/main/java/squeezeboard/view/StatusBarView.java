package squeezeboard.view;

import javafx.scene.control.Label;
import squeezeboard.SqueezeBoardController;
import squeezeboard.model.GameUtils;
import squeezeboard.model.Pair;
import squeezeboard.model.PlayerColor;

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
        Pair<Integer, Integer> left = GameUtils.getComputerPlayerLeft();
        int computerLeft = left.getFirst();
        int playerLeft = left.getSecond();
        
        String leftMessage = String.format("Computer left: %s", computerLeft);
        String rightMessage = String.format("Player left: %s", playerLeft);
        if (GameUtils.computerRole.equals(PlayerColor.orange)) {
            leftMessage = String.format("Player left: %s", playerLeft);
            rightMessage = String.format("Computer left: %s", computerLeft);
        }
        leftStatus.setText(leftMessage);
        leftStatus.setTextFill(PlayerColor.blue.getColor());
        rightStatus.setText(rightMessage);
        rightStatus.setTextFill(PlayerColor.orange.getColor());
        
        String playerName = "Player";
        //System.out.println(GameUtils.currentColor);
        if (GameUtils.currentColor.equals(GameUtils.computerRole)) {
            playerName = "Computer";
        }

        label_currPlayer.setText(String.format(GameUtils.game_started.get()?
                        "Current Player : %s | Round : %s | Move : %s"
                        : "%s Will Firstly Serve.| Round : %s | Move : %s"
                , playerName, GameUtils.round.get(), GameUtils.currentCursor.get()));
        label_currPlayer.setTextFill(GameUtils.currentColor.getColor());
        GameUtils.game_over(computerLeft, playerLeft);
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
