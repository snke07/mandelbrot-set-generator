package main;

import performancetester.MandelbrotGeneratorPerformanceTester;

public class Main {
    public static void main(String args[]) {
        // MandelbrotFractalVisualizer panel = new
        // MandelbrotFractalVisualizer();
        // panel.drawFractal();
        MandelbrotGeneratorPerformanceTester tester = new MandelbrotGeneratorPerformanceTester();
        tester.startPerformanceTest();
    }
}
