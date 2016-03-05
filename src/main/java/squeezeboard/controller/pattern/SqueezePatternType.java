package squeezeboard.controller.pattern;

import squeezeboard.model.PlayerColor;

import java.util.regex.Pattern;

/**
 *
 * @author zhangwei
 */
public enum SqueezePatternType {
    GAP(1000.0d) {
        @Override
        public Pattern getPattern(PlayerColor color) {
            return color.getGapPattern();
        }

        @Override
        public double removalRate(SqueezePattern squeezePattern) {
            int capacity = squeezePattern.capacity();
            int empty = squeezePattern.emptyCount();
            int validRemoval = squeezePattern.validRemovalCount();
            return ((double)(capacity - empty))/(double)capacity * (double)validRemoval;
        }
    },
    FULFILLED_GAP(10000.0d) {
        @Override
        public Pattern getPattern(PlayerColor color) {
            return color.getFulfilledGapPattern();
        }

        @Override
        public double removalRate(SqueezePattern squeezePattern) {
            int capacity = squeezePattern.capacity();
            int empty = squeezePattern.emptyCount();
            int validRemoval = squeezePattern.validRemovalCount();
            return ((double)(capacity - empty))/(double)capacity * (double)validRemoval;
        }
    },
    INCOMPLETE_GAP(100.0d) {
        @Override
        public Pattern getPattern(PlayerColor color) {
            return color.getIncompleteGapPattern();
        }

        @Override
        public double removalRate(SqueezePattern squeezePattern) {
            //FIXME: should check the opening column to make sure the removal rate is meaningful
            //TODO: if opening column has the supportive piece, then return actual removalRate.
            //TODO: if not, return Double.NEGATIVE_INFINITY.
            int capacity = squeezePattern.capacity();
            int empty = squeezePattern.emptyCount();
            int validRemoval = squeezePattern.validRemovalCount();
            return ((double)(capacity - empty))/(double)capacity * (double)validRemoval;
        }
    };

    private double baseScore;

    SqueezePatternType(double baseScore) {
        this.baseScore = baseScore;
    }

    public abstract Pattern getPattern(PlayerColor color);

    public abstract double removalRate(SqueezePattern squeezePattern);

    public double score(SqueezePattern squeezePattern) {
        return (double)this.baseScore + removalRate(squeezePattern);
    }

}
