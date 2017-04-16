package util;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;

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
	 *            the error's header
	 * @param errorContent
	 *            the error's content
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
	 *            the confirmation's header
	 * @param confirmationContent
	 *            the confirmation's content
	 * @return the created confirmation dialog
	 */
	public static Alert createConfirmationDialog(String confirmationHeader, String confirmationContent) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText(confirmationHeader);
		alert.setContentText(confirmationContent);

		return alert;
	}

	public static ChoiceDialog<String> createStringChoiceDialog(String choiceHeader, String coiceContent) {
		List<String> choices = new ArrayList<String>();
		choices.add("Apriori");
		choices.add("Filtered Associator");

		ChoiceDialog<String> dialog = new ChoiceDialog<String>("Apriori", choices);
		dialog.setHeaderText(choiceHeader);
		dialog.setContentText(coiceContent);

		return dialog;
	}

}
