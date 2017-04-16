package controller;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import weka.associations.AbstractAssociator;

public class ResultsController {

	private AbstractAssociator associator;

	@FXML
	private BorderPane borderPane;

	@FXML
	private Text stepNumberText;

	/**
	 * Initializes data for the controller
	 * 
	 * @param associator
	 *            the data mining results
	 */
	public void initData(AbstractAssociator associator) {
		this.associator = associator;

		System.out.println(this.associator);
	}

}