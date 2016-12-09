package org.invisibletech;

import org.invisibletech.State.Point;

public class GeoMath {
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

        for (int i = 0; i < indexOfLastPoint; i++) {
            final Point currentPoint = state.border[i];
            final Point nextPoint = state.border[i + 1];

            onBorder = onBorder || isOnBorderSegment(currentPoint, nextPoint, testPoint);
            if (onBorder)
                break;

            angle += computeSubtendedAngle(currentPoint, nextPoint, testPoint);
        }

        return onBorder || (Math.abs(angle) >= Math.PI);
    }

    private static boolean isOnBorderSegment(final Point currentPoint, final Point nextPoint, final Point testPoint) {
        return (distance(currentPoint, testPoint) + distance(nextPoint, testPoint)) == distance(currentPoint,
                nextPoint);
    }

    private static double distance(final Point currentPoint, final Point nextPoint) {
        return Math.sqrt(Math.pow(currentPoint.longitude - nextPoint.longitude, 2.0)
                + Math.pow(currentPoint.latitude - nextPoint.latitude, 2.0));
    }

    private static double computeSubtendedAngle(final Point currentPoint, final Point nextPoint, final Point testPoint) {
        final double y1 = currentPoint.latitude - testPoint.latitude;
        final double x1 = currentPoint.longitude - testPoint.longitude;

        final double y2 = nextPoint.latitude - testPoint.latitude;
        final double x2 = nextPoint.longitude - testPoint.longitude;

        return angleBewteenVectors(y1, x1, y2, x2);
    }

    public static double angleBewteenVectors(final double y1, final double x1, final double y2, final double x2) {
        double thetaDiff, p1Theta, p2Theta;

        p1Theta = Math.atan2(y1, x1);
        p2Theta = Math.atan2(y2, x2);
        thetaDiff = p2Theta - p1Theta;
        while (thetaDiff > Math.PI)
            thetaDiff -= Math.PI * 2;

        while (thetaDiff < -Math.PI)
            thetaDiff += Math.PI * 2;

        return thetaDiff;
    }

}
