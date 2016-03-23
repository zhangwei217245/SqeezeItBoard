package squeezeboard.controller.ai.minimax;

import squeezeboard.controller.ai.AIHeuristicSelector;
import squeezeboard.controller.ai.SqueezeAI;
import squeezeboard.model.BoardConfiguration;
import squeezeboard.model.CellData;
import squeezeboard.model.GameUtils;
import squeezeboard.model.Pair;
import squeezeboard.model.PlayerColor;
import squeezeboard.model.Tuple;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by zhangwei on 3/23/16.
 */
public enum OptimalMoveFinder {

    SEQUENTIAL {
        @Override
        public Pair<CellData, CellData> findOptimalMove(PlayerColor computerColor, BoardConfiguration boardConfiguration) {
            Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer> optimalMove = null;
            for (AIHeuristicSelector aiHeuristicSelector : AIHeuristicSelector.sortedHeuristics()) {
                System.out.println(aiHeuristicSelector.name());
                SqueezeAI squeezeAI = aiHeuristicSelector.getHeuristic();
                List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> optimalMoveList = squeezeAI
                        .findOptimalMove(GameUtils.computerRole, boardConfiguration.clone());
                if (!optimalMoveList.isEmpty()) {
                    optimalMove =
                            optimalMoveList.stream().max((a, b) -> Integer.compare(a.getThird(), b.getThird())).get();
                    int bestEstimate = optimalMove.getThird();
                    List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> list
                            = optimalMoveList.stream().filter(item -> item.getThird() == bestEstimate).collect(Collectors.toList());

                    optimalMove = list.get(RANDOM.nextInt(list.size()));
                    break;
                } else {
                    continue;
                }
            }
            return optimalMove == null? null: new Pair<>(optimalMove.getFirst().getFirst(), optimalMove.getFirst().getSecond());
        }
    },
    FORKJOIN {
        @Override
        public Pair<CellData, CellData> findOptimalMove(PlayerColor computerColor, BoardConfiguration boardConfiguration) {
            List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> moveList = new ArrayList<>();
            AIHeuristicSelector.sortedHeuristics().parallelStream().forEach( heuristic -> {
                moveList.addAll(
                        heuristic.getHeuristic().findOptimalMove(computerColor, boardConfiguration.clone()));
            });
            Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer> optimalMove =
                    moveList.stream().max((a, b) -> Integer.compare(a.getSecond(), b.getSecond())).get();
            int bestEstimate = optimalMove.getSecond();
            List<Tuple<Tuple<CellData, CellData, Integer>, Integer, Integer>> list
                    = moveList.stream().filter(item -> item.getSecond() == bestEstimate).collect(Collectors.toList());
            optimalMove = list.get(RANDOM.nextInt(list.size()));
            return new Pair<>(optimalMove.getFirst().getFirst(), optimalMove.getFirst().getSecond());
        }
    };

    static Random RANDOM = new SecureRandom();
    public abstract Pair<CellData, CellData> findOptimalMove(PlayerColor computerColor, BoardConfiguration boardConfiguration);
}
