/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.controller.pattern;

import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import squeezeboard.model.GameUtils;
import squeezeboard.model.PatternDirection;
import squeezeboard.model.PlayerColor;

/**
 *
 * @author zhangwei
 */
public enum SqueezePatternType {
    CONSECUTIVE {
        @Override
        public double score(SqueezePattern pattern) {
            return eliminating_2(pattern) - eliminating_consecutive(pattern);
        }
        /**
         * Wreck'em!
         *
         * @param pattern
         * @return
         */
        @Override
        public int tryEliminate(SqueezePattern pattern) {
            
            CellData[] patternBlocks = pattern.getPattern();
            PatternDirection direction = pattern.getPatternDirection();
            CellData[][] currentBoard = GameUtils.getCurrentBoard();
            char opponentChar = PlayerColor.getColorByCursor(pattern.getPatternCreator()
                    .ordinal() + 1).CHAR();
            CellData leftBound = null;
            CellData rightBound = null; 
            Gap opponent_gap = findingOpponentSurroundingGap(pattern);
            if (opponent_gap.left_pos >= 0 && opponent_gap.right_pos >= 0) {//GAP FOUND
                int opponent_gap_size = Math.abs(opponent_gap.right_pos
                        - opponent_gap.left_pos);
                if (opponent_gap_size == this.size(pattern)) {
                    int rowIdx = patternBlocks[0].getRowCord();
                    if (PatternDirection.HORIZONTAL.equals(direction)) {
                        leftBound = currentBoard[rowIdx][opponent_gap.left_pos];
                        rightBound = currentBoard[rowIdx][opponent_gap.right_pos];
                    } else if (PatternDirection.VERTICAL.equals(direction)) {
                        int colIdx = patternBlocks[0].getColCord();
                        leftBound = currentBoard[opponent_gap.left_pos][colIdx];
                        rightBound = currentBoard[opponent_gap.right_pos][colIdx];
                    }
                    if (opponentChar == leftBound.getCellChar() && opponentChar == rightBound.getCellChar()) {
                        leftBound.setCellChar('E');
                        rightBound.setCellChar('E');
                        return 2;
                    }
                }
            }
            
            return 0;
        }

        /**
         * should be attacking score for a consecutive
         *
         * @param pattern
         * @return
         */
        @Override
        public double eliminating_2(SqueezePattern pattern) {
            Gap opponent_gap = findingOpponentSurroundingGap(pattern);
            if (opponent_gap.left_pos >= 0 && opponent_gap.right_pos >= 0) {//GAP FOUND
                int opponent_gap_size = Math.abs(opponent_gap.right_pos
                        - opponent_gap.left_pos);
                if (opponent_gap_size == this.size(pattern)) {
                    //eliminatable
                    return 1.0d * 2.0d;
                } else {
                    //un-eliminatable
                    return (((double) opponent_gap_size - (double) opponent_gap.gap_empty)
                            / (double) opponent_gap_size)
                            * 2.0d;
                }
            }
            //GAP NOT FOUND
            return 0.0d;
        }

        /**
         * danger to be eliminated as consecutive
         *
         * @param pattern
         * @return
         */
        @Override
        public double eliminating_consecutive(SqueezePattern pattern) {
            Gap opponent_gap = findingOpponentSurroundingGap(pattern);
            
            if (opponent_gap.left_pos >= 0 && opponent_gap.right_pos >= 0) {//GAP FOUND
                int opponent_gap_size = Math.abs(opponent_gap.right_pos
                        - opponent_gap.left_pos);
                if (opponent_gap_size == this.size(pattern)) {
                    return this.size(pattern);
                } else {
                    if ((opponent_gap.left_pos + 1 == opponent_gap.left_inner_con_pos)
                            ||opponent_gap.right_pos - 1 == opponent_gap.right_inner_con_pos){
                        return 1.0d/2.0d * this.size(pattern);
                    }
                    return 0.0d;
                }
            } else if (opponent_gap.left_pos >= 0 || opponent_gap.right_pos >= 0){//Incomplete Gap Found
                if ((opponent_gap.left_pos + 1 == opponent_gap.left_inner_con_pos)
                            ||opponent_gap.right_pos - 1 == opponent_gap.right_inner_con_pos){
                    return 1.0d/2.0d * this.size(pattern);
                }
                return 0.0d;
            }
            //No GAP FOUND
            return 0.0d;
        }

    },
    GAP {
        @Override
        public double score(SqueezePattern pattern) {
            return eliminating_consecutive(pattern) - eliminating_2(pattern);
        }

        @Override
        public int tryEliminate(SqueezePattern pattern) {
            String patternString = GameUtils.getPatternString(pattern);
            int eliminated = 0;
            for (PlayerColor color : PlayerColor.values()) {
                if (color.equals(pattern.getPatternCreator())) {
                    if (color.getFullGapPattern().matcher(patternString).find()){
                        for (int i = 1; i < pattern.getPattern().length-1; i++) {
                            pattern.getPattern()[i].setCellChar('E');
                            eliminated++;
                        }
                    }
                }
            }
            return eliminated;
        }

        @Override
        public double eliminating_2(SqueezePattern pattern) {
            int emptys = 0;
            for (CellData cell : pattern.getPattern()) {
                if (cell.getCellChar()=='E'||cell.getCellChar()=='P'){
                    emptys++;
                }
            }
            int gapSize = pattern.getPattern().length - 2;
            
            return ((double)(gapSize-emptys)/(double)gapSize) * 2.0d;
        }

        @Override
        public double eliminating_consecutive(SqueezePattern pattern) {
            String patternString = GameUtils.getPatternString(pattern);
            PlayerColor opponentColor = PlayerColor
                    .getColorByCursor(pattern.getPatternCreator().ordinal() + 1);
            int opponentConsecutive_size = 0;
            char opponentChar = opponentColor.CHAR();
            for (char c : patternString.toCharArray()) {
                if (c == opponentChar) {
                    opponentConsecutive_size ++;
                }
            }
            return (double)opponentConsecutive_size;
        }

    };

    /**
     * The maximum number of pieces required for forming a gap.
     */
    public abstract double eliminating_2(SqueezePattern pattern);

    public abstract double eliminating_consecutive(SqueezePattern pattern);

    public double maxEliminatingGap(SqueezePattern pattern) {
        return 2.0d;
    }
    
    public double maxEliminatingConsecutive(SqueezePattern pattern) {
        return (double)this.size(pattern);
    }
    
    public int size(SqueezePattern pattern) {
        return pattern.getPattern().length;
    }

    public abstract double score(SqueezePattern pattern);

    /**
     * Given a pattern, eliminate its opponent's pieces, return the number of pieces that
     * is eliminated.
     * @param pattern
     * @return 
     */
    public abstract int tryEliminate(SqueezePattern pattern);

    protected Gap findingOpponentSurroundingGap(SqueezePattern pattern) {
        Gap gap = new Gap();
        BoardConfiguration currentBoardConfiguration = GameUtils.getCurrentBoardConfiguration();
        CellData[] patternBlocks = pattern.getPattern();
        CellData startingBlock = patternBlocks[0];
        CellData endingBlock = patternBlocks[patternBlocks.length - 1];
        char opponentChar = PlayerColor.getColorByCursor(pattern.getPatternCreator()
                .ordinal() + 1).CHAR();
        int opponent_gap_empty = 0;
        int opponent_gap_left_pos = -1;
        int opponent_gap_right_pos = -1;
        int groupIndex = 0;
        int i = -1;
        int j = -1;
        char l = ' ';
        char r = ' ';
        if (PatternDirection.HORIZONTAL.equals(pattern.getPatternDirection())) {
            groupIndex = startingBlock.getRowCord();
            i = startingBlock.getColCord();
            j = endingBlock.getColCord();
        } else if (PatternDirection.VERTICAL.equals(pattern.getPatternDirection())) {
            groupIndex = startingBlock.getColCord();
            i = startingBlock.getRowCord();
            j = endingBlock.getRowCord();
        }
        gap.setLeft_inner_con_pos(i);
        gap.setRight_inner_con_pos(j);
        boolean gap_left_found = false;
        boolean gap_right_found = false;
        while (i > 0 || j < currentBoardConfiguration.getDimension()) {
            i--;
            j++;
            if (i >= 0 && (!gap_left_found)) {
                if (PatternDirection.HORIZONTAL.equals(pattern.getPatternDirection())) {
                    l = currentBoardConfiguration
                            .getBoard()[groupIndex][i].getCellChar();
                } else if (PatternDirection.VERTICAL.equals(pattern.getPatternDirection())) {
                    l = currentBoardConfiguration
                            .getBoard()[i][groupIndex].getCellChar();
                }
                if (l != opponentChar) {
                    if (l == 'P' || l == 'E') {
                        opponent_gap_empty++;
                    }
                } else {
                    opponent_gap_left_pos = i;
                }
            }
            if (j < currentBoardConfiguration.getDimension() && (!gap_right_found)) {
                if (PatternDirection.HORIZONTAL.equals(pattern.getPatternDirection())) {
                    r = currentBoardConfiguration
                            .getBoard()[groupIndex][j].getCellChar();
                } else if (PatternDirection.VERTICAL.equals(pattern.getPatternDirection())) {
                    r = currentBoardConfiguration
                            .getBoard()[j][groupIndex].getCellChar();
                }
                if (r != opponentChar) {
                    if (r == 'P' || r == 'E') {
                        opponent_gap_empty++;
                    }
                } else {
                    opponent_gap_right_pos = j;
                }
            }
        }
        
        gap.setLeft_pos(opponent_gap_left_pos);
        gap.setRight_pos(opponent_gap_right_pos);
        gap.setGap_empty(opponent_gap_empty);
        return gap;
    }

    class Gap {

        private int left_pos = -2;
        private int right_pos = -2;
        private int left_inner_con_pos = -2;
        private int right_inner_con_pos = -2;
        private int gap_empty = 0;

        public int getLeft_pos() {
            return left_pos;
        }

        public void setLeft_pos(int left_pos) {
            this.left_pos = left_pos;
        }

        public int getRight_pos() {
            return right_pos;
        }

        public void setRight_pos(int right_pos) {
            this.right_pos = right_pos;
        }

        public int getGap_empty() {
            return gap_empty;
        }

        public void setGap_empty(int gap_empty) {
            this.gap_empty = gap_empty;
        }

        public int getLeft_inner_con_pos() {
            return left_inner_con_pos;
        }

        public void setLeft_inner_con_pos(int left_inner_con_pos) {
            this.left_inner_con_pos = left_inner_con_pos;
        }

        public int getRight_inner_con_pos() {
            return right_inner_con_pos;
        }

        public void setRight_inner_con_pos(int right_inner_con_pos) {
            this.right_inner_con_pos = right_inner_con_pos;
        }
    }
}
