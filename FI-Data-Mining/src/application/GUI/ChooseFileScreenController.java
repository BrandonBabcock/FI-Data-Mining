package application.GUI;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class ChooseFileScreenController {

	private File selectedFile;
	private String fileName;

	@FXML
	private Button nextButton;

	@FXML
	private TextField fileTextField;

	@FXML
	private Button chooseFileButton;

	/**
	 * Handles an event performed on the chooseFileButton. Allows the user to
	 * select a file.
	 * 
	 * @param event
	 *            the event performed on the chooseFileButton
	 */
	@FXML
	void chooseFile(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null && isValidFileType(selectedFile)) {
			fileName = selectedFile.getAbsolutePath();
			fileTextField.setText(fileName);
			nextButton.setDisable(false);
		}
	}

	/**
	 * Checks if the selected file is of a valid file type
	 * 
	 * @param file
	 *            file to check the type of
	 * @return true if the file type is supported and false otherwise
	 */
	public boolean isValidFileType(File file) {
		String fileName = file.getName().toLowerCase();

		if (fileName.contains(".csv") || fileName.contains(".xml")) {
			return true;
		}

		return false;
	}

	// TODO: Functionality to disable/enable the Next button when manually
	// typing a file name into the fileTextField needs to be added.

}
