package controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import converter.CsvToArffConverter;
import converter.XmlToCsvConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import service.PreprocessorService;
import util.DialogsUtil;

/**
 * Controller for the SelectWantedAttributes screen
 */
public class SelectWantedAttributesController {

	/* The list of all inputted files */
	private List<Path> inputtedFiles;

	/* The list of all inputted files converted to CSV files */
	private List<Path> convertedFiles;

	/*
	 * A map containing the file paths as keys and a list of all of the file's
	 * attributes as values
	 */
	private Map<Path, List<String>> allAttributesToFilesMap;

	/*
	 * A map containing the file paths as keys and a list of the user's selected
	 * wanted attributes from the files as values
	 */
	private Map<Path, List<String>> wantedAttributesToFilesMap = new HashMap<Path, List<String>>();

	/*
	 * The index of the current file that the user is selecting attributes for
	 * in the convertedFiles list
	 */
	private int currentFileIndex;

	/* The FXMLLoader to load FXML screens */
	private FXMLLoader fxmlLoader;

	@FXML
	private BorderPane borderPane;

	@FXML
	private Button nextButton;

	@FXML
	private Button restartButton;

	@FXML
	private Button selectAllButton;

	@FXML
	private Button unselectAllButton;

	@FXML
	private Text currentFileName;

	@FXML
	private VBox attributesVbox;

	@FXML
	private Text stepNumberText;

	/**
	 * Initializes the controller
	 * 
	 * @param inputtedFiles
	 *            the user inputted files
	 * @param preprocessor
	 *            the PreprocessorService
	 * @param fxmlLoader
	 *            the FXMLLoader to load the screens
	 */
	public void initData(List<Path> inputtedFiles, PreprocessorService preprocessor, FXMLLoader fxmlLoader) {
		this.inputtedFiles = inputtedFiles;
		this.fxmlLoader = fxmlLoader;

		convertedFiles = preprocessor.convertXmlToCsv(this.inputtedFiles, new XmlToCsvConverter());
		currentFileIndex = 0;
		currentFileName.setText(this.inputtedFiles.get(currentFileIndex).getFileName().toString());

		allAttributesToFilesMap = preprocessor.mapAllAttributesToFiles(convertedFiles);
		loadAttributeCheckBoxes();
	}

	/**
	 * Selects all of the check boxes
	 * 
	 * @param event
	 *            the action performed on the select all button
	 */
	@FXML
	public void selectAll(ActionEvent event) {
		for (Node child : attributesVbox.getChildren()) {
			if (child instanceof CheckBox) {
				((CheckBox) child).setSelected(true);
			}
		}
	}

	/**
	 * Unselects all of the check boxes
	 * 
	 * @param event
	 *            the action performed on the unselect all button
	 */
	@FXML
	public void unselectAll(ActionEvent event) {
		for (Node child : attributesVbox.getChildren()) {
			if (child instanceof CheckBox) {
				((CheckBox) child).setSelected(false);
			}
		}
	}

	/**
	 * Restarts to GUI back to step one
	 * 
	 * @param event
	 *            the action performed on the restart button
	 */
	@FXML
	public void restart(ActionEvent event) {
		Alert alert = DialogsUtil.createConfirmationDialog("Click OK to Restart",
				"Clicking OK will restart to step one.");
		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == ButtonType.OK) {
			try {
				fxmlLoader.setLocation(getClass().getResource("/view/SelectFiles.fxml"));
				BorderPane screen = (BorderPane) fxmlLoader.load();

				SelectFilesController controller = fxmlLoader.getController();
				controller.initData(new FXMLLoader());

				restartButton.getScene().setRoot(screen);
			} catch (IOException e) {
				throw new IllegalArgumentException("Error: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Continues to the next screen or file if there are more files to go. The
	 * user can only continue if they have done everything required on the
	 * current screen.
	 * 
	 * @param event
	 *            the action performed on the next button
	 */
	@FXML
	public void next(ActionEvent event) {
		if (isAbleToContinue()) {
			if (currentFileIndex == convertedFiles.size() - 1) {
				try {
					fxmlLoader.setLocation(getClass().getResource("/view/Configuration.fxml"));
					BorderPane screen = (BorderPane) fxmlLoader.load();

					ConfigurationController controller = fxmlLoader.getController();
					boolean ableToConfigure = controller.initData(wantedAttributesToFilesMap, allAttributesToFilesMap,
							new PreprocessorService(), new CsvToArffConverter(), new FXMLLoader());

					if (ableToConfigure) {
						nextButton.getScene().setRoot(screen);
					} else {
						fxmlLoader.setRoot(null);
						fxmlLoader.setController(null);
						Alert alert = DialogsUtil.createErrorDialog("No Common Attributes Found",
								"No common attributes were found within the files. Click Restart to start over.");
						alert.showAndWait();
					}
				} catch (IOException e) {
					throw new IllegalArgumentException("Error: " + e.getMessage(), e);
				}
			} else {
				currentFileIndex++;
				currentFileName.setText(this.inputtedFiles.get(currentFileIndex).getFileName().toString());
				loadAttributeCheckBoxes();
			}
		}
	}

	/**
	 * Checks if the user has done everything required to continue to the next
	 * step
	 * 
	 * @return true if the user has done everything required to continue and
	 *         false if not
	 */
	private boolean isAbleToContinue() {
		List<String> wantedAttributes = getCheckedAttributes();

		if (!wantedAttributes.isEmpty()) {
			wantedAttributesToFilesMap.put(convertedFiles.get(currentFileIndex), wantedAttributes);
			return true;
		} else {
			Alert alert = DialogsUtil.createErrorDialog("No Attributes Selected", "No attributes have been selected.");
			alert.showAndWait();
			return false;
		}
	}

	/**
	 * Gets all of the currently checked attributes
	 * 
	 * @return a list of all the currently checked attributes
	 */
	private List<String> getCheckedAttributes() {
		List<String> checkedAttributes = new ArrayList<String>();

		for (Node child : attributesVbox.getChildren()) {
			if (child instanceof CheckBox) {
				if (((CheckBox) child).isSelected()) {
					checkedAttributes.add(((CheckBox) child).getText());
				}
			}
		}

		return checkedAttributes;
	}

	/**
	 * Loads the check boxes of file attributes for the current file onto the
	 * screen
	 * 
	 * @param fileIndex
	 *            index of the current file
	 */
	private void loadAttributeCheckBoxes() {
		attributesVbox.getChildren().clear();

		for (String attributeTitle : allAttributesToFilesMap.get(convertedFiles.get(currentFileIndex))) {
			CheckBox checkBox = new CheckBox(attributeTitle);
			attributesVbox.getChildren().add(checkBox);
		}
	}

}
