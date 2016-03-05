package squeezeboard.controller;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import squeezeboard.controller.ai.AlphaBetaPruning;
import squeezeboard.controller.ai.SqueezeAI;
import squeezeboard.model.*;
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

    private final SqueezeAI squeezeAI = new AlphaBetaPruning();
    
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
                            removeHighlight();
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
                    computerDropPiece();
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
        removeHighlight();
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
        removeHighlight();
        cell.setCellChar(GameUtils.pickedCell.getCellChar());
        GameUtils.pickedCell.setCellChar('E');
        GameUtils.pickedCell = null;
        //try to remove pattern here
        int removalCount = GameUtils.tryRemovePattern(cell, GameUtils.getCurrentBoardConfiguration(), 
                GameUtils.currentColor);
        GameUtils.currentColor.getOpponentColor().decreaseLeftCount(removalCount);
        GameUtils.copyCurrentConfiguration(GameUtils.currentColor);
        //currentColor do not change until now, a piece is dropped on board.
        GameUtils.currentColor = GameUtils.currentColor.getOpponentColor();

        refreshGrid();
        refreshStatus();

    }

    private void computerDropPiece() {
        if (GameUtils.game_started.get()) {
            //computer AI thinks now
            Pair<CellData, CellData> optimalMove =
                    squeezeAI.findOptimalMove(GameUtils.computerRole, GameUtils.getCurrentBoardConfiguration().clone());
            // remove highlight first
            removeHighlight();
            // set piece
            GameUtils.getCurrentBoardConfiguration().setPiece(optimalMove);
            // try to remove pattern here
            int removalCount = GameUtils.tryRemovePattern(optimalMove.getSecond(), GameUtils.getCurrentBoardConfiguration(),
                    GameUtils.currentColor);
            GameUtils.currentColor.getOpponentColor().decreaseLeftCount(removalCount);
            GameUtils.copyCurrentConfiguration(GameUtils.currentColor);
            //currentColor do not change until now, a piece is dropped on board.
            GameUtils.currentColor = GameUtils.currentColor.getOpponentColor();
            refreshGrid();
            refreshStatus();
        }
    }
    
    private void refreshGrid(){
        BoardConfiguration currentConfig = GameUtils.getCurrentBoardConfiguration();
        gridPaneView.update(currentConfig);
    }
   
    



    public CellEventListner(GridPaneView gridController, StatusBarView statusBarView) {
        this.gridPaneView = gridController;
        this.statusBarView = statusBarView;
    }
    
    private void removeHighlight() {
        BoardConfiguration currentConfig = GameUtils.getCurrentBoardConfiguration();
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
