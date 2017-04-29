package controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.DataMinerService;
import service.RuntimeRecorderService;

public class ResultsControllerIntegrationTest extends ApplicationTest {

	private ResultsController controller;

	private boolean rulesFoundWithApriori;

	private boolean rulesFoundWithFilteredAssociator;

	private boolean noRulesForInvalidAlgorithm;

	private boolean noRulesForInvalidFileType;

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Results.fxml"));
			BorderPane screen = (BorderPane) loader.load();
			controller = loader.getController();
			initializeController();

			Scene scene = new Scene(screen);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Fischer International Data Mining");
			primaryStage.show();
		} catch (IOException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

	private void initializeController() {
		File arffFile = new File("Data/TestArffOne.arff");
		File invalidFile = new File("Data/TestCsvOne.csv");
		String[] dataMiningOptions = { "10", "0.9", "0.05", "1.0", "0.1" };

		rulesFoundWithApriori = controller.initData(arffFile, "Apriori", dataMiningOptions, true,
				new DataMinerService(), new RuntimeRecorderService(), new FXMLLoader());

		rulesFoundWithFilteredAssociator = controller.initData(arffFile, "Filtered Associator", dataMiningOptions, true,
				new DataMinerService(), new RuntimeRecorderService(), new FXMLLoader());

		noRulesForInvalidAlgorithm = controller.initData(arffFile, "Invalid", dataMiningOptions, true,
				new DataMinerService(), new RuntimeRecorderService(), new FXMLLoader());

		noRulesForInvalidFileType = controller.initData(invalidFile, "Filtered Associator", dataMiningOptions, true,
				new DataMinerService(), new RuntimeRecorderService(), new FXMLLoader());
	}

	@Test
	public void should_find_association_rules_with_apriori() {
		assertThat(rulesFoundWithApriori, equalTo(true));
	}

	@Test
	public void should_find_association_rules_with_filtered_associator() {
		assertThat(rulesFoundWithFilteredAssociator, equalTo(true));
	}

	@Test
	public void should_not_find_rules_for_invalid_algorithm() {
		assertThat(noRulesForInvalidAlgorithm, equalTo(false));
	}

	@Test
	public void should_not_find_rules_for_invalid_file() {
		assertThat(noRulesForInvalidFileType, equalTo(false));
	}

	@Test
	public void should_not_continue_when_cancelling_restart() {
		clickOn("#restartButton");
		clickOn("Cancel");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 4/4:"));
	}

	@Test
	public void should_be_able_to_continue_after_restarting() {
		clickOn("#restartButton");
		clickOn("OK");
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 2/4:"));
	}

}
