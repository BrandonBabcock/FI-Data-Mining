package controller;

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
import service.PerformanceAnalyzerService;
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
	 * Initializes data for the controller
	 * 
	 * @param associator
	 *            the data mining results
	 * @param performanceAnalyzer
	 *            the performance analyzer
	 */
	public void initData(AbstractAssociator associator, PerformanceAnalyzerService performanceAnalyzer) {
		if (performanceAnalyzer != null) {
			runTimeValue.setText(Double.toString(performanceAnalyzer.getRunTime()));
		} else {
			runTimeValue.setText("Not Recorded");
		}

		loadRules(associator);
	}

	private void loadRules(AbstractAssociator associator) {
		if (associator instanceof Apriori) {
			Apriori apriori = (Apriori) associator;
			AssociationRules associationRules = apriori.getAssociationRules();
			List<AssociationRule> rulesList = associationRules.getRules();

			for (AssociationRule rule : rulesList) {
				Text text = new Text(rule.toString());
				text.setFont(Font.font(12));
				rulesVbox.getChildren().add(text);
			}
		} else if (associator instanceof FilteredAssociator) {
			FilteredAssociator filteredAssociator = (FilteredAssociator) associator;
			AssociationRules associationRules = filteredAssociator.getAssociationRules();
			List<AssociationRule> rulesList = associationRules.getRules();

			for (AssociationRule rule : rulesList) {
				Text text = new Text(rule.toString());
				text.setFont(Font.font(12));
				rulesVbox.getChildren().add(text);
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
	void restart(ActionEvent event) {
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

}