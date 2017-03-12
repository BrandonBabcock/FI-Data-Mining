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
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import service.PreprocessingService;
import util.DialogsUtil;

/**
 * Controller for the Configuration screen
 */
public class ConfigurationController {

	PreprocessingService preprocessor = new PreprocessingService();
	private HashMap<Path, ArrayList<String>> wantedAttributesToFilesMap;
	private HashMap<Path, ArrayList<String>> allAttributesToFilesMap;

	@FXML
	private BorderPane borderPane;

	@FXML
	private Button nextButton;

	@FXML
	private Button restartButton;

	@FXML
	private ComboBox<String> groupByAttributeComboBox;

	@FXML
	private ComboBox<String> performanceMetricsComboBox;

	@FXML
	private ComboBox<String> algorithmComboBox;

	/**
	 * Initializes data for the controller
	 * 
	 * @param wantedAttributesToFileMap
	 *            a mapping of wanted attributes to their corresponding file
	 * @param allAttributesToFilesMap
	 *            a mapping of all attributes to their corresponding file
	 */
	public void initData(HashMap<Path, ArrayList<String>> wantedAttributesToFileMap,
			HashMap<Path, ArrayList<String>> allAttributesToFilesMap) {
		this.wantedAttributesToFilesMap = wantedAttributesToFileMap;
		this.allAttributesToFilesMap = allAttributesToFilesMap;

		groupByAttributeComboBox.getItems()
				.addAll(preprocessor.findCommonAttributesInMap(this.wantedAttributesToFilesMap));
		algorithmComboBox.getItems().addAll("Apriori");
		performanceMetricsComboBox.getItems().addAll("Yes", "No");
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
	 * Continues to the next screen and creates the preprocessed file
	 * 
	 * @param event
	 *            the action performed on the next button
	 */
	@FXML
	public void next(ActionEvent event) {
		if (isAbleToContinue()) {
			preprocessor.createPreprocessedFile(wantedAttributesToFilesMap, allAttributesToFilesMap,
					groupByAttributeComboBox.getValue());

			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Results.fxml"));
				BorderPane screen = (BorderPane) loader.load();

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
	 * Checks if the user has done everything required to continue to the next
	 * step
	 * 
	 * @return true if the user has done everything required to continue and
	 *         false if not
	 */
	private boolean isAbleToContinue() {
		if (groupByAttributeComboBox.getValue() != null && algorithmComboBox.getValue() != null
				&& performanceMetricsComboBox.getValue() != null) {
			return true;
		} else {
			Alert alert = DialogsUtil.createErrorDialog("Incomplete Configuration",
					"At least one of the values is not configured.");
			alert.showAndWait();
			return false;
		}
	}

}
