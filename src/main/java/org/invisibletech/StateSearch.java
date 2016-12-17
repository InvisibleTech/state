package org.invisibletech;

import org.invisibletech.State.Point;

public class StateSearch {

    // Sources for the algorithm used:
    // http://paulbourke.net/geometry/polygonmesh/#insidepoly
    // http://stackoverflow.com/questions/4287780/detecting-whether-a-gps-coordinate-falls-within-a-polygon-on-a-map
    //
    // However this algorithm doesn't select points on the border as being in
    // the state. So, added a heuristic. It is arbitrary, but if you find your
    // point on the border, whatever state's border, that is considered first
    // "wins".
    //
    // If we used the ray shooting algorithm, it has to make similarly arbitrary
    // decisions as to points on borders. See:
    // http://paulbourke.net/geometry/polygonmesh/#insidepoly.
    //
    // Reference for method:
    // http://stackoverflow.com/questions/17692922/check-is-a-point-x-y-is-between-two-points-drawn-on-a-straight-line
    //
    public static boolean isCoordInState(final State state, final Point testPoint) {
        double angle = 0.0;
        boolean onBorder = false;

        final int indexOfLastPoint = state.border.length - 1;
        // TODO Consider moving the geometry logic out to GeoMath.
        for (int i = 0; i < indexOfLastPoint; i++) {
            final Point currentPoint = state.border[i];
            final Point nextPoint = state.border[i + 1];

            onBorder = onBorder || GeoMath.isOnBorderSegment(currentPoint, nextPoint, testPoint);
            if (onBorder)
                break;

            angle += GeoMath.computeSubtendedAngle(currentPoint, nextPoint, testPoint);
        }

        return onBorder || (Math.abs(angle) >= Math.PI);
    }

    public static boolean mightCoordBeInMemberState(final StateCluster cluster, final Point testPoint) {
        return cluster.alignedBoundingBox.pointContainedByRect(testPoint);
    }
}
