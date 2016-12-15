package org.invisibletech;

import org.invisibletech.State.Point;

public class GeoMath {
    public static boolean isOnBorderSegment(final Point currentPoint, final Point nextPoint, final Point testPoint) {
        return (distance(currentPoint, testPoint) + distance(nextPoint, testPoint)) == distance(currentPoint,
                nextPoint);
    }

    public static double distance(final Point currentPoint, final Point nextPoint) {
        return Math.sqrt(Math.pow(currentPoint.longitude - nextPoint.longitude, 2.0)
                + Math.pow(currentPoint.latitude - nextPoint.latitude, 2.0));
    }

    public static double computeSubtendedAngle(final Point currentPoint, final Point nextPoint, final Point testPoint) {
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

    public static Point computeCentroidOfPolygon(final Point[] boundary) {
        // See https://en.wikipedia.org/wiki/Centroid#Centroid_of_polygon for
        // formula used.
        double areaSum = 0.0;
        double xSum = 0.0;
        double ySum = 0.0;
        for (int i = 0; i < boundary.length - 1; i++) {
            final double areaTerm = boundary[i].longitude * boundary[i + 1].latitude
                    - boundary[i + 1].longitude * boundary[i].latitude;
            xSum += areaTerm * (boundary[i].longitude + boundary[i + 1].longitude);
            ySum += areaTerm * (boundary[i].latitude + boundary[i + 1].latitude);
            areaSum += areaTerm;
        }

        final double area = areaSum * 0.5;

        final double cx = (1 / (6 * area)) * xSum;
        final double cy = (1 / (6 * area)) * ySum;

        return new Point(cx, cy);
    }
}
