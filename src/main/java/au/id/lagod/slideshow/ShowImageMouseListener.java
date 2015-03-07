package au.id.lagod.slideshow;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ShowImageMouseListener implements MouseListener {
	/**
	 * 
	 */
	final static Logger logger = LoggerFactory.getLogger(ShowImageMouseListener.class);

	private final ShowImage showImage;

	/**
	 * @param showImage
	 */
	ShowImageMouseListener(ShowImage showImage) {
		this.showImage = showImage;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		logger.debug("Mouse button clicked: " + e.getButton());
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (isLeftRegion(e)) {
				showImage.doLeft();
			}
			else if (isRightRegion(e)) {
				showImage.doRight();
			}
		}
		else if (SwingUtilities.isRightMouseButton(e)) {
			
		}
		else if (SwingUtilities.isMiddleMouseButton(e)) {
			System.exit(0);
		}
	}

	private boolean isRightRegion(MouseEvent e) {
		return e.getXOnScreen() <= (showImage.getWidth() /2);
	}

	private boolean isLeftRegion(MouseEvent e) {
		return e.getXOnScreen() > (showImage.getWidth() /2);
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}