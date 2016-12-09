package org.invisibletech;

import static org.invisibletech.State.*;

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
	public static boolean isCoordInState(State state, double longitude, double latitude) {
		double angle = 0.0;
		boolean onBorder = false;

		int indexOfLastPoint = state.border.length - 1;

		for (int i = 0; i < indexOfLastPoint; i++) {
			double[] currentPoint = state.border[i];
			double[] nextPoint = state.border[i + 1];

			onBorder = onBorder || isOnBorderSegment(currentPoint, nextPoint, longitude, latitude);
			if (onBorder)
				break;

			angle += computeSubtendedAngle(currentPoint, nextPoint, longitude, latitude);
		}

		return onBorder || (Math.abs(angle) >= Math.PI);
	}

	private static boolean isOnBorderSegment(double[] currentPoint, double[] nextPoint, double longitude,
			double latitude) {
		double[] testPoint = new double[] { longitude, latitude };

		return (distance(currentPoint, testPoint) + distance(nextPoint, testPoint)) == distance(currentPoint,
				nextPoint);
	}

	private static double distance(double[] currentPoint, double[] testPoint) {
		return Math.sqrt(Math.pow(currentPoint[LONG_INDEX] - testPoint[LONG_INDEX], 2.0)
				+ Math.pow(currentPoint[LAT_INDEX] - testPoint[LAT_INDEX], 2.0));
	}

	private static double computeSubtendedAngle(double[] currentPoint, double[] nextPoint, double longitude,
			double latitude) {
		double p1Lat, p1Long, p2Lat, p2Long;
		p1Lat = currentPoint[LAT_INDEX] - latitude;
		p1Long = currentPoint[LONG_INDEX] - longitude;

		p2Lat = nextPoint[LAT_INDEX] - latitude;
		p2Long = nextPoint[LONG_INDEX] - longitude;

		return angleBewteenVectors(p1Lat, p1Long, p2Lat, p2Long);
	}

	public static double angleBewteenVectors(double y1, double x1, double y2, double x2) {
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
