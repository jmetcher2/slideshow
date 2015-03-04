package au.id.lagod.slideshow;

import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageProcessingException;

public class ShowImage extends JFrame implements ActionListener {
	
	/**
	 * 
	 */
	final static Logger logger = LoggerFactory.getLogger(ShowImage.class);
	
	private static final long serialVersionUID = 1L;
	private int w;
	private int h;
	private BufferedImage screenImage;
	private Timer timer;
	private List<Path> files = new ArrayList<Path>(0);
	private int nextIndex = 0;
	private List<String> excludedCaptionsList;
	private Integer fontSize;

   // Class constructor  
	ShowImage(Properties props, List<Path> files) throws IOException, ImageProcessingException {
		this.files = files;

		excludedCaptionsList = Arrays.asList(props.getProperty("exclude_captions").split("\\s*,\\s*"));
		fontSize = Integer.decode(props.getProperty("fontSize"));
 
        initListeners();
 
        initWindow();
 
		nextImage();
        repaint();

		int delay = Integer.parseInt(props.getProperty("delay"))*1000;
        timer = new Timer(delay, this);
        timer.start();
    }


	private void initWindow() {
		// remove window frame  
        this.setUndecorated(true);
 
        // window should be visible 
        this.setVisible(true);
 
        // switching to fullscreen mode 
        logger.info("Full screen supported: " + GraphicsEnvironment.getLocalGraphicsEnvironment().
        getDefaultScreenDevice().isFullScreenSupported());
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(this);
 
        // getting display resolution: width and height 
        w = this.getWidth();
        h = this.getHeight();
        logger.info("Display resolution: " + String.valueOf(w) + "x" + String.valueOf(h));
	}


	private void initListeners() {
		// Exiting program on window close 
        addWindowListener(new WindowAdapter() { 
            @Override
			public void windowClosing(WindowEvent e) { 
                System.exit(0);
            } 
        });
        addMouseListener(new ShowImageMouseListener(this));
        addKeyListener(new ShowImageKeyListener());
	} 
 
	
    @Override
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
        	logger.debug("screenImage is null");
        }
    }


	@Override
	public void actionPerformed(ActionEvent arg0) {
		SlideShow.nudgeScreenSaver();
		
		nextImage();
		repaint();
	}


	private void nextImage() {
		try {
			long startTime = System.nanoTime();
			
			String imagePath = files.get(nextIndex++).toString();
			if (nextIndex >= files.size()) 
				nextIndex = 0;
			logger.debug(imagePath);
			
			Image image = new Image(imagePath);
	
			BufferedImage newScreenImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

			image.drawScaledOnto(newScreenImage, this);
			image.drawMetadataOnto(newScreenImage, excludedCaptionsList, fontSize);
			
			
		    screenImage = newScreenImage;
	
		    long endTime = System.nanoTime();
		    long duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.
		    
		    logger.debug("next image took " + duration + " milliseconds");
		}
		// convert checked to unchecked exceptions
		catch (Exception e) {
			throw new Error (e);
		}
	    
	}


	void doRight() {
		nextImage();
		repaint();
		timer.restart();
	}

	void doLeft() {
		nextIndex = nextIndex - 2;  
		if (nextIndex < 0) nextIndex = nextIndex + files.size();
		doRight();
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
			else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_KP_LEFT) {
				doLeft();
			}
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_KP_RIGHT) {
				doRight();
			}
			else if (e.getKeyCode() == KeyEvent.VK_PAUSE) {
				if (timer.isRunning()) {
					timer.stop();
				}
				else {
					timer.start();
				}
			}
			
		}

	}

}
