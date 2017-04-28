package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import converter.CsvToArffConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import service.PreprocessorService;
import util.DialogsUtil;

/**
 * Controller for the SelectFiles FXML screen
 */
public class SelectFilesController {

	/* The currently entered file */
	private File selectedFile;

	/* The list of all inputted files */
	private List<Path> inputtedFiles = new ArrayList<Path>();

	/* Whether or not an ARFF file has been added */
	private boolean isArffFileAdded = false;

	/* The FXMLLoader to load FXML screens */
	private FXMLLoader fxmlLoader;

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

	@FXML
	private Text stepNumberText;

	/**
	 * Initializes the controller
	 * 
	 * @param fxmlLoader
	 *            the loader to load FXML screens
	 */
	public void initData(FXMLLoader fxmlLoader) {
		this.fxmlLoader = fxmlLoader;
	}

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
	 * prompts the user to continue directly to the configuration screen.
	 * 
	 * @param event
	 *            the action performed on the add file button
	 */
	@FXML
	public void addFile(ActionEvent event) {
		if (isValidFile(fileTextField.getText()) && !isDuplicateFile(fileTextField.getText())) {
			if (isArffFileAdded) {
				promptToContinueToConfiguration();
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
	 * Takes the user to the next step, which is the select wanted attributes
	 * screen
	 * 
	 * @param event
	 *            the action performed on the next button
	 */
	@FXML
	public void next(ActionEvent event) {
		if (isAbleToContinue()) {
			try {
				fxmlLoader.setLocation(getClass().getResource("/view/SelectWantedAttributes.fxml"));
				BorderPane screen = (BorderPane) fxmlLoader.load();

				SelectWantedAttributesController controller = fxmlLoader.getController();
				controller.initData(inputtedFiles, new PreprocessorService(), new FXMLLoader());

				nextButton.getScene().setRoot(screen);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the list of inputted files
	 * 
	 * @return the list of inputted files
	 */
	public List<Path> getInputtedFiles() {
		return this.inputtedFiles;
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

		if (filePath.substring(filePath.length() - 4).equals(".csv")
				|| filePath.substring(filePath.length() - 4).equals(".xml")) {
			return true;
		} else if (filePath.substring(filePath.length() - 5).equals(".arff")) {
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
	 * Checks if the entered file has already been inputted
	 * 
	 * @param filePath
	 *            the entered file
	 * @return true if the entered file has already been inputted, false if not
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
	 * If an ARFF file is entered, the user is informed that only one ARFF file
	 * can be added and that they would need to continue to the configuration
	 * screen. Also, the user is asked to pick a data mining algorithm to use if
	 * they agree to continue.
	 */
	private void promptToContinueToConfiguration() {
		Alert alert = DialogsUtil.createConfirmationDialog("ARFF File Entered",
				"Only one ARFF file can be added. Click OK to use this file and continue to the configuration screen.");
		Optional<ButtonType> alertResult = alert.showAndWait();

		if (alertResult.get() == ButtonType.OK) {
			try {
				fxmlLoader.setLocation(getClass().getResource("/view/Configuration.fxml"));
				BorderPane screen = (BorderPane) fxmlLoader.load();

				File arffFile = new File(fileTextField.getText());
				ConfigurationController controller = fxmlLoader.getController();
				controller.initDataFromSelectFiles(arffFile, new PreprocessorService(), new CsvToArffConverter(),
						new FXMLLoader());

				addFileButton.getScene().setRoot(screen);
			} catch (IOException e) {
				throw new IllegalArgumentException("Error: " + e.getMessage(), e);
			}
		} else {
			isArffFileAdded = false;
			fileTextField.clear();
		}
	}

}
