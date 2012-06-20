package environment;

interface IWorld {

	public abstract boolean isMitPacMan(int x, int y);
	
	public abstract int getWorldTile(int x, int y);

}