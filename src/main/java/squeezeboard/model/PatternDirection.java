/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.model;

import squeezeboard.controller.pattern.SqueezePattern;

import java.util.List;

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

        @Override
        public List<Pair<CellData, Double>> getEmptyCellsInAPattern(SqueezePattern pattern, CellData[][] board) {
            Pair<CellData, CellData> patternBothEnds = pattern.getPatternBothEnds();
            int start = this.getIndexInAGroup(patternBothEnds.getFirst());
            int end = this.getIndexInAGroup(patternBothEnds.getSecond());
            for (int i = start; i <= end ; i++) {
                CellData emptyCell = this.getCellInAGroup(i, patternBothEnds.getFirst(), board);
                //check horizontally
            }
            return null;
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

        @Override
        public List<Pair<CellData, Double>> getEmptyCellsInAPattern(SqueezePattern pattern, CellData[][] board) {
            return null;
        }
    };


    public abstract CellData getCellInAGroup(int idxInGroup, CellData piece, CellData[][] board);

    public abstract int getIndexInAGroup(CellData cell);

    public abstract List<Pair<CellData, Double>> getEmptyCellsInAPattern(SqueezePattern pattern, CellData[][] board);


}
