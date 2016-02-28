/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.model;

import squeezeboard.model.PlayerColor;
import javafx.scene.image.Image;

/**
 *
 * @author zhangwei
 */
public class BoardConfiguration {
    
    private CellData[][] board;
    
    private int dimension;
    
    private PlayerColor moveMaker;

    public BoardConfiguration(int dimension) {
        this.dimension = dimension;
        board = new CellData[this.dimension][this.dimension];
        initializeBoard();
    }
    
    public void initializeBoard(){
        Image img;
        char cellChar;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (i) {
                    case 0:
                        img= GameUtils.img_blue;
                        cellChar = 'B';
                        break;
                    case 7:
                        img= GameUtils.img_orange;
                        cellChar  = 'O';
                        break;
                    default:
                        img= GameUtils.img_empty; cellChar = 'E';
                }
                board[i][j] = new CellData(j, i, cellChar, img);
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
    
    
    
    public void printMatrix() {
        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++) {
                System.out.print(String.format("%c ", board[i][j].getCellChar()));
            }
            System.out.println("");
        }
        System.out.println("==============");
    }
    
}
