package util;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

/**
 * ComponentAdapter that prevents a JFrame from being resized below a minimum
 * dimension. Use the static {@link #apply} method for convenience.
 */
public class MinSizeEnforcer extends ComponentAdapter {

	private final Dimension minSize;

	public MinSizeEnforcer(Dimension minSize) {
		this.minSize = minSize;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		JFrame frame = (JFrame) e.getComponent();
		int w = Math.max(frame.getWidth(), minSize.width);
		int h = Math.max(frame.getHeight(), minSize.height);
		if (w != frame.getWidth() || h != frame.getHeight()) {
			frame.setSize(w, h);
		}
	}

	/**
	 * Sets the minimum size on the frame and attaches a resize listener to enforce
	 * it.
	 */
	public static void apply(JFrame frame, int width, int height) {
		Dimension min = new Dimension(width, height);
		frame.setMinimumSize(min);
		frame.addComponentListener(new MinSizeEnforcer(min));
	}
}
