package myGUI;

import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
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

	public AddIngredientDialog(RecipeFrame parent, Recipe recipe) {
		super(parent, "Add Ingredient", true);
		this.recipe = recipe;

		this.setSize(1000, 400);
		this.setLocationRelativeTo(parent);
		this.setLayout(null);

		setUpList();
		setUpFields();
		setUpButtons();
		setUpSearchSelect();
		setUpSupplierSearchSelect();

		this.add(jsp);
		this.setVisible(true);
	}

	public void setUpList() {
		model = new DefaultListModel<Ingredient>();

		ingredientList = new JList<>(model);
		ingredientList.setBounds(10, 10, 960, 200);
		ingredientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		loadList();

		ingredientList.setFixedCellHeight(30);
		ingredientList.setFont(new Font("SansSerif", Font.PLAIN, 12));
		
		jsp = new JScrollPane(ingredientList);
		jsp.setBounds(10, 10, 960, 200);
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
		JLabel gramsLabel = new JLabel("Grams used: ");
		gramsLabel.setBounds(10, 220, 100, 25);
		this.add(gramsLabel);

		gramsField = new JTextField();
		gramsField.setBounds(120, 220, 100, 25);
		this.add(gramsField);
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

		addButton.setBounds(20, 270, 100, 40);
		cancelButton.setBounds(120, 270, 100, 40);

		addButton.addActionListener(e -> addIngredient());
		cancelButton.addActionListener(e -> dispose());

		this.add(addButton);
		this.add(cancelButton);
	}
	
	public void setUpSearchSelect() {
    	searchBar = new JTextField();
    	searchBar.setBounds(240,220,200,30);
    	
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
    	searchLabel.setBounds(240,210,200,115);
    	
    	this.add(searchBar);
    	this.add(searchLabel);
    }
    
    public void setUpSupplierSearchSelect() {
    	supplierSearchBar = new JTextField();
    	supplierSearchBar.setBounds(460,220,200,30);
    	
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
    	supplierSearchLabel.setBounds(460,210,200,115);
    	
    	this.add(supplierSearchBar);
    	this.add(supplierSearchLabel);
    }
}
