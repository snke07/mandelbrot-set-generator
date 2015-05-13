package visualizer;

import generator.MandelbrotNumber;
import generator.MandelbrotSetGenerator;
import generator.MandelbrotSetReceiver;
import generator.Pixel;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class MandelbrotFractalVisualizer implements MandelbrotSetReceiver {
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 1024;

	private static final Color[] PALETTE = { Color.yellow, Color.white,
			Color.green, Color.black, Color.red };

	private static final int MAX_ITERATIONS = 1000;

	private static final int MAX_THREADS = 8;

	private BufferedImage canvas = new BufferedImage(WIDTH, HEIGHT,
			BufferedImage.TYPE_INT_ARGB);

	private MandelbrotSetGenerator generator;

	public void drawFractal() {
		this.generator = new MandelbrotSetGenerator(MAX_THREADS,
				MAX_ITERATIONS, this);
		this.generator.generatePixels(HEIGHT, WIDTH);
	}

	private Color getColorForIterations(int iterations) {
		return PALETTE[iterations % PALETTE.length];
	}

	@Override
	public void receiveSet(List<MandelbrotNumber> numbers) {
		this.generator.shutdown();
		Map<MandelbrotNumber, Pixel> pixelMapping = this.generator
				.getPixelMapping();

		for (MandelbrotNumber number : numbers) {
			Color currentColor = getColorForIterations(number.getIterations());
			Pixel pixel = pixelMapping.get(number);

			this.canvas.setRGB(pixel.getX(), pixel.getY(),
					currentColor.getRGB());
		}

		File outputfile = new File("image.png");
		try {
			outputfile.createNewFile();
		} catch (IOException e1) {
			Logger.getLogger("Visualizer").log(Level.WARNING,
					"Failed to create output image file.", e1);
		}

		try {
			ImageIO.write(this.canvas, "png", outputfile);
		} catch (IOException e) {
			Logger.getLogger("Visualizer").log(Level.WARNING,
					"Failed to dump image to a file.", e);
		}
	}
}
