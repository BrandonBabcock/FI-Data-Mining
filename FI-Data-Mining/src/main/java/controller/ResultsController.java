package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import service.DataMinerService;
import service.RuntimeRecorderService;
import util.DialogsUtil;
import weka.associations.AbstractAssociator;
import weka.associations.Apriori;
import weka.associations.AssociationRule;
import weka.associations.AssociationRules;
import weka.associations.FilteredAssociator;

/**
 * Controller for the Results FXML screen
 */
public class ResultsController {

	/* The FXMLLoader use to load FXML screens */
	private FXMLLoader fxmlLoader;

	@FXML
	private BorderPane borderPane;

	@FXML
	private Button restartButton;

	@FXML
	private Text stepNumberText;

	@FXML
	private Text runTimeValue;

	@FXML
	private VBox rulesVbox;

	/**
	 * Initializes the controller
	 * 
	 * @param arffFile
	 *            the ARFF file to data mine
	 * @param algorithm
	 *            the data mining algorithm to use
	 * @param dataMiningOptions
	 *            the data mining parameters to send
	 * @param recordRuntime
	 *            whether or not to record the runtime
	 * @param dataMiner
	 *            the DataMinerService
	 * @param runtimeRecorder
	 *            the RuntimeRecorderService
	 * @param fxmlLoader
	 *            the FXMLLoader to load the screens
	 * @return true if association rules were found, false otherwise
	 */
	public boolean initData(File arffFile, String algorithm, String[] dataMiningOptions, boolean recordRuntime,
			DataMinerService dataMiner, RuntimeRecorderService runtimeRecorder, FXMLLoader fxmlLoader) {
		this.fxmlLoader = fxmlLoader;
		AbstractAssociator associator;

		if (recordRuntime) {
			runtimeRecorder.start();
			associator = dataMiner.findAssociationRules(algorithm, arffFile.getPath(), dataMiningOptions);
			runtimeRecorder.stop();
			runTimeValue.setText(Double.toString(runtimeRecorder.getRunTime()));
		} else {
			associator = dataMiner.findAssociationRules(algorithm, arffFile.getPath(), dataMiningOptions);
			runTimeValue.setText("Not Recorded");
		}

		return loadRules(associator);
	}

	/**
	 * Loads the association rules onto the screen
	 * 
	 * @param associator
	 *            the data mining results
	 * @return true if there were rules to display, false if not
	 */
	private boolean loadRules(AbstractAssociator associator) {
		if (associator instanceof Apriori) {
			Apriori apriori = (Apriori) associator;
			AssociationRules associationRules = apriori.getAssociationRules();
			List<AssociationRule> rulesList = associationRules.getRules();

			if (!rulesList.isEmpty()) {
				for (AssociationRule rule : rulesList) {
					Text ruleText = new Text(rule.toString());
					ruleText.setFont(Font.font(12));
					rulesVbox.getChildren().add(ruleText);
				}
			} else {
				return false;
			}
			return true;
		} else if (associator instanceof FilteredAssociator) {
			FilteredAssociator filteredAssociator = (FilteredAssociator) associator;
			AssociationRules associationRules = filteredAssociator.getAssociationRules();
			List<AssociationRule> rulesList = associationRules.getRules();

			if (!rulesList.isEmpty()) {
				for (AssociationRule rule : rulesList) {
					Text ruleText = new Text(rule.toString());
					ruleText.setFont(Font.font(12));
					rulesVbox.getChildren().add(ruleText);
				}
			} else {
				return false;
			}
			return true;
		} else {
			return false;
		}
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
				"Clicking OK will restart from step one.");
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

}