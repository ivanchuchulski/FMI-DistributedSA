import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.math3.complex.Complex;

public class FractalPartThread extends Thread {
	private final int index;
	private final BufferedImage bufferedImage;
	private final ArrayBlockingQueue<SegmentTask> tasks;

	public FractalPartThread(int index, BufferedImage bufferedImage, ArrayBlockingQueue<SegmentTask> tasks) {
		this.index = index;
		this.bufferedImage = bufferedImage;
		this.tasks = tasks;
	}

	@Override
	public void run() {
        long threadStartTimestamp = Calendar.getInstance().getTimeInMillis();

        if (!Main.quietOutput) {
            System.out.printf("Thread-%d started.%n", index);
        }

        while (!tasks.isEmpty()) {
			SegmentTask task = tasks.poll();

			for (int yPixel = task.heightStart; yPixel < task.heightEnd; yPixel++) {
				double imaginaryValue = mapPixelHeightToImaginaryAxis(yPixel);

				for (int xPixel = task.widthStart; xPixel < task.widthEnd; xPixel++) {
					double realValue = mapPixelWidthToRealAxis(xPixel);

					int numberOfSteps = calculateNumberOfSteps(new Complex(realValue, imaginaryValue));

					int color = getHSBToRGBColor(numberOfSteps);

					drawPixel(bufferedImage, xPixel, yPixel, color);
				}
			}
		}
		long threadFinishTimeStamp = System.currentTimeMillis();
		long threadOverallTime = threadFinishTimeStamp - threadStartTimestamp;

		if (!Main.quietOutput) {
			System.out.printf("Thread-%d stopped%n", index);
			System.out.printf("Thread-%d execution time was (millis): %d%n", index, threadOverallTime);
		}

		return;
    }

	/**
	 * calculates the number of steps for the given point
	 * @param complexPoint point on the complex plane to check whether is in the fractal set
	 * @return number of iterations until the sequence becomes unbounded and goes to infinity,
	 * if 0 is returned, it means the sequence for the point converges and the point is in the fractal set
	 */
	private static int calculateNumberOfSteps(Complex complexPoint) {
		final Complex z0 = new Complex(0.0, 0.0);
		Complex zPrevious = z0;
		Complex zIteration = null;
		Double realPartOfZPrevious = null;

		// maybe tweak maxIterations, 1000 is to much, maybe 500 or little bit less
		int iterations = 0;
		final int maxIterations = Main.maxPointIterations;

		for (int i = 0; i < maxIterations; i++) {
			zIteration = calculateIterationTerm(zPrevious, complexPoint);
			zPrevious = zIteration;

			realPartOfZPrevious = zPrevious.getReal();

			if (realPartOfZPrevious.isInfinite() || realPartOfZPrevious.isNaN()) {
				iterations = i;
				break;
			}
		}

//		System.out.println("iter: " + iterations + "; " + zIteration.getReal() + ", " + zIteration.getImaginary());

		return iterations;
	}

	private static Complex calculateIterationTerm(Complex z, Complex constant) {
//		formula project num 17 : F(Z) = e^(cos(C*Z))
		Complex constantTimesZ = constant.multiply(z);
		Complex cosine = constantTimesZ.cos();

		return cosine.exp();
	}

	private static void printInfoAboutPoint(int xPixel, int yPixel, double realValue, double imaginaryValue, int r) {
		System.out.printf("(%.9f, %.9f) to (%3d, %3d) => %d\n", realValue, imaginaryValue, xPixel, yPixel, r);
	}

	private static double mapPixelWidthToRealAxis(int xPixel) {
		double range = Main.maxReal - Main.minReal;

		return xPixel * (range / Main.width) + Main.minReal;
	}

	private static double mapPixelHeightToImaginaryAxis(int yPixel) {
		double range = Main.maxImaginary - Main.minImaginary;

		return yPixel * (range / Main.height) + Main.minImaginary;
	}

	private static int blackAndWhiteOutput(BufferedImage bufferedImage, int xPixel, int yPixel, int iterations) {
		if (iterations == 0) {
			return 0x000000;
		}
		else {
			return 0xffffff;
		}
	}

	/**
	 * get the pixel color, according to the number of steps
	 * @param iterations number of iterations for a specific point
	 * @return integer hex value, representing the pixel color, on 0 iterations green color is returned
	 */
	private static int getIterationColor(int iterations) {
		if (iterations == 0) {
			return 0x00ff00;
		}
		else if (iterations <= 10) {
			return 0xFFFFFF;
		}
		else if (iterations == 11) {
			return 0x0000ff;
		}
		else if (iterations == 12) {
			return 0x0000ee;
		}
		else if (iterations == 13) {
			return 0x0000dd;
		}
		else if (iterations == 14) {
			return 0x0000cc;
		}
		else if (iterations == 15) {
			return 0x0000bb;
		}
		else if (iterations == 16) {
			return 0x0000aa;
		}
		else if (iterations == 17) {
			return 0x000099;
		}
		else if (iterations == 18) {
			return 0x000088;
		}
		else if (iterations == 19) {
			return 0x000077;
		}
		else if (iterations == 20) {
			return 0x000066;
		}
		else if (iterations <= 30) {
			return 0x666600;
		}
		else if (iterations <= 40) {
			return 0x777700;
		}
		else if (iterations <= 50) {
			return 0x888800;
		}
		else if (iterations <= 100) {
			return 0x999900;
		}
		else if (iterations <= 150) {
			return 0xaaaa00;
		}
		else if (iterations <= 200) {
			return 0xbbbb00;
		}
		else if (iterations <= 250) {
			return 0xcccc00;
		}
		else if (iterations <= 300) {
			return 0xdddd00;
		}
		else {
			return 0xeeee00;
		}
	}

	int getHSBToRGBColor(int numberOfIterations) {
		if (numberOfIterations > Main.maxPointIterations || numberOfIterations == 0) {
			return 0x000000; // black
		}
		else {
			return Color.HSBtoRGB((float) 128 * numberOfIterations / Main.maxPointIterations, 0.67f, 1);
		}
	}

	private synchronized void drawPixel(BufferedImage bufferedImage, int xPixel, int yPixel, int color) {
		bufferedImage.setRGB(xPixel, yPixel, color);
	}
}
