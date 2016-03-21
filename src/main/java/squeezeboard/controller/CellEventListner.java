package squeezeboard.controller;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import squeezeboard.model.GameUtils;
import squeezeboard.model.PromptableException.ExceptFactor;
import squeezeboard.view.GridPaneView;
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
                    GameUtils.showAlertBox(ExceptFactor.NOT_YOUR_TURN);
                    return;
                }
                if(GameUtils.pickedCell!=null){
                    // if try to pick up the same color
                    if (GameUtils.pickedCell.getCellChar()==cell.getCellChar()) {
                        // either it is the original piece. Just remove the highlight.
                        if (cell.equals(GameUtils.pickedCell)){
                            GameUtils.removeHighlight(GameUtils.getCurrentBoardConfiguration());
                            refreshGrid();
                            GameUtils.pickedCell = null;
                        } else {
                            //or, pick up another.
                            pickUpAnother(cell);
                        }
                    } else {
                        GameUtils.showAlertBox(ExceptFactor.PIECE_ON_PIECE);
                    }
                }else{
                    pickUpPiece(cell);
                }   break;
            case 'P':
                if (GameUtils.pickedCell!=null){
                    dropOnPath(cell);
                    GameUtils.computerAction();
                } else {
                    GameUtils.showAlertBox(ExceptFactor.PICKED_UP_DATA_MESS);
                }   break;
            case 'E':
                if (GameUtils.pickedCell != null) {
                    GameUtils.showAlertBox(ExceptFactor.INVALID_MOVE);
                } else {
                    //Nothing to do so far
                }   break;
            default:
                break;
        }
    }
    
    private void pickUpAnother(CellData cell) {
        GameUtils.removeHighlight(GameUtils.getCurrentBoardConfiguration());
        refreshGrid();
        pickUpPiece(cell);
    }
    
    private void pickUpPiece(CellData cell) {
        BoardConfiguration currentConfig = GameUtils.getCurrentBoardConfiguration();
        CellData[][] grid = currentConfig.getBoard();
        GameUtils.pickedCell = grid[cell.getRowCord()][cell.getColCord()];
        // Checking the current row where the picked cell exists.
        GameUtils.checkAndHighlight(GameUtils.pickedCell, grid, 0, 1, null);
        // Checking the current column where the picked cell exists.
        GameUtils.checkAndHighlight(GameUtils.pickedCell, grid, 1, 0, null);
        gridPaneView.update(currentConfig);
    }
    
    
    
    private void dropOnPath(CellData cell) {
        GameUtils.removeHighlight(GameUtils.getCurrentBoardConfiguration());
        refreshGrid();
        GameUtils.copyCurrentConfiguration(GameUtils.currentColor);
        GameUtils.getCurrentBoard()[cell.getRowCord()][cell.getColCord()].setCellChar(GameUtils.pickedCell.getCellChar());
        GameUtils.getCurrentBoard()[GameUtils.pickedCell.getRowCord()][GameUtils.pickedCell.getColCord()].setCellChar('E');
        GameUtils.pickedCell = null;
        //try to remove pattern here
        int removalCount = GameUtils.tryRemovePattern(cell, GameUtils.getCurrentBoardConfiguration(),
                GameUtils.currentColor);
        GameUtils.currentColor.getOpponentColor().decreaseLeftCount(removalCount);

        //currentColor do not change until now, a piece is dropped on board.
        GameUtils.currentColor = GameUtils.currentColor.getOpponentColor();

        refreshGrid();
        refreshStatus();

    }

    private void refreshGrid(){
        BoardConfiguration currentConfig = GameUtils.getCurrentBoardConfiguration();
        gridPaneView.update(currentConfig);
    }


    public CellEventListner(GridPaneView gridController, StatusBarView statusBarView) {
        this.gridPaneView = gridController;
        this.statusBarView = statusBarView;
    }

    private void refreshStatus() {
        statusBarView.update();
    }


    
    
}
