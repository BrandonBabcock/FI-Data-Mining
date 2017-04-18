package util;

import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;

/**
 * Utility class to create JavaFX dialogs
 */
public class DialogsUtil {

	/**
	 * Private constructor to avoid making instances of this utility class
	 */
	private DialogsUtil() {

	}

	/**
	 * Creates a JavaFX error dialog
	 * 
	 * @param errorHeader
	 *            the error dialog's header text
	 * @param errorContent
	 *            the error dialog's content text
	 * @return the created error dialog
	 */
	public static Alert createErrorDialog(String errorHeader, String errorContent) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(errorHeader);
		alert.setContentText(errorContent);

		return alert;
	}

	/**
	 * Creates a JavaFX confirmation dialog
	 * 
	 * @param confirmationHeader
	 *            the confirmation dialog's header text
	 * @param confirmationContent
	 *            the confirmation dialog's content text
	 * @return the created confirmation dialog
	 */
	public static Alert createConfirmationDialog(String confirmationHeader, String confirmationContent) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText(confirmationHeader);
		alert.setContentText(confirmationContent);

		return alert;
	}

	/**
	 * Creates a JavaFX choice dialog
	 * 
	 * @param choiceHeader
	 *            the choice dialog's header text
	 * @param coiceContent
	 *            the choice dialog's content text
	 * @param choices
	 *            the list of choices to choose from
	 * @return
	 */
	public static Dialog<?> createChoiceDialog(String choiceHeader, String coiceContent, List<?> choices) {
		ChoiceDialog<?> dialog = new ChoiceDialog<>(choices.get(0), choices);
		dialog.setHeaderText(choiceHeader);
		dialog.setContentText(coiceContent);

		return dialog;
	}

}
