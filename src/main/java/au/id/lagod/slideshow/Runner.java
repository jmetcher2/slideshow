package au.id.lagod.slideshow;


import java.io.IOException;

import com.drew.imaging.ImageProcessingException;

public class Runner {

	public static void main(String[] args) throws IOException, ImageProcessingException {
        new SlideShow("/config.properties");
	}
	

}
