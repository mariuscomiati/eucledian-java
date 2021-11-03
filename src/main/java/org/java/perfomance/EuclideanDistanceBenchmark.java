package org.java.perfomance;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 0)
@Measurement(iterations = 1)
public class EuclideanDistanceBenchmark {

    private static Map<Integer, List<List<Double>>> allDataAsLists;
    private static Map<Integer, double[][]> allDataAsArrays;

//    @Param({"10", "30", "50", "70", "100", "300", "500", "700", "1000", "5000", "10000"})
    @Param({"10", "30"})
    private int size;

    private double[] v1;
    private double[] v2;
    private List<Double> l1;
    private List<Double> l2;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(EuclideanDistanceBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        initializeDataSet();
        v1 = EuclideanDistanceBenchmark.allDataAsArrays.get(size)[0];
        v2 = EuclideanDistanceBenchmark.allDataAsArrays.get(size)[1];
        l1 = EuclideanDistanceBenchmark.allDataAsLists.get(size).get(0);
        l2 = EuclideanDistanceBenchmark.allDataAsLists.get(size).get(1);
    }

    @Benchmark
    public void euclideanOnArrays(Blackhole bh) {
        double squareDiff = 0;
        for (int i = 0; i < v1.length; i++) {
            squareDiff += (v1[i] - v2[i]) * (v1[i] - v2[i]);
        }
        double result = Math.sqrt(squareDiff);
        bh.consume(result);
    }


    @Benchmark
    public void euclideanOnLists(Blackhole bh) {
        Double squareDiff = IntStream.range(0, l1.size())
                .mapToObj(i -> (l1.get(i) - l2.get(i)) * (l1.get(i) - l2.get(i))).reduce(Double::sum).get();

        double result = Math.sqrt(squareDiff);
        bh.consume(result);
    }

    private void initializeDataSet() {
        try (Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream("test.in"))) {
            allDataAsLists = new HashMap<>();
            allDataAsArrays = new HashMap<>();
            String l1;
            String l2;

            while (scanner.hasNextLine()) {
                l1 = scanner.nextLine();
                l2 = scanner.nextLine();

                double[] arr1 = Arrays.stream(l1.split(" ")).mapToDouble(el -> Double.parseDouble(el)).toArray();
                List<Double> ls1 = Arrays.stream(l1.split(" ")).map(el -> Double.parseDouble(el)).collect(Collectors.toList());
                double[] arr2 = Arrays.stream(l2.split(" ")).mapToDouble(el -> Double.parseDouble(el)).toArray();
                List<Double> ls2 = Arrays.stream(l2.split(" ")).map(el -> Double.parseDouble(el)).collect(Collectors.toList());
                int n = ls1.size();

                allDataAsArrays.put(n, new double[][]{ arr1, arr2 });
                allDataAsLists.put(n, List.of(ls1, ls2));
            }
        } catch (Exception e) {
            e.printStackTrace();
            allDataAsLists = null;
            allDataAsArrays = null;
        }
    }

}