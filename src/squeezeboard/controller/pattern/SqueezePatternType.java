/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.controller.pattern;

import squeezeboard.model.Move;

/**
 *
 * @author zhangwei
 */
public enum SqueezePatternType {
    CONSECUTIVE {

        @Override
        public int size(Move move) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public double score(Move move) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isEliminatable(Move move) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean tryEliminate(Move move) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    },
    
    GAP {

        @Override
        public int size(Move move) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public double score(Move move) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isEliminatable(Move move) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean tryEliminate(Move move) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
   /**
     * The maximum number of pieces required for forming a gap.
     */
    private final int MAX_GAP_PIECES = 2;
            
            
    public double eliminating_2(Move move){
        return 0.0d;
    }
    
    public double eliminating_consecutive(Move move){
        return 0.0d;
    }
    
    public double adjacent_score(Move move){
        return 0.0d;
    }
    
    public abstract int size(Move move);
    
    public abstract double score(Move move);
    
    public abstract boolean isEliminatable(Move move);
    
    public abstract boolean tryEliminate(Move move);
}
