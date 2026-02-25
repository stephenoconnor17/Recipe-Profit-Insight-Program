package myGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import filep.*;
import util.CellRenderer;

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

    String[] tableNames = { "Recipe", "Overall Cost", "Sale", "Difference", "Food Cost Diff", "Profit Margin" };

    MyPanel(MyFrame f) {
        setLayout(new BorderLayout());
        setBackground(new Color(0, 255, 255));

        // --- data init
        fh = new FileHandler();
        fh.loadIngredients();
        fh.loadIdFile();
        fh.loadRecipes();

        // --- table
        setUpDMandTable();
        jsp = new JScrollPane(jt);
        add(jsp, BorderLayout.CENTER);

        // --- bottom controls
        setUpControlBar();     // creates buttonPanel + adds components
        add(buttonPanel, BorderLayout.SOUTH);

        // Good practice when building UI dynamically
        revalidate();
        repaint();
    }

    // -----------------------------
    // Bottom bar (buttons + sort + search)
    // -----------------------------
    private void setUpControlBar() {
        buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.lightGray);

        // IMPORTANT: give it a real height. Don't use getHeight() here.
        buttonPanel.setPreferredSize(new Dimension(10, 90));

        recipeButton = new JButton("Edit Recipes");
        ingredientButton = new JButton("Edit Ingredients");

        recipeButton.addActionListener(e -> {
            Recipe r = grabToPass();
            new RecipeFrame(fh, this, r);
        });

        ingredientButton.addActionListener(e -> {
            new IngredientFrame(fh, this);
        });

        // keep similar sizing to your old 200x50 buttons
        Dimension btnSize = new Dimension(200, 50);
        recipeButton.setPreferredSize(btnSize);
        ingredientButton.setPreferredSize(btnSize);

        // sort select
        sortSelect = new JComboBox<>();
        for (RecipeSortType rst : RecipeSortType.values()) {
            sortSelect.addItem(rst);
        }
        sortSelect.setPreferredSize(new Dimension(200, 50));
        sortSelect.addActionListener(e -> {
            RecipeSortType rst = (RecipeSortType) sortSelect.getSelectedItem();
            if (rst != null) {
                RecipeHandler.sortRecipes(rst);
                String input = (searchBar != null) ? searchBar.getText() : "";
                if (!input.trim().isEmpty()) fillDataModelSearch(input);
                else fillDataModel();
            }
        });

        // search label + field
        JLabel searchLabel = new JLabel("Keyword Search");
        searchBar = new JTextField(16); // columns, better than pixel bounds

        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { run(); }
            @Override public void removeUpdate(DocumentEvent e) { run(); }
            @Override public void changedUpdate(DocumentEvent e) { run(); }

            private void run() {
                String input = searchBar.getText();
                if (!input.trim().isEmpty()) fillDataModelSearch(input);
                else fillDataModel();
            }
        });

        // ---- Layout placements (same “left buttons, middle sort, right search”)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Button 1
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        buttonPanel.add(recipeButton, gbc);

        // Button 2
        gbc.gridx = 1;
        buttonPanel.add(ingredientButton, gbc);

        // Sort in the middle
        gbc.gridx = 2;
        gbc.insets = new Insets(8, 16, 8, 16);
        gbc.anchor = GridBagConstraints.CENTER;
        buttonPanel.add(sortSelect, gbc);

        // Spacer that expands to push search to the right
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        buttonPanel.add(Box.createHorizontalStrut(1), gbc);

        // Search label
        gbc.gridx = 4;
        gbc.weightx = 4.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.EAST;
        buttonPanel.add(searchLabel, gbc);

        // Search field
        gbc.gridx = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 12);
        gbc.ipady = 8; // makes it taller similar to your old 30px
        buttonPanel.add(searchBar, gbc);
    }

    // -----------------------------
    // Table + model
    // -----------------------------
    public void setUpDMandTable() {
        dtm = new DefaultTableModel(tableNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        fillDataModel();
        jt = new JTable(dtm);

        jt.getColumnModel().getColumn(3).setCellRenderer(new CellRenderer("€"));
        jt.getColumnModel().getColumn(5).setCellRenderer(new CellRenderer("%"));
        jt.getColumnModel().getColumn(4).setCellRenderer(new CellRenderer("%"));
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
            if (recipeName != null && recipeName.toLowerCase().contains(s.toLowerCase())) {
                dtm.addRow(RecipeHandler.recipes.get(i).getDisplayArr());
            }
        }
    }

    // -----------------------------
    // Selection -> Recipe
    // -----------------------------
    public Recipe grabToPass() {
        int toPass = jt.getSelectedRow();
        if (toPass < 0) return null;

        String recipeName = (String) dtm.getValueAt(toPass, 0);
        if (recipeName == null || recipeName.trim().isEmpty()) return null;

        recipeName = recipeName.substring(recipeName.indexOf(".") + 1).trim();
        return RecipeHandler.recipeByName.get(recipeName);
    }
}