import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * Baseball Elimination
 * 
 *
 */
public class BaseballElimination {

	private final int n; // number of teams for this graph
	private Team[] teams;
	private int[] leader;
	private Map<String, Integer> teamMap;

	/**
	 * create a baseball division from given filename in the specific format
	 * 
	 * @param filename
	 */
	public BaseballElimination(String filename) {
		/**
		 * You may assume that n ≥ 1 and that the input files are in the specified
		 * format and internally consistent. Note that a team's number of remaining
		 * games does not necessarily equal the sum of the remaining games against teams
		 * in its division because a team may play opponents outside its division.
		 */
		In in = new In(filename);

		// the first line is the number of teams in this league
		n = Integer.parseInt(in.readLine());

		// populate the league table with teams from input
		teams = new Team[n];
		teamMap = new HashMap<>();
		leader = new int[] { 0, 0 };
		int teamIndex = 0;
		while (in.hasNextLine()) {
			String teamname = in.readString();

			int[] record = new int[] { in.readInt(), in.readInt(), in.readInt() };

			int[] schedule = new int[n];
			for (int i = 0; i < n; i++) {
				schedule[i] = in.readInt();
			}

			teams[teamIndex] = new Team(teamname, record, schedule);
			teamMap.put(teamname, teamIndex);

			// update top scoring team for use in trivial elimination round
			if (record[0] > leader[0]) {
				leader[0] = record[0];
				leader[1] = teamIndex;
			}
//			teams[teamIndex].spitOut();

			// prep for next line
			teamIndex++;
			in.readLine();
		}

		// test all teams for for trivial elimination
		for (int team = 0; team < n; team++) {
			if (teams[team].record[0] + teams[team].record[2] < leader[0]) {
				teams[team].isEliminated = true;
				teams[team].eliminators[leader[1]] = true;
			}
		}

		// determine the number of vertices a flow network will require
		int v = 2; // start with the source and sink vertices
		// add a diminishing integer from i = n - 1, which creates enough vertices to
		// account for each possible matchup between the teams
		for (int i = n - 1; i > 0; i--)
			v += i;
		// @formatter:off
		/**
		 * indexing convention, for v = 
		 * 0--(n-2)	: teams (excluding the subject team) 
		 * (n-1)	: source vertex 
		 * n		: sink vertex 
		 * (n+1)--v	: matchups (*use getMatchV(team1, team2))
		 */
		// @formatter:on

		// for each team, run a maxflow network to set elimination values
		for (int subject = 0; subject < n; subject++) {

			// make a simple array of team indices skipping the current team
			int[] lineup = new int[n - 1];
			for (int i = 0; i < subject; i++)
				lineup[i] = i;
			for (int i = subject + 1; i < n; i++)
				lineup[i - 1] = i;

			// set up a flow network
			FlowNetwork fn = new FlowNetwork(v);

			// for each vertex for a matchup between two teams: add v from source; and also
			// a vertex for each team from matchup to the team's vertex
			// TODO test what vertices are necessary to add for proper operation (i.e. if
			// there are no games remaining between two teams, do vertices need to be
			// added?)
			for (int a = 0; a < n - 2; a++) {
				for (int b = a + 1; b < n - 1; b++) {
//					if (getRemainingBetween(lineup[a], lineup[b]) > 0) {
					int mv = getMatchVertex(a, b);
					fn.addEdge(new FlowEdge(n - 1, mv, getRemainingBetween(lineup[a], lineup[b])));
					fn.addEdge(new FlowEdge(mv, a, Double.POSITIVE_INFINITY));
					fn.addEdge(new FlowEdge(mv, b, Double.POSITIVE_INFINITY));
//					}
				}
			}

			// edges between lineup teams and sink vertex
			for (int team = 0; team < n - 1; team++) {
				fn.addEdge(new FlowEdge(team, n, getCapacityToSink(subject, lineup[team])));
			}

			// determine if the team under examination is eliminated
			FordFulkerson ff = new FordFulkerson(fn, n - 1, n);
			for (int i = 0; i < n - 1; i++) {
				boolean inCut = ff.inCut(i);
				if (inCut) {
					teams[subject].isEliminated = true;
					teams[subject].eliminators[lineup[i]] = true;
				}
			}

//			System.out.println(teams[subject] + ": " + ff.value());
//			for (int i = 0; i < n - 1; i++) {
//				if (teams[subject].eliminators[lineup[i]])
//					System.out.println("eliminated by: " + teams[lineup[i]]);
//			}

		}
	}

// number of teams
	public int numberOfTeams() {
		return n;
	}

// all teams
	public Iterable<String> teams() {
		return teamMap.keySet();
	}

// number of wins for given team
	public int wins(String team) {
		return teams[getTeamIndex(team)].record[0];
	}

// number of losses for given team
	public int losses(String team) {
		return teams[getTeamIndex(team)].record[1];
	}

// number of remaining games for given team
	public int remaining(String team) {
		return teams[getTeamIndex(team)].record[2];
	}

// number of remaining games between team1 and team2
	public int against(String team1, String team2) {
		return teams[getTeamIndex(team1)].schedule[getTeamIndex(team2)];
	}

// is given team eliminated?
	public boolean isEliminated(String team) {
		return teams[getTeamIndex(team)].isEliminated;
	}

// subset R of teams that eliminates given team; null if not eliminated
	public Iterable<String> certificateOfElimination(String team) {
		// if not eliminated:
		if (!teams[getTeamIndex(team)].isEliminated)
			return null;

		// else:
		List<String> eliminators = new ArrayList<String>();
		for (int i = 0; i < n; i++) {
			if (teams[teamMap.get(team)].eliminators[i])
				eliminators.add(teams[i].toString());
		}
		return eliminators;
	}

	// throw illegalargexceptions if team name is not valid
	private int getTeamIndex(String team) {
		if (teamMap.containsKey(team))
			return teamMap.get(team);
		else
			throw new IllegalArgumentException();
	}

	/**
	 * return the vertex index corresponding to the matchup between the two given
	 * team indices. calculation is the sum of: the absolute difference between the
	 * team indices; plus a modifier; plus n + 1 (to shift each to a suitable index
	 * in the data structure). The modifier depends on the minimum index (note: the
	 * first team is given index zero) and is a recursive type calculation starting
	 * at n [the number of teams] and iteratively summing a smaller integer for the
	 * min team number of times.
	 * <p>
	 * e.g. for the teams (2, 4) and total teams n = 5: the absolute difference is
	 * 2; the modifier is 4 + 3 (2 iterations, starting at n - 1); and 6 (n + 1) is
	 * added to shift the result to the correct array index.
	 * 
	 * @param i a team index
	 * @param j a team index
	 * @return a vertex index
	 */
	private int getMatchVertex(int i, int j) {
		int v = n - 1;
		for (int count = Math.min(j, i), toAdd = n - 2; count > 0; count--, toAdd--) {
			v += toAdd;
		}
		return v + Math.abs(j - i) + 1;
	}

	// overloaded method for testing takes an n-value manually
	private int getMatchV(int i, int j, int n) {
		int v = n - 1;
		for (int count = Math.min(j, i), toAdd = n - 2; count > 0; count--, toAdd--) {
			v += toAdd;
		}
		return v + Math.abs(j - i) + 1;
	}

	// calculates the capacity for an edge between a team and the sink vertex
	private int getCapacityToSink(int subject, int modifier) {
		int cap = (teams[subject].record[0] + teams[subject].record[2]) - teams[modifier].record[0];
		if (cap < 0)
			return 0;
		else
			return cap;
	}

	private int getRemainingBetween(int teamA, int teamB) {
		return teams[teamA].schedule[teamB];
	}

	/**
	 * 
	 * A basic representation of a team at some point during this league season:
	 * holding its current record (encoded in an int[3] {wins, losses, remaining
	 * games}), and an int[n = teams in this league] representing the number of
	 * remaining games against the other teams in this league.
	 * 
	 * @author PCG
	 *
	 */
	private class Team {
		final String name;
		final int[] record, schedule;
		boolean isEliminated;
		boolean[] eliminators;

		/**
		 * A team in this league.
		 * 
		 * @param name     the team name
		 * @param record   wins, losses, total remaining games
		 * @param schedule remaining matches against other teams
		 */
		Team(String name, int[] record, int[] schedule) {
			this.name = name;
			this.record = record;
			this.schedule = schedule;
			eliminators = new boolean[n];
		}

		@Override
		public String toString() {
			return name;
		}

		public void printAllDetails() {
			System.out.println(name);
			for (int i : record)
				System.out.print(" " + i);
			System.out.println();
			for (int i : schedule)
				System.out.print(" " + i);
			System.out.println();
		}
	}

	// this should be left as-is for the required output formatting:
	public static void main(String[] args) {
		BaseballElimination division = new BaseballElimination(args[0]);
		for (String team : division.teams()) {
			if (division.isEliminated(team)) {
				StdOut.print(team + " is eliminated by the subset R = { ");
				for (String t : division.certificateOfElimination(team)) {
					StdOut.print(t + " ");
				}
				StdOut.println("}");
			} else {
				StdOut.println(team + " is not eliminated");
			}
		}
	}

}
