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
import javax.swing.table.DefaultTableModel;

import filep.Ingredient;
import filep.Recipe;
import filep.RecipeHandler;
import filep.RecipeSortType;
import filep.VATHandler;
import filep.FileHandler;
import util.ComboBoxUtil;
import util.TextChangeListener;

/**
 * Editor window for creating, editing, and deleting recipes. Layout: top bar
 * (recipe selector, save/delete buttons, search, sort), centre (name/cost/sell
 * fields + ingredient table), right (overhead costs, markup, VAT).
 */
public class RecipeFrame extends JFrame {

	// ========================
	// Fields
	// ========================

	JComboBox<String> recipeSelect;
	JComboBox<RecipeSortType> sortSelect;
	JPanel mp;
	FileHandler fh;

	JLabel nameLabel;
	JLabel costToMakeLabel;
	JLabel sellPointLabel;
	JLabel ingredientLabel;

	JTable ingredientTable;
	DefaultTableModel ingredientModel;

	JTextField nameField;
	JTextField costToMakeField;
	JTextField sellPointField;
	JTextField searchBar;
	JTextField electricityField;
	JTextField manpowerField;
	JTextField packagingField;
	JTextField priceAfterMarkupAndVat;
	JTextField markupField;

	JButton vat1;
	JButton vat2;
	JButton vat3;
	JButton vat4;

	JButton saveButton;
	JButton getIngredientsButton;
	JButton removeIngredientButton;
	JButton removeButton;

	JLabel saveStatusLabel;

	MyPanel parent;
	Recipe passed;

	private JPanel topPanel;
	private JPanel centerPanel;
	private JPanel rightPanel;
	private JScrollPane ingredientScroll;

	boolean isUpdating = false;
	int vatSelect = 1;

	// ========================
	// Constructor
	// ========================

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
		util.MinSizeEnforcer.apply(this, 1000, 650);

		this.fh = fh;

		// Build UI components (order matters: some depend on others being initialised)
		setUpPanel();
		setUpField();
		setUpIngredientList();
		setUpUtilFields(); // must be before selectPassed since grabRecipeAndFill uses these fields
		setUpVatAndMarkup();
		comboBoxInit();
		setUpComboBox();
		setUpButtons();
		setUpSearchSelect();
		setUpSortSelect();
		selectPassed(); // last, since it triggers grabRecipeAndFill which needs all fields ready

		// Place top-bar components
		addTo(topPanel, recipeSelect, 0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
				new Insets(4, 6, 4, 4));
		addTo(topPanel, saveButton, 2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.CENTER,
				new Insets(4, 2, 4, 2));
		addTo(topPanel, removeButton, 3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.CENTER,
				new Insets(4, 2, 4, 6));

		saveStatusLabel = new JLabel(" ");
		saveStatusLabel.setPreferredSize(new Dimension(140, 20));
		addTo(topPanel, saveStatusLabel, 0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST,
				new Insets(0, 6, 4, 6));

		// Place centre components (recipe fields + ingredient table)
		addTo(centerPanel, nameLabel, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST,
				new Insets(4, 6, 1, 6));
		addTo(centerPanel, nameField, 0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
				new Insets(0, 6, 4, 6));

		addTo(centerPanel, costToMakeLabel, 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST,
				new Insets(0, 6, 1, 6));
		addTo(centerPanel, costToMakeField, 0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST, new Insets(0, 6, 4, 6));

		addTo(centerPanel, sellPointLabel, 0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST,
				new Insets(0, 6, 1, 6));
		addTo(centerPanel, sellPointField, 0, 5, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
				new Insets(0, 6, 4, 6));

		addTo(centerPanel, ingredientLabel, 0, 6, 2, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST,
				new Insets(0, 6, 1, 6));
		addTo(centerPanel, ingredientScroll, 0, 7, 2, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER,
				new Insets(0, 6, 6, 6));

		// Add/remove ingredient buttons beside the ingredient list
		JPanel ingBtns = new JPanel(new GridBagLayout());
		GridBagConstraints b = new GridBagConstraints();
		b.gridx = 0;
		b.gridy = 0;
		b.fill = GridBagConstraints.HORIZONTAL;
		b.insets = new Insets(0, 0, 8, 0);
		ingBtns.add(getIngredientsButton, b);

		b.gridy = 1;
		b.insets = new Insets(0, 0, 0, 0);
		ingBtns.add(removeIngredientButton, b);

		addTo(centerPanel, ingBtns, 2, 7, 1, 1, 0.0, 1.0, GridBagConstraints.VERTICAL, GridBagConstraints.NORTH,
				new Insets(0, 4, 6, 6));

		this.add(mp);
		this.setVisible(true);
	}

	// ========================
	// Panel Layout Setup
	// ========================

	/** Creates the three-region BorderLayout (top, centre, right). */
	public void setUpPanel() {
		mp = new JPanel();
		mp.setLayout(new BorderLayout());

		topPanel = new JPanel(new GridBagLayout());
		centerPanel = new JPanel(new GridBagLayout());
		rightPanel = new JPanel(new GridBagLayout());

		mp.add(topPanel, BorderLayout.NORTH);
		mp.add(centerPanel, BorderLayout.CENTER);
		mp.add(rightPanel, BorderLayout.EAST);
	}

	// ========================
	// Recipe Fields
	// ========================

	/** Initialises the recipe name, cost, and sell point fields and labels. */
	public void setUpField() {

		nameLabel = new JLabel("Recipe Name");
		costToMakeLabel = new JLabel("Cost To Make");
		sellPointLabel = new JLabel("Selling Point");
		ingredientLabel = new JLabel("Ingredients");

		nameField = new JTextField();
		costToMakeField = new JTextField();
		sellPointField = new JTextField();
		costToMakeField.setEditable(false);
		sellPointField.setEditable(false);

		Dimension fieldSize = new Dimension(300, 35);
		nameField.setPreferredSize(fieldSize);
		costToMakeField.setPreferredSize(fieldSize);
		sellPointField.setPreferredSize(fieldSize);

	}

	/**
	 * Creates overhead cost fields (electricity, manpower, packaging) on the right
	 * panel.
	 */
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

		addTo(rightPanel, electricityLabel, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST,
				new Insets(4, 6, 1, 6));
		addTo(rightPanel, electricityField, 0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST, new Insets(0, 6, 4, 6));

		addTo(rightPanel, manpowerLabel, 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST,
				new Insets(0, 6, 1, 6));
		addTo(rightPanel, manpowerField, 0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
				new Insets(0, 6, 4, 6));

		addTo(rightPanel, packagingLabel, 0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST,
				new Insets(0, 6, 1, 6));
		addTo(rightPanel, packagingField, 0, 5, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
				new Insets(0, 6, 6, 6));
	}

	// ========================
	// Ingredient Table
	// ========================

	/**
	 * Creates the ingredient table (non-editable, single selection) with a scroll
	 * pane.
	 */
	public void setUpIngredientList() {
		String[] cols = { "ID", "Name", "Supplier", "Grams Used", "Cost in Recipe" };
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

	/** Refreshes the ingredient table from the given recipe's ingredient list. */
	public void loadIngredientsIntoModel(Recipe r) {
		ingredientModel.setRowCount(0);

		DecimalFormat df = new DecimalFormat("0.0000");
		for (Ingredient t : r.getIngredients()) {
			ingredientModel.addRow(new Object[] { t.getID(), t.getName(), t.getSupplierName(),
					r.getGramsUsedOfIngredient(t), "€" + df.format(r.getCostOfIngredientInRecipe(t)) });
		}
	}

	// ========================
	// VAT & Markup
	// ========================

	/**
	 * Creates VAT selection buttons, markup field, and price after markup & VAT
	 * display on the right panel.
	 */
	public void setUpVatAndMarkup() {
		vat1 = new JButton(String.format("%.2f", VATHandler.getVatFromSelection(1) * 100) + "%");
		vat2 = new JButton(String.format("%.2f", VATHandler.getVatFromSelection(2) * 100) + "%");
		vat3 = new JButton(String.format("%.2f", VATHandler.getVatFromSelection(3) * 100) + "%");
		vat4 = new JButton(String.format("%.2f", VATHandler.getVatFromSelection(4) * 100) + "%");

		vat1.setPreferredSize(new Dimension(80, 35));
		vat2.setPreferredSize(new Dimension(80, 35));
		vat3.setPreferredSize(new Dimension(80, 35));
		vat4.setPreferredSize(new Dimension(80, 35));

		vat1.addActionListener(e -> showPriceAfterMarkupAndVat(1));
		vat2.addActionListener(e -> showPriceAfterMarkupAndVat(2));
		vat3.addActionListener(e -> showPriceAfterMarkupAndVat(3));
		vat4.addActionListener(e -> showPriceAfterMarkupAndVat(4));

		JLabel markupLabel = new JLabel("Markup %");
		markupField = new JTextField();
		markupField.setPreferredSize(new Dimension(100, 25));

		JLabel priceAfterMarkupAndVatLabel = new JLabel("Price After Markup & VAT");
		priceAfterMarkupAndVat = new JTextField();
		priceAfterMarkupAndVat.setPreferredSize(new Dimension(120, 25));
		priceAfterMarkupAndVat.setEditable(false);

		int baseRow = 6;

		addTo(rightPanel, markupLabel, 0, baseRow, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST,
				new Insets(0, 6, 1, 6));
		addTo(rightPanel, markupField, 0, baseRow + 1, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST, new Insets(0, 6, 4, 6));

		addTo(rightPanel, priceAfterMarkupAndVatLabel, 0, baseRow + 2, 1, 1, 0.0, 0.0, GridBagConstraints.NONE,
				GridBagConstraints.WEST, new Insets(0, 6, 1, 6));
		addTo(rightPanel, priceAfterMarkupAndVat, 0, baseRow + 3, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST, new Insets(0, 6, 4, 6));

		// Arrange VAT buttons in a horizontal row
		JPanel vatRow = new JPanel(new GridBagLayout());
		GridBagConstraints v = new GridBagConstraints();
		v.gridy = 0;
		v.insets = new Insets(0, 0, 0, 6);
		v.gridx = 0;
		vatRow.add(vat1, v);
		v.gridx = 1;
		vatRow.add(vat2, v);
		v.gridx = 2;
		vatRow.add(vat3, v);
		v.gridx = 3;
		v.insets = new Insets(0, 0, 0, 0);
		vatRow.add(vat4, v);

		rightPanel.remove(vat1);
		rightPanel.remove(vat2);
		rightPanel.remove(vat3);
		addTo(rightPanel, vatRow, 0, baseRow + 4, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST, new Insets(0, 6, 6, 6));
	}

	/**
	 * Highlights the selected VAT button, calculates the price after markup & VAT,
	 * and updates the recipe's VAT selection.
	 */
	public void showPriceAfterMarkupAndVat(int selection) {

		setVatSelectionColour(selection);

		String markupString = markupField.getText();

		if (!markupString.isEmpty()) {
			try {
				float markup = Float.valueOf(markupString);
				if (markup > 0) {
					Recipe r = grabRecipe();
					if (r == null)
						return;

					r.setMarkUp(markup);
					r.setVatSelection(selection);

					priceAfterMarkupAndVat.setText(String.format("€ %.2f", r.getSellPoint()));
				}
			} catch (NumberFormatException e) {
				priceAfterMarkupAndVat.setText("Invalid markup");
			}
		}
	}

	public void setVatSelectionColour(int selection) {
		vat1.setBackground((selection == 1) ? Color.GREEN : null);
		vat2.setBackground((selection == 2) ? Color.GREEN : null);
		vat3.setBackground((selection == 3) ? Color.GREEN : null);
		vat4.setBackground((selection == 4) ? Color.GREEN : null);
	}

	// ========================
	// ComboBox (Recipe Selector)
	// ========================

	/** Initialises the recipe selector combo box with its action listener. */
	public void comboBoxInit() {
		recipeSelect = new JComboBox<String>();
		recipeSelect.setPreferredSize(new Dimension(300, 35));
		recipeSelect.setEditable(false);
		recipeSelect.addActionListener(e -> {
			if (!isUpdating) {
				grabRecipeAndFill();
			}
		});

	}

	/** Populates the recipe selector with all recipes (unfiltered). */
	public void setUpComboBox() {
		isUpdating = true;
		recipeSelect.removeAllItems();
		recipeSelect.addItem("New recipe");

		for (int i = 0; i < RecipeHandler.recipes.size(); i++) {
			recipeSelect.addItem((i + 1) + ". " + RecipeHandler.recipes.get(i).getName());
		}
		isUpdating = false;
		grabRecipeAndFill();
	}

	/** Populates the recipe selector filtered by the given search string. */
	public void setUpComboBoxSearch(String s) {
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

	/** If a recipe was passed from the main panel, selects it in the combo box. */
	public void selectPassed() {
		if (passed != null) {
			selectByName(passed.getName());
		}
	}

	public void selectByName(String name) {
		ComboBoxUtil.selectByName(recipeSelect, name, "New recipe");
	}

	// ========================
	// Search & Sort
	// ========================

	public void setUpSearchSelect() {
		searchBar = new JTextField();
		searchBar.setPreferredSize(new Dimension(200, 30));

		TextChangeListener.attach(searchBar, () -> {
			String input = searchBar.getText();
			if (!input.trim().isEmpty()) {
				setUpComboBoxSearch(input);
			} else {
				setUpComboBox();
			}
		});

		JLabel searchLabel = new JLabel("Keyword Search");

		addTo(topPanel, searchLabel, 5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.EAST,
				new Insets(4, 6, 0, 6));
		addTo(topPanel, searchBar, 5, 1, 1, 1, 0.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
				new Insets(0, 6, 4, 6));
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

		addTo(topPanel, sortLabel, 6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.EAST,
				new Insets(4, 6, 0, 6));
		addTo(topPanel, sortSelect, 6, 1, 1, 1, 0.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST,
				new Insets(0, 6, 4, 6));
	}

	// ========================
	// Buttons
	// ========================

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

		removeButton.addActionListener(e -> {
			try {
				removeRecipeAction();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(RecipeFrame.this, "Failed to delete recipe: " + e1.getMessage());
			}
		});

		getIngredientsButton.addActionListener(e -> addIngredientAction());
		removeIngredientButton.addActionListener(e -> removeIngredientAction());

		saveButton.addActionListener(e -> {
			try {
				saveRecipesAction();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(RecipeFrame.this, "Failed to save recipe: " + e1.getMessage());
			}
		});
	}

	// ========================
	// Data Loading & Retrieval
	// ========================

	/** Returns the Recipe selected in the combo box, or null if "New recipe". */
	public Recipe grabRecipe() {
		String trimmed = (String) recipeSelect.getSelectedItem();
		if (trimmed == null || trimmed.equals("New recipe")) {
			return null;
		}
		String myRecipe = ComboBoxUtil.stripPrefix(trimmed);
		return RecipeHandler.recipeByName.get(myRecipe);
	}

	/**
	 * Loads the selected recipe's data into all input fields, or clears them if
	 * none selected.
	 */
	public void grabRecipeAndFill() {
		Recipe tempR = grabRecipe();

		isUpdating = true;
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

			showPriceAfterMarkupAndVat(tempR.getVatSelection());
			loadIngredientsIntoModel(tempR);

		}

		isUpdating = false;
	}

	// ========================
	// Actions (Save, Delete, Add/Remove Ingredient)
	// ========================

	/**
	 * Saves the current fields as a new recipe or updates the selected existing
	 * one.
	 */
	public void saveRecipesAction() throws IOException {
		try {
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
				// Updating an existing recipe
				saveName.setName(name);
				saveName.setMarkUp(markUp);
				saveName.setElectricityCost(electricity);
				saveName.setManPowerCost(manpower);
				saveName.setPackagingCost(packaging);
			} else {
				// Creating a new recipe
				if (name.trim().isEmpty()) {
					JOptionPane.showMessageDialog(this, "Recipe must have a name.");
					return;
				}

				saveName = new Recipe(name);

				saveName.setElectricityCost(electricity);
				saveName.setManPowerCost(manpower);
				saveName.setPackagingCost(packaging);

				// Transfer ingredients from the table model to the new recipe
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
			fh.writeRecipes();

			setUpComboBox();

			parent.fillDataModel();
			selectByName(name);

			saveStatusLabel.setText("Successfully Saved");
			saveStatusLabel.setForeground(new Color(0, 153, 0));
		} catch (Exception e) {
			saveStatusLabel.setText("Failed to Save");
			saveStatusLabel.setForeground(Color.RED);
		}
	}

	/** Deletes the selected recipe after user confirmation. */
	public void removeRecipeAction() throws IOException {
		Recipe toRemove = grabRecipe();
		if (toRemove == null) {
			return;
		}

		int result = JOptionPane.showConfirmDialog(this, "Do you want to delete this recipe?", "Confirmation",
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

	/** Opens the AddIngredientDialog to add an ingredient to the current recipe. */
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

	/** Removes the selected ingredient from the current recipe. */
	public void removeIngredientAction() {
		int selectedRow = ingredientTable.getSelectedRow();
		if (selectedRow < 0)
			return;

		int ingEntryId = (int) ingredientModel.getValueAt(selectedRow, 0);
		Recipe selectedRecipe = grabRecipe();
		Ingredient selectedIngredient = selectedRecipe.getIngredient(RecipeHandler.ingredientIDMap.get(ingEntryId));

		selectedRecipe.removeIngredient(selectedIngredient);
		loadIngredientsIntoModel(selectedRecipe);
		grabRecipeAndFill();
	}

	// ========================
	// Utility Methods
	// ========================

	/** Safely parses a float from a string, returning 0 on failure. */
	public float getFloatFromString(String input) {
		float toReturn = 0;
		try {
			toReturn = Float.valueOf(input);
		} catch (NumberFormatException e) {
		}

		return toReturn;
	}

	// ========================
	// GridBag Layout Helpers
	// ========================

	private void addTo(JPanel panel, JComponent c, int x, int y, int w, int h, double wx, double wy, int fill,
			int anchor, Insets insets) {
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
		panel.add(c, gbc);
	}
}
