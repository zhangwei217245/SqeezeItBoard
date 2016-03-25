package squeezeboard.controller.pattern;

import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.PlayerColor;

import java.util.regex.Pattern;

/**
 *
 * @author zhangwei
 */
public enum SqueezePatternType {
    FULFILLED_GAP(10000.0d) {
        @Override
        public Pattern getPattern(PlayerColor color) {
            return color.getFulfilledGapPattern();
        }

        @Override
        public double removalRate(SqueezePattern squeezePattern, BoardConfiguration boardConfiguration) {
            return (double)squeezePattern.validRemovalCount()/(double)squeezePattern.size();
        }

    },

    GAP(1000.0d) {
        @Override
        public Pattern getPattern(PlayerColor color) {
            return color.getGapPattern();
        }

        @Override
        public double removalRate(SqueezePattern squeezePattern, BoardConfiguration boardConfiguration) {
            return (double)squeezePattern.validRemovalCount()/(double)squeezePattern.size();
        }

    },

    INCOMPLETE_GAP(100.0d) {
        @Override
        public Pattern getPattern(PlayerColor color) {
            return color.getIncompleteGapPattern();
        }

        @Override
        public double removalRate(SqueezePattern squeezePattern, BoardConfiguration boardConfiguration) {
            return (double)squeezePattern.validRemovalCount()/(double)squeezePattern.size();
        }

    };

    public final double weight;

    SqueezePatternType(double weight) {
        this.weight = weight;
    }

    public abstract Pattern getPattern(PlayerColor color);

    public abstract double removalRate(SqueezePattern squeezePattern, BoardConfiguration boardConfiguration);

    public double score(SqueezePattern squeezePattern, BoardConfiguration boardConfiguration) {
        return (double)this.weight + removalRate(squeezePattern, boardConfiguration);
    }
}
