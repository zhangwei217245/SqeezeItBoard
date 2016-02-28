package squeezeboard.model;

/**
 *
 * @author zhangwei
 */
public class CellData {
    
    private int rowCord;
    private int colCord;
    private char cellChar; // O, B, P, E

    public CellData(int colCord, int rowCord, char cellChar) {
        this.rowCord = rowCord;
        this.colCord = colCord;
        this.cellChar = cellChar;
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

    @Override
    public String toString() {
        return "CellData{" + "rowCord=" + rowCord + ", colCord=" + colCord + ", cellChar=" + cellChar + '}';
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + this.rowCord;
        hash = 19 * hash + this.colCord;
        hash = 19 * hash + this.cellChar;
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
        return true;
    }
    
    
}
