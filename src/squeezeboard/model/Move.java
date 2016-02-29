/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.model;

import squeezeboard.controller.pattern.SqueezePattern;

/**
 *
 * @author zhangwei
 */
public class Move {
    
    private final CellData origCell;
    private final CellData goalCell;
    private final PlayerColor moveMaker;

    public Move(CellData origCell, CellData goalCell, PlayerColor moveMaker) {
        this.origCell = origCell;
        this.goalCell = goalCell;
        this.moveMaker = moveMaker;
    }
    
    public SqueezePattern getPattern() {
        return null;
    }

    public CellData getOrigCell() {
        return origCell;
    }

    public CellData getGoalCell() {
        return goalCell;
    }

    public PlayerColor getMoveMaker() {
        return moveMaker;
    }
    
}
