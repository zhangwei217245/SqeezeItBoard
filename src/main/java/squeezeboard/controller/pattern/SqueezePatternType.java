package squeezeboard.controller.pattern;

import squeezeboard.model.PlayerColor;

import java.util.regex.Pattern;

/**
 *
 * @author zhangwei
 */
public enum SqueezePatternType {
    GAP {
        @Override
        public Pattern getPattern(PlayerColor color) {
            return color.getGapPattern();
        }
    },
    FULFILLED_GAP {
        @Override
        public Pattern getPattern(PlayerColor color) {
            return color.getFulfilledGapPattern();
        }
    },
    INCOMPLETE_GAP {
        @Override
        public Pattern getPattern(PlayerColor color) {
            return color.getIncompleteGapPattern();
        }
    };

    public abstract Pattern getPattern(PlayerColor color);

}
