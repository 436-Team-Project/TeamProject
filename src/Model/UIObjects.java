package Model;

/*
 * main class for all objects
 * param: the ID of the object to keep track of and the initial coordinates of the shapes
 */
public abstract class UIObjects {
	//initial starting points of the object we can assume 0,0 or wherever we want them to exist
	int x, x2;
	int y, y2;
	int ID;
	
	public UIObjects(int ID, int x, int y, int x2,int y2) {
		this.x=x;
		this.y=y;
		this.x2=x2;
		this.y2=y2;
		this.ID=ID;
	}
	/*
	 * when the shape is moved or altered the x and y values will be altered for storage
	 */
	public void update(int x,int y,int x2,int y2) {
		this.x=x;
		this.y=y;
		this.x2=x2;
		this.y2=y2;
	}
	
	//abstract classes that will need to be used. Things can be added or removed based on needs
	public abstract void draw();
}
