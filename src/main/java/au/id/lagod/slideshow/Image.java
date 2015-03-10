package au.id.lagod.slideshow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

public class Image {

	final static Logger logger = LoggerFactory.getLogger(Image.class);

	private BufferedImage img;
	private Metadata metadata;

	private List<String> tagList;

	private String caption;

	private Date date;

	public BufferedImage getImg() {
		return img;
	}

	public Image(String imagePath) {
		try {
			File imgFile = new File(imagePath);
			img = ImageIO.read(imgFile);
			metadata = ImageMetadataReader.readMetadata(imgFile);
			
			extractMetaData();
			
		} catch (IOException | ImageProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void extractMetaData() {
		ExifIFD0Directory exifid0 = metadata.getDirectory(ExifIFD0Directory.class);
		
		String tags = "";
		byte[] bytes = exifid0.getByteArray(ExifIFD0Directory.TAG_WIN_KEYWORDS);
		if (bytes != null) {
			try {
				tags = new String(bytes, "UTF-16LE");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		tagList = Arrays.asList(tags.split(";"));

		caption = exifid0.getString(ExifIFD0Directory.TAG_IMAGE_DESCRIPTION);
		if (caption == null) {
			caption = "";
		}
		caption = caption.trim();
		date = exifid0.getDate(ExifIFD0Directory.TAG_DATETIME);
	}

	public void drawScaledOnto(BufferedImage dest, ShowImage showImage) {
		Graphics g = dest.getGraphics();

		g.setColor ( new Color ( 0,0,0 ) );
		g.fillRect ( 0, 0, dest.getWidth(), dest.getHeight() );

		int w = dest.getWidth();
		int h = dest.getHeight();
		
	    double heightRatio = ((double)img.getHeight())/h;
		double widthRatio = ((double)img.getWidth())/w;
		double ratio = heightRatio > widthRatio ? heightRatio : widthRatio;
		if (ratio < 1.0) ratio = 1.0;
		double scaledHeight = img.getHeight()/ratio;
		double scaledWidth =  img.getWidth()/ratio;
		double translateX = (w - scaledWidth)/2;
		double translateY = (h - scaledHeight)/2;
		
		logger.debug(heightRatio + " " + widthRatio + " " + ratio + " " + scaledHeight + " " + scaledWidth + " " + translateY + " " + translateX);

		g.drawImage(img, (int) translateX, (int) translateY, (int) scaledWidth, (int) scaledHeight, Color.black, showImage);
	}
	
	public void drawMetadataOnto(BufferedImage dest, List<String> excludedCaptionsList, TagFilter tagFilter, Integer fontSize)
			throws UnsupportedEncodingException {
		Graphics g = dest.getGraphics();

		SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getInstance();
		dateFormat.applyPattern("yyyy, MMMMM dd (h:mm a)");
		String dateString = dateFormat.format(date);

		logger.debug(caption);
		logger.debug(dateString);

		Integer baseLine = fontSize;
		Integer lineSpacing = (int) (fontSize*1.5);
		
		g.setColor ( Color.WHITE );
	    g.setFont(g.getFont().deriveFont((float) fontSize));
	    
	    g.drawString(dateString, 10, baseLine);
	    
	    if (!excludedCaptionsList.contains(caption)) {
		    baseLine += lineSpacing;
	    	g.drawString(caption, 10, baseLine);
	    }
	    for (String tag: tagList) {
	    	if (tagFilter.showTag(tag)) {
	    		baseLine += lineSpacing;
	    		g.drawString(tag, 10, baseLine);
	    	}
	    }
	}

	

}
