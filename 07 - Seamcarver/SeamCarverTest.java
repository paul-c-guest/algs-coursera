import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import edu.princeton.cs.algs4.Picture;

@TestInstance(Lifecycle.PER_CLASS)
class SeamCarverTest {

	SeamCarver valid, x2, sixBYfive;
	int[] validSeam, invalidSeam;

	@BeforeAll
	void setup() {
		valid = new SeamCarver(new Picture(10, 10));
		x2 = new SeamCarver(new Picture(2,2));
		sixBYfive = new SeamCarver(new Picture("6x5.png"));

		validSeam = new int[10];
		Arrays.fill(validSeam, 5);
	}

	@Test
	void energy_givenValidInput_returnsCorrect() {
		assertTrue(107.89 < sixBYfive.energy(4, 1) && sixBYfive.energy(4, 1) < 107.9);
		assertTrue(133.06 < sixBYfive.energy(3, 2) && sixBYfive.energy(4, 1) < 133.08);
		assertTrue(174.005 < sixBYfive.energy(2, 3) && sixBYfive.energy(4, 1) < 174.01);
	}

	@Test
	void energy_givenCornerCases_returns1000() {
		assertEquals(1000, valid.energy(0, 0));
		assertEquals(1000, valid.energy(9, 9));
		assertEquals(1000, sixBYfive.energy(5, 0));
		assertEquals(1000, sixBYfive.energy(0, 4));
	}

	@Test
	void energy_givenSideCases_returns1000() {
		assertEquals(1000, valid.energy(0, 5));
		assertEquals(1000, valid.energy(5, 9));
		assertEquals(1000, sixBYfive.energy(3, 0));
		assertEquals(1000, sixBYfive.energy(5, 2));
	}

	@Test
	void constructor_givenInvalidArgument_throwsException() {
		SeamCarver sc;

		try {
			sc = new SeamCarver(null);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

	}

	@Test
	void energy_givenInvalidArgument_throwsException() {
		try {
			valid.energy(-1, 5);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
		try {
			valid.energy(5, -1);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
		try {
			valid.energy(5, 10);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	void removeSeamMethods_givenNullOrBadArgs_throwExceptions() {
		try {
			valid.removeHorizontalSeam(null);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		try {
			valid.removeVerticalSeam(null);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		try {
			invalidSeam = new int[9];
			valid.removeHorizontalSeam(invalidSeam);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		try {
			invalidSeam = new int[11];
			valid.removeVerticalSeam(invalidSeam);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		try {
			x2.removeHorizontalSeam(new int[1]);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		try {
			x2.removeVerticalSeam(new int[1]);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

	}

	@Test
	void removeSeamMethods_givenBadSeamInput_throwException() {

		invalidSeam = new int[] { 3, 2, 3, 2, 4, 2 }; // 4 is previous + 2
		try {
			sixBYfive.removeHorizontalSeam(invalidSeam);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		invalidSeam = new int[] { 3, 2, 3, 4, 5, 4 }; // 5 is out of bounds
		try {
			sixBYfive.removeHorizontalSeam(invalidSeam);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		invalidSeam = new int[] { 2, 3, 2, 4, 2 }; // 4 is the bad input
		try {
			sixBYfive.removeVerticalSeam(invalidSeam);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		invalidSeam = new int[] { 2, 1, 0, -1, 0 }; // -1 should be the problem
		try {
			sixBYfive.removeVerticalSeam(invalidSeam);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

	}

}
