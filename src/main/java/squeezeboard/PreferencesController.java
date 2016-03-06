/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import squeezeboard.model.GameUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author zhangwei
 */
public class PreferencesController implements Initializable {

    
    @FXML
    private Slider searchWidth;
    @FXML
    private Slider searchDepth;
    @FXML
    private Slider maxMoves;


    @FXML
    private TextField txt_sWidth;
    @FXML
    private TextField txt_sDepth;
    @FXML
    private TextField txt_mMoves;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {


        searchWidth.valueProperty().addListener((observable, oldValue, newValue) -> {
            txt_sWidth.setText(String.valueOf(newValue.intValue()));
            GameUtils.SEARCH_WIDTH = newValue.intValue();
        });
        searchDepth.valueProperty().addListener((observable, oldValue, newValue) -> {
            txt_sDepth.setText(String.valueOf(newValue.intValue()));
            GameUtils.SEARCH_DEPTH = newValue.intValue();
        });
        maxMoves.valueProperty().addListener((observable, oldValue, newValue) -> {
            txt_mMoves.setText(String.valueOf(newValue.intValue()));
            GameUtils.MAXIMUM_MOVES = newValue.intValue();
        });

        searchWidth.setValue(GameUtils.SEARCH_WIDTH);
        this.searchWidth.fireEvent(new Event(DragEvent.ANY));

        searchDepth.setValue(GameUtils.SEARCH_DEPTH);
        this.searchDepth.fireEvent(new Event(DragEvent.ANY));

        maxMoves.setValue(GameUtils.MAXIMUM_MOVES);
        this.maxMoves.fireEvent(new Event(DragEvent.ANY));
    }

    @FXML
    public void handleMaxMoves(Event e) {
        this.maxMoves.setValue(Double.valueOf(this.txt_mMoves.getText()).intValue());
        this.maxMoves.fireEvent(new Event(DragEvent.ANY));
    }

    @FXML
    public void handleSearchDepth(Event e) {
        this.searchDepth.setValue(Double.valueOf(this.txt_sDepth.getText()).intValue());
        this.searchDepth.fireEvent(new Event(DragEvent.ANY));
    }

    @FXML
    public void handleSearchWidth(Event e) {
        this.searchWidth.setValue(Double.valueOf(this.txt_sWidth.getText()).intValue());
        this.searchWidth.fireEvent(new Event(DragEvent.ANY));
    }
    
    
    
    
}
