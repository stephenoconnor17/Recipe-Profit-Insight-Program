package myGUI;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import filep.Ingredient;
import filep.Recipe;
import filep.RecipeHandler;

public class AddIngredientDialog extends JDialog{
	
	Recipe recipe;
	
	JList<Ingredient> ingredientList;
	DefaultListModel<Ingredient> model;
	JScrollPane jsp;
	
	JTextField gramsField;
	JButton addButton;
	JButton cancelButton;
	
	public AddIngredientDialog(RecipeFrame parent, Recipe recipe) {
		super(parent, "Add Ingredient", true);
		this.recipe = recipe;
		
		this.setSize(900,400);
		this.setLocationRelativeTo(parent);
		this.setLayout(null);
		
		setUpList();
		setUpFields();
		setUpButtons();
		
		this.add(jsp);
		this.setVisible(true);
	}
	
	public void setUpList() {
		model = new DefaultListModel<Ingredient>();
		
		for(Ingredient i: RecipeHandler.ingredients) {
			model.addElement(i);
		}
		
		ingredientList = new JList<>(model);
		ingredientList.setBounds(10,10,860,200);
		ingredientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		jsp = new JScrollPane(ingredientList);
		jsp.setBounds(10,10,860,200);
	}
	
	public void setUpFields() {
		JLabel gramsLabel = new JLabel("Grams used: ");
		gramsLabel.setBounds(10,220,100,25);
		this.add(gramsLabel);
		
		gramsField = new JTextField();
		gramsField.setBounds(120,220,100,25);
		this.add(gramsField);
	}
	
	public void addIngredient() {
		Ingredient selected = ingredientList.getSelectedValue();
		if(selected == null) {
			return;
		}
		
		try {
			float grams = Float.parseFloat(gramsField.getText());
			if(grams <= 0) {
				throw new NumberFormatException();
			}
			recipe.addIngredient(selected, grams);
			dispose();
		}catch(NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Enter a valid number");
		}
	}
	
	public void setUpButtons() {
		addButton = new JButton("Add");
		cancelButton = new JButton("Cancel");
		
		addButton.setBounds(70,270,100,40);
		cancelButton.setBounds(200,270,100,40);
		
		addButton.addActionListener(e -> addIngredient());
		cancelButton.addActionListener(e -> dispose());
		
		this.add(addButton);
		this.add(cancelButton);
	}
}
