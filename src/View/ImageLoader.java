package View;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Deals with everything involving images and file paths
 *
 */
public class ImageLoader {
	
	/**
	 * Gets an image from the "images" folder and return an Image object with the given filename
	 *
	 * @param fileName file path to image
	 * @return Image object
	 */
	public static Image getImage(String fileName) {
		Image result;
		FileInputStream inputStream = null;
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

