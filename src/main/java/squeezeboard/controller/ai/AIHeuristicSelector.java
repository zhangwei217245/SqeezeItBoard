package squeezeboard.controller.ai;

import squeezeboard.controller.ai.localgreedy.LocalGreedyHeuristic;
import squeezeboard.controller.ai.minimax.global.GlobalAlphaBetaPruning;
import squeezeboard.controller.ai.minimax.patternbased.PatternBasedAlphaBetaPruning;

/**
 * Created by zhangwei on 3/13/16.
 */
public enum AIHeuristicSelector {
    LOCAL_GREEDY {
        @Override
        public SqueezeAI getHeuristic() {
            return localGreedy;
        }
    },
    GLOBAL_MINIMAX {
        @Override
        public SqueezeAI getHeuristic() {
            return globalMinimax;
        }
    },
    PATTERN_BASED_MINIMAX {
        @Override
        public SqueezeAI getHeuristic() {
            return patternBasedMinimax;
        }
    };

    protected SqueezeAI localGreedy = new LocalGreedyHeuristic();
    protected SqueezeAI globalMinimax = new GlobalAlphaBetaPruning();
    protected SqueezeAI patternBasedMinimax = new PatternBasedAlphaBetaPruning();

    public abstract SqueezeAI getHeuristic();
}
