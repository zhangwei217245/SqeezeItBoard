/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.logic;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.GridPane;

/**
 *
 * @author zhangwei
 */
public class GridPaneController {
    
    private GridPane gridPane;

    public GridPaneController(GridPane gridPane) {
        this.gridPane = gridPane;
    }
    
    public GridPane getGridPane() {
        return gridPane;
    }

    public void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    public void updateView(BoardConfiguration currentConfig, CellEventListner eventListner) {
        int d = currentConfig.getDimension();
        ImageView imgView;
        
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                CellData cell = currentConfig.getBoard()[i][j];
                currentConfig.printMatrix();
                imgView = (ImageView)GameUtils.getNodeByRowColumnIndex(i, j, gridPane);
                imgView.setImage(cell.getImg());
                imgView.setUserData(cell);
            }
        }
    }

}
