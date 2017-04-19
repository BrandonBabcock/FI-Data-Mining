package util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
		alert.setHeaderText(confirmationHeader);
		alert.setContentText(confirmationContent);

		return alert;
	}

}