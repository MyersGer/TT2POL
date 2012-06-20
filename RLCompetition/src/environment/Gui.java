package environment;

import org.rlcommunity.rlglue.codec.util.EnvironmentLoader;

import jgame.*;
import jgame.platform.*;

public class Gui extends JGEngine {
	
	static JGColor gray = new JGColor(133, 133, 133);
	
//	int sizeX = 10;
//	int sizeY = 10;
	int tileSize = 16;
	WorldDescription world;
	
	int screenX = 640;
	int screenY = 480;

	public static void main(String [] args) {
		// We start the engine with a fixed window size (which happens to
		// be twice the size of the defined playfield, scaling the playfield
		// by a factor 2).  Normally, you'd want this size to be configurable,
		// for example by means of command line parameters.
		PacmanEnvironment pe = new PacmanEnvironment();
        EnvironmentLoader theLoader = new EnvironmentLoader(pe);
        new Gui(pe);
        theLoader.run();
	}

	/** The parameterless constructor is called by the browser, in case we're
	 * an applet. */
	public Gui() {
		// This inits the engine as an applet.
		initEngineApplet(); 
	}

	/** We use a separate constructor for starting as an application. */
	public Gui(PacmanEnvironment pe) {
		this.world = pe.getTheWorld();
		// This inits the engine as an application.
		initEngine(screenX, screenY); 
	}

	/** This method is called by the engine when it is ready to intialise the
	 * canvas (for an applet, this is after the browser has called init()).
	 * Note that applet parameters become available here and not
	 * earlier (don't try to access them from within the parameterless
	 * constructor!).  Use isApplet() to check if we started as an applet.
	 */
	public void initCanvas() {
		// The only thing we need to do in this method is to tell the engine
		// what canvas settings to use.  We should not yet call any of the
		// other game engine methods here!
//		initTSpace();
		
	//	initMock();
		
		
		setCanvasSettings(
			world.getMapSizeX(),  // width of the canvas in tiles
			world.getMapSizeY(),  // height of the canvas in tiles
			tileSize,  // width of one tile
			tileSize,  // height of one tile
			     //    (note: total size = 20*16=320  x  15*16=240)
			null,// foreground colour -> use default colour white
			null,// background colour -> use default colour black
			null // standard font -> use default font
		);
	}
	
	

	


	/** This method is called when the engine has finished initialising and is
	 * ready to produce frames.  Note that the engine logic runs in its own
	 * thread, separate from the AWT event thread, which is used for painting
	 * only.  This method is the first to be called from within its thread.
	 * During this method, the game window shows the intro screen. */
	public void initGame() {
		// We can set the frame rate, load graphics, etc, at this point. 
		// (note that we can also do any of these later on if we wish)
		setFrameRate(
			35,// 35 = frame rate, 35 frames per second
			2  //  2 = frame skip, skip at most 2 frames before displaying
			   //      a frame again
		);
		
		
		for (int i=0; i<world.getMapSizeY(); i++){
			for (int j=0; j<world.getMapSizeX(); j++){
				new TileView(j,i);
			}
		}
			
	}

	/** A timer used to animate the "hello world" text. */
	double texttimer=0;

	/** Game logic is done here.  No painting can be done here, define
	* paintFrame to do that. */
	public void doFrame() {
		// Increment the angle of the moving text.
		texttimer += 0.05;
	}

	/** Any graphics drawing beside objects or tiles should be done here.
	 * Usually, only status / HUD information needs to be drawn here. */
	public void paintFrame() {
		
		
		
		
		
//		setColor(JGColor.yellow);
//		// Draw a text that moves around in a circle.
//		// Note: viewWidth returns the width of the view;
//		//       viewHeight the height.
//		drawString("Hello world",
//			viewWidth()/2  + 50*Math.sin(texttimer), // xpos
//			viewHeight()/2 + 50*Math.cos(texttimer), // ypos
//			0 // the text alignment
//			  // (-1 is left-aligned, 0 is centered, 1 is right-aligned)
//		);
	}
	
	
	
	
	class TileView extends JGObject {
		

		/** Constructor. */
		TileView (int x, int y) {
			// Initialise game object by calling an appropriate constructor
			// in the JGObject class.
			super(
				"tileView",// name by which the object is known
				true,//true means add a unique ID number after the object name.
				     //If we don't do this, this object will replace any object
				     //with the same name.
				x*tileSize,  // X position
				y*tileSize, // Y position
				1, // the object's collision ID (used to determine which classes
				   // of objects should collide with each other)
				null // name of sprite or animation to use (null is none)
			);
			// Give the object an initial speed in a random direction.
			xspeed = 0;
			yspeed = 0;
		}

		/** Update the object. This method is called by moveObjects. */
//		public void move() {
//			// A very "classic" behaviour:
//			// bounce off the borders of the screen.
//			if (x >  pfWidth()-8 && xspeed>0) xspeed = -xspeed;
//			if (x <            8 && xspeed<0) xspeed = -xspeed;
//			if (y > pfHeight()-8 && yspeed>0) yspeed = -yspeed;
//			if (y <            8 && yspeed<0) yspeed = -yspeed;
//			// xspeed and yspeed are added to x and y automatically at the end
//			// of the move() method.
//		}

		/** Draw the object. */
		public void paint() {
			// Draw a yellow ball
			setColor(JGColor.yellow);
//			drawOval(x,y,16,16,true,true);
//			drawCarW();
		}
		
		
		private void drawWand(){
			setColor(gray);
			drawRect(x, y, tileSize, tileSize, true, false);
		}
		
		
		private void drawFeld(){
			setColor(JGColor.black);
			drawRect(x, y, tileSize, tileSize, true, false);
		}
		
		private void drawFeldMitCoin(){
			setColor(JGColor.black);
			drawRect(x, y, tileSize, tileSize, true, false);
		}
		
		private void drawPacMan(){
			setColor(JGColor.black);
			drawRect(x, y, tileSize, tileSize, true, false);
		}
		
		
		
//		private void drawGebaeude(){
//			setColor(JGColor.black);
//			drawRect(x, y, tileSize, tileSize, true, false);
//		}
//		
//		private void drawStrasseS(){
//			drawGebaeude();
//			setColor(gray);
//			drawRect(x+12, y, 40, tileSize, true, false);
//		}
//		
//		private void drawStrasseW(){
//			drawGebaeude();
//			setColor(gray);
//			drawRect(x, y+12, tileSize, 40, true, false);
//		}
//		
//		private void drawKreuzung(){
//			drawGebaeude();
//			setColor(gray);
//			drawRect(x+12, y, 40, tileSize, true, false);
//			drawRect(x, y+12, tileSize, 40, true, false);
//		}
//		
//		private void drawCarS(){
//			setColor(JGColor.cyan);
//			drawRect(x+20, y+15, 24, 34, true, false);
//		}
//		
//		private void drawCarW(){
//			setColor(JGColor.cyan);
//			drawRect(x+15, y+20, 34, 24, true, false);
//		}
		

	} /* end class MyObject */
	
//	private boolean isGebaeude(Roxel r){
//		return true;
//	}
//	
//	private boolean isSenkrecht(Roxel r){
//		return true;
//	}
//	
//	private boolean isWaagerecht(Roxel r){
//		return true;
//	}
//	
//	private boolean isKreuzung(Roxel r){
//		return true;
//	}
}
