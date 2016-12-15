package org.invisibletech;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.invisibletech.State.Point;
import org.junit.Before;
import org.junit.Test;

public class CanComputeClustersTest {
    private List<State> states;

    @Before
    public void beforeEach() {
        states = State.load(this.getClass().getResourceAsStream("/data/states.json"));
    }

    @Test
    public void shouldProduce8ClustersWithTheLastOneAsExpected() {
        final List<StateCluster> clusteredStates = StateCluster.clusterStates(10, states);

        assertEquals(9, clusteredStates.size());
        assertEquals(Arrays.asList("Alabama", "Louisiana", "Mississippi"),
                clusteredStates.get(8).members.stream().map(s -> s.state).sorted().collect(Collectors.toList()));
    }

    @Test
    public void shouldReturnListOfOne() throws Exception {
        final List<StateCluster> clusteredStates = StateCluster.clusterStates(10,
                states.stream().sorted((s1, s2) -> s1.state.compareTo(s2.state)).limit(1)
                        .collect(Collectors.toList()));
        assertEquals(1, clusteredStates.size());
        assertEquals("Alabama", clusteredStates.get(0).members.get(0).state);
    }

    @Test
    public void shouldReturnClusterWithExpectedBoundingBox() throws Exception {
        final List<State> fakeStates = Arrays.asList(
                new State("Chaos", new double[][] { { -10.0, 100.0 }, { 100.0, 20.0 } }),
                new State("Calm", new double[][] { { 0.0, 100.0 }, { 100.0, 100.0 } }));
        final List<StateCluster> clusteredStates = StateCluster.clusterStates(1, fakeStates);

        assertEquals(1, clusteredStates.size());
        assertEquals(new StateCluster.AlignedBoundingBox(new Point(-10.0, 20.0), new Point(100.0, 100.0)),
                clusteredStates.get(0).alignedBoundingBox);
    }
}
