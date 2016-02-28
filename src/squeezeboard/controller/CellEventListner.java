package squeezeboard.controller;

import squeezeboard.view.GridPaneView;
import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import squeezeboard.model.PromptableException.ExceptFactor;
import squeezeboard.model.GameUtils;
import squeezeboard.model.PlayerColor;
import squeezeboard.view.StatusBarView;

/**
 *
 * @author zhangwei
 */
public class CellEventListner implements EventHandler<MouseEvent>{

    private final GridPaneView gridPaneView;
    
    private final StatusBarView statusBarView;
    
    @Override
    public void handle(MouseEvent event) {
        ImageView img_view = ((ImageView)event.getSource());
        CellData cell = (CellData) img_view.getUserData();
        
        switch (cell.getCellChar()) {
            case 'B':
            case 'O':
                if (GameUtils.currentColor.CHAR() != cell.getCellChar()){
                    exceptionMessage(cell, ExceptFactor.NOT_YOUR_TURN);
                    return;
                }
                if(GameUtils.pickedCell!=null){
                    // if try to pick up the same color
                    if (GameUtils.pickedCell.getCellChar()==cell.getCellChar()) {
                        // either it is the original piece. Just remove the highlight.
                        if (cell.equals(GameUtils.pickedCell)){
                            removeHighlight(cell);
                            GameUtils.pickedCell = null;
                        } else {
                            //or, pick up another.
                            pickUpAnother(cell);
                        }
                    } else {
                        exceptionMessage(cell, ExceptFactor.PIECE_ON_PIECE);
                    }
                }else{
                    pickUpPiece(cell);
                }   break;
            case 'P':
                if (GameUtils.pickedCell!=null){
                    dropOnPath(cell);
                } else {
                    exceptionMessage(cell, ExceptFactor.PICKED_UP_DATA_MESS);
                }   break;
            case 'E':
                if (GameUtils.pickedCell != null) {
                    exceptionMessage(cell, ExceptFactor.INVALID_MOVE);
                } else {
                    //Nothing to do so far
                }   break;
            default:
                break;
        }
    }
    
    private void pickUpAnother(CellData cell) {
        removeHighlight(cell);
        pickUpPiece(cell);
    }
    
    private void pickUpPiece(CellData cell) {
        GameUtils.pickedCell = cell;
        BoardConfiguration currentConfig = GameUtils.existingMoves[GameUtils.currentCursor.get()];
        CellData[][] grid = currentConfig.getBoard();
        // Checking the current row where the picked cell exists.
        checkAndHighlight(cell, grid, 0, 1);
        // Checking the current column where the picked cell exists.
        checkAndHighlight(cell, grid, 1, 0);
        
        gridPaneView.update(currentConfig, this);
    }
    
    private void dropOnPath(CellData cell) {
        removeHighlight(cell);
        cell.setCellChar(GameUtils.pickedCell.getCellChar());
        GameUtils.pickedCell.setCellChar('E');
        GameUtils.pickedCell = null;
        refreshGrid();
        GameUtils.currentColor = PlayerColor.getColorByCursor(GameUtils.currentColor.ordinal() + 1);
        refreshStatus();
    }
    
    private void refreshGrid(){
        BoardConfiguration currentConfig = GameUtils.existingMoves[GameUtils.currentCursor.get()];
        gridPaneView.update(currentConfig, this);
    }
   
    
    private void checkAndHighlight(CellData currCell, CellData[][] grid, int rowIncr, int colIncr){
        int ir = rowIncr;
        int ic = colIncr;
        int boundReached = 0;
        int row = currCell.getRowCord();
        int col = currCell.getColCord();
        int newcol = col + ic;
        int newrow = row + ir;
        while (true) {
            if (newcol <= 7 && newrow <=7 
                    && newcol >= 0 && newrow >= 0 && grid[newrow][newcol].getCellChar()=='E') {
                grid[newrow][newcol].setCellChar('P');
                newcol = newcol + ic;
                newrow = newrow + ir;
            } else {
                ir = -ir;
                ic = -ic;
                newcol = col + ic;
                newrow = row + ir;
                boundReached++;
            }
            if (boundReached >= 2) {
                break;
            }
        }
    }

    private void exceptionMessage(CellData cell, ExceptFactor exceptFactor) {
        Alert alert = new Alert(exceptFactor.getAlertType(), exceptFactor.getMsg());
        alert.showAndWait()
        .filter(response -> response == ButtonType.OK)
        .ifPresent(response -> postActionForException(cell, exceptFactor));
    }
    
    private void postActionForException(CellData cell, ExceptFactor exceptFactor) {
        
    }

    public CellEventListner(GridPaneView gridController, StatusBarView statusBarView) {
        this.gridPaneView = gridController;
        this.statusBarView = statusBarView;
    }
    
    private void removeHighlight(CellData cell) {
        BoardConfiguration currentConfig = GameUtils.existingMoves[GameUtils.currentCursor.get()];
        CellData[][] grid = currentConfig.getBoard();
        int d = currentConfig.getDimension();
        for (int i = 0; i < d ; i++) {
            for (int j = 0; j< d; j++) {
                if(grid[i][j].getCellChar() =='P'){
                    grid[i][j].setCellChar('E');
                }
            }
        }
        refreshGrid();
    }

    private void refreshStatus() {
        statusBarView.update();
    }
    
    
}
