package org.invisibletech;

import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.invisibletech.State.Point;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class CompareMethodPerformanceTest {

    static class InterestingBits {

        public final String benchmark;
        public final double score;

        public InterestingBits(final String benchmark, final double score) {
            this.benchmark = benchmark;
            this.score = score;
        }

    }

    @Test
    public void shouldShowClusteredBy8IsFastest() throws RunnerException {

        final Options opt = new OptionsBuilder()
                .include(this.getClass().getName() + ".*")
                .mode(Mode.AverageTime)
                .mode(Mode.Throughput)
                .mode(Mode.SingleShotTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(3)
                .measurementTime(TimeValue.seconds(1))
                .measurementIterations(6)
                .threads(Runtime.getRuntime().availableProcessors() / 2)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();

        final Collection<RunResult> allResults = new Runner(opt).run();

        assertEquals(9, allResults.size());

        final List<InterestingBits> orderedAvgTimeResults = allResults.stream()
                .filter(r -> r.getParams().getMode() == Mode.AverageTime)
                .map(r -> new InterestingBits(r.getParams().getBenchmark(),
                        r.getAggregatedResult().getPrimaryResult().getScore()))
                .sorted((l, r) -> new Double(l.score).compareTo(r.score))
                .collect(Collectors.toList());

        assertEquals(3, orderedAvgTimeResults.size());

        assertThat(orderedAvgTimeResults.get(0).benchmark, endsWith("benchmark8Clustered"));
        assertThat(orderedAvgTimeResults.get(1).benchmark, endsWith("benchmark6Clustered"));
        assertThat(orderedAvgTimeResults.get(2).benchmark, endsWith("benchmarkNonClustered"));

        final long speedUpFactor = (long) (orderedAvgTimeResults.get(2).score
                / orderedAvgTimeResults.get(0).score);

        assertThat(speedUpFactor, greaterThan(3L));
    }

    // The JMH samples are the best documentation for how to use it
    // http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
    @org.openjdk.jmh.annotations.State(Scope.Thread)
    public static class BenchmarkState {
        List<State> states;
        List<StateCluster> clusters8;
        List<Point> centroids;
        List<StateCluster> clusters6;

        @Setup(Level.Trial)
        public void initialize() {
            states = State.load(this.getClass().getResourceAsStream("/data/states.json"));
            clusters8 = StateCluster.clusterStates(8, states);
            clusters6 = StateCluster.clusterStates(6, states);
            centroids = states.stream().map(s -> s.centroid).collect(Collectors.toList());
        }
    }

    @Benchmark
    public void benchmarkNonClustered(final BenchmarkState benchmarkState, final Blackhole bh) {
        benchmarkState.centroids.stream()
                .forEach(centroid -> benchmarkState.states.stream()
                        .forEach(state -> bh.consume(StateSearch.isCoordInState(state, centroid))));
    }

    @Benchmark
    public void benchmark8Clustered(final BenchmarkState benchmarkState, final Blackhole bh) {
        benchmarkState.centroids.stream()
                .forEach(centroid -> benchmarkState.clusters8.stream()
                        .filter(cluster -> StateSearch.mightCoordBeInMemberState(cluster, centroid))
                        .flatMap(filtered -> filtered.members.stream())
                        .forEach(memberState -> bh.consume(StateSearch.isCoordInState(memberState, centroid))));
    }

    @Benchmark
    public void benchmark6Clustered(final BenchmarkState benchmarkState, final Blackhole bh) {
        benchmarkState.centroids.stream()
                .forEach(centroid -> benchmarkState.clusters6.stream()
                        .filter(cluster -> StateSearch.mightCoordBeInMemberState(cluster, centroid))
                        .flatMap(filtered -> filtered.members.stream())
                        .forEach(memberState -> bh.consume(StateSearch.isCoordInState(memberState, centroid))));
    }
}
