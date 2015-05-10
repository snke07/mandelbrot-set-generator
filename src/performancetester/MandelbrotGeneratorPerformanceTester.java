package performancetester;

import generator.MandelbrotNumber;
import generator.MandelbrotSetGenerator;
import generator.MandelbrotSetReceiver;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MandelbrotGeneratorPerformanceTester implements MandelbrotSetReceiver {
    private static int NUMBER_OF_CORES_AVAILABLE = 8;
    private static int NUMBER_OF_ITERATIONS = 10000;

    private static double PRECISION = 0.002;

    private MandelbrotSetGenerator generator;

    private static Logger LOGGER = Logger.getLogger("Performance test");

    private long testStartNanos = -1;

    private int numberOfCoresTested = -1;

    private Queue<Integer> queuedTests = new LinkedList<Integer>();

    public void startPerformanceTest() {
        for (int i = 0; i < NUMBER_OF_CORES_AVAILABLE; ++i) {
            this.queuedTests.add(i + 1);
        }

        runNextTestTask();
    }

    private void runNextTestTask() {
        int testedNumberOfCores = this.queuedTests.poll();
        startPerformanceTest(testedNumberOfCores);
    }

    public void startPerformanceTest(int numberOfCores) {
        this.generator = new MandelbrotSetGenerator(numberOfCores, NUMBER_OF_ITERATIONS, this);
        this.testStartNanos = System.nanoTime();
        this.numberOfCoresTested = numberOfCores;

        this.generator.setFilterGeneratedNumbers(false);
        this.generator.generateNumbers(PRECISION);
    }

    @Override
    public void receiveSet(List<MandelbrotNumber> numbers) {
        long testEndNanos = System.nanoTime();

        this.generator.shutdown();

        String testMessage = String
                .format("Generations finished for number of cores %d with nano time %d. Generated numbers: %d.",
                        this.numberOfCoresTested, testEndNanos - this.testStartNanos,
                        numbers.size());
        LOGGER.log(Level.INFO, testMessage);

        if (!this.queuedTests.isEmpty()) {
            runNextTestTask();
        }
    }
}
