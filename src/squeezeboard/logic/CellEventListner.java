package squeezeboard.logic;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author zhangwei
 */
public class CellEventListner implements EventHandler<MouseEvent>{

    private GridPaneController gridController;
    
    public void handle(MouseEvent event) {
        System.out.println("Event hit");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ImageView img_view = ((ImageView)event.getSource());
                CellData cell = (CellData) img_view.getUserData();
                if (cell.getCellChar()=='B'||cell.getCellChar()=='O'){

                    if(GameUtils.pickedCell!=null){
                        if (GameUtils.pickedCell.getCellChar()==cell.getCellChar()) {
                            removeHighlight(cell);
                            pickUpPiece(cell);
                        }
                        exceptionMessage(cell, ExeptFactor.PieceOnPiece);
                    }else{
                        pickUpPiece(cell);
                    }
                } else if (cell.getCellChar()=='P'){
                    if (GameUtils.pickedCell!=null){
                        dropOnPath(cell);
                    } else {
                        exceptionMessage(cell, ExeptFactor.PickedUpDataMess);
                    }
                } else if (cell.getCellChar() == 'E') {
                    if (GameUtils.pickedCell != null) {
                        exceptionMessage(cell, ExeptFactor.InvalidMove);
                    } else {
                        //Nothing to do so far
                    }
                }
            }
        }).start();
        

//        ((ImageView)event.getSource()).setImage(GameUtils.img_possMove);
//        System.out.println(((ImageView)event.getSource()).getUserData());
    }

    private void pickUpPiece(CellData cell) {
        GameUtils.pickedCell = cell;
        BoardConfiguration currentConfig = GameUtils.existingMoves[GameUtils.currentCursor.get()];
        CellData[][] grid = currentConfig.getBoard();
        // Checking the current row where the picked cell exists.
        checkAndHighlight(cell, grid, 0, 1);
        // Checking the current column where the picked cell exists.
        checkAndHighlight(cell, grid, 1, 0);
        
        gridController.updateView(currentConfig, this);
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
                grid[newrow][newcol].setImg(GameUtils.img_possMove);
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

    private void dropOnPath(CellData cell) {
        removeHighlight(cell);
        cell.setImg(GameUtils.pickedCell.getImg());
        cell.setCellChar(GameUtils.pickedCell.getCellChar());
        GameUtils.pickedCell.setImg(GameUtils.img_empty);
        GameUtils.pickedCell.setCellChar('E');
        BoardConfiguration currentConfig = GameUtils.existingMoves[GameUtils.currentCursor.get()];
        gridController.updateView(currentConfig, this);
        GameUtils.pickedCell = null;
    }

    private void exceptionMessage(CellData cell, ExeptFactor PickedUpDataMess) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public CellEventListner(GridPaneController gridController) {
        this.gridController = gridController;
    }

    private void removeHighlight(CellData cell) {
        BoardConfiguration currentConfig = GameUtils.existingMoves[GameUtils.currentCursor.get()];
        CellData[][] grid = currentConfig.getBoard();
        int d = currentConfig.getDimension();
        for (int i = 0; i < d ; i++) {
            for (int j = 0; j< d; j++) {
                if(grid[i][j].getCellChar() =='P'){
                    grid[i][j].setCellChar('E');
                    grid[i][j].setImg(GameUtils.img_empty);
                }
            }
        }
    }
    
    
}
