package org.invisibletech;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CanCompletelyLocatePointsOnBorderTest {

    @Parameter(value = 0)
    public State current;

    @Parameters
    public static List<State> data() {
        return State.load(CanCompletelyLocatePointsOnBorderTest.class.getResourceAsStream("/data/states.json"));
    }

    @Test
    public void shouldNotSufferFromDoubleErrors() {
        Arrays.stream(current.border).forEach(p -> assertTrue(p.toString(), StateSearch.isCoordInState(current, p)));
    }

}
