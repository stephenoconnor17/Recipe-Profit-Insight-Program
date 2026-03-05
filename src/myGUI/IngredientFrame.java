package myGUI;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.io.IOException;

import filep.Ingredient;
import filep.IngredientSortType;
import filep.RecipeHandler;
import filep.VATHandler;
import filep.FileHandler;
import util.ComboBoxUtil;
import util.TextChangeListener;

public class IngredientFrame extends JFrame {

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

	private void reloadComboBox() {
		isUpdating = true;
		ingredientSelect.removeAllItems();
		ingredientSelect.addItem("New Ingredient"); // New ingredient

		int j = 1;
		for (Ingredient i : RecipeHandler.ingredients) {
			ingredientSelect.addItem(j + ". " + i.getName());
			j++;
		}
		isUpdating = false;
	}

	public void selectByName(String name) {
		ComboBoxUtil.selectByName(ingredientSelect, name, "New Ingredient");
	}

	public void reloadComboBoxFiltered() {
		isUpdating = true;
		ingredientSelect.removeAllItems();
		String nameInput = searchBar.getText().toLowerCase().trim();
		String supplierInput = supplierSearchBar.getText().toLowerCase().trim();

		ingredientSelect.addItem("New Ingredient"); // New ingredient

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

		// VAT buttons
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

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(6, 10, 2, 10);

		// Row 1: Name
		gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
		leftPanel.add(nameLabel, gbc);

		gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 6, 10);
		leftPanel.add(nameField, gbc);

		// Row 2: Supplier
		gbc.insets = new Insets(6, 10, 2, 10);
		gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
		leftPanel.add(supplierLabel, gbc);

		gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 6, 10);
		leftPanel.add(supplierField, gbc);

		// Row 3: Cost Before VAT
		gbc.insets = new Insets(6, 10, 2, 10);
		gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
		leftPanel.add(costLabel, gbc);

		gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 6, 10);
		leftPanel.add(costField, gbc);

		// Row 4: Grams Bought
		gbc.insets = new Insets(6, 10, 2, 10);
		gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
		leftPanel.add(gramsLabel, gbc);

		gbc.gridx = 0; gbc.gridy = 8; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 6, 10);
		leftPanel.add(gramsField, gbc);

		// Row 5: VAT Selection
		gbc.insets = new Insets(6, 10, 2, 10);
		gbc.gridx = 0; gbc.gridy = 9; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
		leftPanel.add(vatLabel, gbc);

		JPanel vatRow = new JPanel(new GridBagLayout());
		GridBagConstraints v = new GridBagConstraints();
		v.gridy = 0;
		v.insets = new Insets(0, 0, 0, 4);
		v.gridx = 0; vatRow.add(vat1, v);
		v.gridx = 1; vatRow.add(vat2, v);
		v.gridx = 2; vatRow.add(vat3, v);
		v.gridx = 3; v.insets = new Insets(0, 0, 0, 0); vatRow.add(vat4, v);

		gbc.gridx = 0; gbc.gridy = 10; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 6, 10);
		leftPanel.add(vatRow, gbc);

		// Row 6: Cost After VAT
		gbc.insets = new Insets(6, 10, 2, 10);
		gbc.gridx = 0; gbc.gridy = 11; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
		leftPanel.add(costAfterVatLabel, gbc);

		gbc.gridx = 0; gbc.gridy = 12; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 10, 10);
		leftPanel.add(costAfterVatField, gbc);
	}

	private void selectVat(int selection) {
		vatSelect = selection;
		highlightVatButton(selection);
		updateCostAfterVatField();
	}

	private void highlightVatButton(int selection) {
		vat1.setBackground(selection == 1 ? Color.green : null);
		vat2.setBackground(selection == 2 ? Color.green : null);
		vat3.setBackground(selection == 3 ? Color.green : null);
		vat4.setBackground(selection == 4 ? Color.green : null);
	}

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
				e1.printStackTrace();
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
	}

	public Ingredient grabIngredient() {
		String select = (String) ingredientSelect.getSelectedItem();
		if (select == null || select.equals("New Ingredient")) {
			return null;
		}
		String selection = ComboBoxUtil.stripPrefix(select);
		return RecipeHandler.ingredientByName.get(selection);
	}

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

	private void saveIngredient() {
		try {
			Ingredient selected = grabIngredient();

			String name = nameField.getText();
			String supplier = supplierField.getText();
			float cost = Float.parseFloat(costField.getText());
			float grams = Float.parseFloat(gramsField.getText());

			if (selected == null) {
				Ingredient i = new Ingredient(name, supplier, cost, grams, Ingredient.NEW_ID_SENTINEL, vatSelect);
				if (RecipeHandler.verifyNoIngredientCopy(i)) {
					RecipeHandler.addIngredient(i);

					fh.writeIngredients();
					reloadComboBox();

					updateParent();

					selectByName(name);
				} else {
						JOptionPane.showMessageDialog(this, "Error writing Ingredient");
				}
			} else {
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
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Invalid input, Make sure all Fields inputted correctly.");
		}
	}

	private void deleteIngredient() throws IOException {
		Ingredient selected = grabIngredient();
		if (selected == null) {
			JOptionPane.showMessageDialog(this, "No ingredient selected");
			return;
		}

		int result = JOptionPane.showConfirmDialog(this, "Delete this ingredient?", "Confirm",
				JOptionPane.YES_NO_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			RecipeHandler.ingredients.remove(selected);
			RecipeHandler.ingredientIDMap.remove(selected.getID());
			RecipeHandler.ingredientByName.remove(selected.getName());

			reloadComboBox();
			ingredientSelect.setSelectedIndex(0);
			fh.writeIngredients();
			updateParent();
		}
	}

	private void updateParent() {
		RecipeHandler.updateRecipes();
		parent.fillDataModel();
	}

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
}