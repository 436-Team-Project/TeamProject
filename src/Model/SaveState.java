package Model;
import java.io.*;
import java.util.ArrayList;

public class SaveState implements Serializable {
	ArrayList<UIObjects> itemList=new ArrayList<UIObjects>();
	
	public SaveState(ArrayList<UIObjects> items) {
		this.itemList=items;
	}
}
