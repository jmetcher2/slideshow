package au.id.lagod.slideshow;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

final class ShowImageMouseListener implements MouseListener {
	/**
	 * 
	 */
	private final ShowImage mouseListener;

	/**
	 * @param showImage
	 */
	ShowImageMouseListener(ShowImage showImage) {
		mouseListener = showImage;
	}

	public void mouseClicked(MouseEvent e) { System.exit(0); }

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}
}