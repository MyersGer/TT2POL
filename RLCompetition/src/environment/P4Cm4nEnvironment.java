package environment;

import java.util.Random;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;
import org.rlcommunity.rlglue.codec.util.EnvironmentLoader;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;


public class P4Cm4nEnvironment implements EnvironmentInterface {

    static final int WORLD_FREE = 0;
    static final int WORLD_OBSTACLE = 1;
    static final int WORLD_PILL = 2;
    static final int WORLD_POWERPILL = 3;
    static final int WORLD_GHOST = 4;

    //WorldDescription contains the state of the world and manages the dynamics
    WorldDescription theWorld;
    
    //These are used if the environment has been sent a message to use a fixed starting state.
    boolean fixedStartState = false;
    int startRow = 0;
    int startCol = 0;
    
    
    
    
    public P4Cm4nEnvironment() {
		super();
		init_map();
	}

	private void init_map(){
        int world_map[][] = new int[][]{
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 3, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 3, 1},
                {1, 2, 1, 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1},
                {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                {1, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                {1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1},
                {1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1},
                {1, 2, 1, 1, 1, 1, 2, 2, 2, 0, 2, 2, 2, 1, 1, 1, 2, 1},
                {1, 2, 2, 2, 2, 1, 2, 1, 1, 0, 1, 1, 2, 1, 1, 1, 2, 1},
                {1, 1, 2, 1, 2, 1, 2, 1, 1, 0, 1, 1, 2, 1, 1, 1, 2, 1},
                {1, 1, 3, 1, 2, 2, 2, 1, 0, 4, 0, 1, 2, 2, 2, 2, 3, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
            };

            theWorld = new WorldDescription(world_map);
    }

    public String env_init() {
        //This is hard coded, but there is no reason it couldn't be automatically
        //generated or read from a file.


        //Create a task spec programmatically.  This task spec encodes that state, action, and reward space for the problem.
        //You could forgo the task spec if your agent and environment have been created specifically to work with each other
        //ie, there is no need to share this information at run time.  You could also use your own ad-hoc task specification language,
        //or use the official one but just hard code the string instead of constructing it this way.
        TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        theTaskSpecObject.setEpisodic();
        theTaskSpecObject.setDiscountFactor(1.0d);

        //Specify that there will be an integer observation [0,107] for the state
        theTaskSpecObject.addDiscreteObservation(new IntRange(0, theWorld.getNumStates() - 1));
        //Specify that there will be an integer action [0,3]
        theTaskSpecObject.addDiscreteAction(new IntRange(0, 3));
        //Specify the reward range [-100,10]
//        theTaskSpecObject.setRewardRange(new DoubleRange(-100.0d, 10.0d + (world_map.length * world_map[0].length)));
        theTaskSpecObject.setRewardRange(new DoubleRange(-100.0d, 10.0d + (theWorld.getMapSizeX() * theWorld.getMapSizeY())));

        theTaskSpecObject.setExtra("PacMan");

        String taskSpecString = theTaskSpecObject.toTaskSpec();
        TaskSpec.checkTaskSpec(taskSpecString);

        return taskSpecString;
    }
    
    /**
     * Put the environment in a random state and return the appropriate observation.
     * @return
     */
    public Observation env_start() {
    	
    	theWorld.resetWorld();
        if (fixedStartState) {
            boolean stateIsValid = theWorld.setAgentState(startRow, startCol);
            if (!stateIsValid) {
                theWorld.setRandomAgentState();
            }
        } else {
            theWorld.setRandomAgentState();
        }
        Observation theObservation = new Observation(1, 0, 0);
        theObservation.setInt(0, theWorld.getState());
        return theObservation;
    }

    /**
     * Make sure the action is in the appropriate range, update the state,
     * generate the new observation, reward, and whether the episode is over.
     * @param thisAction
     * @return
     */
    public Reward_observation_terminal env_step(Action thisAction) {
        /* Make sure the action is valid */
        assert (thisAction.getNumInts() == 1) : "Expecting a 1-dimensional integer action. " + thisAction.getNumInts() + "D was provided";
        assert (thisAction.getInt(0) >= 0) : "Action should be in [0,4], " + thisAction.getInt(0) + " was provided";
        assert (thisAction.getInt(0) < 4) : "Action should be in [0,4], " + thisAction.getInt(0) + " was provided";

        theWorld.updatePosition(thisAction.getInt(0));
        Observation theObservation = new Observation(1, 0, 0);
        theObservation.setInt(0, theWorld.getState());
        Reward_observation_terminal RewardObs = new Reward_observation_terminal();
        RewardObs.setObservation(theObservation);
        RewardObs.setTerminal(theWorld.isTerminal());
        RewardObs.setReward(theWorld.getReward());

        return RewardObs;
    }

    public void env_cleanup() {
    }

    public String env_message(String message) {
        /*	Message Description
         * 'set-random-start-state'
         * Action: Set flag to do random starting states (the default)
         */
        if (message.startsWith("set-random-start-state")) {
            fixedStartState = false;
            return "Message understood.  Using random start state.";
        }

        /*	Message Description
         * 'set-start-state X Y'
         * Action: Set flag to do fixed starting states (row=X, col=Y)
         */
        if (message.startsWith("set-start-state")) {
            String[] theTokens = message.split(" ");
            startRow = Integer.parseInt(theTokens[1]);
            startCol = Integer.parseInt(theTokens[2]);
            fixedStartState = true;
            return "Message understood.  Using fixed start state.";
        }

        /*	Message Description
		*	'print-state'
		*	Action: Print the map and the current agent location
        */
		if (message.startsWith("print-state")){
			theWorld.print_state();
			return "Message understood.  Printed the state.";
        }
		
		if(message.startsWith("team name")) {
			return "P4Cm4n";
		}
		
		if(message.startsWith("team members")) {
			return "Steffen Brauer,André Harms,Florian Johannßen,Jan-Christoph Meier,Florian Ocker,Olaf Potratz,Torben Woggan";			
		}
		
        return "PacMan does not understand your message.";
    }

    /**
     * This is a trick we can use to make the agent easily loadable.
     * @param args
     */
    public static void main(String[] args) {
        EnvironmentLoader theLoader = new EnvironmentLoader(new P4Cm4nEnvironment());
        theLoader.run();
    }

	public WorldDescription getTheWorld() {
		return theWorld;
	}
    

}
