package generator;

public class MandelbrotNumberIterator implements Runnable {
    private static double BAILOUT_NUMBER = 1 << 16;

    private int maxIterations;

    private MandelbrotSetGenerator receiver;

    private MandelbrotNumber number;

    public MandelbrotNumberIterator(MandelbrotNumber number, int maxIterations,
            MandelbrotSetGenerator generator) {
        this.number = number;
        this.maxIterations = maxIterations;
        this.receiver = generator;
    }

    @Override
    public void run() {
        double x = 0;
        double y = 0;
        int iterations = 0;

        for (; iterations < this.maxIterations && !isBailout(x, y); ++iterations) {
            double newX = x * x - y * y + this.number.getRealPart();
            double newY = 2 * x * y + this.number.getImaginaryPart();

            x = newX;
            y = newY;
        }

        this.number.setIterations(iterations);
        this.receiver.computationIsFinished(this.number, !isBailout(x, y));
    }

    private boolean isBailout(double x, double y) {
        return x * x + y * y > BAILOUT_NUMBER;
    }
}
