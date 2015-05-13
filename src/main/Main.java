package main;

import performancetester.MandelbrotGeneratorPerformanceTester;
import performancetester.Test;
import visualizer.MandelbrotFractalVisualizer;

public class Main {
	private static final int[] numberOfThreadsSet = { 1, 2, 3, 4, 5, 6, 7, 8,
			16, 32 };

	// 1/4, 1/8, 1/16, 1/32, 1/64, 1/128, 1/512 of the set per thread, and
	// finally single number per thread, when the default test precision is
	// used
	private static final int[] realPartIterations = { 175, 87, 43, 21, 5, -1 };

	public static void main(String args[]) {
		MandelbrotFractalVisualizer visualizer = new MandelbrotFractalVisualizer();
		visualizer.drawFractal();
		// MandelbrotGeneratorPerformanceTester tester = new
		// MandelbrotGeneratorPerformanceTester();
		// testDifferentGranularity(tester, 1);
		// testDifferentGranularity(tester, 8);
		// testDifferentGranularity(tester, 1);
	}

	private static void testDifferentThreads(
			MandelbrotGeneratorPerformanceTester tester, int realPartIterations) {

		for (int numberOfThreads : numberOfThreadsSet) {
			Test currentTest = new Test(numberOfThreads, realPartIterations);
			tester.startPerformanceTest(currentTest);
		}
	}

	private static void testDifferentGranularity(
			MandelbrotGeneratorPerformanceTester tester, int numberOfThreads) {

		for (int iterations : realPartIterations) {
			Test currentTest = new Test(numberOfThreads, iterations,
					Test.DEFAULT_PRECISION, 500000);
			tester.startPerformanceTest(currentTest);
		}
	}
}
