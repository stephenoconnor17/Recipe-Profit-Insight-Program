package myGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import filep.Ingredient;
import filep.Recipe;
import filep.RecipeHandler;
import filep.RecipeSortType;
import filep.VATHandler;
import filep.FileHandler;

public class RecipeFrame extends JFrame {

	JComboBox<String> recipeSelect;
	JComboBox<RecipeSortType> sortSelect;
	JPanel mp;
	FileHandler fh;

	// text tags over input fields
	JLabel nameLabel;
	JLabel costToMakeLabel;
	JLabel sellPointLabel;

	// ingredient display
	JLabel ingredientLabel;
	JTable ingredientTable;
	DefaultTableModel ingredientModel;

	// text fields for user input.
	JTextField nameField;
	JTextField costToMakeField;
	JTextField sellPointField;
	JTextField searchBar;
	JTextField electricityField;
	JTextField manpowerField;
	JTextField packagingField;

	//FIELDs WHERE FINAL COST IS SUGGEST BY TAKING IN
	//DESIRED MARKUP PERCENTAGE, APPLYING TO COST OF RECIPE FROM INGREDIENTS, MANPOWER, PACKAGING AND ELECTRICITY
	//THEN APPLYING VAT ASWELL.

	JTextField suggestedCost;
	JTextField markupField;
	JButton vat1;
	JButton vat2;
	JButton vat3;
	JButton vat4;
	// JTextField nameField;

	// Buttons for save, remove and add ingredient.
	JButton saveButton;
	JButton getIngredientsButton;
	JButton removeIngredientButton;
	JButton removeButton;

	MyPanel parent;

	Recipe passed;

	// --- NEW: layout panels (layout-only change)
	private JPanel topPanel;
	private JPanel centerPanel;
	private JPanel rightPanel;

	// ingredient list scroll (layout-only improvement)
	private JScrollPane ingredientScroll;

	public RecipeFrame(FileHandler fh, MyPanel parent, Recipe r) {
		this.parent = parent;
		if (r != null) {
			this.passed = r;
		} else {
			this.passed = null;
		}
		this.setSize(1000, 650);
		this.setTitle("Recipe Editor");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);

		this.fh = fh;

		setUpPanel();          // creates mp first, everything else needs it
		setUpField();
		setUpIngredientList();
		setUpUtilFields();     // must be before selectPassed since grabRecipeAndFill uses these fields
		setUpVatAndMarkup();
		comboBoxInit();
		setUpComboBox();
		setUpButtons();
		setUpSearchSelect();
		setUpSortSelect();
		selectPassed();        // last, since it triggers grabRecipeAndFill which needs all fields ready
		

		// Top bar
		addToTop(recipeSelect, 0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(8, 8, 4, 8));
		addToTop(saveButton, 2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(8, 4, 4, 4));
		addToTop(removeButton, 3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(8, 4, 4, 8));

		// Search + sort row in top bar
		// (label added inside setUpSearchSelect / setUpSortSelect into topPanel)
		// keep them right-aligned
		// NOTE: searchBar / sortSelect are already added by their setup methods.

		// Center content (fields + list + ingredient buttons)
		addToCenter(nameLabel, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST, new Insets(8, 8, 2, 8));
		addToCenter(nameField, 0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 8, 8, 8));

		addToCenter(costToMakeLabel, 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST, new Insets(0, 8, 2, 8));
		addToCenter(costToMakeField, 0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 8, 8, 8));

		addToCenter(sellPointLabel, 0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST, new Insets(0, 8, 2, 8));
		addToCenter(sellPointField, 0, 5, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 8, 8, 8));

		addToCenter(ingredientLabel, 0, 6, 2, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST, new Insets(0, 8, 2, 8));
		addToCenter(ingredientScroll, 0, 7, 2, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 8, 8, 8));

		// Ingredient buttons to the right of the fields/list
		
		JPanel ingBtns = new JPanel(new GridBagLayout());
		GridBagConstraints b = new GridBagConstraints();
		b.gridx = 0; b.gridy = 0;
		b.fill = GridBagConstraints.HORIZONTAL;
		b.insets = new Insets(0, 0, 8, 0);
		ingBtns.add(getIngredientsButton, b);

		b.gridy = 1;
		b.insets = new Insets(0, 0, 0, 0);
		ingBtns.add(removeIngredientButton, b);

		// put the button panel beside the list, aligned to top of the list row
		addToCenter(ingBtns, 2, 7, 1, 1,
		        0.0, 1.0,
		        GridBagConstraints.VERTICAL, GridBagConstraints.NORTH,
		        new Insets(0, 8, 8, 8));
		
		this.add(mp);
		this.setVisible(true);
	}

	public void selectPassed() {
		if (passed != null) {
			selectByName(passed.getName());
		}
		// implicit functionality from actionlistener updating every time an action
		// occurs.
	}

	public void setUpPanel() {
		mp = new JPanel();
		// mp.setLayout(null);
		// --- NEW: normal layout manager
		mp.setLayout(new BorderLayout());

		topPanel = new JPanel(new GridBagLayout());
		centerPanel = new JPanel(new GridBagLayout());
		rightPanel = new JPanel(new GridBagLayout());

		mp.add(topPanel, BorderLayout.NORTH);
		mp.add(centerPanel, BorderLayout.CENTER);
		mp.add(rightPanel, BorderLayout.EAST);

		this.setVisible(true);
	}

	boolean isUpdating = false;

	public void comboBoxInit() {
		recipeSelect = new JComboBox<String>();
		// recipeSelect.setBounds(10, 10, 300, 35);
		recipeSelect.setPreferredSize(new Dimension(300, 35));
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
			recipeSelect.addItem((i + 1) + ". " + RecipeHandler.recipes.get(i).getName());
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

			if (name.toLowerCase().contains(s.toLowerCase())) {
				recipeSelect.addItem((i + 1) + ". " + RecipeHandler.recipes.get(i).getName());
			}
		}
		isUpdating = false;
		grabRecipeAndFill();
	}

	public void setUpSortSelect() {
		sortSelect = new JComboBox<RecipeSortType>();
		RecipeSortType[] recipeSorts = RecipeSortType.values();
		for (int i = 0; i < recipeSorts.length; i++) {
			sortSelect.addItem(recipeSorts[i]);
		}

		sortSelect.addActionListener(e -> {
			RecipeSortType rst = (RecipeSortType) sortSelect.getSelectedItem();
			if (rst != null) {
				RecipeHandler.sortRecipes(rst);
				String input = (String) searchBar.getText();
				if (!input.trim().isEmpty()) {
					setUpComboBoxSearch(input);
				} else {
					setUpComboBox();
				}
			}
		});

		sortSelect.setPreferredSize(new Dimension(200, 35));

		JLabel sortLabel = new JLabel("Sort By");
		// sortLabel.setBounds(500,120,200,20);

		// --- NEW: add to topPanel using layout
		addToTop(sortLabel, 6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.EAST, new Insets(8, 12, 0, 8));
		addToTop(sortSelect, 6, 1, 1, 1, 0.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST, new Insets(0, 12, 8, 8));
	}

	public void grabRecipeAndFill() {
		Recipe tempR = grabRecipe();

		isUpdating = true;
		// Recipe r = RecipeHandler.recipes.
		if (tempR == null) {
			nameField.setText("");
			costToMakeField.setText("");
			sellPointField.setText("");
			electricityField.setText("");
			manpowerField.setText("");
			packagingField.setText("");
			ingredientModel.setRowCount(0);
		} else {
			nameField.setText(tempR.getName());
			costToMakeField.setText(String.format("%.4f", tempR.getCostToMake()));
			markupField.setText(String.format("%.4f", tempR.getMarkUp()));
			sellPointField.setText(String.format("%.4f", tempR.getSellPoint()));
			electricityField.setText(String.format("%.4f", tempR.getElectricityCost()));
			manpowerField.setText(String.format("%.4f", tempR.getManPowerCost()));
			packagingField.setText(String.format("%.4f", tempR.getPackagingCost()));

			showSuggestedPricing(tempR.getVatSelection());
			loadIngredientsIntoModel(tempR);
			
		}

		isUpdating = false;
	}

	public Recipe grabRecipe() {
		String trimmed = (String) recipeSelect.getSelectedItem();
		String myRecipe;
		if (!trimmed.equals("New recipe")) {
			myRecipe = trimmed.substring(trimmed.indexOf(".") + 1).trim();
		} else {
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
		costToMakeField.setEditable(false);

		
		Dimension fieldSize = new Dimension(300, 35);
		nameField.setPreferredSize(fieldSize);
		costToMakeField.setPreferredSize(fieldSize);
		sellPointField.setPreferredSize(fieldSize);

	}

	public void setUpIngredientList() {
		String[] cols = {"ID", "Name", "Supplier", "Grams Used", "Cost in Recipe"};
		ingredientModel = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		ingredientTable = new JTable(ingredientModel);
		ingredientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		ingredientScroll = new JScrollPane(ingredientTable);
		ingredientScroll.setPreferredSize(new Dimension(750, 280));
	}

	public void loadIngredientsIntoModel(Recipe r) {
		ingredientModel.setRowCount(0);

		DecimalFormat df = new DecimalFormat("0.0000");
		for (Ingredient t : r.getIngredients()) {
			ingredientModel.addRow(new Object[]{
				t.getID(),
				t.getName(),
				t.getSupplierName(),
				r.getGramsUsedOfIngredient(t),
				"€" + df.format(r.getCostOfIngredientInRecipe(t))
			});
		}
	}

	public void setUpButtons() {
		saveButton = new JButton("Save Recipes");
		getIngredientsButton = new JButton("Add Ingredient");
		removeIngredientButton = new JButton("Remove Ingredient");
		removeButton = new JButton("Delete Recipe");

		Dimension btn = new Dimension(160, 40);
		saveButton.setPreferredSize(btn);
		removeButton.setPreferredSize(btn);
		getIngredientsButton.setPreferredSize(btn);
		removeIngredientButton.setPreferredSize(btn);

		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					removeRecipeAction();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
	public void removeRecipeAction() throws IOException {
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

			fh.writeRecipes();

			setUpComboBox();

			parent.fillDataModel();
		} else if (result == JOptionPane.NO_OPTION) {
			return;
		} else {
			return;
		}
	}

	public float getFloatFromString(String input) {
		float toReturn = 0;
		try {
			toReturn = Float.valueOf(input);
		} catch (NumberFormatException e) {}

		return toReturn;
	}

	// save all recipes
	public void saveRecipesAction() throws IOException {
		Recipe saveName = grabRecipe();

		String name = nameField.getText();
		String markUpText = markupField.getText().trim();
		String electricityText = electricityField.getText().trim();
		String manpowerText = manpowerField.getText().trim();
		String packagingText = packagingField.getText().trim();


		float markUp = 0;
		float electricity = 0;
		float manpower = 0;
		float packaging = 0;
		if (!markUpText.isEmpty()) {
			markUp = getFloatFromString(markUpText);
		}
		if (!electricityText.isEmpty()) {
			electricity = getFloatFromString(electricityText);
		}
		if (!manpowerText.isEmpty()) {
			manpower = getFloatFromString(manpowerText);
		}
		if (!packagingText.isEmpty()) {
			packaging = getFloatFromString(packagingText);
		}

		if (saveName != null) {
			// RECIPE ALREADY EXISTS, JUST UPDATE
			// INGREDIENTS ALREADY ADDED FROM ADDINGREDIENT, NO NEED TO DO ANYTHING,
			// AS SAVING THEM WILL THEN FINALISE THEIR ADDITION TO RECIPE
			saveName.setName(name);
			saveName.setMarkUp(markUp);
			saveName.setElectricityCost(electricity);
			saveName.setManPowerCost(manpower);
			saveName.setPackagingCost(packaging);
		} else {// if null then is new recipe, save.
			if (name.trim().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Recipe must have a name.");

			} else {

				saveName = new Recipe(name);

				saveName.setElectricityCost(electricity);
				saveName.setManPowerCost(manpower);
				saveName.setPackagingCost(packaging);

				for (int i = 0; i < ingredientModel.getRowCount(); i++) {
					int ingId = (int) ingredientModel.getValueAt(i, 0);
					Ingredient ing = RecipeHandler.ingredientIDMap.get(ingId);
					float gramsUsed = (float) ingredientModel.getValueAt(i, 3);
					saveName.addIngredient(ing, gramsUsed);
				}

				if (RecipeHandler.verifyNoRecipeCopy(saveName)) {
					RecipeHandler.addRecipe(saveName);
				}
			}
		}
		fh.writeRecipes();

		setUpComboBox();

		parent.fillDataModel();
		// recipeSelect.setSelectedItem(name);
		selectByName(name);
	}

	public void setUpUtilFields() {
	    electricityField = new JTextField();
	    manpowerField = new JTextField();
	    packagingField = new JTextField();

	    JLabel electricityLabel = new JLabel("Electricity Cost");
	    JLabel manpowerLabel = new JLabel("Manpower Cost");
	    JLabel packagingLabel = new JLabel("Packaging Cost");

	    Dimension util = new Dimension(150, 25);
	    electricityField.setPreferredSize(util);
	    manpowerField.setPreferredSize(util);
	    packagingField.setPreferredSize(util);

	    //add to rightPanel using layout
	    addToRight(electricityLabel, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST, new Insets(8, 8, 2, 8));
	    addToRight(electricityField, 0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 8, 8, 8));

	    addToRight(manpowerLabel, 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST, new Insets(0, 8, 2, 8));
	    addToRight(manpowerField, 0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 8, 8, 8));

	    addToRight(packagingLabel, 0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST, new Insets(0, 8, 2, 8));
	    addToRight(packagingField, 0, 5, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 8, 12, 8));
	}

	public void selectByName(String name) {
		for (int i = 0; i < recipeSelect.getItemCount(); i++) {
			String item = recipeSelect.getItemAt(i);
			if (item.equals("New recipe"))
				continue;
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
			recipe = new Recipe("Unfinished Recipe");
			RecipeHandler.addRecipe(recipe);
			recipeSelect.addItem("Unfinished Recipe");
			recipeSelect.setSelectedItem("Unfinished Recipe");

		}

		new AddIngredientDialog(this, recipe);

		loadIngredientsIntoModel(recipe);
		grabRecipeAndFill();
	}

	// remove ingredient from recipe based on JTable GUI selection.
	public void removeIngredientAction() {
		int selectedRow = ingredientTable.getSelectedRow();
		if (selectedRow < 0) return;

		int ingEntryId = (int) ingredientModel.getValueAt(selectedRow, 0);
		Recipe selectedRecipe = grabRecipe();
		Ingredient selectedIngredient = selectedRecipe.getIngredient(RecipeHandler.ingredientIDMap.get(ingEntryId));

		selectedRecipe.removeIngredient(selectedIngredient);
		loadIngredientsIntoModel(selectedRecipe);
		grabRecipeAndFill();
	}

	public void setUpSearchSelect() {
		searchBar = new JTextField();
		// searchBar.setBounds(500, 15, 200, 30);
		searchBar.setPreferredSize(new Dimension(200, 30));

		searchBar.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				String input = (String) searchBar.getText();
				if (!input.trim().isEmpty()) {
					setUpComboBoxSearch(input);
				} else {
					setUpComboBox();
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				String input = (String) searchBar.getText();
				if (!input.trim().isEmpty()) {
					setUpComboBoxSearch(input);
				} else {
					setUpComboBox();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				String input = (String) searchBar.getText();
				if (!input.trim().isEmpty()) {
					setUpComboBoxSearch(input);
				} else {
					setUpComboBox();
				}

			}
		});

		JLabel searchLabel = new JLabel("Keyword Search");
		// searchLabel.setBounds(500, 0, 200, 115);

		// --- NEW: add to topPanel using layout (right side)
		addToTop(searchLabel, 5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.EAST, new Insets(8, 8, 0, 8));
		addToTop(searchBar, 5, 1, 1, 1, 0.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST, new Insets(0, 8, 8, 8));
	}

	int vatSelect = 1;
	public void setUpVatAndMarkup() {
		vat1 = new JButton(String.format("%.2f",VATHandler.getVatFromSelection(1)*100) + "%");
		vat2 = new JButton(String.format("%.2f",VATHandler.getVatFromSelection(2)*100) + "%");
		vat3 = new JButton(String.format("%.2f",VATHandler.getVatFromSelection(3)*100) + "%");
		vat4 = new JButton(String.format("%.2f",VATHandler.getVatFromSelection(4)*100) + "%");

		// vat1.setBounds(500,200,80,40);
		// vat2.setBounds(580,200,80,40);
		// vat3.setBounds(660,200,80,40);

		vat1.setPreferredSize(new Dimension(80, 35));
		vat2.setPreferredSize(new Dimension(80, 35));
		vat3.setPreferredSize(new Dimension(80, 35));
		vat4.setPreferredSize(new Dimension(80, 35));

		vat1.addActionListener(e -> showSuggestedPricing(1));
		vat2.addActionListener(e -> showSuggestedPricing(2));
		vat3.addActionListener(e -> showSuggestedPricing(3));
		vat4.addActionListener(e -> showSuggestedPricing(4));

		JLabel markupLabel = new JLabel("Markup %");
		// markupLabel.setBounds(500, 150, 100, 20);
		markupField = new JTextField();
		// markupField.setBounds(500, 170, 100, 25);
		markupField.setPreferredSize(new Dimension(100, 25));

		JLabel suggestedCostLabel = new JLabel("Suggested Price");
		// suggestedCostLabel.setBounds(610, 150, 120, 20);
		suggestedCost = new JTextField();
		// suggestedCost.setBounds(610, 170, 120, 25);
		suggestedCost.setPreferredSize(new Dimension(120, 25));
		suggestedCost.setEditable(false);

		// --- NEW: add to rightPanel using layout
		int baseRow = 6;

		addToRight(markupLabel, 0, baseRow, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST, new Insets(0, 8, 2, 8));
		addToRight(markupField, 0, baseRow+1, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 8, 8, 8));

		addToRight(suggestedCostLabel, 0, baseRow+2, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST, new Insets(0, 8, 2, 8));
		addToRight(suggestedCost, 0, baseRow+3, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 8, 8, 8));

		// VAT row
	//	addToRight(vat1, 0, baseRow+4, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST, new Insets(0, 8, 8, 4));
	//	addToRight(vat2, 0, baseRow+4, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(0, 0, 8, 4));
		//addToRight(vat3, 0, baseRow+4, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.EAST, new Insets(0, 0, 8, 8));

		// Make those three VAT buttons sit in a single row nicely:
		// (layout-only trick: put them into a sub-panel)
		JPanel vatRow = new JPanel(new GridBagLayout());
		GridBagConstraints v = new GridBagConstraints();
		v.gridy = 0;
		v.insets = new Insets(0, 0, 0, 6);
		v.gridx = 0; vatRow.add(vat1, v);
		v.gridx = 1; vatRow.add(vat2, v);
		v.gridx = 2; vatRow.add(vat3, v);
		v.gridx = 3; v.insets = new Insets(0, 0, 0, 0); vatRow.add(vat4, v);

		// remove individually-added VAT buttons from rightPanel and add vatRow instead
		rightPanel.remove(vat1);
		rightPanel.remove(vat2);
		rightPanel.remove(vat3);
		addToRight(vatRow, 0, baseRow+4, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 8, 8, 8));
	}
	public void showSuggestedPricing(int selection) {
	    switch(selection) {
	        case 1:
	            vat1.setBackground(Color.green);
	            vat2.setBackground(null);
	            vat3.setBackground(null);
	            vat4.setBackground(null);
	            break;
	        case 2:
	            vat1.setBackground(null);
	            vat2.setBackground(Color.green);
	            vat3.setBackground(null);
	            vat4.setBackground(null);
	            break;
	        case 3:
	            vat1.setBackground(null);
	            vat2.setBackground(null);
	            vat3.setBackground(Color.green);
	            vat4.setBackground(null);
	            break;
	        case 4:
	        	vat1.setBackground(null);
	            vat2.setBackground(null);
	            vat3.setBackground(null);
	            vat4.setBackground(Color.green);
	            break;
	        default:
	            vat1.setBackground(Color.green);
	            vat2.setBackground(null);
	            vat3.setBackground(null);
	            vat4.setBackground(null);
	            break;
	    }

	    String markupString = markupField.getText();

	    if (!markupString.isEmpty()) {
	        try {
	            float markup = Float.valueOf(markupString);
	            if (markup > 0) {
	                Recipe r = grabRecipe();
	                if (r == null) return;

	                r.setMarkUp(markup);
	                r.setVatSelection(selection);

	                suggestedCost.setText(String.format("€ %.2f", r.getSellPoint()));
	            }
	        } catch (NumberFormatException e) {

	        }
	    }
	}

	// -------------------------
	// Layout helpers (layout-only)
	// -------------------------
	private void addToTop(JComponent c, int x, int y, int w, int h,
			double wx, double wy, int fill, int anchor, Insets insets) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.weightx = wx;
		gbc.weighty = wy;
		gbc.fill = fill;
		gbc.anchor = anchor;
		gbc.insets = insets;
		topPanel.add(c, gbc);
	}

	private void addToCenter(JComponent c, int x, int y, int w, int h,
			double wx, double wy, int fill, int anchor, Insets insets) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.weightx = wx;
		gbc.weighty = wy;
		gbc.fill = fill;
		gbc.anchor = anchor;
		gbc.insets = insets;
		centerPanel.add(c, gbc);
	}

	private void addToRight(JComponent c, int x, int y, int w, int h,
			double wx, double wy, int fill, int anchor, Insets insets) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.weightx = wx;
		gbc.weighty = wy;
		gbc.fill = fill;
		gbc.anchor = anchor;
		gbc.insets = insets;
		rightPanel.add(c, gbc);
	}
}