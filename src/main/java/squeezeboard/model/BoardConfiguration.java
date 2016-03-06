/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.model;

import javafx.scene.image.Image;

/**
 *
 * @author zhangwei
 */
public final class BoardConfiguration implements Cloneable{
    
    private CellData[][] board;
    
    private int dimension;
    
    private PlayerColor moveMaker;

    public BoardConfiguration(int dimension) {
        this.dimension = dimension;
        board = new CellData[this.dimension][this.dimension];
        initializeBoard();
    }
    
    public BoardConfiguration(CellData[][] board){
        this.board = board;
        this.dimension = this.board.length;
    }
    
    public void initializeBoard(){
        Image img;
        char cellChar;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (i) {
                    case 0:
                        cellChar = 'B';
                        break;
                    case 7:
                        cellChar  = 'O';
                        break;
                    default:
                        cellChar = 'E';
                }
                board[i][j] = new CellData(j, i, cellChar);
            }
        }
    }

    public CellData[][] getBoard() {
        return board;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public PlayerColor getMoveMaker() {
        return moveMaker;
    }

    public void setMoveMaker(PlayerColor moveMaker) {
        this.moveMaker = moveMaker;
    }
    
    public void destroy(){
        for (int i = 0; i < board.length; i++) {
            for(int j = 0; j < board.length; j++) {
                board[i][j] = null;
            }
            board[i] = null;
        } 
    }
    
    @Override
    public BoardConfiguration clone(){
        CellData[][] board = new CellData[dimension][dimension];
        for (int i = 0; i < board.length; i++) {
            for(int j = 0; j < board.length; j++) {
                board[i][j] = (CellData) this.board[i][j].clone();
            }
        }
        BoardConfiguration result = new BoardConfiguration(board);
        return result;
    }
    
    
    public void printMatrix() {
        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++) {
                System.out.print(String.format("%c ", board[i][j].getCellChar()));
            }
            System.out.println("");
        }
        System.out.println("================");
    }


    public CellData getCellByCoordination(int row, int col){
        return this.board[row][col];
    }

    public void setPiece(Pair<CellData, CellData> move) {
        //System.out.println(move);
        this.board[move.getSecond().getRowCord()][move.getSecond().getColCord()]
                .setCellChar(move.getFirst().getCellChar());
        this.board[move.getFirst().getRowCord()][move.getFirst().getColCord()].setCellChar('E');
    }
}
