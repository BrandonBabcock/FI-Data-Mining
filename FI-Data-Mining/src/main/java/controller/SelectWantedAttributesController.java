package controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.PreprocessingService;
import util.DialogsUtil;

/**
 * Controller for the SelectWantedAttributes screen
 */
public class SelectWantedAttributesController {

	private ArrayList<Path> inputtedFiles;
	private ArrayList<Path> convertedFiles;
	private HashMap<Path, ArrayList<String>> allAttributesToFilesMap;
	private HashMap<Path, ArrayList<String>> wantedAttributesToFilesMap = new HashMap<Path, ArrayList<String>>();
	private PreprocessingService preprocessor;
	private int currentFileIndex;

	@FXML
	private BorderPane borderPane;

	@FXML
	private Button nextButton;

	@FXML
	private Button restartButton;

	@FXML
	private Button unselectAllButton;

	@FXML
	private Text currentFileName;

	@FXML
	private Button selectAllButton;

	@FXML
	private VBox attributesVbox;

	/**
	 * Initializes data for the controller
	 * 
	 * @param inputtedFiles
	 *            the list of inputted files
	 */
	public void initData(ArrayList<Path> inputtedFiles) {
		this.inputtedFiles = inputtedFiles;
		this.preprocessor = new PreprocessingService();

		convertedFiles = this.preprocessor.convertXmlToCsv(this.inputtedFiles);
		currentFileIndex = 0;
		currentFileName.setText(this.convertedFiles.get(currentFileIndex).getFileName().toString());
		allAttributesToFilesMap = preprocessor.mapAllAttributesToFiles(convertedFiles);
		loadAttributeCheckBoxes(currentFileIndex);
	}

	/**
	 * Selects all of the checkboxes
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
	 * Unselects all of the checkboxes
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
	 * Restarts to process back to step one.
	 * 
	 * @param event
	 *            the action performed on the restart button
	 */
	@FXML
	public void restart(ActionEvent event) {
		Alert alert = DialogsUtil.createConfirmationDialog("Click OK to Restart",
				"Clicking OK will restart from step one.");
		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == ButtonType.OK) {
			try {
				BorderPane screen = (BorderPane) FXMLLoader.load(getClass().getResource("/view/SelectFiles.fxml"));

				Scene scene = new Scene(screen);
				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				stage.setScene(scene);

				stage.show();
			} catch (IOException e) {
				throw new IllegalArgumentException("Error: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Continues to the next screen or file is there are more files to go
	 * 
	 * @param event
	 *            the action performed on the next button
	 */
	@FXML
	public void next(ActionEvent event) {
		if (isAbleToContinue()) {
			if (currentFileIndex == convertedFiles.size() - 1) {
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Configuration.fxml"));
					BorderPane screen = (BorderPane) loader.load();

					Scene scene = new Scene(screen);
					Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					stage.setScene(scene);

					ConfigurationController controller = loader.getController();
					controller.initData(wantedAttributesToFilesMap, allAttributesToFilesMap);

					stage.show();
				} catch (IOException e) {
					throw new IllegalArgumentException("Error: " + e.getMessage(), e);
				}
			} else {
				currentFileIndex++;
				currentFileName.setText(this.inputtedFiles.get(currentFileIndex).getFileName().toString());
				loadAttributeCheckBoxes(currentFileIndex);
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
		ArrayList<String> wantedAttributes = getCheckedAttributes();

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
	 * @return a list of all the current checked attributes
	 */
	private ArrayList<String> getCheckedAttributes() {
		ArrayList<String> checkedAttributes = new ArrayList<String>();

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
	 * Loads the checkboxes of file attributes onto the screen
	 * 
	 * @param fileIndex
	 *            index of the current file
	 */
	private void loadAttributeCheckBoxes(int fileIndex) {
		attributesVbox.getChildren().clear();

		for (String attributeTitle : allAttributesToFilesMap.get(convertedFiles.get(fileIndex))) {
			CheckBox checkBox = new CheckBox(attributeTitle);
			attributesVbox.getChildren().add(checkBox);
		}
	}

}
