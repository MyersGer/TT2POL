package examples.mines;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;

/**
This is a very simple MC On-Policy Agent.

 * @author Steffen Brauer
 */
public class P4Cm4nAgentSteffen implements AgentInterface {

    private Random randGenerator = new Random();
    private double[][] qFunction = null;
    private double[][] pi = null;
    private int numActions = 0;
    private int numStates = 0;
    private double epsilon = 0.9;
    
    private List<Integer> states;
    private List<Integer> actions;
    private List<Double>  rewards;
    private List<Double>  rewards2;
    private int episodecounter;

    /**
     * Parse the task spec, make sure it is only 1 integer observation and
     * action, and then allocate the valueFunction.
     *
     * @param taskSpecification
     */
    public void agent_init(String taskSpecification) {
        TaskSpec theTaskSpec = new TaskSpec(taskSpecification);

        /* Lots of assertions to make sure that we can handle this problem.  */
        assert (theTaskSpec.getNumDiscreteObsDims() == 1);
        assert (theTaskSpec.getNumContinuousObsDims() == 0);
        assert (!theTaskSpec.getDiscreteObservationRange(0).hasSpecialMinStatus());
        assert (!theTaskSpec.getDiscreteObservationRange(0).hasSpecialMaxStatus());
        numStates = theTaskSpec.getDiscreteObservationRange(0).getMax() + 1;

        assert (theTaskSpec.getNumDiscreteActionDims() == 1);
        assert (theTaskSpec.getNumContinuousActionDims() == 0);
        assert (!theTaskSpec.getDiscreteActionRange(0).hasSpecialMinStatus());
        assert (!theTaskSpec.getDiscreteActionRange(0).hasSpecialMaxStatus());
        numActions = theTaskSpec.getDiscreteActionRange(0).getMax() + 1;

    //    sarsa_gamma=theTaskSpec.getDiscountFactor();

      pi = new double[numActions][numStates];
      qFunction = new double[numActions][numStates];

 	 	for(int i = 0; i < numActions ; i++){
 	 		for(int j = 0; j < numStates ; j++){
 	 			pi[i][j] = epsilon/numActions + randGenerator.nextDouble();
 	 		}
 	 	}
    }

    /**
     * Choose an action e-greedily from the value function and store the action
     * and observation.
     * @param observation
     * @return
     */
    public Action agent_start(Observation observation) {
    	//System.out.println("start");
    	states = new ArrayList<Integer>();
    	actions = new ArrayList<Integer>();
    	rewards = new ArrayList<Double>();
    	rewards2 = new ArrayList<Double>();
   	 	this.episodecounter = 0;
    	
   	 	//Strategie P ist e-soft, wenn P(s,a) >= e/A(s) für alle s, a
   	 	
        /**
         * Initialize, for all s element S, a element A(s)
         * Q(s,a) <- arbitrary
         * Returns(s,a) <- empty list
         * pi <- an arbitrary e-soft policy
         * 
         * Repeat forever:
         * 	(a) Generate an episode using pi
         *  (b) For each pair s,a appearing in the episode:
         *  	R- return following the first occurence of s,a
         *  	Append R to Returns(s,a)
         *  	Q(s,a) <- average(Returns(s,a))
         *  (c) For each s in the episode:
         *  	a* <- arg max_a Q(s,a)
         *  	For all a element A(s):
         *  				1-e+e/|a(s)| if a = a*
         *  	pi(s,a)
         *  				e/|A(s)|     if a != a*
         */
   	 	int maxIndex = 0;
   	 	for (int a = 1; a < numActions; a++) {
         if (pi[a][observation.getInt(0)] > pi[maxIndex][observation.getInt(0)]) {
             maxIndex = a;
         }
     }

        Action returnAction = new Action(1, 0, 0);

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
    	//System.out.println("step");
    	this.episodecounter = this.episodecounter + 1;
    	states.add(observation.getInt(0));
    	rewards.add( reward);
    	int maxIndex = 0;
        for (int a = 1; a < numActions; a++) {
            if (pi[a][observation.getInt(0)] > pi[maxIndex][observation.getInt(0)]) {
                maxIndex = a;
            }
        }
        Action returnAction = new Action();
        returnAction.intArray = new int[]{maxIndex};
        actions.add( maxIndex);
        return returnAction;
    }

    /**
     * The episode is over, learn from the last reward that was received.
     * @param reward
     */
    public void agent_end(double reward) {
    	//System.out.println("end");
    	/*(b) For each pai s,a appearing in the episode:
            *  	R- return following the first occurence of s,a
            *  	Append R to Returns(s,a)
            *  	Q(s,a) <- average(Returns(s,a))
            */
    	for(int i = 0; i < states.size(); i++){
    		double r = 0;
    		for(int j = i; j < states.size(); j++){
    			r = r + rewards.get(j);
    		}
    		rewards2.add( r);	
    	}
    	for(int i = 0; i< states.size(); i++){
    		int state = states.get(i);
    		int action = actions.get(i);
    		int count = 0;
    		double sum = 0;
    		for(int j = 0; j < states.size(); j ++){
    			if(state == states.get(j) && action == actions.get(j)){
    				count = count + 1;
    				sum = sum + rewards2.get(j);
    			}
    		}
    		qFunction[action][state] = sum/count;
    	}
    	List<Integer> temp = new ArrayList<Integer>();
    	for(Integer state : states){
    		if(!temp.contains(state)){
    			temp.add(state);
    			int maxIndex = 0;
    	   	 	for (int a = 1; a < numActions; a++) {
    	   	 		if (qFunction[a][state] > pi[maxIndex][state]) {
    	   	 			maxIndex = a;
    	   	 		}
    	   	 	}
    	         int a_star = maxIndex;
    	         for(int i = 0; i < actions.size(); i++){
    	        	 if(actions.get(i) == a_star){
    	        		 pi[i][state] = 1-epsilon+(epsilon/numActions);
    	        	 }else{
    	        		 pi[i][state] = epsilon/numActions;
    	        	 }
    	         }
    	         
    		}
    	}
            /*  (c) For each s in the episode:
            *  	a* <- arg max_a Q(s,a)
            *  	For all a element A(s):
            *  				1-e+e/|a(s)| if a = a*
            *  	pi(s,a)
            *  				e/|A(s)|     if a != a*
            */
 
    }

    /**
     * Release memory that is no longer required/used.
     */
    public void agent_cleanup() {
    	System.out.println("clean");
        //alles auf null setzen
    }

    /**
     * This agent responds to some simple messages for freezing learning and
     * saving/loading the value function to a file.
     * @param message
     * @return
     */
    public String agent_message(String message) {
    	//System.out.println("a_message");
    	if (message.equals("team name")) {
            return "P4Cm4n";
        }
        if (message.equals("team members")) {
            return "Steffen Brauer, Andre Harms, Florian Johannßen, Jan-Christoph Meier, Florian Ocker, Olaf Potratz, Torben Woggan";
        }
        return "P4Cm4n(MC On-Policy) does not understand your message.";

    }

    public static void main(String[] args) {
        AgentLoader theLoader = new AgentLoader(new P4Cm4nAgentSteffen());
        theLoader.run();
    }

    /**
     * Dumps the value function to a file named theFileName.  Not fancy. Must be
     * called after init but before cleanup.
     * @param theFileName
     */
    private void saveValueFunction(String theFileName) {
        try {
            DataOutputStream DO = new DataOutputStream(new FileOutputStream(new File(theFileName)));
            for (int a = 0; a < numActions; a++) {
                for (int s = 0; s < numStates; s++) {
                    DO.writeDouble(pi[a][s]);
                }
            }
            DO.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Problem saving value function to file: " + theFileName + " :: " + ex);
        } catch (IOException ex) {
            System.err.println("Problem writing value function to file:: " + ex);
        }

    }

    /**
     * Loads the value function from a file named theFileName.  Must be called after
     * init but before cleanup.
     * @param theFileName
     */
    private void loadValueFunction(String theFileName) {
        try {
            DataInputStream DI = new DataInputStream(new FileInputStream(new File(theFileName)));
            for (int a = 0; a < numActions; a++) {
                for (int s = 0; s < numStates; s++) {
                    pi[a][s] = DI.readDouble();
                }
            }
            DI.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Problem loading value function from file: " + theFileName + " :: " + ex);
        } catch (IOException ex) {
            System.err.println("Problem reading value function from file:: " + ex);
        }
    }
}
