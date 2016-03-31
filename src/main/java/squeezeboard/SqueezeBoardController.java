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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Stage;
import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.GameUtils;
import squeezeboard.model.PlayerColor;
import squeezeboard.model.PromptableException.ExceptFactor;
import squeezeboard.view.GridPaneView;
import squeezeboard.view.StatusBarView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import static squeezeboard.model.GameUtils.*;

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

    @FXML
    private Button btn_undo;

    @FXML
    private MenuItem menu_pref;

    private final ToggleGroup radioGroup = new ToggleGroup();

    private GridPaneView gridViewController;

    private StatusBarView statusBarController;

    private boolean isGridInitialized = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initiateBoard();
        initRadioGroup();
        refreshStatusBar();
    }

    private void initiateBoard() {
        gridViewController = new GridPaneView(grid_view);
        statusBarController = new StatusBarView(leftStatus, rightStatus, label_currPlayer, this);
        GameUtils.mainController = this;
        currentCursor.set(0);
        BoardConfiguration initialBoard = new BoardConfiguration(GameUtils.GRID_DIMENSION);
        GameUtils.existingMoves = new BoardConfiguration[GameUtils.MAXIMUM_MOVES * 2 + GameUtils.SEARCH_DEPTH + 10];
        GameUtils.existingMoves[currentCursor.get()] = initialBoard;
        initialBoard.setMoveMaker(GameUtils.currentColor.getOpponentColor());
//        GameUtils.existingMoves[GameUtils.currentCursor.incrementAndGet()] = initialBoard.clone();
//        GameUtils.existingMoves[GameUtils.currentCursor.get()].setMoveMaker(initialBoard.getMoveMaker());

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
        radioGroup.selectedToggleProperty().addListener(
                (ObservableValue<? extends Toggle> ov,
                 Toggle old_toggle, Toggle new_toggle) -> {
            if (radioGroup.getSelectedToggle() != null) {
                if (radioGroup.getSelectedToggle().equals(radio_blue)) {
                    computerRole = PlayerColor.blue;
                } else if (radioGroup.getSelectedToggle().equals(radio_orange)) {
                    computerRole = PlayerColor.orange;
                }
            }
            GameUtils.currentColor = PlayerColor.orange;
            refreshStatusBar();
        });
        radio_blue.fire();
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
        //initiateBoard();
        radioGroup.getToggles().stream().forEach(radio -> {
            if (((RadioButton)radio).isSelected()) {
                ((RadioButton)radio).fire();
            }
        });
        radioGroup.getToggles().stream().forEach(radio -> ((RadioButton) radio).setDisable(true));
        GameUtils.game_started.compareAndSet(false, true);
        grid_view.setDisable(false);
        menu_undo.setDisable(false);
        btn_undo.setDisable(false);
        grid_view.setVisible(true);
        menu_pref.setDisable(true);
        System.out.println("start Game");
        Platform.runLater(() ->{
            computerAction();
        });

    }

    public void endGame() {
        btn_start.setText("Start");
        btn_start.setSelected(false);
        grid_view.setDisable(true);
        menu_undo.setDisable(true);
        btn_undo.setDisable(true);
        menu_pref.setDisable(false);
        GameUtils.game_started.compareAndSet(true, false);
        radioGroup.getToggles().stream().forEach(radio -> {
            if (((RadioButton)radio).isSelected()) {
                ((RadioButton)radio).fire();
            }
        });
        radioGroup.getToggles().stream().forEach(radio ->
                ((RadioButton) radio).setDisable(false));
        System.out.println("end game");
    }

    @FXML
    private void handleReset(ActionEvent event) {
        //btn_start.fire();
        resetBoard();
        resetMemory();
        resetStatus();
        resetRadio();

    }

    @FXML
    private void handleQuit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleUndo(ActionEvent event) {
        if (currentCursor.get() == 0) {
            GameUtils.showAlertBox(ExceptFactor.NO_MOVES_TO_BE_UNDONE);
            return;
        }
        if (GameUtils.currentColor.equals(computerRole)) {
            GameUtils.showAlertBox(ExceptFactor.WAIT_FOR_COMPUTER);
            return;
        }
        Platform.runLater(() -> {
            int redo = 2;
            for (int i = 0; i < redo; i++) {
                this.gridViewController.update(GameUtils.undoConfiguration());
            }
            if (currentCursor.get() == 0 && computerRole.equals(PlayerColor.orange)) {
                computerAction();
                gridViewController.update(GameUtils.getCurrentBoardConfiguration());
            }
            refreshStatusBar();
        });
    }

    @FXML
    private void handleAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Author\n  Wei Zhang (x-spirit.zhang@ttu.edu)\n"
                + "  Ahmad Aseeri(aseeri.ahmad@ttu.edu)\n TTU 2016 All rights reserved.\n");
        alert.setTitle("About SqueezeIt v1.6");
        alert.setHeaderText("About SqueezeIt v1.6");
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> System.out.println(response.getButtonData()));
    }

    @FXML
    private void handleTutorial(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/TutorialViewer.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        //stage.initModality(Modality.WINDOW_MODAL);
        //stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Tutorial Viewer for SqueezeIt!");
        stage.setResizable(false);
        stage.show();

    }

    @FXML
    private void handleMenuPref(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Preferences.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);

        stage.setScene(scene);
//        stage.initModality(Modality.WINDOW_MODAL);
//        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Preferences for SqueezeIt!");
        stage.setResizable(false);
        stage.show();
    }

    private void resetRadio() {
        initRadioGroup();
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
        computerRole = PlayerColor.blue;
        currentCursor.set(0);
        GameUtils.currentColor = PlayerColor.orange;
        GameUtils.blueLeft.set(GameUtils.GRID_DIMENSION);
        GameUtils.orangeLeft.set(GameUtils.GRID_DIMENSION);
        initiateBoard();
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
