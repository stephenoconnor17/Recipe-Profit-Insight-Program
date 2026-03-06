package myGUI;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import filep.FileHandler;
import filep.RecipeHandler;
import filep.VATHandler;

/**
 * Editor window for the four configurable VAT rates. Values are entered as
 * percentages (0-100) and stored as decimals (0-1).
 */
public class VATFrame extends JFrame {

	// ========================
	// Fields
	// ========================

	JTextField vat1Field;
	JTextField vat2Field;
	JTextField vat3Field;
	JTextField vat4Field;

	JButton saveButton;

	FileHandler fh;
	MyPanel parent;

	private JPanel mainPanel;

	// ========================
	// Constructor
	// ========================

	public VATFrame(FileHandler fh, MyPanel parent) {
		this.fh = fh;
		this.parent = parent;

		setTitle("VAT Editor");
		setSize(300, 280);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		util.MinSizeEnforcer.apply(this, 300, 280);

		mainPanel = new JPanel(new GridBagLayout());
		add(mainPanel, BorderLayout.CENTER);

		setUpFields();
		setUpSaveButton();
		loadVats();

		setVisible(true);
	}

	// ========================
	// UI Setup
	// ========================

	private void setUpFields() {
		vat1Field = new JTextField();
		vat2Field = new JTextField();
		vat3Field = new JTextField();
		vat4Field = new JTextField();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;

		addLabelAndField("VAT 1 (%)", vat1Field, 0, gbc);
		addLabelAndField("VAT 2 (%)", vat2Field, 2, gbc);
		addLabelAndField("VAT 3 (%)", vat3Field, 4, gbc);
		addLabelAndField("VAT 4 (%)", vat4Field, 6, gbc);
	}

	/**
	 * Helper to add a label + text field pair to the main panel at the given grid
	 * row.
	 */
	private void addLabelAndField(String labelText, JTextField field, int gridy, GridBagConstraints gbc) {
		JLabel label = new JLabel(labelText);

		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(8, 10, 2, 10);
		mainPanel.add(label, gbc);

		gbc.gridy = gridy + 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 6, 10);
		mainPanel.add(field, gbc);
	}

	private void setUpSaveButton() {
		saveButton = new JButton("Save");
		saveButton.setPreferredSize(new Dimension(120, 40));
		saveButton.addActionListener(e -> saveVats());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.CENTER;
		mainPanel.add(saveButton, gbc);
	}

	// ========================
	// Load & Save
	// ========================

	/** Populates text fields with current VAT rates (displayed as percentages). */
	private void loadVats() {
		vat1Field.setText(String.format("%.4f", VATHandler.vats[0] * 100));
		vat2Field.setText(String.format("%.4f", VATHandler.vats[1] * 100));
		vat3Field.setText(String.format("%.4f", VATHandler.vats[2] * 100));
		vat4Field.setText(String.format("%.4f", VATHandler.vats[3] * 100));
	}

	/**
	 * Parses fields, validates range, saves to VATHandler and file, then refreshes
	 * parent.
	 */
	private void saveVats() {
		try {
			float v1 = Float.parseFloat(vat1Field.getText()) / 100f;
			float v2 = Float.parseFloat(vat2Field.getText()) / 100f;
			float v3 = Float.parseFloat(vat3Field.getText()) / 100f;
			float v4 = Float.parseFloat(vat4Field.getText()) / 100f;

			if (v1 < 0 || v1 > 1 || v2 < 0 || v2 > 1 || v3 < 0 || v3 > 1 || v4 < 0 || v4 > 1) {
				JOptionPane.showMessageDialog(this, "VAT values must be between 0 and 100.");
				return;
			}

			VATHandler.vats[0] = v1;
			VATHandler.vats[1] = v2;
			VATHandler.vats[2] = v3;
			VATHandler.vats[3] = v4;

			fh.writeVATFile();
			RecipeHandler.updateIngredients();
			RecipeHandler.updateRecipes();
			parent.fillDataModel();

			JOptionPane.showMessageDialog(this, "VATs saved successfully.");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Invalid input. Enter numeric values between 0 and 100.");
		}
	}
}
