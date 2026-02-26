package myGUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.io.IOException;

import filep.Ingredient;
import filep.IngredientSortType;
import filep.RecipeHandler;
import filep.FileHandler;

public class IngredientFrame extends JFrame {

	JComboBox<String> ingredientSelect;
	JComboBox<IngredientSortType> sortSelect;

	JTextField nameField;
	JTextField supplierField;
	JTextField costField;
	JTextField gramsField;

	JTextField searchBar;
	JTextField supplierSearchBar;

	JButton saveButton;
	JButton deleteButton;

	FileHandler fh;
	MyPanel parent;
	boolean isUpdating = false;

	// --- NEW: layout panels (layout-only change)
	private JPanel mainPanel;
	private JPanel leftPanel;
	private JPanel rightPanel;

	public IngredientFrame(FileHandler fh, MyPanel mf) {
		this.fh = fh;
		this.parent = mf;

		setTitle("Ingredient Editor");
		setSize(600, 400);
		// setLayout(null);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);

		// --- NEW: create panels (layout-only change)
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
		// ingredientSelect.setBounds(10, 10, 300, 35);
		ingredientSelect.setPreferredSize(new Dimension(300, 35));

		reloadComboBox();

		ingredientSelect.addActionListener(e -> {
			if (!isUpdating) {
				loadIngredient();
			}
		});

		// add(ingredientSelect);
		// --- NEW: add to leftPanel using layout (layout-only change)
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
		for (int i = 0; i < ingredientSelect.getItemCount(); i++) {
			String item = ingredientSelect.getItemAt(i);
			if (item.equals("New Ingredient"))
				continue;
			String stripped = item.substring(item.indexOf(".") + 1).trim();
			if (stripped.equals(name)) {
				ingredientSelect.setSelectedItem(item);
				return;
			}
		}
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
		JLabel costLabel = new JLabel("Total Cost");
		JLabel gramsLabel = new JLabel("Grams Bought");

		nameField = new JTextField();
		supplierField = new JTextField();
		costField = new JTextField();
		gramsField = new JTextField();

		// nameLabel.setBounds(10, 60, 120, 20);
		// nameField.setBounds(10, 80, 200, 30);
		// supplierLabel.setBounds(10, 115, 120, 20);
		// supplierField.setBounds(10, 135, 200, 30);
		// costLabel.setBounds(10, 170, 120, 20);
		// costField.setBounds(10, 190, 200, 30);
		// gramsLabel.setBounds(10, 225, 120, 20);
		// gramsField.setBounds(10, 245, 200, 30);

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

		// Row 3: Total Cost
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
		gbc.insets = new Insets(0, 10, 10, 10);
		leftPanel.add(gramsField, gbc);
	}

	private void setUpButtons() {
		saveButton = new JButton("Save");
		deleteButton = new JButton("Delete");

		// saveButton.setBounds(225, 80, 120, 40);
		// deleteButton.setBounds(225, 140, 120, 40);
		saveButton.setPreferredSize(new Dimension(120, 40));
		deleteButton.setPreferredSize(new Dimension(120, 40));

		saveButton.addActionListener(e -> saveIngredient());
		deleteButton.addActionListener(e -> {
			try {
				deleteIngredient();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		// add(saveButton);
		// add(deleteButton);

		// --- NEW: place beside fields, similar to your old position (layout-only change)
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
		if (select == null)
			return null;
		if (select.equals("New Ingredient"))
			return null;
		String selection = select.substring(select.indexOf(".") + 1).trim();
		Ingredient i = RecipeHandler.ingredientByName.get(selection);

		return i;
	}

	private void loadIngredient() {
		Ingredient i = grabIngredient();

		if (i == null) {
			nameField.setText("");
			supplierField.setText("");
			costField.setText("");
			gramsField.setText("");
		} else {
			nameField.setText(i.getName());
			supplierField.setText(i.getSupplierName());
			costField.setText(String.valueOf(i.getCostPer1g() * i.getGrams()));
			gramsField.setText(String.valueOf(i.getGrams()));
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
				Ingredient i = new Ingredient(name, supplier, cost, grams, Ingredient.NEW_ID_SENTINEL, 0);
				if (RecipeHandler.verifyNoIngredientCopy(i)) {
					RecipeHandler.addIngredient(i);

					fh.writeIngredients();
					reloadComboBox();

					updateParent();

					selectByName(name);
				} else {
					// CANNOT SAVE DUPLICATE INGREDIENT RESPONSE
					JOptionPane.showMessageDialog(this, "Error writing Ingredient");
				}
			} else {
				RecipeHandler.ingredientByName.remove(selected.getName());

				selected.setName(name);
				selected.setSupplierName(supplier);
				selected.setCost(cost);
				selected.setGrams(grams);
				RecipeHandler.ingredientByName.put(selected.getName(), selected);
				fh.writeIngredients();
				reloadComboBox();

				updateParent();

				selectByName(name);
			}
		} catch (Exception e) {
			//e.printStackTrace();
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

			// ingredientSelect.setSelectedIndex(0); // select “New Ingredient”

			reloadComboBox();
			ingredientSelect.setSelectedIndex(0); // select “New Ingredient”
			fh.writeIngredients();// save ingredients after deleting.
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

		// sortSelect.setSize(200, 50);
		// sortSelect.setLocation(350, 10);
		sortSelect.setPreferredSize(new Dimension(200, 35));

		JLabel sortLabel = new JLabel("Sort Type");
		// sortLabel.setBounds(400, 60, 200, 20);

		// this.add(sortLabel);
		// this.add(sortSelect);

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
		// searchBar.setBounds(350, 90, 200, 30);

		searchBar.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				reloadComboBoxFiltered();
				loadIngredient();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				reloadComboBoxFiltered();
				loadIngredient();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				reloadComboBoxFiltered();
				loadIngredient();
			}
		});

		JLabel searchLabel = new JLabel("Keyword Search");
		// searchLabel.setBounds(400, 70, 200, 115);

		// this.add(searchBar);
		// this.add(searchLabel);

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
		// supplierSearchBar.setBounds(350, 150, 200, 30);

		supplierSearchBar.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				reloadComboBoxFiltered();
				loadIngredient();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				reloadComboBoxFiltered();
				loadIngredient();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				reloadComboBoxFiltered();
				loadIngredient();
			}
		});

		JLabel supplierSearchLabel = new JLabel("Supplier Search");
		// supplierSearchLabel.setBounds(400, 130, 200, 115);

		// this.add(supplierSearchBar);
		// this.add(supplierSearchLabel);

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