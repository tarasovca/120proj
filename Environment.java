package Model;

import sim.util.Bag;
import spaces.Spaces;
import sweep.SimStateSweep;
import sim.util.distribution.Normal;
import Model.Experimenter;
import observer.Observer;

public class Environment extends SimStateSweep {
	int gridWidth = 100;
	int gridHeight = 100;
	boolean oneAgentPerCell = false;
	double pActive = 1;
	double p = 1;
	boolean aggregate = false;

	public boolean charts = true;
	public Bag all_agents;

	int recoveryError = 5;

	double randMove = 0.3;
	boolean shareSpace = true;

	public enum Status {
		SUSCEPTIBLE, EXPOSED, INFECTED, RECOVERED
	};

	public int clock = 0;

	// parameter sweeps
	int searchRadius = 1;
	int recoveryTime = 50;
	int numAgents = 150;
	int recoveryRate = 100;
	double complianceAvg = 0.1;
	double complianceSD = 0.05;
	int burninTime = 10;
	double baseInfectionRate = 0.8;
	public int quarantineTime = 10;

	public Environment(long seed) {
		super(seed);
		// TODO Auto-generated constructor stub
	}

	public Environment(long seed, Class observer) {
		super(seed, observer);
		// TODO Auto-generated constructor stub
	}

	public Environment(long seed, Class observer, String runTimeFileName) {
		super(seed, observer, runTimeFileName);
		// TODO Auto-generated constructor stub
	}

	public void makeAgents() {

		if (oneAgentPerCell) {
			int size = gridWidth * gridHeight;
			if (numAgents > size) {
				numAgents = size;
				System.out.println("Changed the number of agents to" + numAgents);
			}
		}

		// Normal distribution for compliance
		Normal normalDist = new Normal(complianceAvg, complianceSD, random);

		// Create Patient zero (INFECTED)
		int x = random.nextInt(gridWidth);
		int y = random.nextInt(gridHeight);
		int xdir = random.nextInt(3) - 1; // -1, 0, or 1
		int ydir = random.nextInt(3) - 1; // -1, 0, or 1
		double compliance = normalDist.nextDouble();
		compliance = Math.max(0, Math.min(compliance, 1)); // compliance between 0 and 1
		// compliance = random.nextDouble(); //TODO edit how it is calculated if needed

		Agent patientZero = new Agent(x, y, xdir, ydir, compliance, Environment.Status.INFECTED, false);
		sparseSpace.setObjectLocation(patientZero, x, y);
		schedule.scheduleRepeating(patientZero);

		// Create the rest of the agents (SUSCEPTIBLE)
		for (int i = 1; i < numAgents; i++) {
			int tempx = random.nextInt(gridWidth);
			int tempy = random.nextInt(gridHeight);
			if (oneAgentPerCell) {
				Bag objectsAtLocation = sparseSpace.getObjectsAtLocation(tempx, tempy);
				while (objectsAtLocation != null && !objectsAtLocation.isEmpty()) {
					tempx = random.nextInt(gridWidth);
					tempy = random.nextInt(gridHeight);
					objectsAtLocation = sparseSpace.getObjectsAtLocation(tempx, tempy);

				}
			}

			int tempXdir = random.nextInt(3) - 1;
			int tempYdir = random.nextInt(3) - 1;
			double tempCompliance = normalDist.nextDouble();
			tempCompliance = Math.max(0, Math.min(compliance, 1)); // compliance between 0 and 1
			// tempCompliance = random.nextDouble(); //TODO edit how it is calculated if
			// needed

			Agent a = new Agent(tempx, tempy, tempXdir, tempYdir, tempCompliance, Environment.Status.SUSCEPTIBLE,
					false);

			sparseSpace.setObjectLocation(a, tempx, tempy);
			schedule.scheduleRepeating(a);

		}
	}

	public void start() {
		super.start();
		// spaces = Spaces.SPARSE;
//		make2DSpace(spaces, gridWidth, gridHeight);
		makeSpace(gridWidth, gridHeight);
		makeAgents();
		if (observer != null) {
			observer.initialize(sparseSpace, spaces);
			// initialize the experimenter by calling initialize in the parent class
		}
	}

	public int getGridWidth() {
		return gridWidth;
	}

	public void setGridWidth(int gridWidth) {
		this.gridWidth = gridWidth;
	}

	public int getGridHeight() {
		return gridHeight;
	}

	public void setGridHeight(int gridHeight) {
		this.gridHeight = gridHeight;
	}

	public int getN() {
		return numAgents;
	}

	public void setN(int n) {
		this.numAgents = n;
	}

	public boolean isOneAgentPerCell() {
		return oneAgentPerCell;
	}

	public void setOneAgentPerCell(boolean oneAgentPerCell) {
		this.oneAgentPerCell = oneAgentPerCell;
	}

	public double getpActive() {
		return pActive;
	}

	public void setpActive(double pActive) {
		this.pActive = pActive;
	}

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}

	public boolean isAggregate() {
		return aggregate;
	}

	public void setAggregate(boolean aggregate) {
		this.aggregate = aggregate;
	}

	public int getSearchRadius() {
		return searchRadius;
	}

	public void setSearchRadius(int searchRadius) {
		this.searchRadius = searchRadius;
	}

}
