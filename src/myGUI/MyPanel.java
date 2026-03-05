package myGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import filep.*;
import util.CellRenderer;
import util.ComboBoxUtil;
import util.TextChangeListener;

/**
 * Main panel of the application. Displays a recipe summary table at the centre
 * and a control bar at the bottom with navigation buttons, sorting, and search.
 */
public class MyPanel extends JPanel {

    // ========================
    // Fields
    // ========================

    DefaultTableModel dtm;
    JTable jt;
    JScrollPane jsp;
    FileHandler fh;
    JTextField searchBar;

    JPanel buttonPanel;
    JButton recipeButton;
    JButton ingredientButton;
    JButton vatButton;
    JComboBox<RecipeSortType> sortSelect;

    String[] tableNames = { "Recipe", "Overall Cost", "Sale", "Difference", "Food Cost Diff", "Profit Margin" };

    // ========================
    // Constructor
    // ========================

    MyPanel(MyFrame f) {
        setLayout(new BorderLayout());
        setBackground(new Color(0, 255, 255));

        // Load all persistent data
        fh = new FileHandler();
        fh.loadVATFile();
        fh.loadIngredients();
        fh.loadIdFile();
        fh.loadRecipes();

        // Set up the recipe summary table
        setUpDMandTable();
        jsp = new JScrollPane(jt);
        add(jsp, BorderLayout.CENTER);

        // Set up the bottom control bar (buttons, sort, search)
        setUpControlBar();
        add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    // ========================
    // Table Setup & Data
    // ========================

    /** Initialises the table model (non-editable) and JTable with colour-coded renderers. */
    public void setUpDMandTable() {
        dtm = new DefaultTableModel(tableNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        fillDataModel();
        jt = new JTable(dtm);

        // Colour-code the difference, food cost, and profit margin columns
        jt.getColumnModel().getColumn(3).setCellRenderer(new CellRenderer("€"));
        jt.getColumnModel().getColumn(5).setCellRenderer(new CellRenderer("%"));
        jt.getColumnModel().getColumn(4).setCellRenderer(new CellRenderer("%"));
    }

    /** Reloads all recipes into the table model. */
    public void fillDataModel() {
        dtm.setRowCount(0);
        for (int i = 0; i < RecipeHandler.recipes.size(); i++) {
            dtm.addRow(RecipeHandler.recipes.get(i).getDisplayArr());
        }
    }

    /** Reloads only recipes whose name contains the search string (case-insensitive). */
    public void fillDataModelSearch(String s) {
        dtm.setRowCount(0);
        for (int i = 0; i < RecipeHandler.recipes.size(); i++) {
            String recipeName = RecipeHandler.recipes.get(i).getName();
            if (recipeName != null && recipeName.toLowerCase().contains(s.toLowerCase())) {
                dtm.addRow(RecipeHandler.recipes.get(i).getDisplayArr());
            }
        }
    }

    // ========================
    // Selection Helper
    // ========================

    /** Returns the Recipe object for the currently selected table row, or null if none selected. */
    public Recipe grabToPass() {
        int toPass = jt.getSelectedRow();
        if (toPass < 0) return null;

        String recipeName = (String) dtm.getValueAt(toPass, 0);
        if (recipeName == null || recipeName.trim().isEmpty()) return null;

        recipeName = ComboBoxUtil.stripPrefix(recipeName);
        return RecipeHandler.recipeByName.get(recipeName);
    }

    // ========================
    // Bottom Control Bar
    // ========================

    /** Builds the bottom panel with editor buttons, sort dropdown, and keyword search. */
    private void setUpControlBar() {
        buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.lightGray);
        buttonPanel.setPreferredSize(new Dimension(10, 90));

        // --- Navigation buttons ---

        recipeButton = new JButton("Edit Recipes");
        ingredientButton = new JButton("Edit Ingredients");
        vatButton = new JButton("Edit VATs");

        recipeButton.addActionListener(e -> {
            Recipe r = grabToPass();
            new RecipeFrame(fh, this, r);
        });

        ingredientButton.addActionListener(e -> {
            new IngredientFrame(fh, this);
        });

        vatButton.addActionListener(e -> {
            new VATFrame(fh, this);
        });

        Dimension btnSize = new Dimension(200, 50);
        recipeButton.setPreferredSize(btnSize);
        ingredientButton.setPreferredSize(btnSize);
        vatButton.setPreferredSize(btnSize);

        // --- Sort dropdown ---

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

        // --- Search field ---

        JLabel searchLabel = new JLabel("Keyword Search");
        searchBar = new JTextField(16);

        TextChangeListener.attach(searchBar, () -> {
            String input = searchBar.getText();
            if (!input.trim().isEmpty()) fillDataModelSearch(input);
            else fillDataModel();
        });

        // --- GridBag layout for the control bar ---

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        buttonPanel.add(recipeButton, gbc);

        gbc.gridx = 1;
        buttonPanel.add(ingredientButton, gbc);

        gbc.gridx = 2;
        buttonPanel.add(vatButton, gbc);

        // Sort in the middle
        gbc.gridx = 3;
        gbc.insets = new Insets(8, 16, 8, 16);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        buttonPanel.add(sortSelect, gbc);

        // Spacer pushes search to the right
        gbc.gridx = 4;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        buttonPanel.add(Box.createHorizontalStrut(1), gbc);

        // Search label
        gbc.gridx = 5;
        gbc.weightx = 4.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.EAST;
        buttonPanel.add(searchLabel, gbc);

        // Search field
        gbc.gridx = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 12);
        gbc.ipady = 8;
        buttonPanel.add(searchBar, gbc);
    }
}
