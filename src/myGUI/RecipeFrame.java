package myGUI;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RecipeFrame extends JFrame{
	
	JComboBox<String> recipeSelect;
	JPanel mp;
	
	JTextField nameField;
	JTextField costToMakeField;
	JTextField nameField;
	JTextField nameField;
	
	public RecipeFrame() {
		this.setSize(600,400);
		this.setTitle("Recipe Editor");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		
		setUpPanel();
		//setups
		setUpComboBox();
		//
		
		mp.add(recipeSelect);
		
		this.add(mp);
		this.setVisible(true);
	}
	public void setUpPanel() {
		mp = new JPanel();
		mp.setLayout(null);
		
		
		
		this.setVisible(true);
	}
	
	public void setUpComboBox() {
		recipeSelect = new JComboBox<String>();
		recipeSelect.addItem("Help");
		recipeSelect.addItem("ME");
		
		recipeSelect.setBounds(10,10,300,35);
	}
	
}
