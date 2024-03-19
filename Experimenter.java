package Model;

import observer.Observer;
import sim.engine.SimState;
import sim.util.Bag;
import sweep.ParameterSweeper;
import sweep.SimStateSweep;
import Model.Agent;
import Model.AgentsGUI;
import Model.Environment.Status;

//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;

public class Experimenter extends Observer {
	public int infectedCount = 0;

	// Maps to hold aggregated data
//	private Map<String, Integer> stateCounts = new HashMap<>();
//	private List<Map<String, Object>> simulationData = new ArrayList<>();

	public Experimenter(String fileName, String folderName, SimStateSweep state, ParameterSweeper sweeper,
			String precision, String[] headers) {
		super(fileName, folderName, state, sweeper, precision, headers);

	}

	public void step(SimState state) {
		super.step(state);
//		Environment estate = (Environment) state;
//		if (estate.paramSweeps) {
//			reset(state);
//			countInfectedAgents(estate);
//			nextInterval();
//		}

//        if (step % this.state.dataSamplingInterval == 0) {
//            countAgentsByStatus((Environment) state);
//        }
		if (step % this.state.dataSamplingInterval == 0) {
			reset(state);
			countInfectedAgents((Environment) state);
			nextInterval();
		}

	}

	public void countInfectedAgents(Environment state) {
		Bag agents = state.sparseSpace.getAllObjects();

		for (int i = 0; i < agents.numObjs; i++) {
			Agent a = (Agent) agents.objs[i];
			if (a.status == Status.INFECTED) {
				infectedCount++;
			}
		}

		System.out.println("Infected Count: " + infectedCount);

		double time = (double) state.schedule.getTime();
		this.upDateTimeChart(time, infectedCount, true, 1000);
	}

	public boolean nextInterval() {
		int infected = infectedCount;
		data.add(infected);
		return false;
	}

	public boolean reset(SimState state) {
		infectedCount = 0;
		return true;
	}

}
