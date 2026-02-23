package myGUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.io.IOException;

import filep.Ingredient;
import filep.IngredientSortType;
import filep.RecipeHandler;
import filep.RecipeSortType;
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

    public IngredientFrame(FileHandler fh, MyPanel mf) {
        this.fh = fh;
        this.parent = mf;

        setTitle("Ingredient Editor");
        setSize(600, 400);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

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
        ingredientSelect.setBounds(10, 10, 300, 35);

        reloadComboBox();

        ingredientSelect.addActionListener(e -> {
            if (!isUpdating) {
                loadIngredient();
            }
        });

        add(ingredientSelect);
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
            if (item.equals("New Ingredient")) continue;
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
	        boolean matchesSupplier = supplierInput.isEmpty() || i.getSupplierName().toLowerCase().contains(supplierInput);

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

        nameLabel.setBounds(10, 60, 120, 20);
        nameField.setBounds(10, 80, 200, 30);

        supplierLabel.setBounds(10, 115, 120, 20);
        supplierField.setBounds(10, 135, 200, 30);

        costLabel.setBounds(10, 170, 120, 20);
        costField.setBounds(10, 190, 200, 30);

        gramsLabel.setBounds(10, 225, 120, 20);
        gramsField.setBounds(10, 245, 200, 30);

        add(nameLabel);
        add(nameField);
        add(supplierLabel);
        add(supplierField);
        add(costLabel);
        add(costField);
        add(gramsLabel);
        add(gramsField);
    }

    private void setUpButtons() {
        saveButton = new JButton("Save");
        deleteButton = new JButton("Delete");

        saveButton.setBounds(225, 80, 120, 40);
        deleteButton.setBounds(225, 140, 120, 40);

        saveButton.addActionListener(e -> saveIngredient());
        deleteButton.addActionListener(e -> {
			try {
				deleteIngredient();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

        add(saveButton);
        add(deleteButton);
    }
    
    public Ingredient grabIngredient() {
    	String select = (String) ingredientSelect.getSelectedItem();
    	String selection = select.substring(select.indexOf(".") + 1).trim();
    	Ingredient i = RecipeHandler.ingredientByName.get(selection);
    	
    	return i;
    }

    private void loadIngredient() {
        Ingredient i = grabIngredient();

        isUpdating = true;

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

        isUpdating = false;
    }

    private void saveIngredient() {
        try {
        	Ingredient selected = grabIngredient();
        	
            String name = nameField.getText();
            String supplier = supplierField.getText();
            float cost = Float.parseFloat(costField.getText());
            float grams = Float.parseFloat(gramsField.getText());

            if (selected == null) {
                Ingredient i = new Ingredient(name, supplier, cost, grams, Ingredient.newID);
                if(RecipeHandler.verifyNoIngredientCopy(i)) {
                	RecipeHandler.addIngredient(i);
                	
                }else {
                	//CANNOT SAVE DUPLICATE INGREDIENT RESPONSE
                }
            } else {
            	RecipeHandler.recipeByName.remove(selected.getName());
            	
            	selected.setName(name);
            	selected.setSupplierName(supplier);
            	selected.setCost(cost);
            	selected.setGrams(grams);
            	RecipeHandler.ingredientByName.put(selected.getName(), selected);
            }
            
            fh.writeIngredients();
            reloadComboBox();
            
            updateParent();
            
            selectByName(name);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input");
        }
    }

    private void deleteIngredient() throws IOException {
        Ingredient selected = grabIngredient();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No ingredient selected");
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Delete this ingredient?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            RecipeHandler.ingredients.remove(selected);
            RecipeHandler.ingredientIDMap.remove(selected.getID());
            RecipeHandler.ingredientByName.remove(selected.getName());
            
            //ingredientSelect.setSelectedIndex(0); // select “New Ingredient”
            
            reloadComboBox();
            ingredientSelect.setSelectedIndex(0); // select “New Ingredient”
            fh.writeIngredients();//save ingredients after deleting.
        }
        
        updateParent();
    }
    
    private void updateParent() {
    	RecipeHandler.updateRecipes();
    	parent.fillDataModel();
    }
    
    public void setUpSortSelect() {
		sortSelect = new JComboBox<IngredientSortType>();
		IngredientSortType[] ingredientSorts = IngredientSortType.values();
		for(int i = 0; i < ingredientSorts.length; i++) {
			sortSelect.addItem(ingredientSorts[i]);   
		}
		
		sortSelect.addActionListener(e -> {
			IngredientSortType ist = (IngredientSortType)sortSelect.getSelectedItem();
			if(ist != null) {
				RecipeHandler.sortIngredients(ist);
				
				reloadComboBoxFiltered();
				loadIngredient();
			}
		});
		
		sortSelect.setSize(200,50);
		sortSelect.setLocation(350,10);
		JLabel sortLabel = new JLabel("Sort Type");
		sortLabel.setBounds(400,60,200,20);
		
		this.add(sortLabel);
		this.add(sortSelect);
		
	}
    
    public void setUpSearchSelect() {
    	searchBar = new JTextField();
    	searchBar.setBounds(350,90,200,30);
    	
    	searchBar.getDocument().addDocumentListener(new DocumentListener(){
    		
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
    	searchLabel.setBounds(400,70,200,115);
    	
    	this.add(searchBar);
    	this.add(searchLabel);
    }
    
    public void setUpSupplierSearchSelect() {
    	supplierSearchBar = new JTextField();
    	supplierSearchBar.setBounds(350,150,200,30);
    	
    	supplierSearchBar.getDocument().addDocumentListener(new DocumentListener(){
    		
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
    	supplierSearchLabel.setBounds(400,130,200,115);
    	
    	this.add(supplierSearchBar);
    	this.add(supplierSearchLabel);
    }
}
