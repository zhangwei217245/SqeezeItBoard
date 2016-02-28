/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.model;

import java.util.concurrent.atomic.AtomicInteger;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import squeezeboard.SqueezeBoard;
import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import squeezeboard.model.PlayerColor;

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
    
            
}
