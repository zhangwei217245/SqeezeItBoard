package squeezeboard.model;

import java.util.Objects;
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

    @Override
    public String toString() {
        return "CellData{" + "rowCord=" + rowCord + ", colCord=" + colCord + ", cellChar=" + cellChar + ", img=" + img + '}';
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + this.rowCord;
        hash = 19 * hash + this.colCord;
        hash = 19 * hash + this.cellChar;
        hash = 19 * hash + Objects.hashCode(this.img);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CellData other = (CellData) obj;
        if (this.rowCord != other.rowCord) {
            return false;
        }
        if (this.colCord != other.colCord) {
            return false;
        }
        if (this.cellChar != other.cellChar) {
            return false;
        }
        if (!Objects.equals(this.img, other.img)) {
            return false;
        }
        return true;
    }
    
    
}
