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
	public Agent(int x, int y, int xdir, int ydir, double compliance, Environment.Status status, boolean inQuarantine) {
		super();
		this.x = x;
		this.y = y;
		this.xdir = xdir;
		this.ydir = ydir;
		this.status = status;
		this.compliance = compliance;
		this.inQuarantine = inQuarantine;
		this.sickTime = 0;
	}

	@Override
	public void step(SimState state) {
		Environment env = (Environment) state;
		env.clock++;

		if (this.status == Environment.Status.EXPOSED) {
			this.status = Environment.Status.INFECTED;
			// return; // no need to return anything?
		}
		// Check quarantine
		if (this.status == Environment.Status.INFECTED) {
			this.sickTime++;
			if (env.clock >= env.burninTime) {
				if (this.inQuarantine) {
					checkRecover(env);
				}
				if (this.sickTime > env.quarantineTime) {
					this.inQuarantine = false;
				}
				checkQuarantine(env);
				checkRecover(env);
				if (this.inQuarantine) {
					return;
				}
			}

		}

		// According to the model screenshot
		else if (!this.inQuarantine) {
			move(env);
            Bag neighbors = findSickNeighbors(env);
            interact(env, neighbors);

		} else if (this.inQuarantine) {
			if (this.sickTime >= env.quarantineTime) {
				checkRecover(env);
			} else {
				this.sickTime++;
			}
			return;

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
		// calculate recovery time randomly
	    int recoveryTime = env.recoveryTime + env.random.nextInt(2 * env.recoveryError) - env.recoveryError;

	    // check if sickTime == recoveryTime
	    if (this.sickTime == recoveryTime) {
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


	Bag findSickNeighbors(Environment env) {
		Bag neighbors = env.sparseSpace.getMooreNeighbors(this.x, this.y, env.searchRadius, SparseGrid2D.TOROIDAL,
				false);
		Bag sickNeighbors = new Bag();
		for (int i = 0; i < neighbors.numObjs; i++) {
			Agent a = (Agent) neighbors.objs[i];
			if (a.status == Environment.Status.INFECTED && !a.inQuarantine) {
				sickNeighbors.add(a);
			}
		}
		return sickNeighbors;

	}

	void interact(Environment env, Bag neighbors) {
	    if (neighbors.isEmpty()) {
	        return;
	    }
	    for (int i = 0; i < neighbors.numObjs; i++) {
	        Agent neighbor = (Agent) neighbors.objs[i];
	        if (neighbor.status == Environment.Status.INFECTED) {
	            boolean becomesExposed = env.random.nextDouble() < env.baseInfectionRate;
	            if (becomesExposed) {
	                this.status = Environment.Status.EXPOSED;
	                return;
	            }
	        }
	    }
	}


	public void placeAgent(Environment state) {
		x = state.sparseSpace.stx(x + xdir);
		y = state.sparseSpace.stx(y + ydir);
		state.sparseSpace.setObjectLocation(this, x, y);
	}

}
