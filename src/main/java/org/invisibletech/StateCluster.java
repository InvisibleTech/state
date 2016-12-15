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

    public static class AlignedBoundingBox {
    
        public final Point leftLower;
        public final Point rightUpper;
    
        public AlignedBoundingBox(final Point leftLower, final Point rightUpper) {
            this.leftLower = leftLower;
            this.rightUpper = rightUpper;
        }
    
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((leftLower == null) ? 0 : leftLower.hashCode());
            result = prime * result + ((rightUpper == null) ? 0 : rightUpper.hashCode());
            return result;
        }
    
        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final AlignedBoundingBox other = (AlignedBoundingBox) obj;
            if (leftLower == null) {
                if (other.leftLower != null)
                    return false;
            } else if (!leftLower.equals(other.leftLower))
                return false;
            if (rightUpper == null) {
                if (other.rightUpper != null)
                    return false;
            } else if (!rightUpper.equals(other.rightUpper))
                return false;
            return true;
        }
    
        @Override
        public String toString() {
            return "Rectangle [leftLower=" + leftLower + ", rightUpper=" + rightUpper + "]";
        }

        public boolean pointContainedByRect(final Point testPoint) {
            return this.leftLower.longitude <= testPoint.longitude && this.leftLower.latitude <= testPoint.latitude
                    && testPoint.longitude <= this.rightUpper.longitude
                    && testPoint.latitude <= this.rightUpper.latitude;
        }
    }

    public final List<State> members;
    public final AlignedBoundingBox alignedBoundingBox;

    public StateCluster(final List<State> members) {
        this.members = members;
        this.alignedBoundingBox = StateCluster.computeAlignedBoundingRectanlge(members);
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

    public static AlignedBoundingBox computeAlignedBoundingRectanlge(final List<State> states) {
        double minLongitude = Double.MAX_VALUE, minLatitude = Double.MAX_VALUE;
        double maxLongitude = -Double.MAX_VALUE, maxLatitude = -Double.MAX_VALUE;
    
        for (final State state : states) {
            for (final Point point : state.border) {
                minLongitude = Math.min(minLongitude, point.longitude);
                minLatitude = Math.min(minLatitude, point.latitude);
                maxLongitude = Math.max(maxLongitude, point.longitude);
                maxLatitude = Math.max(maxLatitude, point.latitude);
            }
        }
    
        return new AlignedBoundingBox(new Point(minLongitude, minLatitude),
                new Point(maxLongitude, maxLatitude));
    }

}
