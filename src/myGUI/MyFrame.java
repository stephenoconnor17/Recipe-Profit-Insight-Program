package myGUI;

import javax.swing.*;

/** Main application window. Hosts MyPanel which contains the recipe table and controls. */
public class MyFrame extends JFrame {

	MyPanel mp;

	public MyFrame() {
		this.setSize(1000, 550);
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		this.setTitle("Recipe Book");
		util.MinSizeEnforcer.apply(this, 1000, 550);

		mp = new MyPanel(this);

		this.add(mp);
		this.setVisible(true);

	}
}
