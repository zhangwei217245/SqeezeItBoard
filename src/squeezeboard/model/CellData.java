package squeezeboard.model;

import javafx.scene.image.Image;

/**
 *
 * @author zhangwei
 */
public class CellData {
    
    private int rowCord;
    private int colCord;
    private char cellChar; // O, B, P, E
    private Image img;

    public CellData(int colCord, int rowCord, char cellChar, Image img) {
        this.rowCord = rowCord;
        this.colCord = colCord;
        this.cellChar = cellChar;
        this.img = img;
    }

    public int getRowCord() {
        return rowCord;
    }

    public void setRowCord(int rowCord) {
        this.rowCord = rowCord;
    }

    public int getColCord() {
        return colCord;
    }

    public void setColCord(int colCord) {
        this.colCord = colCord;
    }

    public char getCellChar() {
        return cellChar;
    }

    public void setCellChar(char cellChar) {
        this.cellChar = cellChar;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }
}
