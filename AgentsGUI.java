package Model;

import java.awt.Color;

import spaces.Spaces;
import sweep.GUIStateSweep;
import sweep.SimStateSweep;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.engine.SimState;


public class AgentsGUI extends GUIStateSweep {

    SparseGridPortrayal2D agentsPortrayal = new SparseGridPortrayal2D();

    public AgentsGUI(SimStateSweep state, int gridWidth, int gridHeight, Color backdrop, Color agentDefaultColor,
            boolean agentPortrayal) {
        super(state, gridWidth, gridHeight, backdrop, agentDefaultColor, agentPortrayal);
        // TODO Auto-generated constructor stub
    }

    public AgentsGUI(SimStateSweep state) {
        super(state);
        // TODO Auto-generated constructor stub
    }

    public void setupPortrayals() {
        Environment env = (Environment) state;

        agentsPortrayal.setField(env.sparseSpace);
        agentsPortrayal.setPortrayalForAll(new OvalPortrayal2D() {
            public void draw(Object object, java.awt.Graphics2D graphics, sim.portrayal.DrawInfo2D info) {
            	Agent agent = (Agent) object;
                if (object instanceof Agent) {
                    if (agent.inQuarantine) {
                        paint = Color.RED; // Quarantined agents
                    } else if (agent.status == Environment.Status.SUSCEPTIBLE) {
                        paint = Color.GREEN; // Susceptible agents
                    } else if (agent.status == Environment.Status.EXPOSED) {
                        paint = Color.ORANGE; // Exposed agents
                    } else if (agent.status == Environment.Status.INFECTED) {
                        paint = Color.RED; // Infected agents
                    } else if (agent.status == Environment.Status.RECOVERED) {
                        paint = Color.BLUE; // Recovered agents
                    }
                }
                super.draw(object, graphics, info);
                // If the agent is quarantined, draw a black border around it
                if (agent.inQuarantine) {
                    graphics.setColor(Color.BLACK);
                    graphics.drawOval((int) (info.draw.x - info.draw.width / 2.0),
                                      (int) (info.draw.y - info.draw.height / 2.0),
                                      (int) info.draw.width, (int) info.draw.height);
                }
            }
        });

        display.reset();
        display.setBackdrop(Color.WHITE);
        display.repaint();
    }

    public void start() {
        super.start();
        setupPortrayals();
        display.attach(agentsPortrayal, "Agents");
    }

    public void load(SimState state) {
        super.load(state);
        setupPortrayals();
    }

    public static void main(String[] args) {
        AgentsGUI.initialize(Environment.class, null, AgentsGUI.class, 400, 400, Color.WHITE, Color.BLUE, true,
                Spaces.SPARSE);
    }

}
