package util;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Convenience DocumentListener that fires a single Runnable on any text change
 * (insert, remove, or attribute change). Attach to a JTextField via {@link #attach}.
 */
public class TextChangeListener implements DocumentListener {

	private final Runnable action;

	private TextChangeListener(Runnable action) {
		this.action = action;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		action.run();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		action.run();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		action.run();
	}

	/** Attaches a listener to the field's document that runs the given action on every change. */
	public static void attach(JTextField field, Runnable action) {
		field.getDocument().addDocumentListener(new TextChangeListener(action));
	}
}
