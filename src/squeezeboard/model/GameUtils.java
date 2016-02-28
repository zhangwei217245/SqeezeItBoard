/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import squeezeboard.SqueezeBoard;
import squeezeboard.controller.CellEventListner;
import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import squeezeboard.model.PlayerColor;
import squeezeboard.view.GridPaneView;
import squeezeboard.view.StatusBarView;

/**
 *
 * @author zhangwei
 */
public class GameUtils {
    
    public static final String file_empty="/squeezeboard/emptyCell.png";
    public static final String file_orange="/squeezeboard/orangeButton.png";
    public static final String file_blue="/squeezeboard/blueButton.png";
    public static final String file_possMove="/squeezeboard/possMove.png";
    
    public static final Image img_empty = new Image(SqueezeBoard.class.getResourceAsStream(file_empty));
    public static final Image img_orange = new Image(SqueezeBoard.class.getResourceAsStream(file_orange));
    public static final Image img_blue = new Image(SqueezeBoard.class.getResourceAsStream(file_blue));
    public static final Image img_possMove = new Image(SqueezeBoard.class.getResourceAsStream(file_possMove));
    
    public static double bloomThreshold = 0.6;
    
    private static Effect bloom = new Bloom(bloomThreshold);
    
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
        switch (cellChar) {
            case 'P':
                imgView.setEffect(bloom);
                imgView.setImage(GameUtils.img_possMove);
                break;
            case 'E':
                imgView.setImage(GameUtils.img_empty);
                imgView.setEffect(null);
                break;
            case 'O':
                imgView.setImage(GameUtils.img_orange);
                imgView.setEffect(null);
                break;
            case 'B':
                imgView.setImage(GameUtils.img_blue);
                imgView.setEffect(null);
                break;
            default:
                ;
        }
        if (cell.equals(GameUtils.pickedCell)) {
            imgView.setEffect(bloom);
        }
    }
            
}
