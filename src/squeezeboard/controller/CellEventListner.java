package squeezeboard.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import squeezeboard.view.GridPaneView;
import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import squeezeboard.controller.pattern.SqueezePattern;
import squeezeboard.controller.pattern.SqueezePatternFinder;
import squeezeboard.controller.pattern.SqueezePatternType;
import squeezeboard.model.PromptableException.ExceptFactor;
import squeezeboard.model.GameUtils;
import squeezeboard.model.PatternDirection;
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
                    GameUtils.showAlertBox(ExceptFactor.NOT_YOUR_TURN);
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
                        GameUtils.showAlertBox(ExceptFactor.PIECE_ON_PIECE);
                    }
                }else{
                    pickUpPiece(cell);
                }   break;
            case 'P':
                if (GameUtils.pickedCell!=null){
                    dropOnPath(cell);
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
        removeHighlight(cell);
        GameUtils.undoConfiguration();
        pickUpPiece(cell);
    }
    
    private void pickUpPiece(CellData cell) {
        // Copy the configuration.
        BoardConfiguration currentConfig = GameUtils.copyCurrentConfiguration();
        CellData[][] grid = currentConfig.getBoard();
        GameUtils.pickedCell = grid[cell.getRowCord()][cell.getColCord()];
        
        // Checking the current row where the picked cell exists.
        checkAndHighlight(GameUtils.pickedCell, grid, 0, 1);
        // Checking the current column where the picked cell exists.
        checkAndHighlight(GameUtils.pickedCell, grid, 1, 0);
        
        gridPaneView.update(currentConfig);
    }
    
    
    
    private void dropOnPath(CellData cell) {
        removeHighlight(cell);
        cell.setCellChar(GameUtils.pickedCell.getCellChar());
        GameUtils.pickedCell.setCellChar('E');
        GameUtils.pickedCell = null;
        //try to remove pattern here
        tryRemovePattern(cell);
        refreshGrid();
        //currentColor do not change until now, a piece is dropped on board.
        GameUtils.currentColor = PlayerColor.getColorByCursor(GameUtils.currentCursor.get());
        refreshStatus();
    }
    
    private void refreshGrid(){
        BoardConfiguration currentConfig = GameUtils.getCurrentBoardConfiguration();
        gridPaneView.update(currentConfig);
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


    public CellEventListner(GridPaneView gridController, StatusBarView statusBarView) {
        this.gridPaneView = gridController;
        this.statusBarView = statusBarView;
    }
    
    private void removeHighlight(CellData cell) {
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

    private void tryRemovePattern(CellData cell) {
        List<SqueezePattern> allPatterns = new ArrayList<>();
        allPatterns.addAll(SqueezePatternFinder.findPattern(cell, 
                GameUtils.currentColor.CHAR(),
                PatternDirection.VERTICAL));
        allPatterns.addAll(SqueezePatternFinder.findPattern(cell, 
                GameUtils.currentColor.CHAR(),
                PatternDirection.HORIZONTAL));
        
        SqueezePattern maxGapPattern = null;
        SqueezePattern maxConPattern = null;
        
        for (SqueezePattern pattern : allPatterns) {
            if (SqueezePatternType.CONSECUTIVE.equals(pattern.getPatternType())){
                if (maxConPattern == null) {
                    maxConPattern = pattern;
                } else if (maxConPattern.getPattern().length < pattern.getPattern().length){
                    maxConPattern = pattern;
                }
            }
            
            if (SqueezePatternType.GAP.equals(pattern.getPatternType())){
                if (maxGapPattern == null) {
                    maxGapPattern = pattern;
                } else if (maxGapPattern.getPattern().length < pattern.getPattern().length){
                    maxGapPattern = pattern;
                }
            }
        }
        int eliminated = 0;
        //compare the size and priority.
        SqueezePattern patternToBeEliminated = null;
        if (maxConPattern != null && maxGapPattern != null) {
            patternToBeEliminated = maxGapPattern.eliminating_consecutives()> 
                    maxConPattern.eliminating_2()?
                    maxGapPattern : maxConPattern;
        } else {
            patternToBeEliminated = maxGapPattern != null ? maxGapPattern : maxConPattern;
        }
        if (patternToBeEliminated != null) {
            eliminated = patternToBeEliminated.tryEliminate();
        }
        if (eliminated > 0) {
            AtomicInteger counterToBeDecreased = 
            PlayerColor.blue.equals(patternToBeEliminated.getPatternCreator())?
                    GameUtils.blueLeft : GameUtils.orangeLeft;
            int originalCounter = counterToBeDecreased.get();
            counterToBeDecreased.compareAndSet(originalCounter, originalCounter - eliminated);
        }
    }
    
    
}
