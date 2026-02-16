package myGUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultEditorKit;

import filep.*;

public class MyPanel extends JPanel {

	DefaultTableModel dtm;
	JTable jt;
	JScrollPane jsp;
	FileHandler fh;
	JTextField searchBar;
	
	JPanel buttonPanel;
	JButton recipeButton;
	JButton ingredientButton;
	JComboBox<RecipeSortType> sortSelect;

	String[] tableNames = { "Recipe", "Overall Cost", "Sale", "Difference", "Food Cost Diff" , "Profit Margin"}; // adding here adds
																								// another column of
																								// data., which is display from the recipe display arr
	Object[] rowtest;																			// only n columns are shown from display arr for n amount of tableName elements.

	MyPanel(MyFrame f) {
		this.setLayout(null);
		Dimension screenSize = f.getSize();

		this.setSize(screenSize);
		this.setBackground(new Color(0, 255, 255));

		// recipe table and filehandler init
		fh = new FileHandler();

		// setUpDMandTable();
		fh.loadIngredients();
		fh.loadRecipes();
		setUpDMandTable();

		jsp = new JScrollPane(jt);

		jsp.setSize(screenSize.width, (screenSize.height / 4 * 3));

		this.add(jsp);
		// ----
		setUpButtons(this);
		setUpSortSelect();
		setUpSearchSelect();

		// Buttons panel init

		this.add(buttonPanel);
		this.setVisible(true);
	}
	
	public void setUpSortSelect() {
		sortSelect = new JComboBox<RecipeSortType>();
		RecipeSortType[] recipeSorts = RecipeSortType.values();
		for(int i = 0; i < recipeSorts.length; i++) {
			sortSelect.addItem(recipeSorts[i]);   
		}
		
		sortSelect.addActionListener(e -> {
			RecipeSortType rst = (RecipeSortType)sortSelect.getSelectedItem();
			if(rst != null) {
				RecipeHandler.sortRecipes(rst);
				String input = (String) searchBar.getText();
	    		if(!input.trim().isEmpty()) {
	    			fillDataModelSearch(input);
	    		}else {
	    			fillDataModel();
	    		}
			}
		});
		
		sortSelect.setSize(200,50);
		sortSelect.setLocation(400,0);
		
		buttonPanel.add(sortSelect);
		
		
	}
	public Recipe grabToPass() {
		int toPass = jt.getSelectedRow();
		if(toPass < 0) {
			return null;
		}
		
		String recipeName = (String) dtm.getValueAt(toPass, 0);
		if(recipeName == null || recipeName.equals(" ")) {
			return null;
		}
		
		
		Recipe r = RecipeHandler.recipeByName.get(recipeName);
		if(r == null) {
			return null;
		}
		
		return r;
	}

	public void setUpButtons(MyPanel mp) {
		buttonPanel = new JPanel();
		buttonPanel.setLayout(null);
		buttonPanel.setSize(mp.getSize().width, mp.getSize().height / 5);
		buttonPanel.setLocation(0, ((mp.getSize().height / 4) * 3));
		buttonPanel.setBackground(Color.lightGray);

		recipeButton = new JButton("Edit Recipes");
		ingredientButton = new JButton("Edit Ingredients");

		recipeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Recipe r = grabToPass();
				new RecipeFrame(fh, mp, r);
			}
		});
		
		

		ingredientButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new IngredientFrame(fh, mp);
			}
		});

		recipeButton.setBounds(0, 0, 200, 50);
		ingredientButton.setBounds(200, 0, 200, 50);

		buttonPanel.add(ingredientButton);
		buttonPanel.add(recipeButton);

		buttonPanel.setVisible(true);
	}

	// SET UP DefaultTableModel and JTable
	public void setUpDMandTable() {

		dtm = new DefaultTableModel(tableNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // all cells are non-editable
			}

		};
		fillDataModel();
		jt = new JTable(dtm);

		// THIS BIT IS WHAT LETS US DRAW THE DIFFERENCE AS RED OR GREEN
		// get last column, set its cell renderer to new renderer, which must implement
		// the method we manipulate to decide draw color;
		jt.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				// TODO Auto-generated method stub
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				if (value != null) {

					try {
						// Remove the "+" sign if present, then parse as double
						double diff = Double.parseDouble(value.toString().replace("+", "").replace("€", ""));

						if (diff > 0) {
							c.setBackground(Color.GREEN); // positive -> green
							c.setForeground(Color.BLACK);
						} else if (diff < 0) {
							c.setBackground(Color.RED); // negative -> red
							c.setForeground(Color.WHITE);
						} else {
							c.setBackground(Color.YELLOW); // zero -> yellow
							c.setForeground(Color.BLACK);
						}
					} catch (NumberFormatException e) {
						c.setBackground(Color.WHITE); // fallback
						c.setForeground(Color.BLACK);
					}
				} else {
					c.setBackground(Color.WHITE);
					c.setForeground(Color.BLACK);
				}

				return c;
			}

		});

		jt.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				// TODO Auto-generated method stub
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				if (value != null) {

					try {
						// Remove the "+" sign if present, then parse as double
						double diff = Double.parseDouble(value.toString().replace("+", "").replace("%", ""));

						if (diff > 0) {
							c.setBackground(Color.GREEN); // positive -> green
							c.setForeground(Color.BLACK);
						} else if (diff < 0) {
							c.setBackground(Color.RED); // negative -> red
							c.setForeground(Color.WHITE);
						} else {
							c.setBackground(Color.YELLOW); // zero -> yellow
							c.setForeground(Color.BLACK);
						}
					} catch (NumberFormatException e) {
						c.setBackground(Color.WHITE); // fallback
						c.setForeground(Color.BLACK);
					}
				} else {
					c.setBackground(Color.WHITE);
					c.setForeground(Color.BLACK);
				}

				return c;
			}

		});
		
		jt.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				// TODO Auto-generated method stub
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				if (value != null) {

					try {
						// Remove the "+" sign if present, then parse as double
						double diff = Double.parseDouble(value.toString().replace("+", "").replace("%", ""));

						if (diff > 0) {
							c.setBackground(Color.GREEN); // positive -> green
							c.setForeground(Color.BLACK);
						} else if (diff < 0) {
							c.setBackground(Color.RED); // negative -> red
							c.setForeground(Color.WHITE);
						} else {
							c.setBackground(Color.YELLOW); // zero -> yellow
							c.setForeground(Color.BLACK);
						}
					} catch (NumberFormatException e) {
						c.setBackground(Color.WHITE); // fallback
						c.setForeground(Color.BLACK);
					}
				} else {
					c.setBackground(Color.WHITE);
					c.setForeground(Color.BLACK);
				}

				return c;
			}

		});
		// ---END OF COLOR
	}

	public void fillDataModel() {
		dtm.setRowCount(0);

		for (int i = 0; i < RecipeHandler.recipes.size(); i++) {	
			dtm.addRow(RecipeHandler.recipes.get(i).getDisplayArr());
		}
	}
	
	public void fillDataModelSearch(String s) {
		dtm.setRowCount(0);
		
		for (int i = 0; i < RecipeHandler.recipes.size(); i++) {
			String recipeName = RecipeHandler.recipes.get(i).getName();
			if(recipeName.toLowerCase().contains(s.toLowerCase())) {
				dtm.addRow(RecipeHandler.recipes.get(i).getDisplayArr());
			}
		}
	}
	
	public void setUpSearchSelect() {
    	searchBar = new JTextField();
    	searchBar.setBounds(620,15,200,30);
  
    	searchBar.getDocument().addDocumentListener(new DocumentListener(){
    		
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				String input = (String) searchBar.getText();
	    		if(!input.trim().isEmpty()) {
	    			fillDataModelSearch(input);
	    		}else {
	    			fillDataModel();
	    		}
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				String input = (String) searchBar.getText();
	    		if(!input.trim().isEmpty()) {
	    			fillDataModelSearch(input);
	    		}else {
	    			fillDataModel();
	    		}
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				String input = (String) searchBar.getText();
	    		if(!input.trim().isEmpty()) {
	    			fillDataModelSearch(input);
	    		}else {
	    			fillDataModel();
	    		}
	    	
			}
    	});
    	
    	JLabel searchLabel = new JLabel("Keyword Search");
    	searchLabel.setBounds(650,0,200,115);
    	
    	buttonPanel.add(searchBar);
    	buttonPanel.add(searchLabel);
    }

}
