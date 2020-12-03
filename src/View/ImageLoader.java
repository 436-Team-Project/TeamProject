package View;

import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Deals with everything involving images and file paths
 *
 */
public class ImageLoader {
	final static String floorPlanDir = "Saved/";
	
	/**
	 * Gets an image from the "images" folder and return an Image object with the given filename
	 *
	 * @param fileName file path to image
	 * @return Image object
	 */
	public static Image getImage(String fileName) {
		Image result;
		FileInputStream inputStream;
		try {
			
			inputStream = new FileInputStream("src/images/"+ fileName);
			result = new Image(inputStream);
			return result;
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}