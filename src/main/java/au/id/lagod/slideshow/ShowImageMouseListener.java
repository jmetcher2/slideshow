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

	@Override
	public void mouseClicked(MouseEvent e) { System.exit(0); }

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}