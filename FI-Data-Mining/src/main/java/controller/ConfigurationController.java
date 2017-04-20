package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import service.RuntimeRecorderService;
import service.PreprocessorService;
import util.CsvToArffConverter;
import util.DialogsUtil;
import weka.associations.AbstractAssociator;

/**
 * Controller for the Configuration FXML screen
 */
public class ConfigurationController {

	/* The PreprocessorService */
	private PreprocessorService preprocessor;

	/* The DataMinerService */
	private DataMinerService dataMiner;

	/* The RuntimeRecorderService */
	private RuntimeRecorderService runtimeRecorder;

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

	/* The ARFF file */
	private File arffFile;

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
	 * @return true if the controller initialized properly, false if not
	 */
	public boolean initData(Map<Path, List<String>> wantedAttributesToFileMap,
			Map<Path, List<String>> allAttributesToFilesMap) {
		this.wantedAttributesToFilesMap = wantedAttributesToFileMap;
		this.allAttributesToFilesMap = allAttributesToFilesMap;
		preprocessor = new PreprocessorService();
		dataMiner = new DataMinerService();

		List<String> commonAttributes = preprocessor.findCommonAttributesInMap(this.wantedAttributesToFilesMap);
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

	public void initDataFromSelectFiles(File arffFile) {
		this.arffFile = arffFile;
		preprocessor = new PreprocessorService();
		dataMiner = new DataMinerService();
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
				BorderPane screen = (BorderPane) FXMLLoader.load(getClass().getResource("/view/SelectFiles.fxml"));
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
				arffFile = CsvToArffConverter.convertToArff(preprocessedFile);
			}

			AbstractAssociator associator;
			String[] dataMiningOptions = { numberOfRulesTextField.getText(), minimumConfidenceTextField.getText(),
					minimumSupportDeltaTextField.getText(), minimumSupportUpperBoundTextField.getText(),
					minimumSupportLowerBoundTextField.getText() };

			if (recordRuntimeComboBox.getValue().equals("Yes")) {
				runtimeRecorder = new RuntimeRecorderService();
				runtimeRecorder.start();
				associator = dataMiner.findAssociationRules(algorithmComboBox.getValue(), arffFile.getPath(),
						dataMiningOptions);
				runtimeRecorder.stop();
			} else {
				associator = dataMiner.findAssociationRules(algorithmComboBox.getValue(), arffFile.getPath(),
						dataMiningOptions);
			}

			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Results.fxml"));
				BorderPane screen = (BorderPane) loader.load();

				ResultsController controller = loader.getController();
				controller.initData(associator, runtimeRecorder);

				nextButton.getScene().setRoot(screen);
			} catch (IOException e) {
				throw new IllegalArgumentException("Error: " + e.getMessage(), e);
			}
		}
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
			if (groupByAttributeComboBox.getValue() != null && algorithmComboBox.getValue() != null
					&& recordRuntimeComboBox.getValue() != null && isNumeric(minimumConfidenceTextField.getText())
					&& isNumeric(minimumSupportLowerBoundTextField.getText())
					&& isNumeric(minimumSupportUpperBoundTextField.getText())
					&& isNumeric(minimumSupportDeltaTextField.getText())
					&& isNumeric(numberOfRulesTextField.getText())) {
				return true;
			}
		} else {
			if (algorithmComboBox.getValue() != null && recordRuntimeComboBox.getValue() != null
					&& isNumeric(minimumConfidenceTextField.getText())
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
