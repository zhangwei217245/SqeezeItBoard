/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.model;

import squeezeboard.controller.pattern.SqueezePattern;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhangwei
 */
public enum PatternDirection {
    
    VERTICAL {
        @Override
        public CellData getCellInAGroup(int idxInGroup, int groupIndex, CellData[][] board) {
            return board[idxInGroup][groupIndex];
        }

        @Override
        public CellData getCellInAGroup(int idxInGroup, CellData piece, CellData[][] board) {
            return getCellInAGroup(idxInGroup, piece.getColCord(), board);
        }

        @Override
        public int getIndexInAGroup(CellData cell) {
            return cell.getRowCord();
        }

        @Override
        public List<Pair<CellData, CellData>> findPossibleAttackingMoves(SqueezePattern pattern, CellData[][] board, PlayerColor playerColor) {
            List<Pair<CellData, CellData>> result = new ArrayList<>();
            Pair<CellData, CellData> patternBothEnds = pattern.getPatternBothEnds();
            int c = patternBothEnds.getFirst().getColCord();
            int start = patternBothEnds.getFirst().getRowCord()-1 < 0?
                    patternBothEnds.getFirst().getRowCord():
                    patternBothEnds.getFirst().getRowCord()-1;
            int end = patternBothEnds.getSecond().getRowCord()+1 >= board.length?
                    patternBothEnds.getSecond().getRowCord():
                    patternBothEnds.getSecond().getRowCord()+1;
            for (int r = start; r <= end; r++) {
                CellData cell = board[r][c];
                if (cell.getCellChar()=='E') {
                    for (int i = 0; i < board.length ; i++) {
                        CellData testingCell = board[r][i];
                        if (testingCell.getCellChar() == playerColor.CHAR()) {
                            Pair<CellData, CellData> possibleMove = new Pair<>(testingCell, cell);
                            result.add(possibleMove);
                        }
                    }
                }
            }
            return result;
        }

        @Override
        public List<Pair<CellData, CellData>> findPossibleDefensiveMoves(SqueezePattern squeezePattern, CellData[][] board, PlayerColor playerColor) {
            List<Pair<CellData, CellData>> result = new ArrayList<>();
            Pair<CellData, CellData> patternBothEnds = squeezePattern.getPatternBothEnds();
            int c = patternBothEnds.getFirst().getColCord();
            int start = patternBothEnds.getFirst().getRowCord();
            int end = patternBothEnds.getSecond().getRowCord();
            for (int r = start; r <= end; r++) {
                CellData cell = board[r][c];
                if (cell.getCellChar()==playerColor.CHAR()) {
                    for (int i = 0; i < board.length ; i++) {
                        CellData testingCell = board[r][i];
                        if (testingCell.getCellChar() == playerColor.CHAR()) {
                            Pair<CellData, CellData> possibleMove = new Pair<>(testingCell, cell);
                            result.add(possibleMove);
                        }
                    }
                }
            }
            return result;
        }


        @Override
        public List<Pair<String,Integer>> getGroupStrList(CellData[][] board) {
            List<Pair<String,Integer>> result = new ArrayList<>();
            StringBuilder sb = null;
            for (int c = 0; c < board.length; c++) {
                sb = new StringBuilder();
                for (int r = 0; r < board.length; r++) {
                    CellData cell = board[r][c];
                    sb.append(cell.getCellChar());
                }
                result.add(new Pair<>(sb.toString(), c));
            }
            return result;
        }
    },
    HORIZONTAL {
        @Override
        public CellData getCellInAGroup(int idxInGroup, int groupIndex, CellData[][] board) {
            return board[groupIndex][idxInGroup];
        }

        @Override
        public CellData getCellInAGroup(int idxInGroup, CellData piece, CellData[][] board) {
            return getCellInAGroup(idxInGroup, piece.getRowCord(), board);
        }

        @Override
        public int getIndexInAGroup(CellData cell) {
            return cell.getColCord();
        }

        @Override
        public List<Pair<CellData, CellData>> findPossibleAttackingMoves(SqueezePattern squeezePattern, CellData[][] board, PlayerColor playerColor) {
            List<Pair<CellData, CellData>> result = new ArrayList<>();
            Pair<CellData, CellData> patternBothEnds = squeezePattern.getPatternBothEnds();
            int r = patternBothEnds.getFirst().getRowCord();
            int start = patternBothEnds.getFirst().getColCord()-1 < 0?
                    patternBothEnds.getFirst().getColCord():
                    patternBothEnds.getFirst().getColCord()-1;
            int end = patternBothEnds.getSecond().getColCord()+1 >= board.length?
                    patternBothEnds.getSecond().getColCord():
                    patternBothEnds.getSecond().getColCord()+1;
            for (int c = start; c <= end; c++) {
                CellData cell = board[r][c];
                if (cell.getCellChar()=='E') {
                    for (int i = 0; i < board.length ; i++) {
                        CellData testingCell = board[i][c];
                        if (testingCell.getCellChar() == playerColor.CHAR()) {
                            Pair<CellData, CellData> possibleMove = new Pair<>(testingCell, cell);
                            result.add(possibleMove);
                        }
                    }
                }
            }
            return result;
        }

        @Override
        public List<Pair<CellData, CellData>> findPossibleDefensiveMoves(SqueezePattern squeezePattern, CellData[][] board, PlayerColor playerColor) {
            return null;
        }


        @Override
        public List<Pair<String,Integer>> getGroupStrList(CellData[][] board) {
            List<Pair<String,Integer>> result = new ArrayList<>();
            StringBuilder sb = null;
            for (int r = 0; r < board.length; r++) {
                sb = new StringBuilder();
                for (int c = 0; c < board.length; c++) {
                    CellData cell = board[r][c];
                    sb.append(cell.getCellChar());
                }
                result.add(new Pair<>(sb.toString(), r));
            }
            return result;
        }
    };

    public abstract CellData getCellInAGroup(int idxInGroup, int groupIndex, CellData[][] board);

    public abstract CellData getCellInAGroup(int idxInGroup, CellData piece, CellData[][] board);

    public abstract int getIndexInAGroup(CellData cell);

    public abstract List<Pair<CellData, CellData>> findPossibleAttackingMoves(SqueezePattern squeezePattern, CellData[][] board, PlayerColor playerColor);

    public abstract List<Pair<CellData, CellData>> findPossibleDefensiveMoves(SqueezePattern squeezePattern, CellData[][] board, PlayerColor playerColor);

    public abstract List<Pair<String,Integer>> getGroupStrList(CellData[][] board);


}
