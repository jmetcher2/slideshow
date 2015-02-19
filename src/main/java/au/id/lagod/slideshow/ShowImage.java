package au.id.lagod.slideshow;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

public class ShowImage extends JFrame implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int w;
	private int h;
	private BufferedImage screenImage;
	private Timer timer;
	private List<Path> files = new ArrayList<Path>(0);
	private int currentIndex = 0;
	private List<String> excludeCaptions;

   // Class constructor  
	ShowImage(String propFileName) throws IOException, ImageProcessingException {
		
		Properties props = loadProperties(propFileName);
		System.out.println(props.toString());

		String source = props.getProperty("source");
		String extensions = props.getProperty("extensions");
		String fileGlob = "*.{" + extensions.toLowerCase() + "," + extensions.toUpperCase() + "}";
		int delay = Integer.parseInt(props.getProperty("delay"))*1000;
		Boolean shuffle = Boolean.parseBoolean(props.getProperty("shuffle"));
		excludeCaptions = Arrays.asList(props.getProperty("exclude_captions").split("\\s*,\\s*"));
 
        initListeners();
 
        initWindow();
 
		loadFileNames(Paths.get(source), fileGlob, shuffle);
        
		nextImage();
        repaint();

        timer = new Timer(delay, this);
        timer.start();
        
        
    }


	private void initWindow() {
		// remove window frame  
        this.setUndecorated(true);
 
        // window should be visible 
        this.setVisible(true);
 
        // switching to fullscreen mode 
        System.out.println("Full screen supported: " + GraphicsEnvironment.getLocalGraphicsEnvironment().
        getDefaultScreenDevice().isFullScreenSupported());
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(this);
 
        // getting display resolution: width and height 
        w = this.getWidth();
        h = this.getHeight();
        System.out.println("Display resolution: " + String.valueOf(w) + "x" + String.valueOf(h));
	}


	private void initListeners() {
		// Exiting program on window close 
        addWindowListener(new WindowAdapter() { 
            public void windowClosing(WindowEvent e) { 
                System.exit(0);
            } 
        });
        addMouseListener(new ShowImageMouseListener(this));
        addKeyListener(new ShowImageKeyListener());
	} 
 
	
    public void paint (Graphics g) { 
        if (screenImage != null) { // if screenImage is not null (image loaded and ready)
        	
            g.drawImage(screenImage, // draw it  
                        w/2 - screenImage.getWidth(this) / 2, // at the center  
                        h/2 - screenImage.getHeight(this) / 2, // of screen 
                        this);
            // to draw image at the center of screen 
            // we calculate X position as a half of screen width minus half of image width 
            // Y position as a half of screen height minus half of image height
            
        }
        else {
        	System.out.println("screenImage is null");
        }
    }


	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			// Generate activity to keep the screensaver from triggering
			Robot r = new Robot();
			r.keyPress(KeyEvent.VK_CONTROL);
			r.keyRelease(KeyEvent.VK_CONTROL);
			
			nextImage();
		} catch (ImageProcessingException | IOException | AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();
	}
	
	private void nextImage() throws ImageProcessingException, IOException {
		long startTime = System.nanoTime();
		
		String imagePath = files.get(currentIndex++).toString();
		if (currentIndex >= files.size()) 
			currentIndex = 0;
		System.out.println(imagePath);
		
		File imgFile = new File(imagePath);
		BufferedImage img = ImageIO.read(imgFile);
		

		Metadata metadata = ImageMetadataReader.readMetadata(imgFile);

		//System.out.println(tags);
		//System.out.println(caption);
		//System.out.println(date);
		
	    double heightRatio = ((double)img.getHeight())/h;
		double widthRatio = ((double)img.getWidth())/w;
		double ratio = heightRatio > widthRatio ? heightRatio : widthRatio;
		if (ratio < 1.0) ratio = 1.0;
		double scaledHeight = img.getHeight()/ratio;
		double scaledWidth =  img.getWidth()/ratio;
		double translateX = (w - scaledWidth)/2;
		double translateY = (h - scaledHeight)/2;
		
		System.out.println(heightRatio + " " + widthRatio + " " + ratio + " " + scaledHeight + " " + scaledWidth + " " + translateY + " " + translateX);

		BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = after.getGraphics();
		g.setColor ( new Color ( 0,0,0 ) );
		g.fillRect ( 0, 0, after.getWidth(), after.getHeight() );
		g.drawImage(img, (int) translateX, (int) translateY, (int) scaledWidth, (int) scaledHeight, Color.black, this);
		
		
		//System.out.println("before: " + img.getWidth() + "x" + img.getHeight() + "; after " + after.getWidth() + "x" + after.getHeight() + "; translate: " + translateX + "x" + translateY);
		
		printMetadataToImage(metadata, g);
	    
	    g.dispose();

	    screenImage = after;


	    long endTime = System.nanoTime();
	    long duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.
	    
	    System.out.println("next image took " + duration + " milliseconds");
	    
	}


	private void printMetadataToImage(Metadata metadata, Graphics g)
			throws UnsupportedEncodingException {
		ExifIFD0Directory exifid0 = metadata.getDirectory(ExifIFD0Directory.class);
		
		String tags = "";
		byte[] bytes = exifid0.getByteArray(ExifIFD0Directory.TAG_WIN_KEYWORDS);
		if (bytes != null) {
			tags = new String(bytes, "UTF-16LE");
		}
		List<String> tagList = Arrays.asList(tags.split(";"));
		String caption = exifid0.getString(ExifIFD0Directory.TAG_IMAGE_DESCRIPTION);
		if (caption == null) {
			caption = "";
		}
		caption = caption.trim();
		Date date = exifid0.getDate(ExifIFD0Directory.TAG_DATETIME);

		g.setColor ( Color.WHITE );
	    g.drawString(date.toString(), 10, 15);
	    g.setFont(g.getFont().deriveFont(15f));
	    if (!excludeCaptions.contains(caption))
	    	g.drawString(caption, 10, 40);
	    int lineOffset = 40;
	    for (String tag: tagList) {
	    	lineOffset += 25;
		    g.drawString(tag, 10, lineOffset);
	    }
	}

	private void loadFileNames(Path startingPath, String fileGlob, Boolean shuffle) throws IOException
	  {  
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

	  }
	
	private Properties loadProperties(String fileName) throws IOException {
		Properties prop = new Properties();
		 
		InputStream inputStream = this.getClass().getResourceAsStream(fileName);
		
		prop.load(inputStream);
		
		return prop;
	}

	public final class ShowImageKeyListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				System.exit(0);
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				currentIndex = currentIndex - 2;
				if (currentIndex < 0) currentIndex = files.size() - 1;
				try {
					nextImage();
				} catch (ImageProcessingException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				repaint();
				timer.restart();
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				try {
					nextImage();
				} catch (ImageProcessingException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				repaint();
				timer.restart();
			}
		}
	}

}
