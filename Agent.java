package Model;

import Model.Environment.Status;
import ec.util.MersenneTwisterFast;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;

public class Agent implements Steppable {

	int x;
	int y;
	int xdir;
	int ydir;

	// new variables
	double compliance; // probability of quarantine compliance
	boolean inQuarantine;
	int sickTime = 0; // # of steps since becoming infected
	// SEIR statuses
	Environment.Status status;

	// Agent constructor
	public Agent(int x, int y, int xdir, int ydir, double compliance, Status status) {
		super();
		this.x = x;
		this.y = y;
		this.xdir = xdir;
		this.ydir = ydir;
		this.status = status;
		this.compliance = compliance;
		this.inQuarantine = false;
	}

	@Override
	public void step(SimState state) {
		Environment env = (Environment) state;

		if (this.status == Environment.Status.EXPOSED) {
			this.status = Environment.Status.INFECTED;
			return; // might need to refine logic?
		}
		// Check quarantine
		if (this.status == Environment.Status.INFECTED) {
			this.sickTime++;
			if (env.clock >= env.burninTime && this.inQuarantine) {
				checkRecover(env);
				if (this.sickTime > env.quarantineTime) {
					this.inQuarantine = false;
				}
			}

		}
		if (!this.inQuarantine) {
			move(env);
			if (this.status == Status.SUSCEPTIBLE) {
				Bag neighbors = findSickNeighbors(env);
				interact(env, neighbors);
			}
		}
	}

	void checkQuarantine(Environment env) {
		if (env.random.nextBoolean(compliance)) {
			this.inQuarantine = true;
		}
	}

	void checkRecover(Environment env) {
		int recoveryTime = env.recoveryTime + env.random.nextInt(2 * env.recoveryError) - env.recoveryError;
		if (this.sickTime >= recoveryTime && env.random.nextBoolean(env.recoveryRate)) {
			this.status = Environment.Status.RECOVERED;
			this.inQuarantine = false;
		}
	}

	// double check move
	public void move(Environment state) {
		if (!state.random.nextBoolean(state.getpActive())) {
			return;
		}
		if (state.random.nextBoolean(state.getP())) {
			xdir = state.random.nextInt(3) - 1;
			ydir = state.random.nextInt(3) - 1;
		}
		placeAgent(state);

	}

	// implement TODO
	Bag findSickNeighbors(Environment env) {

	}

	void interact(Environment env, Bag neighbors) {

	}

	public void placeAgent(Environment state) {
		x = state.sparseSpace.stx(x + xdir);
		y = state.sparseSpace.stx(y + ydir);
		state.sparseSpace.setObjectLocation(this, x, y);
	}

}
