package generator;


public class SingleNumberComputationRunnable implements Runnable {
	private MandelbrotNumber number;

	private int maxIterations;

	private MandelbrotSetGenerator receiver;

	public SingleNumberComputationRunnable(MandelbrotNumber number,
			int maxIterations, MandelbrotSetGenerator receiver) {
		this.number = number;
		this.maxIterations = maxIterations;

		this.receiver = receiver;
	}

	@Override
	public void run() {
		EscapeIterationsComputer iterationComputer = new EscapeIterationsComputer(
				this.maxIterations);
		iterationComputer.computeEscapeIterations(this.number);

		this.receiver.computationIsFinished(this.number);
	}
}
