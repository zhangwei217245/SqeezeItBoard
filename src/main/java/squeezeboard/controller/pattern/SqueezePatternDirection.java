/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.controller.pattern;

import squeezeboard.controller.ai.AIUtils;
import squeezeboard.model.CellData;
import squeezeboard.model.GameUtils;
import squeezeboard.model.Pair;
import squeezeboard.model.PlayerColor;
import squeezeboard.model.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhangwei
 */
public enum SqueezePatternDirection {
    
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
        public List<Tuple<CellData, CellData, Integer>> findPossibleAttackingMoves(SqueezePattern pattern, CellData[][] board, PlayerColor playerColor, boolean recursive) {
            List<Tuple<CellData, CellData, Integer>> result = new ArrayList<>();
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
                    List<Tuple<CellData, CellData, Integer>> allSupportivePieces = findAllSupportivePieces(0, c, r, playerColor, board, recursive);
                    result.addAll(allSupportivePieces);
                }
            }
            return result;
        }

        @Override
        public List<Tuple<CellData, CellData, Integer>> findPossibleDefensiveMoves(SqueezePattern squeezePattern, CellData[][] board, PlayerColor playerColor) {
            List<Tuple<CellData, CellData, Integer>> result = new ArrayList<>();
            Pair<CellData, CellData> patternBothEnds = squeezePattern.getPatternBothEnds();
            int c = patternBothEnds.getFirst().getColCord();
            int start = patternBothEnds.getFirst().getRowCord();
            int end = patternBothEnds.getSecond().getRowCord();
            for (int r = start; r <= end; r++) {
                CellData cell = board[r][c];
                if (cell.getCellChar()==playerColor.CHAR()) {
                    List<Pair<CellData, CellData>> allPossibleMoves = AIUtils.getAllPossibleMovesForACell(cell, board);
                    allPossibleMoves.forEach(move -> {
                        int distance = move.getFirst().getRowCord() == move.getSecond().getRowCord()?
                                move.getFirst().getColCord()-move.getSecond().getColCord():
                                move.getFirst().getRowCord()-move.getSecond().getRowCord();
                        result.add(new Tuple<>(move.getFirst(), move.getSecond(), Math.abs(distance)));
                    });
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
        public List<Tuple<CellData, CellData, Integer>> findPossibleAttackingMoves(SqueezePattern squeezePattern, CellData[][] board, PlayerColor playerColor, boolean recursive) {
            List<Tuple<CellData, CellData, Integer>> result = new ArrayList<>();
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
                    List<Tuple<CellData, CellData, Integer>> allSupportivePieces = findAllSupportivePieces(0, c, r, playerColor, board, recursive);
                    result.addAll(allSupportivePieces);
                }
            }
            return result;
        }



        @Override
        public List<Tuple<CellData, CellData, Integer>> findPossibleDefensiveMoves(SqueezePattern squeezePattern, CellData[][] board, PlayerColor playerColor) {
            List<Tuple<CellData, CellData, Integer>> result = new ArrayList<>();
            Pair<CellData, CellData> patternBothEnds = squeezePattern.getPatternBothEnds();
            int r = patternBothEnds.getFirst().getRowCord();
            int start = patternBothEnds.getFirst().getColCord();
            int end = patternBothEnds.getSecond().getColCord();
            for (int c = start; c <= end; c++) {
                CellData cell = board[r][c];
                if (cell.getCellChar()==playerColor.CHAR()) {
                    List<CellData> destinations = new ArrayList<>();
                    GameUtils.checkAndHighlight(cell, board, 1, 0, destinations);
                    GameUtils.checkAndHighlight(cell, board, 0, 1, destinations);
                    GameUtils.removeHighlight(board);
                    destinations.forEach(destination -> {
                        int distance = cell.getRowCord() == destination.getRowCord()?
                                cell.getColCord()-destination.getColCord():
                                cell.getRowCord()-destination.getRowCord();
                        result.add(new Tuple<>(cell, destination, Math.abs(distance)));
                    });
                }
            }
            return result;
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

    public abstract List<Tuple<CellData, CellData, Integer>> findPossibleAttackingMoves(SqueezePattern squeezePattern, CellData[][] board, PlayerColor playerColor, boolean recursive);

    public abstract List<Tuple<CellData, CellData, Integer>> findPossibleDefensiveMoves(SqueezePattern squeezePattern, CellData[][] board, PlayerColor playerColor);

    public abstract List<Pair<String,Integer>> getGroupStrList(CellData[][] board);

    public static List<Tuple<CellData, CellData, Integer>> findAllSupportivePieces(int depth, int c, int r, PlayerColor playerColor, CellData[][] board, boolean recursive){
        List<Tuple<CellData, CellData, Integer>> allSupportivePieces = new ArrayList<>();
        //checking horizontally for supportive pieces.
        List<Tuple<CellData, CellData, Integer>> subSupportiveMoves = new ArrayList<>();
        for (int i = c - 1; i >= 0; i--) {
            CellData testingCell = board[r][i];
            if (testingCell.getCellChar() == playerColor.getOpponentColor().CHAR()) {
                break;
            }
            if (testingCell.getCellChar() == playerColor.CHAR()) {
                allSupportivePieces.add(new Tuple<>(testingCell, board[r][c], depth));
                break;
            }
            if (depth <= GameUtils.SEARCH_DEPTH && recursive) {
                if (testingCell.getCellChar() == 'E' || testingCell.getCellChar() == 'P') {
                        subSupportiveMoves.addAll(findAllSupportivePieces(depth + 1, c, r, playerColor, board, recursive));
                }
            }
        }
        for (int i = c + 1; i < board.length; i++) {
            CellData testingCell = board[r][i];
            if (testingCell.getCellChar() == playerColor.getOpponentColor().CHAR()) {
                break;
            }
            if (testingCell.getCellChar() == playerColor.CHAR()) {
                allSupportivePieces.add(new Tuple<>(testingCell, board[r][c], depth));
                break;
            }
            if (depth <= GameUtils.SEARCH_DEPTH && recursive) {
                if (testingCell.getCellChar() == 'E' || testingCell.getCellChar() == 'P') {
                    subSupportiveMoves.addAll(findAllSupportivePieces(depth + 1, c, r, playerColor, board, recursive));
                }
            }
        }

        //check vertically for supportive pieces
        for (int i = r - 1; i >= 0; i--) {
            CellData testingCell = board[i][c];
            if (testingCell.getCellChar() == playerColor.getOpponentColor().CHAR()) {
                break;
            }
            if (testingCell.getCellChar() == playerColor.CHAR()) {
                allSupportivePieces.add(new Tuple<>(testingCell, board[r][c], depth));
                break;
            }
            if (depth <= GameUtils.SEARCH_DEPTH && recursive) {
                if (testingCell.getCellChar() == 'E' || testingCell.getCellChar() == 'P') {
                    subSupportiveMoves.addAll(findAllSupportivePieces(depth + 1, c, r, playerColor, board, recursive));
                }
            }
        }
        for (int i = r + 1; i < board.length; i++) {
            CellData testingCell = board[i][c];
            if (testingCell.getCellChar() == playerColor.getOpponentColor().CHAR()) {
                break;
            }
            if (testingCell.getCellChar() == playerColor.CHAR()) {
                allSupportivePieces.add(new Tuple<>(testingCell, board[r][c], depth));
                break;
            }
            if (depth <= GameUtils.SEARCH_DEPTH && recursive) {
                if (testingCell.getCellChar() == 'E' || testingCell.getCellChar() == 'P') {
                    subSupportiveMoves.addAll(findAllSupportivePieces(depth + 1, c, r, playerColor, board, recursive));
                }
            }
        }
        return allSupportivePieces.isEmpty()?subSupportiveMoves:allSupportivePieces;
    }


}
