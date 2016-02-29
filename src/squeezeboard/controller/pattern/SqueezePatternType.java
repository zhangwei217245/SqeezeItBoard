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
        public int size(SqueezePattern pattern) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public double score(SqueezePattern pattern) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isEliminatable(SqueezePattern pattern) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean tryEliminate(SqueezePattern pattern) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    },
    
    GAP {
        @Override
        public int size(SqueezePattern pattern) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public double score(SqueezePattern pattern) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isEliminatable(SqueezePattern pattern) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean tryEliminate(SqueezePattern pattern) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    };
    
   /**
     * The maximum number of pieces required for forming a gap.
     */
    private final int MAX_GAP_PIECES = 2;
            
            
    public double eliminating_2(SqueezePattern pattern){
        return 0.0d;
    }
    
    public double eliminating_consecutive(SqueezePattern pattern){
        return 0.0d;
    }
    
    public double adjacent_score(SqueezePattern pattern){
        return 0.0d;
    }
    
    public abstract int size(SqueezePattern pattern);
    
    public abstract double score(SqueezePattern pattern);
    
    public abstract boolean isEliminatable(SqueezePattern pattern);
    
    public abstract boolean tryEliminate(SqueezePattern pattern);
}
