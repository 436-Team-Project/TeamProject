package Model;

import java.util.ArrayList;

public class ExtendedTables extends Tables {
	
	ArrayList<Tables> tList = new ArrayList<Tables> ();

	public ExtendedTables(int ID, double x, double y, double x2, double y2) {
		super(ID, x, y, x2, y2);
		// TODO Auto-generated constructor stub
	}
	
	public ExtendedTables(int ID, double x, double y, double x2, double y2, Tables t1, Tables t2) {
		super(ID, x, y, x2, y2);
		// TODO Auto-generated constructor stub
	}
	
	void addTable(Tables t1) {
		
	}

}
