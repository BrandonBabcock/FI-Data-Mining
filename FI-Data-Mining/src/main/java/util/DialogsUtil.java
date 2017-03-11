package util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Utility class to create JavaFX dialogs
 */
public class DialogsUtil {

	/**
	 * Creates a JavaFX error dialog
	 * 
	 * @param errorHeader
	 *            the error's header
	 * @param errorContent
	 *            the error's content
	 * @return the error dialog
	 */
	public static Alert createErrorDialog(String errorHeader, String errorContent) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
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
	 * @return the confirmation dialog
	 */
	public static Alert createConfirmationDialog(String confirmationHeader, String confirmationContent) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText(confirmationHeader);
		alert.setContentText(confirmationContent);

		return alert;
	}

}
