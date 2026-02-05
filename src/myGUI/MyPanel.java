package myGUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import filep.*;

public class MyPanel extends JPanel{
	
	DefaultTableModel dtm;
	JTable jt;
	JScrollPane jsp;
	FileHandler fh;
	
	//
	JPanel buttonPanel;
	JButton recipeButton;
	JButton ingredientButton;
	//
	
	String[] tableNames = {"Recipe","Overall Cost","Sale","Difference"};
	Object[] rowtest;
	
	MyPanel(MyFrame f){
		this.setLayout(null);
		Dimension screenSize = f.getSize();
		
		this.setSize(screenSize);
		this.setBackground(new Color(0,255,255));
		
		//recipe table and filehandler init
		fh = new FileHandler();
		
		setUpDMandTable();
		fh.loadIngredients();
		fh.loadRecipes();
		
		jsp = new JScrollPane(jt);
		
		jsp.setSize(screenSize.width, (screenSize.height / 4 * 3));
		
		this.add(jsp);
		//----
		setUpButtons(this);
		
		//Buttons panel init
		
		this.add(buttonPanel);
		this.setVisible(true);
	}
	
	public void setUpButtons(MyPanel mp) {
		buttonPanel = new JPanel();
		buttonPanel.setLayout(null);
		buttonPanel.setSize(mp.getSize().width, mp.getSize().height / 4);
		buttonPanel.setLocation(0, ((mp.getSize().height / 4) * 3));
		buttonPanel.setBackground(Color.black);
		
		recipeButton = new JButton("Edit Recipes");
		ingredientButton = new JButton("Edit Ingredients");
		
		recipeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				 new RecipeFrame(fh);
			}
		});
		
		ingredientButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				 JOptionPane.showMessageDialog(mp, "Button clicked!");
			}
		});
		
		recipeButton.setBounds(0,0,200,50);
		ingredientButton.setBounds(0,50,200,50);
		
		buttonPanel.add(ingredientButton);
		buttonPanel.add(recipeButton);
		buttonPanel.setVisible(true);
	}
	
	//SET UP DefaultTableModel and JTable
	public void setUpDMandTable() {

		dtm = new DefaultTableModel(tableNames, 0) {
			@Override
		    public boolean isCellEditable(int row, int column) {
		        return false; // all cells are non-editable
		    }

		};
		/*
		Recipe testr = new Recipe("name", 67f);
		testr.addIngredient(new Ingredient("butter", "butterco", 4, 100, 18), 100);
		testr.addIngredient(new Ingredient("ham", "butterco", 8, 100, 18), 100);
		
		//ADD ROWS OF INFO
		dtm.addRow(testr.getDisplayArr());
		*/
		jt = new JTable(dtm);
		
		//THIS BIT IS WHAT LETS US DRAW THE DIFFERENCE AS RED OR GREEN
		jt.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				// TODO Auto-generated method stub
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				
				if(value != null) {
					
				     try {
			                // Remove the "+" sign if present, then parse as double
			                double diff = Double.parseDouble(value.toString().replace("+", "").replace("$", ""));
			                
			                if (diff > 0) {
			                    c.setBackground(Color.GREEN);   // positive -> green
			                    c.setForeground(Color.BLACK);
			                } else if (diff < 0) {
			                    c.setBackground(Color.RED);     // negative -> red
			                    c.setForeground(Color.WHITE);
			                } else {
			                    c.setBackground(Color.YELLOW);  // zero -> yellow
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
		//---END OF COLOR
	}
}
