package org.invisibletech;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class CanComputePointContainedTest {
	private static final double PA_TEST_LAT = 40.513799;
	private static final double PA_TEST_LONG = -77.036133;
	private static final double TRENTON_LAT = 40.2170534;
	private static final double TRENTON_LONG = -74.7429384;
	private static final double HBURG_LAT = 40.263680;
	private static final double HBURG_LONG = -76.890739;

	private static final State PENN = new State("Pennsylvania",
			new double[][] { { -77.475793, 39.719623 }, { -80.524269, 39.721209 }, { -80.520592, 41.986872 },
					{ -74.705273, 41.375059 }, { -75.142901, 39.881602 }, { -77.475793, 39.719623 } });;
					
	private static State OHIO = new State("Ohio",
			new double[][] { { -83.272755, 38.609257 }, { -84.81148, 39.102585 }, { -84.790377, 41.697494 },
					{ -80.520592, 41.986872 }, { -80.88111, 39.624081 }, { -83.272755, 38.609257 } });;

	@Test
	public void shouldReturnTrueForHarrisburgInPAFalseInOH() {
		assertTrue("Harrisburg should be in PA.", GeoMath.isCoordInState(PENN, HBURG_LONG, HBURG_LAT));
		assertFalse("Harrisburg should not be in OH.", GeoMath.isCoordInState(OHIO, HBURG_LONG, HBURG_LAT));
	}

	@Test
	public void shouldReturnFalseForTrentonInPAOrOH() {
		assertFalse("Trenton should not be in PA.", GeoMath.isCoordInState(PENN, TRENTON_LONG, TRENTON_LAT));
		assertFalse("Trenton should not be in OH.", GeoMath.isCoordInState(OHIO, TRENTON_LONG, TRENTON_LAT));
	}

	@Test
	public void shouldReturnTrueForAllBorderPointsOfStateBeingInState() {
		Arrays.stream(PENN.border).forEach(p -> assertTrue(Arrays.toString(p),
				GeoMath.isCoordInState(PENN, p[State.LONG_INDEX], p[State.LAT_INDEX])));
	}
	
	@Test
	public void shouldReturnTrueForPointInPAFalseInOH() {
		assertTrue("Point should be in PA.", GeoMath.isCoordInState(PENN, PA_TEST_LONG, PA_TEST_LAT));
		assertFalse("Point should not be in OH.", GeoMath.isCoordInState(OHIO, PA_TEST_LONG, PA_TEST_LAT));
	}

}
