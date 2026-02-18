package myGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import filep.Ingredient;
import filep.Recipe;
import filep.RecipeHandler;
import filep.FileHandler;

public class RecipeFrame extends JFrame {

	JComboBox<String> recipeSelect;
	JPanel mp;
	FileHandler fh;

	// text tags over input fields
	JLabel nameLabel;
	JLabel costToMakeLabel;
	JLabel sellPointLabel;

	// ingredient display
	JLabel ingredientLabel;
	JList<String> ingredientList;
	DefaultListModel<String> model;

	// text fields for user input.
	JTextField nameField;
	JTextField costToMakeField;
	JTextField sellPointField;
	JTextField searchBar;
	// JTextField nameField;
	
	// Buttons for save, remove and add ingredient.
	JButton saveButton;
	JButton getIngredientsButton;
	JButton removeIngredientButton;
	JButton removeButton;

	MyPanel parent;
	
	Recipe passed;

	public RecipeFrame(FileHandler fh, MyPanel parent, Recipe r) {
		this.parent = parent;
		if(r != null) {
			this.passed = r;
		}else {
			this.passed = null;
		}
		this.setSize(800, 600);
		this.setTitle("Recipe Editor");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);

		this.fh = fh;

		setUpPanel();
		// setups
		setUpField();
		setUpIngredientList();
		comboBoxInit();
		setUpComboBox();
		setUpButtons();
		selectPassed();
		setUpSearchSelect();
		

		// setUpField();
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
	
	public void selectPassed() {
		if (passed != null) {
			selectByName(passed.getName());
	    }
		//implicit functionality from actionlistener updating every time an action occurs.
	}

	public void setUpPanel() {
		mp = new JPanel();
		mp.setLayout(null);

		this.setVisible(true);
	}

	boolean isUpdating = false;

	public void comboBoxInit() {
		recipeSelect = new JComboBox<String>();
		recipeSelect.setBounds(10, 10, 300, 35);
		recipeSelect.setEditable(false);
		recipeSelect.addActionListener(e -> {
			if (!isUpdating) {
				grabRecipeAndFill();
			}
		});

	}

	public void setUpComboBox() {
		// Load options for recipe select.
		isUpdating = true;
		recipeSelect.removeAllItems();
		recipeSelect.addItem("New recipe");

		for (int i = 0; i < RecipeHandler.recipes.size(); i++) {
			recipeSelect.addItem((i+1) +". " +RecipeHandler.recipes.get(i).getName());
		}
		isUpdating = false;
		grabRecipeAndFill();
	}
	
	public void setUpComboBoxSearch(String s) {
		// Load options for recipe select.
		isUpdating = true;
		recipeSelect.removeAllItems();
		recipeSelect.addItem("New recipe");

		for (int i = 0; i < RecipeHandler.recipes.size(); i++) {
			String name = RecipeHandler.recipes.get(i).getName();
			
			if(name.toLowerCase().contains(s.toLowerCase())) {
				recipeSelect.addItem((i+1) +". " +RecipeHandler.recipes.get(i).getName());
			}
		}
		isUpdating = false;
		grabRecipeAndFill();
	}

	public void grabRecipeAndFill() {
		Recipe tempR = grabRecipe();

		isUpdating = true;
		// Recipe r = RecipeHandler.recipes.
		if (tempR == null) {
			nameField.setText("");
			costToMakeField.setText("");
			sellPointField.setText("");

			model.clear();
		} else {
			nameField.setText(tempR.getName());
			costToMakeField.setText(String.valueOf(tempR.getCostToMake()));
			sellPointField.setText(String.valueOf(tempR.getSellPoint()));

			loadIngredientsIntoModel(tempR);
		}

		isUpdating = false;
	}

	public Recipe grabRecipe() {
		String trimmed =  (String) recipeSelect.getSelectedItem();
		String myRecipe;
		if(!trimmed.equals("New recipe")) {
			myRecipe = trimmed.substring(trimmed.indexOf(".") + 1).trim();
		}else {
			myRecipe = null;
		}
	
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

		nameLabel.setBounds(10, 55, 300, 20);
		nameField.setBounds(10, 75, 300, 35);

		costToMakeLabel.setBounds(10, 110, 300, 20);
		costToMakeField.setBounds(10, 130, 300, 35);
		costToMakeField.setEditable(false);

		sellPointLabel.setBounds(10, 165, 300, 20);
		sellPointField.setBounds(10, 185, 300, 35);

		ingredientLabel.setBounds(10, 220, 300, 20);

	}

	public void setUpIngredientList() {
		model = new DefaultListModel<String>();
		ingredientList = new JList<String>(model);

		ingredientList.setBounds(10, 240, 750, 280);
		ingredientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public void loadIngredientsIntoModel(Recipe r) {
		model.clear();

		int i = 0;
		for (Ingredient t : r.getIngredients()) {

			String toAdd = "ID: " + t.getID() + "| NAME: " + t.getName() + "| SUPPLIER: " + t.getSupplierName()
					+ "| GRAMS: " + r.getGramsUsedOfIngredient(t) + "| COST IN RECIPE: €"
					+ (r.getCostOfIngredientInRecipe(t));

			model.addElement(toAdd); // each elements display in the Model is the element's toString();
			// there was certain values which I couldnt get from passing through an
			// Ingredient object and making my own toString();
			// because the ingredient object stores its name, cost, supplier, amount BOUGHT
			// IN, never amount used in each recipe
			// that information belongs to the recipe itself.

			i++;
		}
	}

	public void setUpButtons() {
		saveButton = new JButton("Save Recipes");
		getIngredientsButton = new JButton("Add Ingredient");
		removeIngredientButton = new JButton("Remove Ingredient");
		removeButton = new JButton("Delete Recipe");

		saveButton.setBounds(320, 10, 160, 50);
		getIngredientsButton.setBounds(320, 130, 160, 50);
		removeIngredientButton.setBounds(320, 190, 160, 50);
		removeButton.setBounds(320, 70, 160, 50);

		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				removeRecipeAction();
			}
		});

		getIngredientsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				addIngredientAction();
			}
		});
		removeIngredientButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				removeIngredientAction();
			}
		});
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
			}
		});
	}

	// remove selected recipe
	public void removeRecipeAction() {
		Recipe toRemove = grabRecipe();
		if (toRemove == null) {
			return;
		}

		int result = JOptionPane.showConfirmDialog(this, // parent component
				"Do you want to delete this recipe?", // message
				"Confirmation", // title
				JOptionPane.YES_NO_CANCEL_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			RecipeHandler.recipes.remove(toRemove);
			RecipeHandler.recipeByName.remove(toRemove.getName());
			setUpComboBox();
		} else if (result == JOptionPane.NO_OPTION) {
			return;
		} else {
			return;
		}
	}

	// save all recipes
	public void saveRecipesAction() throws IOException {
		Recipe saveName = grabRecipe();

		String name = nameField.getText();
		String text = sellPointField.getText().trim();

		float sellPoint = 0;
		if (!text.isEmpty()) {
			try {
				sellPoint = Float.valueOf(text);
			} catch (NumberFormatException e) {

			}
		}
		// String ingList[] = ingredientList.

		if (saveName != null) {
			// RECIPE ALREADY EXISTS, JUST UPDATE
			// INGREDIENTS ALREADY ADDED FROM ADDINGREDIENT, NO NEED TO DO ANYTHING,
			// AS SAVING THEM WILL THEN FINALISE THEIR ADDITION TO RECIPE
			saveName.setName(name);
			saveName.setSellPoint(sellPoint);
		} else {// if null then is new recipe, save.
			saveName = new Recipe(name, sellPoint);

			DefaultListModel<String> model = (DefaultListModel<String>) ingredientList.getModel();

			for (int i = 0; i < model.getSize(); i++) {
				String ingEntry = model.getElementAt(i);

				String[] parts = ingEntry.split("\\|"); // because | is a special character.
				String idPart = parts[0].replace("ID:", "").trim();
				int ingId = Integer.parseInt(idPart);

				Ingredient ing = RecipeHandler.ingredientIDMap.get(ingId);

				String gramsPart = parts[3].replace("GRAMS:", "").trim();
				float gramsUsed = Float.parseFloat(gramsPart);

				saveName.addIngredient(ing, gramsUsed);
			}

			if (RecipeHandler.verifyNoRecipeCopy(saveName)) {
				RecipeHandler.addRecipe(saveName);
			}

		}
		fh.writeRecipes();

		setUpComboBox();

		parent.fillDataModel();
		//recipeSelect.setSelectedItem(name);
		selectByName(name);
	}
	
	public void selectByName(String name) {
	    for (int i = 0; i < recipeSelect.getItemCount(); i++) {
	        String item = recipeSelect.getItemAt(i);
	        if (item.equals("New recipe")) continue;
	        String trimmed = item.substring(item.indexOf(".") + 1).trim();
	        if (trimmed.equals(name)) {
	            recipeSelect.setSelectedItem(item);
	            return;
	        }
	    }
	}

	// add ingredient to recipe via popup list of all available ingredients
	public void addIngredientAction() {
		Recipe recipe = grabRecipe();
		if (recipe == null) {
			recipe = new Recipe("Unfinished Recipe", 0);
			RecipeHandler.addRecipe(recipe);
			recipeSelect.addItem("Unfinished Recipe");
			recipeSelect.setSelectedItem("Unfinished Recipe");

		}

		new AddIngredientDialog(this, recipe);

		loadIngredientsIntoModel(recipe);
		grabRecipeAndFill();
	}

	// remove ingredient from recipe based on JList GUI selection.
	public void removeIngredientAction() {
		String ingEntry = ingredientList.getSelectedValue();
		if (ingEntry != null) {
			String[] seperated = ingEntry.split("\\|");
			String idPart = seperated[0].replace("ID:", "").trim();
			Integer ingEntryId = Integer.valueOf(idPart);
			// System.out.println(mr);
			Recipe selectedRecipe = grabRecipe();
			Ingredient selectedIngredient = selectedRecipe.getIngredient(RecipeHandler.ingredientIDMap.get(ingEntryId));

			selectedRecipe.removeIngredient(selectedIngredient);
			loadIngredientsIntoModel(selectedRecipe);
			grabRecipeAndFill();
			// selectedRecipe.getIngredientFromList()
		}
	}
	
	public void setUpSearchSelect() {
    	searchBar = new JTextField();
    	searchBar.setBounds(550,15,200,30);
  
    	searchBar.getDocument().addDocumentListener(new DocumentListener(){
    		
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				String input = (String) searchBar.getText();
	    		if(!input.trim().isEmpty()) {
	    			setUpComboBoxSearch(input);
	    		}else {
	    			setUpComboBox();
	    		}
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				String input = (String) searchBar.getText();
	    		if(!input.trim().isEmpty()) {
	    			setUpComboBoxSearch(input);
	    		}else {
	    			setUpComboBox();
	    		}
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				String input = (String) searchBar.getText();
	    		if(!input.trim().isEmpty()) {
	    			setUpComboBoxSearch(input);
	    		}else {
	    			setUpComboBox();
	    		}
	    	
			}
    	});
    	
    	JLabel searchLabel = new JLabel("Keyword Search");
    	searchLabel.setBounds(550,0,200,115);
    	
    	mp.add(searchBar);
    	mp.add(searchLabel);
    }

}
