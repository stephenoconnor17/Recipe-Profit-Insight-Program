package myGUI;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import util.TextChangeListener;

import filep.Ingredient;
import filep.Recipe;
import filep.RecipeHandler;

/**
 * Modal dialog for adding an ingredient to a recipe. Shows a
 * searchable/filterable table of all available ingredients and a grams input
 * field.
 */
public class AddIngredientDialog extends JDialog {

	// ========================
	// Fields
	// ========================

	Recipe recipe;

	JTable ingredientTable;
	DefaultTableModel model;
	JScrollPane jsp;

	JTextField searchBar;
	JTextField supplierSearchBar;
	JTextField gramsField;

	JButton addButton;
	JButton cancelButton;

	private JPanel listPanel;
	private JPanel controlsPanel;

	// ========================
	// Constructor
	// ========================

	public AddIngredientDialog(RecipeFrame parent, Recipe recipe) {
		super(parent, "Add Ingredient", true);
		this.recipe = recipe;

		this.setSize(1000, 400);
		this.setLocationRelativeTo(parent);
		this.setLayout(new BorderLayout());

		setUpList();
		setUpFields();
		setUpButtons();
		setUpSearchSelect();
		setUpSupplierSearchSelect();

		this.add(listPanel, BorderLayout.CENTER);
		this.add(controlsPanel, BorderLayout.SOUTH);

		this.setVisible(true);
	}

	// ========================
	// Ingredient List (Table)
	// ========================

	/** Creates the ingredient table with a non-editable model and populates it. */
	public void setUpList() {
		String[] cols = { "ID", "Name", "Supplier", "Amount/Unit", "Unit Type", "Cost/Unit", "Cost/100", "Cost/1" };
		model = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		ingredientTable = new JTable(model);
		ingredientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		loadList();

		jsp = new JScrollPane(ingredientTable);

		listPanel = new JPanel(new BorderLayout());
		listPanel.add(jsp, BorderLayout.CENTER);
	}

	/** Loads all ingredients into the table. */
	public void loadList() {
		model.setRowCount(0);

		DecimalFormat df = new DecimalFormat("0.0000");
		for (Ingredient i : RecipeHandler.ingredients) {
			model.addRow(new Object[] { i.getID(), i.getName(), i.getSupplierName(), i.getGrams(),
					i.getUnitType(), "€" + df.format(i.getCost()), "€" + df.format(i.getCostPer100g()),
					"€" + df.format(i.getCostPer1g()) });
		}
	}

	/**
	 * Loads only ingredients matching the current name and supplier search filters.
	 */
	public void loadListFiltered() {
		model.setRowCount(0);
		String nameInput = searchBar.getText().toLowerCase().trim();
		String supplierInput = supplierSearchBar.getText().toLowerCase().trim();

		DecimalFormat df = new DecimalFormat("0.0000");
		for (Ingredient i : RecipeHandler.ingredients) {
			boolean matchesName = nameInput.isEmpty() || i.getName().toLowerCase().contains(nameInput);
			boolean matchesSupplier = supplierInput.isEmpty()
					|| i.getSupplierName().toLowerCase().contains(supplierInput);

			if (matchesName && matchesSupplier) {
				model.addRow(new Object[] { i.getID(), i.getName(), i.getSupplierName(), i.getGrams(),
						i.getUnitType(), "€" + df.format(i.getCost()), "€" + df.format(i.getCostPer100g()),
						"€" + df.format(i.getCostPer1g()) });
			}
		}
	}

	// ========================
	// Input Fields
	// ========================

	public void setUpFields() {
		if (controlsPanel == null) {
			controlsPanel = new JPanel(new GridBagLayout());
		}

		JLabel gramsLabel = new JLabel("Amount used: ");
		gramsField = new JTextField();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.insets = new Insets(8, 10, 2, 10);
		gbc.anchor = GridBagConstraints.WEST;

		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		controlsPanel.add(gramsLabel, gbc);

		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.0;
		controlsPanel.add(gramsField, gbc);
	}

	// ========================
	// Buttons
	// ========================

	public void setUpButtons() {
		addButton = new JButton("Add");
		cancelButton = new JButton("Cancel");

		addButton.addActionListener(e -> addIngredient());
		cancelButton.addActionListener(e -> dispose());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 1;
		gbc.insets = new Insets(8, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;

		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.NONE;
		controlsPanel.add(addButton, gbc);

		gbc.gridx = 1;
		controlsPanel.add(cancelButton, gbc);
	}

	// ========================
	// Search Fields
	// ========================

	public void setUpSearchSelect() {
		searchBar = new JTextField();

		TextChangeListener.attach(searchBar, () -> loadListFiltered());

		JLabel searchLabel = new JLabel("Keyword Search");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.insets = new Insets(8, 10, 2, 10);
		gbc.anchor = GridBagConstraints.WEST;

		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		controlsPanel.add(searchLabel, gbc);

		gbc.gridy = 1;
		gbc.insets = new Insets(0, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.5;
		controlsPanel.add(searchBar, gbc);
	}

	public void setUpSupplierSearchSelect() {
		supplierSearchBar = new JTextField();

		TextChangeListener.attach(supplierSearchBar, () -> loadListFiltered());

		JLabel supplierSearchLabel = new JLabel("Supplier Search");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.insets = new Insets(8, 10, 2, 10);
		gbc.anchor = GridBagConstraints.WEST;

		gbc.gridx = 3;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		controlsPanel.add(supplierSearchLabel, gbc);

		gbc.gridy = 1;
		gbc.insets = new Insets(0, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.5;
		controlsPanel.add(supplierSearchBar, gbc);
	}

	// ========================
	// Actions
	// ========================

	/**
	 * Validates selection and grams input, adds the ingredient to the recipe, then
	 * closes the dialog.
	 */
	public void addIngredient() {
		int selectedRow = ingredientTable.getSelectedRow();
		if (selectedRow < 0)
			return;

		int id = (int) model.getValueAt(selectedRow, 0);
		Ingredient selected = RecipeHandler.ingredientIDMap.get(id);
		if (selected == null)
			return;

		try {
			float grams = Float.parseFloat(gramsField.getText());
			if (grams <= 0) {
				throw new NumberFormatException();
			}
			recipe.addIngredient(selected, grams);
			dispose();
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Enter a valid number");
		}
	}
}
