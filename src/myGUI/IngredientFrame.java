package myGUI;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.io.IOException;

import java.util.ArrayList;

import filep.Ingredient;
import filep.IngredientSortType;
import filep.Recipe;
import filep.RecipeHandler;
import filep.VATHandler;
import filep.FileHandler;
import util.ComboBoxUtil;
import util.TextChangeListener;

/**
 * Editor window for creating, editing, and deleting ingredients. Left side:
 * ingredient fields, VAT buttons, save/delete buttons. Right side: sort
 * dropdown, keyword search, supplier search.
 */
public class IngredientFrame extends JFrame {

	// ========================
	// Fields
	// ========================

	JComboBox<String> ingredientSelect;
	JComboBox<IngredientSortType> sortSelect;

	JTextField nameField;
	JTextField supplierField;
	JTextField costField;
	JTextField gramsField;
	JTextField costAfterVatField;
	JTextField searchBar;
	JTextField supplierSearchBar;

	JButton saveButton;
	JButton deleteButton;

	JLabel saveStatusLabel;

	JButton vat1;
	JButton vat2;
	JButton vat3;
	JButton vat4;
	int vatSelect = 1;

	FileHandler fh;
	MyPanel parent;
	boolean isUpdating = false;

	private JPanel mainPanel;
	private JPanel leftPanel;
	private JPanel rightPanel;

	// ========================
	// Constructor
	// ========================

	public IngredientFrame(FileHandler fh, MyPanel mf) {
		this.fh = fh;
		this.parent = mf;

		setTitle("Ingredient Editor");
		setSize(650, 420);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		util.MinSizeEnforcer.apply(this, 650, 420);

		mainPanel = new JPanel(new BorderLayout());
		leftPanel = new JPanel(new GridBagLayout());
		rightPanel = new JPanel(new GridBagLayout());

		mainPanel.add(leftPanel, BorderLayout.CENTER);
		mainPanel.add(rightPanel, BorderLayout.EAST);
		add(mainPanel, BorderLayout.CENTER);

		setUpComboBox();
		setUpFields();
		setUpButtons();
		setUpSortSelect();
		setUpSearchSelect();
		setUpSupplierSearchSelect();

		setVisible(true);
	}

	// ========================
	// ComboBox Setup
	// ========================

	/**
	 * Creates the ingredient selector combo box and places it in the left panel.
	 */
	private void setUpComboBox() {
		ingredientSelect = new JComboBox<>();
		ingredientSelect.setPreferredSize(new Dimension(300, 35));

		reloadComboBox();

		ingredientSelect.addActionListener(e -> {
			if (!isUpdating) {
				loadIngredient();
			}
		});

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(8, 10, 8, 10);
		leftPanel.add(ingredientSelect, gbc);
	}

	/** Repopulates the combo box with all ingredients (unfiltered). */
	private void reloadComboBox() {
		isUpdating = true;
		ingredientSelect.removeAllItems();
		ingredientSelect.addItem("New Ingredient");

		int j = 1;
		for (Ingredient i : RecipeHandler.ingredients) {
			ingredientSelect.addItem(j + ". " + i.getName());
			j++;
		}
		isUpdating = false;
	}

	/**
	 * Repopulates the combo box filtered by current name and supplier search text.
	 */
	public void reloadComboBoxFiltered() {
		isUpdating = true;
		ingredientSelect.removeAllItems();
		String nameInput = searchBar.getText().toLowerCase().trim();
		String supplierInput = supplierSearchBar.getText().toLowerCase().trim();

		ingredientSelect.addItem("New Ingredient");

		int j = 1;
		for (Ingredient i : RecipeHandler.ingredients) {
			boolean matchesName = nameInput.isEmpty() || i.getName().toLowerCase().contains(nameInput);
			boolean matchesSupplier = supplierInput.isEmpty()
					|| i.getSupplierName().toLowerCase().contains(supplierInput);

			if (matchesName && matchesSupplier) {
				ingredientSelect.addItem(j + ". " + i.getName());
				j++;
			}
		}

		isUpdating = false;
	}

	/** Selects the combo box item matching the given ingredient name. */
	public void selectByName(String name) {
		ComboBoxUtil.selectByName(ingredientSelect, name, "New Ingredient");
	}

	// ========================
	// Input Fields & VAT Buttons
	// ========================

	/**
	 * Creates all input fields (name, supplier, cost, grams) and VAT selection
	 * buttons.
	 */
	private void setUpFields() {
		JLabel nameLabel = new JLabel("Name");
		JLabel supplierLabel = new JLabel("Supplier");
		JLabel costLabel = new JLabel("Cost Before VAT");
		JLabel gramsLabel = new JLabel("Grams Bought");
		JLabel vatLabel = new JLabel("VAT Selection");
		JLabel costAfterVatLabel = new JLabel("Cost After VAT");

		nameField = new JTextField();
		supplierField = new JTextField();
		costField = new JTextField();
		gramsField = new JTextField();
		costAfterVatField = new JTextField();
		costAfterVatField.setEditable(false);

		// VAT buttons showing the rate as a percentage
		vat1 = new JButton(String.format("%.2f", VATHandler.getVatFromSelection(1) * 100) + "%");
		vat2 = new JButton(String.format("%.2f", VATHandler.getVatFromSelection(2) * 100) + "%");
		vat3 = new JButton(String.format("%.2f", VATHandler.getVatFromSelection(3) * 100) + "%");
		vat4 = new JButton(String.format("%.2f", VATHandler.getVatFromSelection(4) * 100) + "%");

		Dimension vatBtnSize = new Dimension(70, 30);
		vat1.setPreferredSize(vatBtnSize);
		vat2.setPreferredSize(vatBtnSize);
		vat3.setPreferredSize(vatBtnSize);
		vat4.setPreferredSize(vatBtnSize);

		vat1.addActionListener(e -> selectVat(1));
		vat2.addActionListener(e -> selectVat(2));
		vat3.addActionListener(e -> selectVat(3));
		vat4.addActionListener(e -> selectVat(4));

		highlightVatButton(vatSelect);

		// --- Layout all fields onto leftPanel ---

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(6, 10, 2, 10);

		// Row 1: Name
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		leftPanel.add(nameLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 6, 10);
		leftPanel.add(nameField, gbc);

		// Row 2: Supplier
		gbc.insets = new Insets(6, 10, 2, 10);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		leftPanel.add(supplierLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 6, 10);
		leftPanel.add(supplierField, gbc);

		// Row 3: Cost Before VAT
		gbc.insets = new Insets(6, 10, 2, 10);
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		leftPanel.add(costLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 6, 10);
		leftPanel.add(costField, gbc);

		// Row 4: Grams Bought
		gbc.insets = new Insets(6, 10, 2, 10);
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		leftPanel.add(gramsLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 6, 10);
		leftPanel.add(gramsField, gbc);

		// Row 5: VAT Selection buttons
		gbc.insets = new Insets(6, 10, 2, 10);
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		leftPanel.add(vatLabel, gbc);

		JPanel vatRow = new JPanel(new GridBagLayout());
		GridBagConstraints v = new GridBagConstraints();
		v.gridy = 0;
		v.insets = new Insets(0, 0, 0, 4);
		v.gridx = 0;
		vatRow.add(vat1, v);
		v.gridx = 1;
		vatRow.add(vat2, v);
		v.gridx = 2;
		vatRow.add(vat3, v);
		v.gridx = 3;
		v.insets = new Insets(0, 0, 0, 0);
		vatRow.add(vat4, v);

		gbc.gridx = 0;
		gbc.gridy = 10;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 6, 10);
		leftPanel.add(vatRow, gbc);

		// Row 6: Cost After VAT (read-only)
		gbc.insets = new Insets(6, 10, 2, 10);
		gbc.gridx = 0;
		gbc.gridy = 11;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		leftPanel.add(costAfterVatLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 12;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 10, 10);
		leftPanel.add(costAfterVatField, gbc);
	}

	// ========================
	// VAT Selection Helpers
	// ========================

	private void selectVat(int selection) {
		vatSelect = selection;
		highlightVatButton(selection);
		updateCostAfterVatField();
	}

	/** Highlights the selected VAT button green and resets the others. */
	private void highlightVatButton(int selection) {
		vat1.setBackground(selection == 1 ? Color.green : null);
		vat2.setBackground(selection == 2 ? Color.green : null);
		vat3.setBackground(selection == 3 ? Color.green : null);
		vat4.setBackground(selection == 4 ? Color.green : null);
	}

	/**
	 * Recalculates and displays the cost-after-VAT based on current cost field and
	 * VAT selection.
	 */
	private void updateCostAfterVatField() {
		try {
			float cost = Float.parseFloat(costField.getText());
			float vatRate = VATHandler.getVatFromSelection(vatSelect);
			float costAfterVat = cost * (1 + vatRate);
			costAfterVatField.setText(String.format("%.4f", costAfterVat));
		} catch (NumberFormatException e) {
			costAfterVatField.setText("");
		}
	}

	// ========================
	// Buttons
	// ========================

	private void setUpButtons() {
		saveButton = new JButton("Save");
		deleteButton = new JButton("Delete");

		saveButton.setPreferredSize(new Dimension(120, 40));
		deleteButton.setPreferredSize(new Dimension(120, 40));

		saveButton.addActionListener(e -> saveIngredient());
		deleteButton.addActionListener(e -> {
			try {
				deleteIngredient();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(IngredientFrame.this, "Failed to delete ingredient: " + e1.getMessage());
			}
		});

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.insets = new Insets(6, 0, 6, 10);

		gbc.gridy = 2;
		leftPanel.add(saveButton, gbc);

		gbc.gridy = 4;
		leftPanel.add(deleteButton, gbc);

		saveStatusLabel = new JLabel(" ");
		saveStatusLabel.setPreferredSize(new Dimension(120, 20));
		gbc.gridy = 3;
		gbc.gridx = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 0, 10);
		leftPanel.add(saveStatusLabel, gbc);
	}

	// ========================
	// Sort & Search Setup
	// ========================

	public void setUpSortSelect() {
		sortSelect = new JComboBox<IngredientSortType>();
		IngredientSortType[] ingredientSorts = IngredientSortType.values();
		for (int i = 0; i < ingredientSorts.length; i++) {
			sortSelect.addItem(ingredientSorts[i]);
		}

		sortSelect.addActionListener(e -> {
			IngredientSortType ist = (IngredientSortType) sortSelect.getSelectedItem();
			if (ist != null) {
				RecipeHandler.sortIngredients(ist);

				reloadComboBoxFiltered();
				loadIngredient();
			}
		});

		sortSelect.setPreferredSize(new Dimension(200, 35));

		JLabel sortLabel = new JLabel("Sort Type");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 10, 2, 10);

		gbc.gridy = 0;
		rightPanel.add(sortLabel, gbc);

		gbc.gridy = 1;
		gbc.insets = new Insets(0, 10, 10, 10);
		rightPanel.add(sortSelect, gbc);
	}

	public void setUpSearchSelect() {
		searchBar = new JTextField();

		TextChangeListener.attach(searchBar, () -> {
			reloadComboBoxFiltered();
			loadIngredient();
		});

		JLabel searchLabel = new JLabel("Keyword Search");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 2, 10);

		gbc.gridy = 2;
		rightPanel.add(searchLabel, gbc);

		gbc.gridy = 3;
		gbc.insets = new Insets(0, 10, 10, 10);
		rightPanel.add(searchBar, gbc);
	}

	public void setUpSupplierSearchSelect() {
		supplierSearchBar = new JTextField();

		TextChangeListener.attach(supplierSearchBar, () -> {
			reloadComboBoxFiltered();
			loadIngredient();
		});

		JLabel supplierSearchLabel = new JLabel("Supplier Search");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 2, 10);

		gbc.gridy = 4;
		rightPanel.add(supplierSearchLabel, gbc);

		gbc.gridy = 5;
		gbc.insets = new Insets(0, 10, 10, 10);
		rightPanel.add(supplierSearchBar, gbc);
	}

	// ========================
	// Data Loading & Retrieval
	// ========================

	/**
	 * Returns the currently selected ingredient from the combo box, or null if "New
	 * Ingredient".
	 */
	public Ingredient grabIngredient() {
		String select = (String) ingredientSelect.getSelectedItem();
		if (select == null || select.equals("New Ingredient")) {
			return null;
		}
		String selection = ComboBoxUtil.stripPrefix(select);
		return RecipeHandler.ingredientByName.get(selection);
	}

	/**
	 * Populates input fields from the currently selected ingredient (or clears them
	 * if none).
	 */
	private void loadIngredient() {
		Ingredient i = grabIngredient();

		if (i == null) {
			nameField.setText("");
			supplierField.setText("");
			costField.setText("");
			gramsField.setText("");
			costAfterVatField.setText("");
			vatSelect = 1;
			highlightVatButton(vatSelect);
		} else {
			nameField.setText(i.getName());
			supplierField.setText(i.getSupplierName());
			costField.setText(String.format("%.4f", i.getCostPer1g() * i.getGrams()));
			gramsField.setText(String.valueOf(i.getGrams()));
			vatSelect = i.getVatSelection();
			highlightVatButton(vatSelect);
			costAfterVatField.setText(String.format("%.4f", i.getCostAfterVat()));
		}
	}

	// ========================
	// CRUD Actions
	// ========================

	/**
	 * Saves the current fields as a new ingredient or updates the selected existing
	 * one.
	 */
	private void saveIngredient() {
		try {
			Ingredient selected = grabIngredient();

			String name = nameField.getText();
			String supplier = supplierField.getText();
			float cost = Float.parseFloat(costField.getText());
			float grams = Float.parseFloat(gramsField.getText());

			if (selected == null) {
				// Creating a new ingredient
				Ingredient i = new Ingredient(name, supplier, cost, grams, Ingredient.NEW_ID_SENTINEL, vatSelect);
				if (RecipeHandler.verifyNoIngredientCopy(i)) {
					RecipeHandler.addIngredient(i);

					fh.writeIngredients();
					reloadComboBox();

					updateParent();

					selectByName(name);
				} else {
					JOptionPane.showMessageDialog(this, "Error writing Ingredient");
					saveStatusLabel.setText("Failed to Save");
					saveStatusLabel.setForeground(Color.RED);
					return;
				}
			} else {
				// Updating an existing ingredient
				RecipeHandler.ingredientByName.remove(selected.getName());

				selected.setName(name);
				selected.setSupplierName(supplier);
				selected.setCost(cost);
				selected.setGrams(grams);
				selected.setVatSelection(vatSelect);
				RecipeHandler.ingredientByName.put(selected.getName(), selected);
				fh.writeIngredients();
				reloadComboBox();

				updateParent();

				selectByName(name);
			}

			saveStatusLabel.setText("Successfully Saved");
			saveStatusLabel.setForeground(new Color(0, 153, 0));
		} catch (Exception e) {
			saveStatusLabel.setText("Failed to Save");
			saveStatusLabel.setForeground(Color.RED);
		}
	}

	/** Deletes the currently selected ingredient after user confirmation. */
	private void deleteIngredient() throws IOException {
		Ingredient selected = grabIngredient();
		if (selected == null) {
			JOptionPane.showMessageDialog(this, "No ingredient selected");
			return;
		}

		// Find recipes that use this ingredient
		ArrayList<Recipe> affectedRecipes = new ArrayList<>();
		for (Recipe recipe : RecipeHandler.recipes) {
			if (recipe.getIngredients().contains(selected)) {
				affectedRecipes.add(recipe);
			}
		}

		// Build confirmation message
		String message = "Delete this ingredient?";
		if (!affectedRecipes.isEmpty()) {
			StringBuilder sb = new StringBuilder("This ingredient is used in " + affectedRecipes.size() + " recipe(s):\n");
			for (Recipe r : affectedRecipes) {
				sb.append("  - ").append(r.getName()).append("\n");
			}
			sb.append("\nIt will be removed from these recipes. Continue?");
			message = sb.toString();
		}

		int result = JOptionPane.showConfirmDialog(this, message, "Confirm",
				JOptionPane.YES_NO_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			// Remove ingredient from all recipes that use it
			for (Recipe recipe : affectedRecipes) {
				recipe.removeIngredient(selected);
			}

			RecipeHandler.ingredients.remove(selected);
			RecipeHandler.ingredientIDMap.remove(selected.getID());
			RecipeHandler.ingredientByName.remove(selected.getName());

			reloadComboBox();
			ingredientSelect.setSelectedIndex(0);
			fh.writeIngredients();
			fh.writeRecipes();
			updateParent();
		}
	}

	/**
	 * Refreshes all recipe calculations and the parent panel's table after an
	 * ingredient change.
	 */
	private void updateParent() {
		RecipeHandler.updateRecipes();
		parent.fillDataModel();
	}
}
