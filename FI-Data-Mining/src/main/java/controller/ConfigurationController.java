package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import service.DataMinerService;
import service.PerformanceAnalyzerService;
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

	/* The PerformanceAnalyzerService */
	private PerformanceAnalyzerService performanceAnalyzer;

	/*
	 * A map containing the file paths as keys and a list of the user's selected
	 * wanted attributes from the files as values
	 */
	private HashMap<Path, ArrayList<String>> wantedAttributesToFilesMap;

	/*
	 * A map containing the file paths as keys and a list of all of the file's
	 * attributes as values
	 */
	private HashMap<Path, ArrayList<String>> allAttributesToFilesMap;

	/* Whether or not the entered an ARFF file */
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
	public boolean initData(HashMap<Path, ArrayList<String>> wantedAttributesToFileMap,
			HashMap<Path, ArrayList<String>> allAttributesToFilesMap) {
		this.wantedAttributesToFilesMap = wantedAttributesToFileMap;
		this.allAttributesToFilesMap = allAttributesToFilesMap;
		this.preprocessor = new PreprocessorService();
		this.dataMiner = new DataMinerService();

		ArrayList<String> commonAttributes = this.preprocessor
				.findCommonAttributesInMap(this.wantedAttributesToFilesMap);
		groupByAttributeComboBox.getItems().addAll(commonAttributes);

		if (!commonAttributes.isEmpty()) {
			groupByAttributeComboBox.setValue(commonAttributes.get(0));
		} else {
			return false;
		}

		algorithmComboBox.getItems().addAll("Apriori", "Filtered Associator");
		algorithmComboBox.setValue("Apriori");

		performanceMetricsComboBox.getItems().addAll("Yes", "No");
		performanceMetricsComboBox.setValue("Yes");

		return true;
	}

	public void initDataFromSelectFiles(File arffFile) {
		this.preprocessor = new PreprocessorService();
		this.dataMiner = new DataMinerService();
		this.usingArffFile = true;
		this.arffFile = arffFile;

		groupByAttributeComboBox.setDisable(true);
		algorithmComboBox.getItems().addAll("Apriori", "Filtered Associator");
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

			if (performanceMetricsComboBox.getValue().equals("Yes")) {
				performanceAnalyzer = new PerformanceAnalyzerService();
				performanceAnalyzer.start();
				associator = dataMiner.findAssociationRules(algorithmComboBox.getValue(), arffFile.getPath());
				performanceAnalyzer.stop();
			} else {
				associator = dataMiner.findAssociationRules(algorithmComboBox.getValue(), arffFile.getPath());
			}

			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Results.fxml"));
				BorderPane screen = (BorderPane) loader.load();

				ResultsController controller = loader.getController();
				controller.initData(associator, performanceAnalyzer);

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
	 * @return true if the user has done everything required to continue and
	 *         false if not
	 */
	private boolean isAbleToContinue() {
		if (!usingArffFile) {
			if (groupByAttributeComboBox.getValue() != null && algorithmComboBox.getValue() != null
					&& performanceMetricsComboBox.getValue() != null) {
				return true;
			}
		} else {
			if (algorithmComboBox.getValue() != null && performanceMetricsComboBox.getValue() != null) {
				return true;
			}
		}

		Alert alert = DialogsUtil.createErrorDialog("Incomplete Configuration",
				"At least one of the values is not configured.");
		alert.showAndWait();
		return false;
	}

}
