package performancetester;

import generator.MandelbrotNumber;
import generator.MandelbrotSetGenerator;
import generator.MandelbrotSetReceiver;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MandelbrotGeneratorPerformanceTester implements
		MandelbrotSetReceiver {
	private static final String TEST_RESULT_MESSAGE_FORMAT = "Generations finished for number of cores %d, max iterations %d, real part iterations per thread %d and precision %f with nano time %d. Generated numbers: %d.";

	private MandelbrotSetGenerator generator;

	private boolean isRunning = false;

	private static Logger LOGGER = Logger.getLogger("Performance test");

	private long testStartNanos = -1;

	private Test currentTestConfiguration;

	private Queue<Test> queuedTests = new LinkedList<Test>();

	private void runNextTestTask() {
		isRunning = true;
		Test nexTest = queuedTests.poll();
		startGeneration(nexTest);
	}

	private void startGeneration(Test testConfiguration) {
		currentTestConfiguration = testConfiguration;
		generator = new MandelbrotSetGenerator(
				testConfiguration.getNumberOfThreads(),
				testConfiguration.getMaxIterations(), this);

		testStartNanos = System.nanoTime();
		if (testConfiguration.getRealPartIterationsPerThread() == Test.ONE_NUMBER_PER_THREAD_VALUE) {
			generator
					.generateNumbersUsingThreadsForIterationComputing(testConfiguration
							.getPrecision());
		} else {
			generator.generateNumbersUsingThreadsForGeneratingSubseths(
					testConfiguration.getPrecision(),
					testConfiguration.getRealPartIterationsPerThread());
		}
	}

	public void startPerformanceTest(Test testConfiguration) {
		synchronized (queuedTests) {
			queuedTests.add(testConfiguration);

			if (!isRunning) {
				this.runNextTestTask();
			}
		}
	}

	@Override
	public void receiveSet(List<MandelbrotNumber> numbers) {
		long testEndNanos = System.nanoTime();
		generator.shutdown();

		String testMessage = String.format(TEST_RESULT_MESSAGE_FORMAT,
				currentTestConfiguration.getNumberOfThreads(),
				currentTestConfiguration.getMaxIterations(),
				currentTestConfiguration.getRealPartIterationsPerThread(),
				currentTestConfiguration.getPrecision(), testEndNanos
						- testStartNanos, numbers.size());
		LOGGER.log(Level.INFO, testMessage);

		synchronized (queuedTests) {
			if (!queuedTests.isEmpty()) {
				runNextTestTask();
			} else {
				isRunning = false;
			}
		}
	}
}
