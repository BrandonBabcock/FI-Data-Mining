package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import service.DataMinerService;
import util.DialogsUtil;
import weka.associations.AbstractAssociator;

/**
 * Controller for the SelectFiles FXML screen
 */
public class SelectFilesController {

	/* The currently entered file */
	private File selectedFile;

	/* The list of all inputted files */
	private ArrayList<Path> inputtedFiles = new ArrayList<Path>();

	/* Whether or not an ARFF file has been added */
	private boolean isArffFileAdded = false;

	@FXML
	private BorderPane borderPane;

	@FXML
	private Button removeLastFileButton;

	@FXML
	private Button clearFieldButton;

	@FXML
	private Button addFileButton;

	@FXML
	private Button removeAllFilesButton;

	@FXML
	private TextField fileTextField;

	@FXML
	private Button chooseFileButton;

	@FXML
	private Button nextButton;

	/**
	 * Allows the user to select a file from their machine to input
	 * 
	 * @param event
	 *            the action performed on the choose file button
	 */
	@FXML
	public void chooseFile(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		selectedFile = fileChooser.showOpenDialog(borderPane.getScene().getWindow());

		if (selectedFile != null) {
			fileTextField.setText(selectedFile.getAbsolutePath());
		}
	}

	/**
	 * Clears the file input text field
	 * 
	 * @param event
	 *            the action performed on the clear field button
	 */
	@FXML
	public void clearField(ActionEvent event) {
		fileTextField.clear();
	}

	/**
	 * Adds a file to the list of inputted files. If an ARFF file is added,
	 * prompts the user to continue directly to the results screen.
	 * 
	 * @param event
	 *            the action performed on the add file button
	 */
	@FXML
	public void addFile(ActionEvent event) {
		if (isValidFile(fileTextField.getText()) && !isDuplicateFile(fileTextField.getText())) {
			if (isArffFileAdded) {
				promptToContinueToResults();
			} else {
				Path filePath = Paths.get(fileTextField.getText());
				inputtedFiles.add(filePath);
				fileTextField.clear();
			}
		}
	}

	/**
	 * Removes the last inputted file from the list of inputted files
	 * 
	 * @param event
	 *            the action performed on the remove last file button
	 */
	@FXML
	public void removeLastFile(ActionEvent event) {
		if (!inputtedFiles.isEmpty()) {
			inputtedFiles.remove(inputtedFiles.size() - 1);
			Alert alert = DialogsUtil.createConfirmationDialog("File Removed", "The last inputted file was removed.");
			alert.showAndWait();
		} else {
			Alert alert = DialogsUtil.createErrorDialog("No Files To Remove", "There are no files to remove");
			alert.showAndWait();
		}
	}

	/**
	 * Removes all of the inputted files from the list of inputted files
	 * 
	 * @param event
	 *            the action performed on the remove all files button
	 */
	@FXML
	public void removeAllFiles(ActionEvent event) {
		if (!inputtedFiles.isEmpty()) {
			inputtedFiles.clear();
			Alert alert = DialogsUtil.createConfirmationDialog("All Files Removed",
					"All of the inputted files have been removed.");
			alert.showAndWait();
		} else {
			Alert alert = DialogsUtil.createErrorDialog("No Files To Remove", "There are no files to remove");
			alert.showAndWait();
		}
	}

	/**
	 * Takes the user to the next step, which is the SelectWantedAttributes
	 * screen
	 * 
	 * @param event
	 *            the action performed on the next button
	 */
	@FXML
	public void next(ActionEvent event) {
		if (isAbleToContinue()) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SelectWantedAttributes.fxml"));
				BorderPane screen = (BorderPane) loader.load();

				SelectWantedAttributesController controller = loader.getController();
				controller.initData(inputtedFiles);

				nextButton.getScene().setRoot(screen);
			} catch (IOException e) {
				throw new IllegalArgumentException("Error: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Checks if the user can continue to the next step or not
	 * 
	 * @return true if the user has done everything required to continue to the
	 *         next step, false if not
	 */
	private boolean isAbleToContinue() {
		if (!inputtedFiles.isEmpty()) {
			return true;
		} else {
			Alert alert = DialogsUtil.createErrorDialog("No Files Added", "No files have been added.");
			alert.showAndWait();
			return false;
		}
	}

	/**
	 * Checks if the entered file exists on the user's machine and is a valid
	 * type
	 * 
	 * @param filePath
	 *            the entered file
	 * @return true if the entered file is valid, false if not
	 */
	private boolean isValidFile(String filePath) {
		return fileExists(filePath) && isValidFileType(filePath);
	}

	/**
	 * Checks if the entered file exists on the user's machine
	 * 
	 * @param filePath
	 *            the entered file
	 * @return true if the entered file exists on the user's machine, false if
	 *         not
	 */
	private boolean fileExists(String filePath) {
		if (new File(filePath).exists()) {
			return true;
		} else {
			Alert alert = DialogsUtil.createErrorDialog("File Not Found", "The entered file could not be found.");
			alert.showAndWait();
			return false;
		}
	}

	/**
	 * Checks if the entered file is of a supported type
	 * 
	 * @param filePath
	 *            the entered file
	 * @return true if the file is of a supported type, false if not
	 */
	private boolean isValidFileType(String filePath) {
		filePath = filePath.toLowerCase();

		if (filePath.contains(".csv") || filePath.contains(".xml")) {
			return true;
		} else if (filePath.contains(".arff")) {
			isArffFileAdded = true;
			return true;
		} else {
			Alert alert = DialogsUtil.createErrorDialog("Invalid File Type",
					"Only CSV, XML, and ARFF file types are supported.");
			alert.showAndWait();
			return false;
		}
	}

	/**
	 * If an ARFF file is entered, the user is informed that only one ARFF file
	 * can be added and that they would need to continue to the results screen.
	 * Also, the user is asked to pick a data mining algorithm to use if they
	 * agree to continue.
	 */
	private void promptToContinueToResults() {
		Alert alert = DialogsUtil.createConfirmationDialog("ARFF File Entered",
				"Only one ARFF file can be added. Click OK to data mine this file and continue to the results screen.");
		Optional<ButtonType> alertResult = alert.showAndWait();

		if (alertResult.get() == ButtonType.OK) {
			ChoiceDialog<String> dialog = DialogsUtil.createStringChoiceDialog(
					"Which data mining algorithm would you like to use?", "Choose an algorithm:");
			Optional<String> dialogResult = dialog.showAndWait();

			if (dialogResult.isPresent()) {
				continueToResults(dialogResult.get());
			}
		} else {
			fileTextField.clear();
		}
	}

	/**
	 * Goes to the results screen
	 */
	private void continueToResults(String algorithm) {
		DataMinerService dataMiner = new DataMinerService();
		AbstractAssociator associator = dataMiner.findAssociationRules(algorithm, fileTextField.getText());

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Results.fxml"));
			BorderPane screen = (BorderPane) loader.load();

			ResultsController controller = loader.getController();
			controller.initData(associator);

			addFileButton.getScene().setRoot(screen);
		} catch (IOException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

	/**
	 * Checks if the entered file has already been inputted
	 * 
	 * @param filePath
	 *            the entered file
	 * @return true if the file has already been inputted, false if not
	 */
	private boolean isDuplicateFile(String filePath) {
		if (inputtedFiles.contains(Paths.get(filePath))) {
			Alert alert = DialogsUtil.createErrorDialog("File Already Inputted",
					"The entered file has already been inputted.");
			alert.showAndWait();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the list of inputted files
	 * 
	 * @return the list of inputted files
	 */
	public ArrayList<Path> getInputtedFiles() {
		return this.inputtedFiles;
	}

}
