package myGUI;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import filep.Ingredient;
import filep.Recipe;
import filep.RecipeHandler;

public class AddIngredientDialog extends JDialog {

	Recipe recipe;

	JList<Ingredient> ingredientList;
	DefaultListModel<Ingredient> model;
	JScrollPane jsp;

	JTextField searchBar;
	JTextField supplierSearchBar;

	JTextField gramsField;
	JButton addButton;
	JButton cancelButton;

	// --- NEW: layout panels (layout-only change)
	private JPanel listPanel;
	private JPanel controlsPanel;

	public AddIngredientDialog(RecipeFrame parent, Recipe recipe) {
		super(parent, "Add Ingredient", true);
		this.recipe = recipe;

		this.setSize(1000, 400);
		this.setLocationRelativeTo(parent);
		// this.setLayout(null);
		// --- NEW: normal layout manager
		this.setLayout(new BorderLayout());

		setUpList();
		setUpFields();
		setUpButtons();
		setUpSearchSelect();
		setUpSupplierSearchSelect();

		// this.add(jsp);
		// --- NEW: add panels instead (layout-only change)
		this.add(listPanel, BorderLayout.CENTER);
		this.add(controlsPanel, BorderLayout.SOUTH);

		this.setVisible(true);
	}

	public void setUpList() {
		model = new DefaultListModel<Ingredient>();

		ingredientList = new JList<>(model);
		// ingredientList.setBounds(10, 10, 960, 200);
		ingredientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		loadList();

		ingredientList.setFixedCellHeight(30);
		ingredientList.setFont(new Font("SansSerif", Font.PLAIN, 12));

		jsp = new JScrollPane(ingredientList);
		// jsp.setBounds(10, 10, 960, 200);

		// --- NEW: list panel (layout-only change)
		listPanel = new JPanel(new BorderLayout());
		listPanel.add(jsp, BorderLayout.CENTER);
	}

	public void loadListFiltered() {
		model.clear();
		String nameInput = searchBar.getText().toLowerCase().trim();
		String supplierInput = supplierSearchBar.getText().toLowerCase().trim();

		for (Ingredient i : RecipeHandler.ingredients) {
			boolean matchesName = nameInput.isEmpty() || i.getName().toLowerCase().contains(nameInput);
			boolean matchesSupplier = supplierInput.isEmpty() || i.getSupplierName().toLowerCase().contains(supplierInput);

			if (matchesName && matchesSupplier) {
				model.addElement(i);
			}
		}
	}

	public void loadList() {
		model.clear();

		for (Ingredient i : RecipeHandler.ingredients) {
			model.addElement(i);
		}
	}

	public void setUpFields() {
		// --- NEW: controls panel (layout-only change)
		if (controlsPanel == null) {
			controlsPanel = new JPanel(new GridBagLayout());
		}

		JLabel gramsLabel = new JLabel("Grams used: ");
		// gramsLabel.setBounds(10, 220, 100, 25);
		// this.add(gramsLabel);

		gramsField = new JTextField();
		// gramsField.setBounds(120, 220, 100, 25);
		// this.add(gramsField);

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

	public void addIngredient() {
		Ingredient selected = ingredientList.getSelectedValue();
		if (selected == null) {
			return;
		}

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

	public void setUpButtons() {
		addButton = new JButton("Add");
		cancelButton = new JButton("Cancel");

		// addButton.setBounds(20, 270, 100, 40);
		// cancelButton.setBounds(120, 270, 100, 40);

		addButton.addActionListener(e -> addIngredient());
		cancelButton.addActionListener(e -> dispose());

		// this.add(addButton);
		// this.add(cancelButton);

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

	public void setUpSearchSelect() {
		searchBar = new JTextField();
		// searchBar.setBounds(240,220,200,30);

		searchBar.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void insertUpdate(DocumentEvent e) {
				loadListFiltered();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				loadListFiltered();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				loadListFiltered();
			}
		});

		JLabel searchLabel = new JLabel("Keyword Search");
		// searchLabel.setBounds(240,210,200,115);

		// this.add(searchBar);
		// this.add(searchLabel);

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
		// supplierSearchBar.setBounds(460,220,200,30);

		supplierSearchBar.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				loadListFiltered();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				loadListFiltered();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				loadListFiltered();
			}
		});

		JLabel supplierSearchLabel = new JLabel("Supplier Search");
		// supplierSearchLabel.setBounds(460,210,200,115);

		// this.add(supplierSearchBar);
		// this.add(supplierSearchLabel);

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
}