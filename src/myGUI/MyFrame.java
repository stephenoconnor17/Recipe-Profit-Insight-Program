package myGUI;

import javax.swing.*;

public class MyFrame extends JFrame{
	
	MyPanel mp;
	
	public MyFrame(){
		this.setSize(800,600);
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		this.setTitle("Recipe Book");
		this.setResizable(false);
		
		mp = new MyPanel(this);
		
		this.add(mp);
		this.setVisible(true);
		
	}
}
