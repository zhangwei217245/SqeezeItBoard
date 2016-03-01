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

        @Override
        public boolean isEliminatable(SqueezePattern pattern) {
            CellData[] patternBlocks = pattern.getPattern();
            PatternDirection direction = pattern.getPatternDirection();
            CellData[][] currentBoard = GameUtils.getCurrentBoard();
            char opponentChar = PlayerColor.getColorByCursor(pattern.getPatternCreator()
                    .ordinal() + 1).CHAR();
            char leftChar = ' '; 
            char rightChar = ' ';
            if (PatternDirection.HORIZONTAL.equals(direction)) {
                 leftChar = currentBoard[patternBlocks[0].getRowCord()]
                        [patternBlocks[0].getColCord() + 1].getCellChar();
                 rightChar = currentBoard[patternBlocks[patternBlocks.length - 1]
                        .getRowCord()]
                        [patternBlocks[patternBlocks.length - 1].getColCord() + 1]
                        .getCellChar();
            } else if (PatternDirection.VERTICAL.equals(direction)) {
                leftChar = currentBoard[patternBlocks[0].getColCord()]
                        [patternBlocks[0].getRowCord() + 1].getCellChar();
                rightChar = currentBoard[patternBlocks[patternBlocks.length - 1].getColCord()]
                        [patternBlocks[patternBlocks.length - 1].getRowCord() + 1]
                        .getCellChar();
            }
            if (opponentChar == leftChar && opponentChar == rightChar) {
                return true;
            }
            return false;
        }

        /**
         * Wreck'em!
         * @param pattern
         * @return 
         */
        @Override
        public boolean tryEliminate(SqueezePattern pattern) {
            CellData[] patternBlocks = pattern.getPattern();
            PatternDirection direction = pattern.getPatternDirection();
            CellData[][] currentBoard = GameUtils.getCurrentBoard();
            if (PatternDirection.HORIZONTAL.equals(direction)) {
                currentBoard[patternBlocks[0].getRowCord()]
                        [patternBlocks[0].getColCord() + 1].setCellChar('E');
                currentBoard[patternBlocks[patternBlocks.length - 1].getRowCord()]
                        [patternBlocks[patternBlocks.length - 1].getColCord() + 1]
                        .setCellChar('E');
            } else if (PatternDirection.VERTICAL.equals(direction)) {
                currentBoard[patternBlocks[0].getColCord()]
                        [patternBlocks[0].getRowCord() + 1].setCellChar('E');
                currentBoard[patternBlocks[patternBlocks.length - 1].getColCord()]
                        [patternBlocks[patternBlocks.length - 1].getRowCord() + 1]
                        .setCellChar('E');
            }
            return true;
        }
        
        /**
         * should be attacking score for a consecutive
         * @param pattern
         * @return 
         */
        @Override
        public double eliminating_2(SqueezePattern pattern) {
            
            Gap opponent_gap = findingOpponentGap(pattern);
            
            if (opponent_gap.left_pos >= 0 && opponent_gap.right_pos >= 0) {//GAP FOUND
                int opponent_gap_size = Math.abs(opponent_gap.right_pos 
                        - opponent_gap.left_pos);
                if (opponent_gap_size == this.size(pattern)) {
                    //eliminatable
                    return 1.0d * 2.0d;
                } else {
                    //un-eliminatable
                    return (
                                ((double)opponent_gap_size-(double)opponent_gap.gap_empty)
                                /
                                (double)opponent_gap_size
                            ) 
                            * 
                            2.0d;
                }
            }
            //GAP NOT FOUND
            return 0.0d;
        }
        
        private Gap findingOpponentGap(SqueezePattern pattern) {
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
            if (PatternDirection.HORIZONTAL.equals(pattern.getPatternDirection())){
                groupIndex = startingBlock.getRowCord();
                i = startingBlock.getColCord();
                j = endingBlock.getColCord();
            } else if (PatternDirection.VERTICAL.equals(pattern.getPatternDirection())){
                groupIndex = startingBlock.getColCord();
                i = startingBlock.getRowCord();
                j = endingBlock.getRowCord();
            }
            boolean gap_left_found = false;
            boolean gap_right_found = false;
            while (i > 0 || j < currentBoardConfiguration.getDimension()) {
                i--;j++;
                if (i >= 0 && (!gap_left_found)) {
                    if (PatternDirection.HORIZONTAL.equals(pattern.getPatternDirection())){
                        l = currentBoardConfiguration
                                .getBoard()[groupIndex][i].getCellChar();
                    } else if (PatternDirection.VERTICAL.equals(pattern.getPatternDirection())) {
                        l = currentBoardConfiguration
                                .getBoard()[i][groupIndex].getCellChar();
                    }
                    if (l != opponentChar){
                        if (l == 'P' || l == 'E'){
                            opponent_gap_empty ++;
                        }
                    } else {
                        opponent_gap_left_pos = i;
                    }
                }
                if (j <= currentBoardConfiguration.getDimension() && (!gap_right_found)) {
                    if (PatternDirection.HORIZONTAL.equals(pattern.getPatternDirection())){
                        r = currentBoardConfiguration
                                .getBoard()[groupIndex][j].getCellChar();
                    } else if (PatternDirection.VERTICAL.equals(pattern.getPatternDirection())) {
                        r = currentBoardConfiguration
                                .getBoard()[j][groupIndex].getCellChar();
                    }
                    if (r != opponentChar){
                        if (r == 'P' || r == 'E'){
                            opponent_gap_empty ++;
                        }
                    } else {
                        opponent_gap_right_pos = j;
                    }
                }
            }
            Gap gap = new Gap();
            gap.setLeft_pos(opponent_gap_left_pos);
            gap.setRight_pos(opponent_gap_right_pos);
            gap.setGap_empty(opponent_gap_empty);
            return gap;
        }
        /**
         * danger to be eliminated as consecutive
         * @param pattern
         * @return 
         */
        @Override
        public double eliminating_consecutive(SqueezePattern pattern) {
            return 0.0d;
        }
        
        
        class Gap {
            private int left_pos = -1;
            private int right_pos = -1;
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
        }
        
    },
    
    GAP {
        @Override
        public double score(SqueezePattern pattern) {
            return eliminating_consecutive(pattern) - eliminating_2(pattern);
        }

        @Override
        public boolean isEliminatable(SqueezePattern pattern) {
            return false;
        }

        @Override
        public boolean tryEliminate(SqueezePattern pattern) {
            return false;
        }

        @Override
        public double eliminating_2(SqueezePattern pattern) {
            return 0.0d;
        }

        @Override
        public double eliminating_consecutive(SqueezePattern pattern) {
            return 0.0d;
        }
        
    };
    
   /**
     * The maximum number of pieces required for forming a gap.
     */
    private final int MAX_GAP_PIECES = 2;
            
            
    public abstract double eliminating_2(SqueezePattern pattern);
    
    public abstract double eliminating_consecutive(SqueezePattern pattern);
    
    public int size(SqueezePattern pattern) {
        return pattern.getPattern().length;
    }
    
    public abstract double score(SqueezePattern pattern);
    
    public abstract boolean isEliminatable(SqueezePattern pattern);
    
    public abstract boolean tryEliminate(SqueezePattern pattern);
}
