package myGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import filep.Ingredient;
import filep.Recipe;
import filep.RecipeHandler;
import filep.FileHandler;

public class RecipeFrame extends JFrame{
	
	JComboBox<String> recipeSelect;
	JPanel mp;
	FileHandler fh;
	
	//text tags over input fields
	JLabel nameLabel;
	JLabel costToMakeLabel;
	JLabel sellPointLabel;
	
	//ingredient display
	JLabel ingredientLabel;
	JList<String> ingredientList;
	DefaultListModel<String> model;
	
	//text fields for user input.
	JTextField nameField;
	JTextField costToMakeField;
	JTextField sellPointField;
	//JTextField nameField;
	
	//Buttons for save, remove and add ingredient.
	JButton saveButton;
	JButton getIngredientsButton;
	JButton removeIngredientButton;
	JButton removeButton;
	
	public RecipeFrame(FileHandler fh) {
		this.setSize(600,600);
		this.setTitle("Recipe Editor");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		
		this.fh = fh;
		
		setUpPanel();
		//setups
		setUpField();
		setUpIngredientList();
		setUpComboBox();
		setUpButtons();
	
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
		
		mp.add(saveButton);
		mp.add(removeButton);
		mp.add(removeIngredientButton);
		mp.add(getIngredientsButton);
		
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
			
			model.clear();
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
		costToMakeField.setEditable(false);
		
		sellPointLabel.setBounds(10,165,300,20);
		sellPointField.setBounds(10,185,300,35);
		
		ingredientLabel.setBounds(10,220,300,20);
		
		
	}
	
	public void setUpIngredientList() {
		model = new DefaultListModel<String>();
		ingredientList = new JList<String>(model);
		
		ingredientList.setBounds(10,240,550,280);
		ingredientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public void loadIngredientsIntoModel(Recipe r) {
		model.clear();
		
		int i = 0;
		for(Ingredient t : r.getIngredients()) {
			
			String toAdd = "ID: " + t.getID() + ", Name: " + t.getName() + ", Supplier: " + t.getSupplierName() +", Grams: " + r.getGramsUsedOfIngredient(t) + ", Cost In Recipe: " + (r.getCostOfIngredientInRecipe(t));
			
			
			model.addElement(toAdd); //each elements display in the Model is the element's toString();
			//there was certain values which I couldnt get from passing through an Ingredient object and making my own toString();
			//because the ingredient object stores its name, cost, supplier, amount BOUGHT IN, never amount used in each recipe
			//that information belongs to the recipe itself.
			
			i++;
		}
	}
	
	public void setUpButtons() {
		saveButton = new JButton("Save Recipes");
		getIngredientsButton = new JButton("Add Ingredient");
		removeIngredientButton = new JButton("Remove Ingredient");
		removeButton = new JButton("Delete Recipe");
		
		saveButton.setBounds(320,10,160,50);
		getIngredientsButton.setBounds(320,130,160,50);
		removeIngredientButton.setBounds(320,190,160,50);
		removeButton.setBounds(320,70,160,50);
		
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				removeRecipeAction();
		}});
		
		getIngredientsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				addIngredientAction();
		}});	
		removeIngredientButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				removeIngredientAction();
		}});	
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					saveRecipesAction();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}});	
	}
	
	//remove selected recipe
	public void removeRecipeAction() {
		
	}
	
	//save all recipes
	public void saveRecipesAction() throws IOException {
		fh.writeRecipes();
	}
	
	//add ingredient to recipe via popup list of all available ingredients
	public void addIngredientAction() {
		
	}
	
	//remove ingredient from recipe based on JList GUI selection.
	public void removeIngredientAction() {
		String ingEntry = ingredientList.getSelectedValue();
		if(ingEntry != null) {
		String[] seperated = ingEntry.split(",");
		String idPart = seperated[0].replace("ID:","").trim();
		Integer ingEntryId = Integer.valueOf(idPart);
		//System.out.println(mr);
		Recipe selectedRecipe = grabRecipe();
		Ingredient selectedIngredient = selectedRecipe.getIngredient(RecipeHandler.ingredientIDMap.get(ingEntryId));
		
		selectedRecipe.removeIngredient(selectedIngredient);
		loadIngredientsIntoModel(selectedRecipe);
		grabRecipeAndFill();
		//selectedRecipe.getIngredientFromList()
		}
	}
	
}
