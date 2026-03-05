package util;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

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

	public static void apply(JFrame frame, int width, int height) {
		Dimension min = new Dimension(width, height);
		frame.setMinimumSize(min);
		frame.addComponentListener(new MinSizeEnforcer(min));
	}
}
