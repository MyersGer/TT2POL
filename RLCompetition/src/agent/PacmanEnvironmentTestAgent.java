package agent;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

public class PacmanEnvironmentTestAgent implements AgentInterface {

    public void agent_init(String taskSpecification) {
    	System.out.println("agent_init");
    }

    /**
     * Choose an action e-greedily from the value function and store the action
     * and observation.
     * @param observation
     * @return
     */
    public Action agent_start(Observation observation) {
    	System.out.println("agent_start()");
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = 1;
        return returnAction;
    }

    /**
     * Choose an action e-greedily from the value function and store the action
     * and observation.  Update the valueFunction entry for the last
     * state,action pair.
     * @param reward
     * @param observation
     * @return
     */
    public Action agent_step(double reward, Observation observation) {
        Action returnAction = new Action();
        returnAction.intArray = new int[]{ 1 };
        System.out.println("agent_step()");
        return returnAction;
    }

    /**
     * The episode is over, learn from the last reward that was received.
     * @param reward
     */
    public void agent_end(double reward) {
    	System.out.println("agent_end()");
    }

    /**
     * Release memory that is no longer required/used.
     */
    public void agent_cleanup() {
    	System.out.println("agent_cleanup()");
    }

    /**
     * This agent responds to some simple messages for freezing learning and
     * saving/loading the value function to a file.
     * @param message
     * @return
     */
    public String agent_message(String message) {

        if (message.equals("team name")) {
            return "PacMan";
        }
        if (message.equals("team members")) {
            return "Steffen Brauer, Andre Harms, Florian Johann�en, Jan-Christoph Meier, Florian Ocker, Olaf Potratz, Torben Woggan";
        }

        return this.getClass().getName() + " does not understand your message.";

    }

    /**
     * This is a trick we can use to make the agent easily loadable.  Using this
     * trick you can directly execute the class and it will load itself through
     * AgentLoader and connect to the rl_glue server.
     * @param args
     */
    public static void main(String[] args) {
        AgentLoader theLoader = new AgentLoader(new PacmanEnvironmentTestAgent());
        theLoader.run();
    }

}
