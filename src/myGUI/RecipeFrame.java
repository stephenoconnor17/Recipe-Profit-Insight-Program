package myGUI;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import filep.Ingredient;
import filep.Recipe;
import filep.RecipeHandler;

public class RecipeFrame extends JFrame{
	
	JComboBox<String> recipeSelect;
	JPanel mp;
	
	JLabel nameLabel;
	JLabel costToMakeLabel;
	JLabel sellPointLabel;
	
	JLabel ingredientLabel;
	JList<String> ingredientList;
	DefaultListModel<String> model;
	
	JTextField nameField;
	JTextField costToMakeField;
	JTextField sellPointField;
	//JTextField nameField;
	
	public RecipeFrame() {
		this.setSize(600,600);
		this.setTitle("Recipe Editor");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		
		setUpPanel();
		//setups
		setUpField();
		setUpComboBox();
		setUpIngredientList();
		//setUpField();
		//
		
		
		mp.add(recipeSelect);
		
		mp.add(nameField);
		mp.add(nameLabel);
		
		mp.add(sellPointLabel);
		mp.add(sellPointField);
		
		mp.add(costToMakeField);
		mp.add(costToMakeLabel);
		
		mp.add(ingredientLabel);
		mp.add(ingredientList);
		
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
		
		recipeSelect.addItem("New recipe");
		
		//Load options for recipe select.
		for(int i = 0; i < RecipeHandler.recipes.size(); i++) {
			recipeSelect.addItem(RecipeHandler.recipes.get(i).getName());
		}
		
		recipeSelect.setBounds(10,10,300,35);
		
		recipeSelect.addActionListener(e -> {
			grabRecipeAndFill();
		});
		
		grabRecipeAndFill();
	}
	
	public void grabRecipeAndFill() {
		Recipe tempR = grabRecipe();
		//Recipe r = RecipeHandler.recipes.
		if(tempR == null) {
			nameField.setText("Default");
			costToMakeField.setText("Default");
			sellPointField.setText("Default");
		}else {
			nameField.setText(tempR.getName());
			costToMakeField.setText(String.valueOf(tempR.getCostToMake()));
			sellPointField.setText(String.valueOf(tempR.getSellPoint()));
			
			loadIngredientsIntoModel(tempR);
		}
	}
	
	public Recipe grabRecipe() {
		String myRecipe = (String)recipeSelect.getSelectedItem();
		Recipe tempR = RecipeHandler.recipeByName.get(myRecipe);
		
		return tempR;
	}
	
	public void setUpField() {
		
		nameLabel = new JLabel("Recipe Name");
		costToMakeLabel = new JLabel("Cost To Make");
		sellPointLabel = new JLabel("Selling Point");
		ingredientLabel = new JLabel("Ingredients");
		
		nameField = new JTextField();
		costToMakeField = new JTextField();
		sellPointField = new JTextField();
		
		nameLabel.setBounds(10,55,300,20);
		nameField.setBounds(10,75,300,35);
		
		costToMakeLabel.setBounds(10,110,300,20);
		costToMakeField.setBounds(10,130,300,35);
		
		sellPointLabel.setBounds(10,165,300,20);
		sellPointField.setBounds(10,185,300,35);
		
		ingredientLabel.setBounds(10,220,300,20);
		
		
	}
	
	public void setUpIngredientList() {
		model = new DefaultListModel<String>();
		ingredientList = new JList<String>(model);
		
		ingredientList.setBounds(10,240,550,280);
		
	}
	
	public void loadIngredientsIntoModel(Recipe r) {
		model.clear();
		
		int i = 0;
		for(Ingredient t : r.getIngredients()) {
			
			String toAdd = "Name: " + t.getName() + ", Supplier: " + t.getSupplierName() +", Grams: " + r.getGramsUsedOfIngredient(t) + ", Cost In Recipe: " + (r.getCostOfIngredientInRecipe(t));
			
			
			model.addElement(toAdd);
			
			i++;
		}
	}
	
	public void setUpButtons() {
		
	}
	
}
