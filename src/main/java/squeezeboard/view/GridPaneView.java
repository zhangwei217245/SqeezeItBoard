/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.view;

import squeezeboard.model.BoardConfiguration;

import javafx.scene.layout.GridPane;
import squeezeboard.controller.CellEventListner;
import squeezeboard.model.GameUtils;

/**
 *
 * @author zhangwei
 */
public class GridPaneView {
    
    private GridPane gridPane;

    public GridPaneView(GridPane gridPane) {
        this.gridPane = gridPane;
    }
    
    public GridPane getGridPane() {
        return gridPane;
    }

    public void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    public void update(BoardConfiguration currentConfig) {
        GameUtils.renderGridView(currentConfig, gridPane, currentConfig.getDimension()
                , null, null);
    }
}
