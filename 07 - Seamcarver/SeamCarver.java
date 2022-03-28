import java.util.Arrays;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stopwatch;

public class SeamCarver {

	private final boolean natural = false, transposed = true;
	private boolean dataOrientation;
	private int[][] pixels;
	private double[][] energies;
	private int width, height;
	private double xTotal, yTotal;
	private int[] left, right, up, down;

	// create a seam carver object based on the given picture
	public SeamCarver(Picture picture) {
		if (picture == null)
			throw new IllegalArgumentException("something's wrong with the incoming picture");

		width = picture.width();
		height = picture.height();

		pixels = new int[height][width];

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				this.pixels[row][col] = picture.getRGB(col, row);
			}
		}

		// current picture is initially ready for vertical operations
		dataOrientation = natural;

		// construct a 2D array of weighted* pixel energies for faster method calls
		energies = new double[height][width];
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				energies[h][w] = calculate(w, h);
			}
		}
	}

	/**
	 * Retrieve the image in its current state, after any number of successful seam
	 * operations
	 * 
	 * @return a {@link Picture} object
	 */
	public Picture picture() {
		transposeDataTo(natural);

		Picture output = new Picture(pixels[0].length, pixels.length);

		for (int row = 0; row < output.height(); row++) {
			for (int col = 0; col < output.width(); col++) {
				output.setRGB(col, row, pixels[row][col]);
			}
		}

		return output;
	}

	// width of current picture
	public int width() {
		if (dataOrientation == natural)
			return width;
		else
			return height;
	}

	// height of current picture
	public int height() {
		if (dataOrientation == natural)
			return height;
		else
			return width;
	}

	// energy of pixel at column x and row y
	public double energy(int x, int y) {
		// test if request is out of range
		if (x > width - 1 || y > height - 1 || x < 0 || y < 0)
			throw new IllegalArgumentException();

		// get energy from array matrix
		if (dataOrientation == natural)
			return energies[y][x];
		else
			return energies[x][y];
	}

	/**
	 * Internal method to return energy for a coordinate, returns correct result
	 * regardless of current data orientation
	 * <p>
	 * POZOR! this method will be faster but only because it does not do full
	 * argument validation checks!
	 */
	private double calculate(int x, int y) {

		if (width < 3 || height < 3 || x < 1 || y < 1 || x > width - 2 || y > height - 2)
			return 1000.0;

		// get the four surrounding pixel colour values
		left = getColourArray(pixels[y][x - 1]);
		right = getColourArray(pixels[y][x + 1]);
		up = getColourArray(pixels[y - 1][x]);
		down = getColourArray(pixels[y + 1][x]);

		// sum the squares of the absolute colour value differences across both x and y
		xTotal = Math.pow(Math.abs(left[0] - right[0]), 2) + Math.pow(Math.abs(left[1] - right[1]), 2)
				+ Math.pow(Math.abs(left[2] - right[2]), 2);

		yTotal = Math.pow(Math.abs(up[0] - down[0]), 2) + Math.pow(Math.abs(up[1] - down[1]), 2)
				+ Math.pow(Math.abs(up[2] - down[2]), 2);

		// return the sq-root of the sum of x and y direction results
		return Math.sqrt(xTotal + yTotal);
	}

	private int[] getColourArray(int colour) {
		return new int[] { (colour >> 16) & 0xFF, (colour >> 8) & 0xFF, (colour >> 0) & 0xFF };
	}

	// sequence of indices for horizontal seam
	public int[] findHorizontalSeam() {
		transposeDataTo(transposed);
		return new MinimumEnergySeam().seam();
	}

	// sequence of indices for vertical seam
	public int[] findVerticalSeam() {
		transposeDataTo(natural);
		return new MinimumEnergySeam().seam();
	}

	// remove horizontal seam from current picture
	public void removeHorizontalSeam(int[] seam) {
		transposeDataTo(transposed);
		prepareToRemove(seam);
		remove(seam);
	}

	// remove vertical seam from current picture
	public void removeVerticalSeam(int[] seam) {
		transposeDataTo(natural);
		prepareToRemove(seam);
		remove(seam);
	}

	private void prepareToRemove(int[] seam) {
		if (width < 2)
			throw new IllegalArgumentException("a dimension is too narrow");
		if (seam == null)
			throw new IllegalArgumentException("null seam");

		if (seam.length != height)
			throw new IllegalArgumentException("seam object was the wrong length");

		for (int index = 1, s = seam[0]; index < seam.length; index++) {
			if (Math.abs(s - seam[index]) > 1)
				throw new IllegalArgumentException("seam makes a step further than one pixel");
			s = seam[index];
		}
	}

	// internal method to call from both public removal calls
	private void remove(int[] seam) {
		int col;
		double[] energySwap = new double[width - 1];
		int[] pixelSwap = new int[width - 1];

		for (int row = 0; row < height; row++) {
			// get column index from the seam
			col = seam[row];
			if (col < 0 || col > width - 1)
				throw new IllegalArgumentException("seam index out of range: " + col);

			// operate on picture matrix for this row
			System.arraycopy(pixels[row], 0, pixelSwap, 0, col);
			System.arraycopy(pixels[row], col + 1, pixelSwap, col, pixelSwap.length - col);
			pixels[row] = pixelSwap.clone();

			// operate on energies matrix for this row
			System.arraycopy(energies[row], 0, energySwap, 0, col);
			System.arraycopy(energies[row], col + 1, energySwap, col, energySwap.length - col);
			energies[row] = energySwap.clone();
		}

		// change local width value before updating energies
		width--;

		// iterate over narrowed energies[][] along seam to update pixel values
		for (int row = 0; row < height; row++) {
			col = seam[row];
			if (col > 0)
				energies[row][col - 1] = calculate(col - 1, row);
			if (col < width)
				energies[row][col] = calculate(col, row);
		}

	}

	/**
	 * Only when necessary, transpose the energies matrix, and fields associated
	 * with the current Picture. Operation is dependant on whether the operation
	 * will be done on the picture in its natural state or transposed (i.e. for
	 * horizontal seam operations).
	 * <p>
	 * Also reinitialises the local MES object after transposition of the energies
	 * data, thus repeated switches of orientation will cause slower performance for
	 * seam removal.
	 * 
	 * @param requiredState set <b>true</b> if operation will be for
	 *                      <b>horizontal</b> seam operations, otherwise false for
	 *                      vertical operations
	 */
	private void transposeDataTo(boolean requiredState) {
		// do transposition operations only if necessary
		if (dataOrientation != requiredState) {
			// transpose the energies and picture matrices
			double[][] enSwap = new double[width][height];
			int[][] picSwap = new int[width][height];

			for (int row = 0; row < width; row++) {
				for (int col = 0; col < height; col++) {
					enSwap[row][col] = energies[col][row];
					picSwap[row][col] = pixels[col][row];
				}
			}
			energies = enSwap;
			pixels = picSwap;

			// swap internal dimensions width <-> height
			int dim = width;
			width = height;
			height = dim;

			// update update local transposition state
			dataOrientation = requiredState;
		}
	}

	private class MinimumEnergySeam {

		int[][] stepFrom;
		double[][] energyTo;
		int[] seam;
		int w, h;

		public MinimumEnergySeam() {
			// use transposed width and height
			w = width;
			h = height;

			// set up and do the seam finding
			initArrays();
			getSeam();
		}

		private void initArrays() {
			// init arrays
			stepFrom = new int[h][w];
			energyTo = new double[h][w];
			seam = new int[h];

			// set max values preparing for relaxation across all vertices
			for (int row = 0; row < h; row++) {
				if (row == 0) {
					Arrays.fill(energyTo[row], 1000.0);
				} else {
					Arrays.fill(energyTo[row], Double.POSITIVE_INFINITY);
				}
			}
		}

		private void getSeam() {
			if (h < 3) {
				Arrays.fill(seam, w / 2);
				return;
			}
			if (w < 4) {
				Arrays.fill(seam, w / 2);
				return;

			} else {
				// init internal fields
				int seamEnd = Integer.MAX_VALUE;
				double bestResult = Double.POSITIVE_INFINITY, pixelEnergy, energyToHere;

				// to begin finding best path, relax all pixels except those at the borders
				for (int row = 1; row < h; row++) {

					for (int col = 1; col < w - 1; col++) {
						pixelEnergy = energies[row][col];

						for (int i = 0, index = col - 1; i < 3; i++, index++) {
							energyToHere = energyTo[row - 1][index] + pixelEnergy;

							// test for chance to improve this location's value, i.e. relax
							if (energyToHere < energyTo[row][col]) {
								energyTo[row][col] = energyToHere;
								stepFrom[row][col] = index;
//							System.out.println("relaxed the pixel " + col + ", " + row + " (from " + index + ", "
//									+ (row - 1) + ") to " + energyToHere);

								if (row == h - 1 && energyToHere < bestResult) {
									seamEnd = col;
									bestResult = energyToHere;
								}
							}
						}
					}
				}
				// construct the seam array by backtracking through stepFrom[] records
				for (int row = seam.length - 1, col = seamEnd; row > -1; row--) {
					seam[row] = col;
					col = stepFrom[row][col];
				}
			}
		}

		/**
		 * the lowest energy seam for this object
		 * 
		 * @return the seam as an array of integers
		 */
		public int[] seam() {
			return seam;
		}
	}

	private void printMatrix(double[][] matrix) {
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				System.out.print(String.format("%06.1f | ", matrix[row][col]));
			}
			System.out.println();
		}
		System.out.println();
	}

	private void printMatrix(int[][] matrix) {
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				System.out.print(String.format("%d | ", matrix[row][col]));
			}
			System.out.println();
		}
		System.out.println();
	}

	private void printE() {
		printMatrix(energies);
	}

	// unit testing (optional)
	public static void main(String[] args) {

		Stopwatch time = new Stopwatch();

//		SeamCarver sc = new SeamCarver(new Picture("project/6x5.png"));
////		sc.transposeDataTo(sc.transposed);
//		sc.printE();
//		sc.removeHorizontalSeam(sc.findHorizontalSeam());
//		sc.removeVerticalSeam(sc.findVerticalSeam());
//		sc.removeHorizontalSeam(sc.findHorizontalSeam());
////		sc.removeVerticalSeam(sc.findVerticalSeam());
////		sc.removeHorizontalSeam(sc.findHorizontalSeam());
////		sc.removeVerticalSeam(sc.findVerticalSeam());
////		sc.removeVerticalSeam(sc.findVerticalSeam());
//		sc.removeHorizontalSeam(sc.findHorizontalSeam());
//		sc.removeVerticalSeam(sc.findVerticalSeam());
////		System.out.println(sc.width() + " x " + sc.height());
//		sc.transposeDataTo(sc.natural);
//		sc.printE();

		double carve = 3.0;
		SeamCarver sc = new SeamCarver(new Picture("project/chameleon.png"));
		int widthlimit = (int) (sc.picture().width() / carve), heightlimit = (int) (sc.picture().height() / carve);
		for (int i = 0; i < widthlimit; i++)
			sc.removeVerticalSeam(sc.findVerticalSeam());
		for (int i = 0; i < heightlimit; i++)
			sc.removeHorizontalSeam(sc.findHorizontalSeam());
		sc.picture().show();
		
//		sc.picture().show();
//		for (int i = 0; i < 150; i++) {
//			sc.removeVerticalSeam(sc.findVerticalSeam());
//		}
//		sc.picture().show();
//		sc.removeHorizontalSeam(sc.findHorizontalSeam());
//		sc.pic.show();

		// int[] test = new int[] {0,1,2,3,4,5,6};
//		for (int i : test) 
//			System.out.print("" + i + " ");
//		System.out.println();
//
//		int[] dest = new int[test.length-1];
//		System.arraycopy(test, 0, dest, 0 , 3);
//		System.arraycopy(test, 4, dest, 3, dest.length - 3);
//		
//		for (int i : dest) 
//			System.out.print("" + i + " ");
//		System.out.println();

//		sc.picture().show();

//		System.out.println("vertical seam:");
//		for (int i : sc.findVerticalSeam()) {
//			System.out.print("" + i + " ");
//		}
//		System.out.println("\nhorizontal seam:");
//
//		for (int i : sc.findHorizontalSeam()) {
//			System.out.print("" + i + " ");
//		}

//		Picture input = new Picture("logo.png");
//		Picture flipped = new Picture(input.height(), input.width());
//
//		for (int row = 0; row < input.width(); row++) {
//			for (int col = 0; col < input.height(); col++) {
//				flipped.setRGB(col, row, input.getRGB(row, col));
//			}
//		}
//		
//		flipped.show();
		System.out.println(time.elapsedTime());
	}
}
