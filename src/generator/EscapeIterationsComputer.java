package generator;

public class EscapeIterationsComputer {
	private static double BAILOUT_NUMBER = 1 << 16;

	private int maxIterations;

	public EscapeIterationsComputer(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	public void computeEscapeIterations(MandelbrotNumber number) {
		double x = 0;
		double y = 0;
		int iterations = 0;

		for (; iterations < this.maxIterations && !isBailout(x, y); ++iterations) {
			double newX = x * x - y * y + number.getRealPart();
			double newY = 2 * x * y + number.getImaginaryPart();

			x = newX;
			y = newY;
		}

		number.setIterations(iterations);
	}

	private boolean isBailout(double x, double y) {
		return x * x + y * y > BAILOUT_NUMBER;
	}
}
