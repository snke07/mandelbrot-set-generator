package performancetester;

public class Test {
	public static final double DEFAULT_PRECISION = 0.005;
	public static final int DEFAULT_MAX_ITERATIONS = 10000;

	public static final int ONE_NUMBER_PER_THREAD_VALUE = -1;

	private double precision;
	private int maxIterations;

	private int numberOfThreads;
	private int realPartIterationsPerThread;

	public Test(int numberOfThreads, int realPartIterationsPerThread) {
		this(numberOfThreads, realPartIterationsPerThread, DEFAULT_PRECISION,
				DEFAULT_MAX_ITERATIONS);
	}

	public Test(int numberOfThreads, int realPartIterationsPerThread,
			double precision, int maxIterations) {
		this.numberOfThreads = numberOfThreads;
		this.realPartIterationsPerThread = realPartIterationsPerThread;
		this.precision = precision;
		this.maxIterations = maxIterations;
	}

	public double getPrecision() {
		return precision;
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public int getNumberOfThreads() {
		return numberOfThreads;
	}

	public int getRealPartIterationsPerThread() {
		return realPartIterationsPerThread;
	}
}
