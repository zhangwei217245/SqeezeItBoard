/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.GameUtils;
import squeezeboard.model.PlayerColor;
import squeezeboard.model.PromptableException.ExceptFactor;
import squeezeboard.view.GridPaneView;
import squeezeboard.view.StatusBarView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author zhangwei
 */
public class SqueezeBoardController implements Initializable {

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

    @FXML
    private MenuItem menu_undo;

    private final ToggleGroup radioGroup = new ToggleGroup();

    private GridPaneView gridViewController;

    private StatusBarView statusBarController;

    private boolean isGridInitialized = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initiateBoard();
        initRadioGroup();
        refreshStatusBar();
    }

    private void initiateBoard() {
        gridViewController = new GridPaneView(grid_view);
        statusBarController = new StatusBarView(leftStatus, rightStatus, label_currPlayer, this);
        GameUtils.currentCursor.set(0);
        BoardConfiguration initialBoard = new BoardConfiguration(GameUtils.GRID_DIMENSION);
        //initialBoard.setMoveMaker(GameUtils.currentColor);
        GameUtils.existingMoves = new BoardConfiguration[GameUtils.MAXIMUM_MOVES * 2];
        GameUtils.existingMoves[GameUtils.currentCursor.get()] = initialBoard;
        GameUtils.renderGridView(GameUtils.getCurrentBoardConfiguration(),
                grid_view, GameUtils.GRID_DIMENSION,
                (isGridInitialized ? null : gridViewController),
                (isGridInitialized ? null : statusBarController));

        isGridInitialized = true;
        GameUtils.orangeLeft = new AtomicInteger(GameUtils.GRID_DIMENSION);
        GameUtils.blueLeft = new AtomicInteger(GameUtils.GRID_DIMENSION);
        refreshStatusBar();
        grid_view.setDisable(true);
        grid_view.setVisible(false);
    }

    private void initRadioGroup() {

        radio_blue.setToggleGroup(radioGroup);
        radio_orange.setToggleGroup(radioGroup);
        radio_blue.setSelected(true);
        radioGroup.selectedToggleProperty().addListener(
                (ObservableValue<? extends Toggle> ov,
                 Toggle old_toggle, Toggle new_toggle) -> {
            if (radioGroup.getSelectedToggle() != null) {
                if (radioGroup.getSelectedToggle().equals(radio_blue)) {
                    GameUtils.computerRole = PlayerColor.blue;
                } else if (radioGroup.getSelectedToggle().equals(radio_orange)) {
                    GameUtils.computerRole = PlayerColor.orange;
                }
            }
            GameUtils.currentColor = PlayerColor.orange;
            refreshStatusBar();
        });

    }

    @FXML
    private void handleStart(ActionEvent event) {
        if (btn_start.getText().equals("Start")) {
            startGame();
        } else if (btn_start.getText().equals("End")) {
            endGame();
        }
    }

    private void startGame() {
        btn_start.setText("End");
        btn_start.setSelected(true);
        GameUtils.round.incrementAndGet();
        initiateBoard();
        radioGroup.getToggles().stream().forEach(radio -> ((RadioButton) radio).setDisable(true));
        GameUtils.game_started.compareAndSet(false, true);
        grid_view.setDisable(false);
        menu_undo.setDisable(false);
        grid_view.setVisible(true);
        System.out.println("start GAme");
    }

    public void endGame() {
        btn_start.setText("Start");
        btn_start.setSelected(false);
        radioGroup.getToggles().stream().forEach(radio -> ((RadioButton) radio).setDisable(false));
        grid_view.setDisable(true);
        menu_undo.setDisable(true);
        System.out.println("end game");
    }

    @FXML
    private void handleReset(ActionEvent event) {
        resetMemory();
        resetStatus();
        resetBoard();
    }

    @FXML
    private void handleQuit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleUndo(ActionEvent event) {
        if (GameUtils.currentCursor.get() == 0) {
            GameUtils.showAlertBox(ExceptFactor.NO_MOVES_TO_BE_UNDONE);
            return;
        }
        if (GameUtils.currentColor.equals(GameUtils.computerRole)) {
            GameUtils.showAlertBox(ExceptFactor.WAIT_FOR_COMPUTER);
            return;
        }
        Platform.runLater(() -> {
            this.gridViewController.update(GameUtils.undoConfiguration());
            refreshStatusBar();
            this.gridViewController.update(GameUtils.undoConfiguration());
            refreshStatusBar();
        });
    }

    @FXML
    private void handleAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Author\n  Wei Zhang (x-spirit.zhang@ttu.edu)\n"
                + "  Ahmad Aseeri(aseeri.ahmad@ttu.edu)\n TTU 2016 All rights reserved.\n");
        alert.setTitle("About SqueezeIt v1.0");
        alert.setHeaderText("About SqueezeIt v1.0");
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> System.out.println(response.getButtonData()));
    }

    private void resetBoard() {
        endGame();
        grid_view.setVisible(false);
    }

    private void resetMemory() {
        GameUtils.pickedCell = null;
        if (GameUtils.existingMoves != null) {
            for (int i = 0; i < GameUtils.existingMoves.length; i++) {
                GameUtils.existingMoves[i] = null;
            }
        }
        GameUtils.existingMoves = null;
        GameUtils.computerRole = PlayerColor.blue;
        GameUtils.currentCursor.set(0);
        GameUtils.currentColor = PlayerColor.orange;
        GameUtils.blueLeft.set(GameUtils.GRID_DIMENSION);
        GameUtils.orangeLeft.set(GameUtils.GRID_DIMENSION);
    }

    private void resetStatus() {
        btn_start.setSelected(false);
        btn_start.setText("Start");
        refreshStatusBar();
    }

    private void refreshStatusBar() {
        statusBarController.update();
    }


    public ToggleButton getBtn_start() {
        return btn_start;
    }
}
