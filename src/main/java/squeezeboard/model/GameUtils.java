/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.model;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import squeezeboard.SqueezeBoard;
import squeezeboard.controller.CellEventListner;
import squeezeboard.controller.pattern.SqueezePattern;
import squeezeboard.model.Animation.AnimatedGif;
import squeezeboard.view.GridPaneView;
import squeezeboard.view.StatusBarView;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author zhangwei
 */
public class GameUtils {
    
    public static final String file_empty="/images/emptyCell.png";
    public static final String file_orange="/images/orangeButton.png";
    public static final String file_blue="/images/blueButton.png";
    public static final String file_possMove="/images/possMove.png";
    public static final String file_you_win="/images/Fireworks.jpg";
    public static final String file_computer_win="/images/oops.gif";
    public static final String file_draw_game="/images/draw.jpeg";
    
    
    public static final Image img_empty = new Image(SqueezeBoard.class.getResourceAsStream(file_empty));
    public static final Image img_orange = new Image(SqueezeBoard.class.getResourceAsStream(file_orange));
    public static final Image img_blue = new Image(SqueezeBoard.class.getResourceAsStream(file_blue));
    public static final Image img_possMove = new Image(SqueezeBoard.class.getResourceAsStream(file_possMove));
    public static final Image img_you_win = new Image(SqueezeBoard.class.getResourceAsStream(file_you_win));
    public static final Image img_draw = new Image(SqueezeBoard.class.getResourceAsStream(file_draw_game));
    //public static final Image img_computer_win = new Image(SqueezeBoard.class.getResourceAsStream(file_computer_win));
    
    public static double effecthreshold = 0.6d;
    
    private static Effect bloom = new Bloom(effecthreshold);
    
    public static final AtomicInteger round = new AtomicInteger(0);
    
    public static final AtomicBoolean game_started = new AtomicBoolean(false);
    
    /**
     * computer is blue originally
     */
    public static PlayerColor computerRole = PlayerColor.blue;
    
    public static PlayerColor currentColor = PlayerColor.orange;
    
    public static AtomicInteger orangeLeft;
    public static AtomicInteger blueLeft;
    
    
    public static CellData pickedCell = null;

    public static final int GRID_DIMENSION = 8;

    public static final int MAXIMUM_MOVES = 50;
    
    
    public static BoardConfiguration[] existingMoves;
    
    public static final AtomicInteger currentCursor = new AtomicInteger(0);
    
    public static Node getNodeByRowColumnIndex(final int row,final int column,GridPane gridPane) {
        Node result = null;
        Integer r = new Integer(row);
        Integer c = new Integer(column);
        ObservableList<Node> childrens = gridPane.getChildren();
        for(Node node : childrens) {
            if(r.equals(gridPane.getRowIndex(node)) && c.equals(gridPane.getColumnIndex(node))) {
                result = node;
                break;
            }
        }
        return result;
    }
    
    public static void renderGridView(BoardConfiguration boardConfiguration, GridPane gridView, 
            int dimension, GridPaneView gridController, StatusBarView statusBarView) {
        ImageView imgView;
        boardConfiguration.printMatrix();
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                CellData cell = boardConfiguration.getBoard()[i][j];
                if (gridController != null) {
                    imgView = new ImageView();
                    imgView.addEventHandler(MouseEvent.MOUSE_CLICKED, new CellEventListner(gridController, statusBarView));
                    gridView.add(imgView, j, i);
                } else {
                    imgView = (ImageView) getNodeByRowColumnIndex(i, j, gridView);
                }
                imgView.setUserData(cell);
                setPictureToImageView(cell, imgView);
            }
        }
    }
    
    private static void setPictureToImageView(CellData cell, ImageView imgView) {
        char cellChar = cell.getCellChar();
        Effect effect = null;
        Image img = imgView.getImage();
        switch (cellChar) {
            case 'P':
                effect = bloom;
                img = GameUtils.img_possMove;
                break;
            case 'E':
                img = GameUtils.img_empty;
                effect = null;
                break;
            case 'O':
                img = GameUtils.img_orange;
                effect = null;
                break;
            case 'B':
                img = GameUtils.img_blue;
                effect = null;
                break;
            default:
                ;
        }
        
        imgView.setImage(img);
        imgView.setEffect(effect);
        if (cell.equals(GameUtils.pickedCell)) {
            imgView.setEffect(bloom);
        }
    }
    
    public static BoardConfiguration getCurrentBoardConfiguration() {
        return existingMoves[currentCursor.get()];
    }
    
    public static CellData[][] getCurrentBoard(){
        return getCurrentBoardConfiguration().getBoard();
    }
    
    public static BoardConfiguration copyCurrentConfiguration() {
        BoardConfiguration currentConfig = existingMoves[currentCursor.get()];
        currentConfig = currentConfig.clone();
        existingMoves[currentCursor.incrementAndGet()] = currentConfig;
        currentConfig.setMoveMaker(PlayerColor.getColorByCursor(currentColor.ordinal()+1));
        return currentConfig;
    }
    
    public static BoardConfiguration undoConfiguration() {
        BoardConfiguration currentConfig = existingMoves[currentCursor.get()];
        if (currentCursor.get() > 0) {
            currentConfig.destroy();
            currentConfig = existingMoves[currentCursor.decrementAndGet()];
        }
        currentColor = currentConfig.getMoveMaker();
        return currentConfig;
    }
    
    
    public static void showAlertBox(PromptableException.ExceptFactor exceptFactor) {
        Alert alert = new Alert(exceptFactor.getAlertType().equals(Alert.AlertType.NONE)?
                Alert.AlertType.INFORMATION:exceptFactor.getAlertType(), exceptFactor.getMsg());
        alert.setTitle(exceptFactor.getTitleText());
        alert.setHeaderText(exceptFactor.getHeaderText());
        if(exceptFactor.getAlertType().equals(Alert.AlertType.NONE)){
            if (exceptFactor.equals(PromptableException.ExceptFactor.YOU_WIN)) {
                alert.setGraphic(new ImageView(img_you_win));
            } else if (exceptFactor.equals(PromptableException.ExceptFactor.COMPUTER_WIN)){
                AnimatedGif img_computer_win = new AnimatedGif(
                        SqueezeBoard.class.getResource(file_computer_win).toExternalForm(), 2200);
                img_computer_win.setCycleCount(5);
                img_computer_win.play();
                alert.setGraphic(img_computer_win.getView());
            } else if (exceptFactor.equals(PromptableException.ExceptFactor.DRAW_GAME)) {
                alert.setGraphic(new ImageView(img_draw));
            }
        }
        alert.showAndWait()
        .filter(response -> response == ButtonType.OK)
        .ifPresent(response -> System.out.println(response.getButtonData()));
    }
    
    
    public static String getPatternString(SqueezePattern pattern){
        StringBuilder sb = new StringBuilder();
        for (CellData cell : pattern.getPattern()) {
            sb.append(cell.getCellChar());
        }
        return sb.toString();
    }
    
    

    
}
