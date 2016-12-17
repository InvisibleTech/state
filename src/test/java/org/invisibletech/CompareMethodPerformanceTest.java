package org.invisibletech;

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
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class CompareMethodPerformanceTest {

    @Test
    public void test() throws RunnerException {

        final Options opt = new OptionsBuilder()
                // Specify which benchmarks to run.
                // You can be more specific if you'd like to run only one
                // benchmark per test.
                .include(this.getClass().getName() + ".*")
                // Set the following options as needed
                .mode(Mode.AverageTime)
                .mode(Mode.Throughput)
                .mode(Mode.SingleShotTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(3)
                .measurementTime(TimeValue.seconds(5))
                .measurementIterations(6)
                .threads(Runtime.getRuntime().availableProcessors() / 2)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                // .jvmArgs("-XX:+UnlockDiagnosticVMOptions",
                // "-XX:+PrintInlining")
                // .addProfiler(WinPerfAsmProfiler.class)
                .build();

        new Runner(opt).run();

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
