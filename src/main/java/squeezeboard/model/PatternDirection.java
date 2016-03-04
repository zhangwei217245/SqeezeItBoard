/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.model;

/**
 *
 * @author zhangwei
 */
public enum PatternDirection {
    
    VERTICAL {
        @Override
        public CellData getCellInAGroup(int idxInGroup, CellData piece, CellData[][] board) {
            return board[idxInGroup][piece.getColCord()];
        }

        @Override
        public int getIndexInAGroup(CellData cell) {
            return cell.getRowCord();
        }
    },
    HORIZONTAL {
        @Override
        public CellData getCellInAGroup(int idxInGroup, CellData piece, CellData[][] board) {
            return board[piece.getRowCord()][idxInGroup];
        }

        @Override
        public int getIndexInAGroup(CellData cell) {
            return cell.getColCord();
        }
    };


    public abstract CellData getCellInAGroup(int idxInGroup, CellData piece, CellData[][] board);

    public abstract int getIndexInAGroup(CellData cell);


}
