package generator;

import java.util.ArrayList;
import java.util.List;

public class MandelbrotSubsetGeneratorRunnable implements Runnable {
	private MandelbrotNumber startingNumber;

	private int realPartIterations;
	private double precision;
	private int maxIterations;

	private MandelbrotSetGenerator receiver;

	public MandelbrotSubsetGeneratorRunnable(MandelbrotNumber startingNumber,
			int realPartIterations, double precision,
			MandelbrotSetGenerator receiver, int maxIterations) {
		this.startingNumber = startingNumber;
		this.realPartIterations = realPartIterations;
		this.precision = precision;
		this.maxIterations = maxIterations;

		this.receiver = receiver;
	}

	@Override
	public void run() {
		double initialRealPart = this.startingNumber.getRealPart();
		double initialImaginaryPart = this.startingNumber.getImaginaryPart();

		List<MandelbrotNumber> generatedNumbers = new ArrayList<MandelbrotNumber>();
		EscapeIterationsComputer iterationComputer = new EscapeIterationsComputer(
				this.maxIterations);
		int currentRealPartIteration = 1;

		for (double realPart = initialRealPart; realPart <= MandelbrotSetGenerator.MANDELBROT_MAX_X; realPart += this.precision) {
			for (double imaginaryPart = initialImaginaryPart; imaginaryPart <= MandelbrotSetGenerator.MANDELBROT_MAX_Y; imaginaryPart += this.precision) {
				MandelbrotNumber number = new MandelbrotNumber(realPart,
						imaginaryPart);
				iterationComputer.computeEscapeIterations(number);
				generatedNumbers.add(number);
			}

			if (currentRealPartIteration == this.realPartIterations) {
				break;
			}

			currentRealPartIteration++;
		}

		this.receiver.computationIsFinished(generatedNumbers);
	}
}
