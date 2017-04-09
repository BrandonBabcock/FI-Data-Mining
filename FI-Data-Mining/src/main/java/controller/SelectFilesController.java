package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.DialogsUtil;

/**
 * Controller for the SelectFiles fxml screen
 */
public class SelectFilesController {

	private File selectedFile;

	private ArrayList<Path> inputtedFiles = new ArrayList<Path>();

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
	 *            the action performed on clear field button
	 */
	@FXML
	public void clearField(ActionEvent event) {
		fileTextField.clear();
	}

	/**
	 * Adds a file to the list of inputted files
	 * 
	 * @param event
	 *            the action performed on the add file button
	 */
	@FXML
	public void addFile(ActionEvent event) {
		if (isValidFile(fileTextField.getText()) && !isDuplicateFile(fileTextField.getText())) {
			Path filePath = Paths.get(fileTextField.getText());
			inputtedFiles.add(filePath);
			fileTextField.clear();
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
		inputtedFiles.clear();
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

				Scene scene = new Scene(screen);
				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				stage.setScene(scene);

				SelectWantedAttributesController controller = loader.getController();
				controller.initData(inputtedFiles);

				stage.show();
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
	 * Checks if the entered file type is valid
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
	 * Checks is the entered file is of a supported type
	 * 
	 * @param filePath
	 *            the entered file
	 * @return true if the file is of a support type, false if not
	 */
	private boolean isValidFileType(String filePath) {
		filePath = filePath.toLowerCase();

		if (filePath.contains(".csv") || filePath.contains(".xml")) {
			return true;
		} else {
			Alert alert = DialogsUtil.createErrorDialog("Invalid File Type",
					"Only CSV, XML, and ARFF file types are supported.");
			alert.showAndWait();
			return false;
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
