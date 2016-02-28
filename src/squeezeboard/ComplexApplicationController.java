/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard;

import squeezeboard.model.GameUtils;
import squeezeboard.model.PlayerColor;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import squeezeboard.model.BoardConfiguration;
import squeezeboard.view.GridPaneView;

/**
 *
 * @author zhangwei
 */
public class ComplexApplicationController implements Initializable {
    
    @FXML
    private ToggleButton btn_start;
    
    @FXML
    private Button btn_reset;
    
    @FXML
    private GridPane grid_view;
    
    @FXML
    private RadioButton radio_orange;
    
    @FXML
    private RadioButton radio_blue;
    
    @FXML
    private Label leftStatus;
    
    @FXML
    private Label rightStatus;
    
    @FXML
    private Label label_currPlayer;
      
    private final ToggleGroup radioGroup = new ToggleGroup();
    
    private GridPaneView gridViewController;
    
    private int gridDimension = 8;
    
    private int maximumMoves = 50;
    
    private int round = 0;
    
    private boolean isGridInitialized = false;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initiateBoard();
        initRadioGroup();
        refreshStatusBar();
    }
    
    private void initRadioGroup() {
        
        radio_blue.setToggleGroup(radioGroup);
        radio_orange.setToggleGroup(radioGroup);
        radio_blue.setSelected(true);
        radioGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, 
        Toggle new_toggle) -> {
            if (radioGroup.getSelectedToggle() != null) {
                if (radioGroup.getSelectedToggle().equals(radio_blue)){
                    GameUtils.computerRole = PlayerColor.blue;
                } else if (radioGroup.getSelectedToggle().equals(radio_orange)) {
                    GameUtils.computerRole = PlayerColor.orange;
                }
            }
            GameUtils.currentColor = PlayerColor.orange;
            refreshStatusBar();
        });
        
    }
    
    private void initiateBoard(){
        
        gridViewController = new GridPaneView(grid_view);
        GameUtils.existingMoves = new BoardConfiguration[maximumMoves * 2];
        GameUtils.existingMoves[GameUtils.currentCursor.get()] = new BoardConfiguration(this.gridDimension);
        GameUtils.renderGridView(GameUtils.existingMoves[GameUtils.currentCursor.get()], 
                grid_view, this.gridDimension, (isGridInitialized ? null : gridViewController));
        isGridInitialized = true;
        GameUtils.orangeLeft = new AtomicInteger(this.gridDimension);
        GameUtils.blueLeft = new AtomicInteger(this.gridDimension);
        refreshStatusBar();
        grid_view.setDisable(true);
        grid_view.setVisible(false);
    }
    
    @FXML
    private void handleStart(ActionEvent event) {
        if (btn_start.getText().equals("Start")) {
            btn_start.setText("End");
            btn_start.setSelected(true);
            startGame();
        } else if (btn_start.getText().equals("End")){
            btn_start.setText("Start");
            endGame();
        }
    }
    
    private void startGame() {
        round++;
        initiateBoard();
        radioGroup.getToggles().stream().forEach(radio -> ((RadioButton)radio).setDisable(true));
        grid_view.setDisable(false);
        grid_view.setVisible(true);
        System.out.println("start GAme");
    }

    private void endGame() {
        radioGroup.getToggles().stream().forEach(radio -> ((RadioButton)radio).setDisable(false));
        grid_view.setDisable(true);
        System.out.println("end game");
    }
    
    @FXML
    private void handleReset(ActionEvent event){
        resetMemory();
        resetStatus();
        resetBoard();
    }
    
    @FXML
    private void handleQuit(ActionEvent event) {
        System.exit(0);
    }
    
    @FXML
    private void handleAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Author\n  Wei Zhang (x-spirit.zhang@ttu.edu)\n"
                + "  Ahmad Aseeri(aseeri.ahmad@ttu.edu)\n TTU 2016 All rights reserved.\n");
        alert.setTitle("About SqueezeIt v1.0");
        alert.setHeaderText("About SqueezeIt v1.0");
        alert.showAndWait()
        .filter(response -> response == ButtonType.OK)
        .ifPresent(response -> System.out.println(""));
    }
    
    private void resetBoard() {
        endGame();
        grid_view.setVisible(false);
    }

    private void resetMemory() {
        GameUtils.pickedCell = null;
        GameUtils.existingMoves = null;
        GameUtils.computerRole = PlayerColor.blue;
        GameUtils.currentCursor.set(0);
        GameUtils.currentColor = PlayerColor.orange;
        GameUtils.blueLeft.set(this.gridDimension);
        GameUtils.orangeLeft.set(this.gridDimension);
    }

    private void resetStatus() {
        btn_start.setSelected(false);
        btn_start.setText("Start");
        refreshStatusBar();
    }

    private void refreshStatusBar() {
        int computerLeft = GameUtils.blueLeft.get();
        int playLeft = GameUtils.orangeLeft.get();
        String leftMessage = String.format("Computer left: %s", computerLeft);
        String rightMessage = String.format("Player left: %s", playLeft);
        if (GameUtils.computerRole.equals(PlayerColor.orange)) {
            playLeft = GameUtils.blueLeft.get();
            computerLeft = GameUtils.orangeLeft.get();
            leftMessage = String.format("Player left: %s", playLeft);
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
        label_currPlayer.setText(String.format(btn_start.isSelected()?"Current Player : %s | Round : %s | Move : %s" : "%s Will Firstly Serve.| Round : %s | Move : %s"
                , playerName, this.round, GameUtils.currentCursor.get()));
        label_currPlayer.setTextFill(GameUtils.currentColor.getColor());
        
    }
    
}
