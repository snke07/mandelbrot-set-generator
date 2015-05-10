package generator;

public class MandelbrotNumber {
    private double realPart;

    private double imaginaryPart;

    private int iterations;

    public MandelbrotNumber(double realPart, double imaginaryPart) {
        this.realPart = realPart;
        this.imaginaryPart = imaginaryPart;
    }

    public int getIterations() {
        return this.iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public double getRealPart() {
        return this.realPart;
    }

    public double getImaginaryPart() {
        return this.imaginaryPart;
    }

    @Override
    public int hashCode() {
        String stringValue = String.format("%f%f", this.realPart, this.imaginaryPart,
                this.iterations);
        return stringValue.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MandelbrotNumber)) {
            return false;
        }

        return obj.hashCode() == hashCode();
    }
}
