package environment;

import org.rlcommunity.rlglue.codec.util.EnvironmentLoader;

import sun.awt.windows.ThemeReader;

import jgame.*;
import jgame.platform.*;

public class Gui extends JGEngine {

	static JGColor gray = new JGColor(133, 133, 133);

	int tileSize = 16;
	WorldDescription world;

	int screenX = 640;
	int screenY = 480;

	public static void main(String[] args) {
		PacmanEnvironment pe = new PacmanEnvironment();
		EnvironmentLoader theLoader = new EnvironmentLoader(pe);
		new Gui(pe);
		theLoader.run();
	}

	public Gui() {
		// This inits the engine as an applet.
		initEngineApplet();
	}

	public Gui(PacmanEnvironment pe) {
		this.world = pe.getTheWorld();
		// This inits the engine as an application.
		initEngine(screenX, screenY);
	}

	public void initCanvas() {

		setCanvasSettings(world.getMapSizeX(), world.getMapSizeY(), tileSize,
				tileSize,

				null, null, null);
	}

	public void initGame() {

		setFrameRate(35,// 35 = frame rate, 35 frames per second
				2 // 2 = frame skip, skip at most 2 frames before displaying
					// a frame again
		);

		for (int i = 0; i < world.getMapSizeY(); i++) {
			for (int j = 0; j < world.getMapSizeX(); j++) {
				new TileView(j, i, world);
			}
		}

	}

	/** A timer used to animate the "hello world" text. */
	double texttimer = 0;

	/**
	 * Game logic is done here. No painting can be done here, define paintFrame
	 * to do that.
	 */
	public void doFrame() {
		// Increment the angle of the moving text.
		texttimer += 0.05;
	}

	/**
	 * Any graphics drawing beside objects or tiles should be done here.
	 * Usually, only status / HUD information needs to be drawn here.
	 */
	public void paintFrame() {

	}

	class TileView extends JGObject {

		IWorld world;
		int tx;
		int ty;

		/** Constructor. */
		TileView(int x, int y, IWorld world) {
			// Initialise game object by calling an appropriate constructor
			// in the JGObject class.

			super("tileView",// name by which the object is known
					true,// true means add a unique ID number after the object
							// name.
							// If we don't do this, this object will replace any
							// object
							// with the same name.
					x * tileSize, // X position
					y * tileSize, // Y position
					1, // the object's collision ID (used to determine which
						// classes
						// of objects should collide with each other)
					null // name of sprite or animation to use (null is none)
			);

			tx = x;
			ty = y;

			this.world = world;

			// Give the object an initial speed in a random direction.
			xspeed = 0;
			yspeed = 0;
		}

		/** Draw the object. */
		public void paint() {
			// Draw a yellow ball
			setColor(JGColor.yellow);
			// drawOval(x,y,16,16,true,true);
			// drawCarW();

			switch (world.getWorldTile(tx, ty)) {

			case PacmanEnvironment.WORLD_FREE:
				drawFeld();
				break;
			case PacmanEnvironment.WORLD_OBSTACLE:
				drawWand();
				break;
			case PacmanEnvironment.WORLD_PILL:
				drawFeldMitPill();
				break;
			case PacmanEnvironment.WORLD_POWERPILL:
				drawFeldMitPowerPill();
				break;
			case PacmanEnvironment.WORLD_GHOST:
				break;

			default:
				break;
			}

		}

		private void drawWand() {
			setColor(gray);
			drawRect(x, y, tileSize, tileSize, true, false);
		}

		private void drawFeld() {
			setColor(JGColor.black);
			drawRect(x, y, tileSize, tileSize, true, false);
			if (world.isMitPacMan(tx, ty)) {
				drawPacMan();
			}
		}

		private void drawFeldMitPill() {
			setColor(JGColor.black);
			drawRect(x, y, tileSize, tileSize, true, false);
			setColor(JGColor.red);
			drawOval(x + tileSize / 2, y + tileSize / 2, tileSize / 4,
					tileSize / 4, true, true);
		}

		private void drawFeldMitPowerPill() {
			setColor(JGColor.black);
			drawRect(x, y, tileSize, tileSize, true, false);
			setColor(JGColor.orange);
			drawOval(x + tileSize / 2, y + tileSize / 2, tileSize / 3,
					tileSize / 3, true, true);
		}

		private void drawPacMan() {

			setColor(JGColor.yellow);
			drawOval(x + tileSize / 2, y + tileSize / 2, tileSize / 1.5,
					tileSize / 1.5, true, true);
		}

	}
}
