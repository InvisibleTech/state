package org.invisibletech;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.invisibletech.State.Point;

public class StateCluster {
    private static class StateDistance {
        public final State state;
        public final double distanceFromSeed;

        public StateDistance(final State state, final State seed) {
            this.state = state;
            this.distanceFromSeed = GeoMath.distance(state.centroid, seed.centroid);
        }
    }

    public final List<State> members;
    public final Point boundingRectangleCentroid;

    public StateCluster(final List<State> members) {
        this.members = members;
        this.boundingRectangleCentroid = GeoMath.computeCentroidOfBoundingBox(members);
    }

    public static List<StateCluster> clusterStates(final int maxNumberOfClusters, final List<State> states) {
        final int maxStatesPerCluster = Optional.of(states.size() / maxNumberOfClusters).filter(n -> n > 0)
                .orElse(states.size());

        final List<State> statesToProcess = new ArrayList<>(states);
        final List<StateCluster> clusters = new ArrayList<>();
        while (!statesToProcess.isEmpty()) {
            final List<State> computedMembers = new ArrayList<>();
            final State seedState = statesToProcess.remove(0);
            computedMembers.add(seedState);

            final List<State> collected = statesToProcess.stream().map(s -> new StateDistance(s, seedState))
                    .sorted((sd1, sd2) -> Double.compare(sd1.distanceFromSeed, sd2.distanceFromSeed))
                    .limit(maxStatesPerCluster).map(sd -> sd.state).collect(Collectors.toList());

            computedMembers.addAll(collected);

            clusters.add(new StateCluster(computedMembers));

            statesToProcess.removeAll(collected);
        }

        return clusters;
    }

}
