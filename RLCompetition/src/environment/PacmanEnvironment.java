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


public class PacmanEnvironment implements EnvironmentInterface {

    static final int WORLD_FREE = 0;
    static final int WORLD_OBSTACLE = 1;
    static final int WORLD_PILL = 2;
    static final int WORLD_POWERPILL = 3;
    static final int WORLD_GHOST = 4;

    //WorldDescription contains the state of the world and manages the dynamics.
    WorldDescription theWorld;
    //These are used if the environment has been sent a message to use a fixed
    //starting state.
    boolean fixedStartState = false;
    int startRow = 0;
    int startCol = 0;

    public String env_init() {
        //This is hard coded, but there is no reason it couldn't be automatically
        //generated or read from a file.

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
        theTaskSpecObject.setRewardRange(new DoubleRange(-100.0d, 10.0d));

        theTaskSpecObject.setExtra("SampleMinesEnvironment(Java) by Brian Tanner.");

        String taskSpecString = theTaskSpecObject.toTaskSpec();
        TaskSpec.checkTaskSpec(taskSpecString);

        return taskSpecString;
    }
    
    /**
     * Put the environment in a random state and return the appropriate observation.
     * @return
     */
    public Observation env_start() {
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

        return "SamplesMinesEnvironment(Java) does not understand your message.";
    }

    /**
     * This is a trick we can use to make the agent easily loadable.
     * @param args
     */
    public static void main(String[] args) {
        EnvironmentLoader theLoader = new EnvironmentLoader(new PacmanEnvironment());
        theLoader.run();
    }
}








/**
 * This class holds all of the internal state information about the environment,
 * and manages the dynamics, state update, reward calculation, etc.
 * @author btanner
 */
class WorldDescription {

    private final int numRows;
    private final int numCols;
    public int agentRow;
    public int agentCol;
    private final int[][] theMap;
    private Random randGen = new Random();

    public WorldDescription(int[][] worldMap) {
        this.theMap = worldMap;

        this.numRows = theMap.length;
        this.numCols = theMap[0].length;
    }

    public int getNumStates() {
        return numRows * numCols;
    }
    

    /**
     * Puts the agent into a random state.  Uses a generate and test method, in
     * a loop, only accepts the state if it is valid and not terminal.
     */
    public void setRandomAgentState() {

        int startRow = randGen.nextInt(numRows);
        int startCol = randGen.nextInt(numCols);



        this.agentRow = startRow;
        this.agentCol = startCol;
    }

    /**
     * Convert the row/col state into a single number.
     * @return
     */
    public int getState() {
        return agentCol * numRows + agentRow;
    }

    /**
     * Sets the agent current state to startRow,startCol.
     * @param startRow
     * @param startCol
     * @return true if the state is valid and not terminal, otherwise
     * return false.
     */
    boolean setAgentState(int startRow, int startCol) {
        this.agentRow = startRow;
        this.agentCol = startCol;

        return isValid(startRow, startCol) && !isTerminal();
    }

    
    private int getPillCount(){
    	int pillCount=0;
    	
    	//über alle Felder iterieren
    	for(int y=0; y<theMap.length; y++){
    		for(int x=0; x<theMap[y].length; x++){
    			if(theMap[y][x] == PacmanEnvironment.WORLD_PILL || theMap[y][x] == PacmanEnvironment.WORLD_POWERPILL)
    				pillCount++; //Pillen zählen
    		}
    	}
    	return pillCount;
    }

    public boolean isTerminal() {
    	if(getPillCount() <= 0){
            return true;
        }
        return false;
    }

    private boolean isValid(int row, int col) {
        boolean valid = false;
        if (row < numRows && row >= 0 && col < numCols && col >= 0) {
            if (theMap[row][col] != PacmanEnvironment.WORLD_OBSTACLE) {
                valid = true;
            }
        }
        return valid;
    }

    /**
     * Calculate the reward for the current agent state.
     * @return
     */
    public double getReward() {
      /*  if (theMap[agentRow][agentCol] == PacmanEnvironment.WORLD_GOAL) {
            return 10.0f;
        }

        if (theMap[agentRow][agentCol] == PacmanEnvironment.WORLD_MINE) {
            return -100.0f;
        }
       */
        return -1.0f;
    }

    public void updatePosition(int theAction) {
        /* When the move would result in hitting an obstacles, the agent simply doesn't move */
        int newRow = agentRow;
        int newCol = agentCol;


        if (theAction == 0) {/*move down*/
            newCol = agentCol - 1;
        }
        if (theAction == 1) { /*move up*/
            newCol = agentCol + 1;
        }
        if (theAction == 2) {/*move left*/
            newRow = agentRow - 1;
        }
        if (theAction == 3) {/*move right*/
            newRow = agentRow + 1;
        }


        /*Check if new position is out of bounds or inside an obstacle */
        if (isValid(newRow, newCol)) {
            agentRow = newRow;
            agentCol = newCol;
        }
        
        /*Geisterupdate Krempel (Skizze) / Pseudocode
        ghost.getLastPos();
        lastPos.State = ghost.getLastPosState()
        ghost.update();
        */
    }

    /**
     * Print out the current state to the screen
     */
    void print_state() {
        System.out.printf("Agent is at: %d,%d\n", agentRow, agentCol);
        System.out.printf("Columns:0-10                10-17\n");
        System.out.printf("Col    ");
        for (int col = 0; col < 18; col++) {
            System.out.printf("%d ", col % 10);
        }

        for (int row = 0; row < 6; row++) {
            System.out.printf("\nRow: %d ", row);

            for (int col = 0; col < 18; col++) {
                if (agentRow == row && agentCol == col) {
                    System.out.printf("A ");
                } else {
                    if (this.getPillCount() <= 0) {
                        System.out.printf("G ");
                    }
                    if (theMap[row][col] == PacmanEnvironment.WORLD_PILL) {
                        System.out.printf("P");
                    }
                    if (theMap[row][col] == PacmanEnvironment.WORLD_POWERPILL) {
                        System.out.printf("PWP ");
                    }
                    if (theMap[row][col] == PacmanEnvironment.WORLD_OBSTACLE) {
                        System.out.printf("* ");
                    }
                    if (theMap[row][col] == PacmanEnvironment.WORLD_FREE) {
                        System.out.printf("  ");
                    }
                }
            }
        }
        System.out.printf("\n");
    }
}
