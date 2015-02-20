package au.id.lagod.slideshow;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.drew.imaging.ImageProcessingException;

public class SlideShow {
	
	public static void nudgeScreenSaver() {
		// Generate activity to keep the screensaver from triggering
		Robot r;
		try {
			r = new Robot();
			r.keyPress(KeyEvent.VK_CONTROL);
			r.keyRelease(KeyEvent.VK_CONTROL);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	public SlideShow(String propFileName) {
		Properties props;
		try {
			props = loadProperties(propFileName);
			System.out.println(props.toString());

			List<Path> files = loadFilenames(props);
			
			new ShowImage(props, files);
		} catch (IOException | ImageProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Properties loadProperties(String fileName) throws IOException {
		Properties prop = new Properties();
		 
		InputStream inputStream = this.getClass().getResourceAsStream(fileName);
		
		prop.load(inputStream);
		
		return prop;
	}

	private List<Path> loadFilenames(Properties props) throws IOException {
		List<Path> files = new ArrayList<Path>(0);
		
		String source = props.getProperty("source");
		Path startingPath = Paths.get(source);
		String extensions = props.getProperty("extensions");
		String fileGlob = "*.{" + extensions.toLowerCase() + "," + extensions.toUpperCase() + "}";
		Boolean shuffle = Boolean.parseBoolean(props.getProperty("shuffle"));

		ImageFileLister lister = new ImageFileLister(files, fileGlob);
		Files.walkFileTree(startingPath, lister);
		files.addAll(lister.getFiles());
		
        if (files.size() == 0) {
        	System.out.println("No files found in " + startingPath + " with pattern " + fileGlob);
        	System.exit(0);
        }
        
        if (shuffle) {
        	Collections.shuffle(files);
        }
        
        return files;
	}
	

}
