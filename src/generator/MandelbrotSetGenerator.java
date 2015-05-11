package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A class that generates the Mandelbrot set with a given precision or for a
 * given screen resolution, if it is going to be used to draw a fractal.
 */
public class MandelbrotSetGenerator {
	public static final double MANDELBROT_MIN_X = -2.5;
	public static final double MANDELBROT_MAX_X = 1;
	public static final double MANDELBROT_X_INTERVAL_LENGTH = MANDELBROT_MAX_X
			- MANDELBROT_MIN_X;

	public static final double MANDELBROT_MAX_Y = 1.25;
	public static final double MANDELBROT_MIN_Y = -1.25;
	public static final double MANDELBROT_Y_INTERVAL_LENGTH = MANDELBROT_MAX_Y
			- MANDELBROT_MIN_Y;

	private List<MandelbrotNumber> generatedNumbers;
	private int finalNumberCount;

	private Map<MandelbrotNumber, Pixel> numberPixelMapping;

	private MandelbrotSetReceiver receiver;
	private int maximumActiveThreads;
	private int maximumIterations;

	private int numberOfExpectedThreads;
	private int receivedThreads;

	private ThreadPoolExecutor executor;

	/**
	 * Creates a new generator that has the specified limitations.
	 * 
	 * @param maximumThreads
	 *            - maximum threads to be used during the generations
	 * @param maximumIterations
	 *            - maximum iterations that could be applied during the check
	 *            for each number's belonging in the set
	 * @param receiver
	 *            - the object that will receive the generated set (should
	 *            implement {@link MandelbrotSetReceiver})
	 */
	public MandelbrotSetGenerator(int maximumThreads, int maximumIterations,
			MandelbrotSetReceiver receiver) {
		this.maximumActiveThreads = maximumThreads;
		this.executor = new ThreadPoolExecutor(this.maximumActiveThreads,
				this.maximumActiveThreads, 0, TimeUnit.NANOSECONDS,
				new LinkedBlockingQueue<Runnable>());

		this.maximumIterations = maximumIterations;
		this.receiver = receiver;
	}

	private double getScaledXPixel(int xCoordinate, int width) {
		double scale = MANDELBROT_X_INTERVAL_LENGTH / width;

		double scaledCoordinate = xCoordinate * scale + MANDELBROT_MIN_X;
		return scaledCoordinate;
	}

	private double getScaledYPixel(int yCoordinate, int height) {
		double scale = MANDELBROT_Y_INTERVAL_LENGTH / height;

		double scaledCoordinate = yCoordinate * scale + MANDELBROT_MIN_Y;
		return scaledCoordinate;
	}

	/**
	 * Generates a Mandelbort set of numbers with a precision such that there is
	 * a number for every pixel in the specified resolution. After the
	 * generation is complete the receiveSet method is called with the generated
	 * set.
	 * 
	 * The number pixel mapping could be received through the getPixelMapping
	 * method.
	 * 
	 * This method uses a single thread to calculate escape iterations for every
	 * examined number.
	 * 
	 * @param height
	 *            - the height of the desired resolution
	 * @param width
	 *            - the width of the desired resolution
	 */
	public void generatePixels(int height, int width) {
		// synchronize on the object, so that this method cannot be run
		// simultaneously with the other generation methods
		synchronized (this) {
			this.generatedNumbers = new ArrayList<MandelbrotNumber>();
			this.finalNumberCount = height * width;

			this.numberPixelMapping = new HashMap<MandelbrotNumber, Pixel>();

			for (int pixelX = 0; pixelX < width; ++pixelX) {
				for (int pixelY = 0; pixelY < height; ++pixelY) {
					double realPart = getScaledXPixel(pixelX, width);
					double imaginaryPart = getScaledYPixel(pixelY, height);
					MandelbrotNumber number = new MandelbrotNumber(realPart,
							imaginaryPart);

					this.numberPixelMapping.put(number, new Pixel(pixelX,
							pixelY));

					computeBoundedIterations(number);
				}
			}
		}
	}

	/**
	 * Generates a Mandelbrot set by iterating with the specified precision.
	 * After the generation is complete the receiveSet method is called with the
	 * generated set.
	 * 
	 * This method of computing will use separate threads for computing escape
	 * iterations for each number.
	 * 
	 * @param precision
	 *            - the precision to be used
	 */
	public void generateNumbersUsingThreadsForIterationComputing(
			double precision) {
		// synchronize on the executor, so that this method cannot be run
		// simultaneously with the other generate methods
		synchronized (this) {
			this.generatedNumbers = new ArrayList<MandelbrotNumber>();

			generateFinalNumberCount(precision);

			for (double realPart = MANDELBROT_MIN_X; realPart <= MANDELBROT_MAX_X; realPart += precision) {
				for (double imaginaryPart = MANDELBROT_MIN_Y; imaginaryPart <= MANDELBROT_MAX_Y; imaginaryPart += precision) {
					MandelbrotNumber number = new MandelbrotNumber(realPart,
							imaginaryPart);
					computeBoundedIterations(number);
				}
			}
		}
	}

	private void generateFinalNumberCount(double precision) {
		double xValueCount = MANDELBROT_X_INTERVAL_LENGTH / precision + 1;
		double yValueCount = MANDELBROT_Y_INTERVAL_LENGTH / precision + 1;
		this.finalNumberCount = (int) (xValueCount * yValueCount);
	}

	/**
	 * Generates a Mandelbrot set by iterating with the specified precision.
	 * After the generation is complete the receiveSet method is called with the
	 * generated set.
	 * 
	 * This method of generating the set will use separate threads to generate
	 * subsets of the Mandelbrot set depending on the realPartIterations
	 * parameter.
	 * 
	 * @param precision
	 *            - the precision for iterating through the set
	 * @param realPartIterations
	 *            - specifies how many complex numbers with a given real part
	 *            and all possible imaginary parts will be examined by a single
	 *            thread
	 */
	public void generateNumbersUsingThreadsForGeneratingSubseths(
			double precision, int realPartIterations) {
		// synchronize on the executor, so that this method cannot be run
		// simultaneously with the other generate methods
		synchronized (this) {
			this.generatedNumbers = new ArrayList<MandelbrotNumber>();
			generateFinalNumberCount(precision);

			this.numberOfExpectedThreads = (int) (MANDELBROT_X_INTERVAL_LENGTH / (realPartIterations * precision));
			if (this.numberOfExpectedThreads * realPartIterations * precision < MANDELBROT_X_INTERVAL_LENGTH) {
				this.numberOfExpectedThreads++;
			}

			this.receivedThreads = 0;

			MandelbrotNumber startNumber = new MandelbrotNumber(
					MANDELBROT_MIN_X, MANDELBROT_MIN_Y);

			while (startNumber.getRealPart() <= MANDELBROT_MAX_X) {
				computeBoundedIterations(startNumber, realPartIterations,
						precision);

				double newRealPart = startNumber.getRealPart()
						+ realPartIterations * precision;
				startNumber = new MandelbrotNumber(newRealPart,
						MANDELBROT_MIN_Y);
			}
		}
	}

	private void computeBoundedIterations(MandelbrotNumber number) {
		SingleNumberComputationRunnable computationRunnable = new SingleNumberComputationRunnable(
				number, this.maximumIterations, this);

		this.executor.execute(computationRunnable);
	}

	private void computeBoundedIterations(MandelbrotNumber number,
			int realPartIterations, double precision) {
		MandelbrotSubsetGeneratorRunnable subsetGenerator = new MandelbrotSubsetGeneratorRunnable(
				number, realPartIterations, precision, this,
				this.maximumIterations);

		this.executor.execute(subsetGenerator);
	}

	/* default */void computationIsFinished(List<MandelbrotNumber> numbers) {
		synchronized (this.generatedNumbers) {
			this.generatedNumbers.addAll(numbers);
			this.receivedThreads++;

			if (this.receivedThreads == this.numberOfExpectedThreads) {
				this.receiver.receiveSet(this.generatedNumbers);
			}
		}
	}

	/* default */void computationIsFinished(MandelbrotNumber number) {
		synchronized (this.generatedNumbers) {
			this.generatedNumbers.add(number);

			if (this.generatedNumbers.size() == this.finalNumberCount) {
				this.receiver.receiveSet(this.generatedNumbers);
			}
		}
	}

	public Map<MandelbrotNumber, Pixel> getPixelMapping() {
		return this.numberPixelMapping;
	}

	/**
	 * Shuts down the executor used for parallel computation of the set. Best if
	 * it is called after the set is received in the receiveSet method.
	 */
	public void shutdown() {
		this.executor.shutdown();
	}
}