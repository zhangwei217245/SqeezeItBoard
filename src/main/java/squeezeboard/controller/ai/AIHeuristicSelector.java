package squeezeboard.controller.ai;

import squeezeboard.controller.ai.minimax.global.GlobalMoveGenerator;
import squeezeboard.controller.ai.minimax.patternbased.PatternBasedDefender;
import squeezeboard.controller.ai.minimax.patternbased.PatternBasedMoveGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zhangwei on 3/13/16.
 */
public enum AIHeuristicSelector {
    GLOBAL_MINIMAX(3) {
        @Override
        public SqueezeAI getHeuristic() {
            return globalMoveGenerator;
        }
    },
    DEFENDER(2) {
        @Override
        public SqueezeAI getHeuristic() {
            return patternBasedDefender;
        }
    },
    ATTACKER(1) {
        @Override
        public SqueezeAI getHeuristic() {
            return patternBasedMoveGenerator;
        }
    };

    protected SqueezeAI patternBasedDefender = new PatternBasedDefender();
    protected SqueezeAI globalMoveGenerator = new GlobalMoveGenerator();
    protected SqueezeAI patternBasedMoveGenerator = new PatternBasedMoveGenerator();

    AIHeuristicSelector(int index) {
        this.index = index;
    }

    private int index;

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public abstract SqueezeAI getHeuristic();

    public static List<AIHeuristicSelector> sortedHeuristics() {
        List<AIHeuristicSelector> aiHeuristics = Arrays.asList(AIHeuristicSelector.values()).stream().sorted((a, b) ->
                Integer.compare(a.getIndex(), b.getIndex())).collect(Collectors.toList());
        return aiHeuristics;
    }
}
