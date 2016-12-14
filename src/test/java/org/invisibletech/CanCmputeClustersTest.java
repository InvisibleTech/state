package org.invisibletech;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

public class CanCmputeClustersTest {
    private List<State> states;

    @Before
    public void beforeEach() {
        states = State.load(this.getClass().getResourceAsStream("/data/states.json"));
        System.out.println("Number of states " + states.size());
    }

    @Test
    public void test() {
        final List<StateCluster> clusterStates = StateCluster.clusterStates(10, states);

        IntStream.range(0, clusterStates.size()).forEach(i -> {
            System.out.println("Cluster number " + i);
            System.out.println("Centroid of bounding box " + clusterStates.get(i).boundingRectangleCentroid);
            System.out.println(clusterStates.get(i).members.stream().map(s -> s.state).collect(Collectors.toList()));
        });

    }

}
