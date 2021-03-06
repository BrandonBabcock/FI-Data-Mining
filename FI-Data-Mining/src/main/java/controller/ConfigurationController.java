package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import converter.CsvToArffConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import service.DataMinerService;
import service.PreprocessorService;
import service.RuntimeRecorderService;
import util.DialogsUtil;

/**
 * Controller for the Configuration FXML screen
 */
public class ConfigurationController {

	/* The PreprocessorService */
	private PreprocessorService preprocessor;

	/*
	 * A map containing the file paths as keys and a list of the user's selected
	 * wanted attributes from the files as values
	 */
	private Map<Path, List<String>> wantedAttributesToFilesMap;

	/*
	 * A map containing the file paths as keys and a list of all of the file's
	 * attributes as values
	 */
	private Map<Path, List<String>> allAttributesToFilesMap;

	/* Whether or not the user entered an ARFF file */
	private boolean usingArffFile = false;

	/* The FXMLLoader use to load FXML screens */
	private FXMLLoader fxmlLoader;

	/* The ARFF file */
	private File arffFile;

	/* The CsvToArffConverter */
	private CsvToArffConverter csvToArffConverter;

	@FXML
	private BorderPane borderPane;

	@FXML
	private Button nextButton;

	@FXML
	private Button restartButton;

	@FXML
	private ComboBox<String> groupByAttributeComboBox;

	@FXML
	private ComboBox<String> algorithmComboBox;

	@FXML
	private ComboBox<String> recordRuntimeComboBox;

	@FXML
	private TextField minimumConfidenceTextField;

	@FXML
	private TextField minimumSupportLowerBoundTextField;

	@FXML
	private TextField minimumSupportUpperBoundTextField;

	@FXML
	private TextField minimumSupportDeltaTextField;

	@FXML
	private TextField numberOfRulesTextField;

	/**
	 * Initializes data for the controller
	 * 
	 * @param wantedAttributesToFileMap
	 *            a mapping of wanted attributes to their corresponding file
	 * @param allAttributesToFilesMap
	 *            a mapping of all attributes to their corresponding file
	 * @param preprocessor
	 *            the PreprocessorService
	 * @param csvToArffConverter
	 *            the CsvToArffConverter
	 * @param fxmlLoader
	 *            the FXMLLoader to load the screens
	 * @return true if the controller initialized properly, false if not
	 */
	public boolean initData(Map<Path, List<String>> wantedAttributesToFileMap,
			Map<Path, List<String>> allAttributesToFilesMap, PreprocessorService preprocessor,
			CsvToArffConverter csvToArffConverter, FXMLLoader fxmlLoader) {
		this.wantedAttributesToFilesMap = wantedAttributesToFileMap;
		this.allAttributesToFilesMap = allAttributesToFilesMap;
		this.preprocessor = preprocessor;
		this.csvToArffConverter = csvToArffConverter;
		this.fxmlLoader = fxmlLoader;

		List<String> commonAttributes = this.preprocessor.findCommonAttributesInMap(this.wantedAttributesToFilesMap);
		groupByAttributeComboBox.getItems().addAll(commonAttributes);

		if (!commonAttributes.isEmpty()) {
			groupByAttributeComboBox.setValue(commonAttributes.get(0));
		} else {
			return false;
		}

		algorithmComboBox.getItems().addAll("Apriori", "Filtered Associator");
		algorithmComboBox.setValue("Apriori");

		recordRuntimeComboBox.getItems().addAll("Yes", "No");
		recordRuntimeComboBox.setValue("Yes");

		return true;
	}

	/**
	 * Initializes data for the controller when coming from the file selection
	 * screen
	 * 
	 * @param arffFile
	 *            the inputted ARFF file
	 * @param fxmlLoader
	 *            the FXMLLoader to load the screens
	 */
	public void initDataFromSelectFiles(File arffFile, FXMLLoader fxmlLoader) {
		this.arffFile = arffFile;
		this.fxmlLoader = fxmlLoader;
		usingArffFile = true;

		groupByAttributeComboBox.setDisable(true);
		algorithmComboBox.getItems().addAll("Apriori", "Filtered Associator");
		algorithmComboBox.setValue("Apriori");
		recordRuntimeComboBox.getItems().addAll("Yes", "No");
		recordRuntimeComboBox.setValue("Yes");
	}

	/**
	 * Restarts the process back to step one
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
				BorderPane screen = fxmlLoader.load();

				SelectFilesController controller = fxmlLoader.getController();
				controller.initData(new FXMLLoader());

				restartButton.getScene().setRoot(screen);
			} catch (IOException e) {
				throw new IllegalArgumentException("Error: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Continues to the next screen and creates the preprocessed file if the
	 * user has done everything required on the current step
	 * 
	 * @param event
	 *            the action performed on the next button
	 */
	@FXML
	public void next(ActionEvent event) {
		if (isAbleToContinue()) {
			if (!usingArffFile) {
				File preprocessedFile = preprocessor.createPreprocessedFile(wantedAttributesToFilesMap,
						allAttributesToFilesMap, groupByAttributeComboBox.getValue());
				arffFile = csvToArffConverter.convertToArff(preprocessedFile);
			}

			String[] dataMiningOptions = { numberOfRulesTextField.getText(), minimumConfidenceTextField.getText(),
					minimumSupportDeltaTextField.getText(), minimumSupportUpperBoundTextField.getText(),
					minimumSupportLowerBoundTextField.getText() };
			boolean recordRuntime = false;

			if (recordRuntimeComboBox.getValue().equals("Yes")) {
				recordRuntime = true;
			}

			try {
				fxmlLoader.setLocation(getClass().getResource("/view/Results.fxml"));
				BorderPane screen = fxmlLoader.load();

				ResultsController controller = fxmlLoader.getController();
				boolean rulesFound = controller.initData(arffFile, algorithmComboBox.getValue(), dataMiningOptions,
						recordRuntime, new DataMinerService(), new RuntimeRecorderService(), new FXMLLoader());

				if (rulesFound) {
					nextButton.getScene().setRoot(screen);
				} else {
					fxmlLoader.setRoot(null);
					fxmlLoader.setController(null);
					alertNoResults();
				}
			} catch (IOException e) {
				throw new IllegalArgumentException("Error: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Alerts the user that no association rules were found
	 */
	private void alertNoResults() {
		Alert alert = DialogsUtil.createErrorDialog("No Rules Found",
				"No association rules were found. Click Restart to start over.");
		alert.showAndWait();
	}

	/**
	 * Checks if the user has done everything required to continue to the next
	 * step
	 * 
	 * @return true if the user has done everything required to continue, false
	 *         if not
	 */
	private boolean isAbleToContinue() {
		if (!usingArffFile) {
			if (isNumeric(minimumConfidenceTextField.getText())
					&& isNumeric(minimumSupportLowerBoundTextField.getText())
					&& isNumeric(minimumSupportUpperBoundTextField.getText())
					&& isNumeric(minimumSupportDeltaTextField.getText())
					&& isNumeric(numberOfRulesTextField.getText())) {
				return true;
			}
		} else {
			if (isNumeric(minimumConfidenceTextField.getText())
					&& isNumeric(minimumSupportLowerBoundTextField.getText())
					&& isNumeric(minimumSupportUpperBoundTextField.getText())
					&& isNumeric(minimumSupportDeltaTextField.getText())
					&& isNumeric(numberOfRulesTextField.getText())) {
				return true;
			}
		}

		Alert alert = DialogsUtil.createErrorDialog("Incomplete Configuration",
				"At least one of the values is not configured properly.");
		alert.showAndWait();
		return false;
	}

	/**
	 * Checks if a String is a valid number
	 * 
	 * @param str
	 *            the String to check
	 * @return true if the String is a valid number, false if it is not
	 */
	private boolean isNumeric(String str) {
		return str.matches("[-+]?\\d*\\.?\\d+");
	}

}
