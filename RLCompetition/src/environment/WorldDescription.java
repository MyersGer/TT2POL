package environment;

import java.util.Random;

/**
 * This class holds all of the internal state information about the environment,
 * and manages the dynamics, state update, reward calculation, etc.
 */
class WorldDescription {

    private final int numRows;
    private final int numCols;
    public int agentRow;
    public int agentCol;
    private int[][] theMap;
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

    
    public int getPillCount(){
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
    	 if (theMap[agentRow][agentCol] == PacmanEnvironment.WORLD_OBSTACLE) {
             return -100.0f;
         }
    	 
    	double reward = rewardDistanceToGhost();
    	
        if (theMap[agentRow][agentCol] == PacmanEnvironment.WORLD_PILL) {
            return reward + 10.0f;
        }
        
        if (theMap[agentRow][agentCol] == PacmanEnvironment.WORLD_POWERPILL) {  
        	return reward + 20.0f;
        }
        
        if (theMap[agentRow][agentCol] == PacmanEnvironment.WORLD_FREE) {
        	return reward + 0f;
        }
        
        // Hier sollte 
        return -1f;
    }

    /** Berechnet einen Reward anhand des Abstands zum Geist
     *  
     * @param agentRow Aktuelle Zeilen-Position des Agenten
     * @param agentCol Aktuelle Spalten-Position des Agenten
     * @return Einen hohen Reward, falls der Abstand groß ist, ansonsten einen niedrigen.
     */
    private double rewardDistanceToGhost() { 
    	int ghostX = 0;
    	int ghostY = 0;
    	for(int x = 0; x<numCols; x++) { 
    		for(int y=0; y<numRows; y++) {
    			if(theMap[y][x] == PacmanEnvironment.WORLD_GHOST) { 
    				ghostX = x;
    				ghostY = y;
    			}
    		}
    	}
    	double dX =  agentRow - ghostX;
    	double dY = agentCol - ghostY;
    	
    	return Math.sqrt((dX * dX) + (dY * dY)) * 5;
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
            
            if(theMap[newRow][newCol] == PacmanEnvironment.WORLD_PILL || 
            		theMap[newRow][newCol] == PacmanEnvironment.WORLD_POWERPILL) {
            	
            	theMap[newRow][newCol] = PacmanEnvironment.WORLD_FREE;
            	
            }
            
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
    
    /** Die Map muss nach jeder Episode neu initialisiert werden,
     *  da der Junky die ganzen Pillen aufgefressen hat.
     */
    public void resetWorld() {
    	theMap = new int[][]{
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
    }
}
