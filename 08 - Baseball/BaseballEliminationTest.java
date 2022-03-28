import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class BaseballEliminationTest {

	BaseballElimination teams12, teams4, teams12all, teams4b;

	@BeforeAll
	void setup() {
		teams12 = new BaseballElimination("project/teams12.txt");
		teams4 = new BaseballElimination("project/teams4.txt");
		teams12all = new BaseballElimination("project/teams12-allgames.txt");
		teams4b = new BaseballElimination("project/teams4b.txt");
		
	}

	@Test
	void getMatchVertex_givenValidInput_BehavesAsExpected() {
		// special case when only one team exists
//		assertEquals(2, be.getMatchV(1, 0, 1));

		// some regular cases
		assertEquals(3, teams12.getMatchV(1, 0, 2));
		assertEquals(5, teams12.getMatchV(1, 0, 4));
		assertEquals(6, teams12.getMatchV(0, 2, 4));
		assertEquals(9, teams12.getMatchV(1, 2, 5));
		assertEquals(9, teams12.getMatchV(2, 1, 5));
		assertEquals(14, teams12.getMatchV(2, 3, 6));
		assertEquals(15, teams12.getMatchV(4, 2, 6));
	}

	@Test
	void getTeamIndex_givenValidInput_BehavesAsExpected() {
		assertEquals(11, teams12.getTeamIndex("China"));
		assertEquals(0, teams12.getTeamIndex("Poland"));
	}

	@Test
	void getTeamIndex_givenBadInput_throwsCorrectException() {
		try {
			teams12.getTeamIndex("Unlikely-tEam-nAm3");
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	void publicAPIGetters_givenValidQuery_returnExpected() {
		// methods which rely on the team's record array
		assertEquals(5, teams12.wins("Iran"));
		assertEquals(5, teams12.losses("Japan"));
		assertEquals(4, teams12.remaining("Egypt"));

		// methods which rely on the team's schedule array
		assertEquals(1, teams12.against("Brazil", "Cuba"));
		assertEquals(0, teams12.against("Brazil", "Argentina"));
		assertEquals(0, teams12.against("Argentina", "Brazil"));

		// methods which rely on the 'isEliminated' boolean and the eliminators
		// array, which are set during construction and flow network calculations
		assertTrue(teams4.isEliminated("Philadelphia"));
		assertFalse(teams4.isEliminated("New_York"));
		assertTrue(teams12.isEliminated("Japan"));
		assertFalse(teams12.isEliminated("Iran"));
		
		// no tests yet for cert of elimination 
	}
	
	@Test
	void privateGetters_givenValidValues_returnExpected() {
		assertEquals(5, teams12.getCapacityToSink(3, 4));
		assertEquals(6, teams12.getCapacityToSink(4, 6));
		assertEquals(0, teams12.getCapacityToSink(11, 0));
	}

}
