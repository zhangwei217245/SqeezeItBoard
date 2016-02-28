/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard;

import squeezeboard.logic.GameUtils;
import squeezeboard.logic.PlayerColor;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import squeezeboard.logic.BoardConfiguration;
import squeezeboard.logic.CellData;
import squeezeboard.logic.CellEventListner;
import squeezeboard.logic.GridPaneController;

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
    
    private final ToggleGroup radioGroup = new ToggleGroup();
    
    private BoardConfiguration currentBoard;
    
    private GridPaneController gridController;
    
    private int gridDimension = 8;
    
    private int maximumMoves = 50;
    
    @FXML
    private void handleStart(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof ToggleButton) {
            ToggleButton tg_btn = (ToggleButton)source;
            System.out.println(tg_btn.isSelected());
            if (tg_btn.isSelected()) {
                tg_btn.setText("End");
                startGame();
            } else {
                tg_btn.setText("Start");
                endGame();
            }
        }
    }
    
    @FXML
    private void handleReset(ActionEvent event){
        Object source = event.getSource();
        if (source instanceof Button) {
            resetBoard();
            resetMemory();
            resetStatus();
        }
    }
    
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
        });
    }
    
    private void initiateBoard(){
        gridController = new GridPaneController(grid_view);
        currentBoard = new BoardConfiguration(this.gridDimension);
        GameUtils.existingMoves = new BoardConfiguration[maximumMoves];
        GameUtils.existingMoves[GameUtils.currentCursor.get()] = currentBoard;
        ImageView imgView;
        for (int i = 0; i < this.gridDimension; i++) {
            for (int j = 0; j < this.gridDimension; j++) {
                CellData cell = currentBoard.getBoard()[i][j];
                imgView = new ImageView(cell.getImg());
                imgView.setUserData(cell);
                imgView.addEventHandler(MouseEvent.MOUSE_CLICKED, new CellEventListner(gridController));
                grid_view.add(imgView, j, i);
            }
        }
        currentBoard.printMatrix();
        GameUtils.orangeLeft = new AtomicInteger(this.gridDimension);
        GameUtils.blueLeft = new AtomicInteger(this.gridDimension);
        refreshStatusBar();
    }
    
    

    private void startGame() {
        radioGroup.getToggles().stream().forEach(radio -> ((RadioButton)radio).setDisable(true));
        System.out.println("start GAme");
    }

    private void endGame() {
        radioGroup.getToggles().stream().forEach(radio -> ((RadioButton)radio).setDisable(false));
        System.out.println("end game");
    }

    private void resetBoard() {
        System.out.println("reset board");
    }

    private void resetMemory() {
        GameUtils.pickedCell = null;
        GameUtils.existingMoves = null;
        GameUtils.computerRole = PlayerColor.blue;
    }

    private void resetStatus() {
        btn_start.setSelected(false);
        btn_start.setText("Start");
        initiateBoard();
        endGame();
    }

    private void refreshStatusBar() {
        int computerLeft = GameUtils.blueLeft.get();
        int playLeft = GameUtils.orangeLeft.get();
        if (GameUtils.computerRole.equals(PlayerColor.orange)) {
            playLeft = GameUtils.blueLeft.get();
            computerLeft = GameUtils.orangeLeft.get();
        }
        leftStatus.setText("Player left: " + playLeft);
        rightStatus.setText("Computer left: " + computerLeft);
    }
    
}
