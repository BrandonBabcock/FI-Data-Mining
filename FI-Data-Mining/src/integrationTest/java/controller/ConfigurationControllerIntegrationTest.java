package controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import converter.CsvToArffConverter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.PreprocessorService;

public class ConfigurationControllerIntegrationTest extends ApplicationTest {

	private ConfigurationController controller;

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Configuration.fxml"));
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

	private void initializeController() throws Exception {
		Map<Path, List<String>> wantedAttributesToFileMap = new HashMap<Path, List<String>>();
		Map<Path, List<String>> allAttributesToFilesMap = new HashMap<Path, List<String>>();

		List<String> wantedAttributesList = new ArrayList<String>();
		wantedAttributesList.add("attributeOne");
		wantedAttributesList.add("attributeTwo");

		ArrayList<String> allAttributesList = new ArrayList<String>();
		allAttributesList.add("attributeOne");
		allAttributesList.add("attributeTwo");
		allAttributesList.add("attributeThree");

		wantedAttributesToFileMap.put(Paths.get("Data/TestCsvOne.csv"), wantedAttributesList);
		allAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), allAttributesList);

		controller.initData(wantedAttributesToFileMap, allAttributesToFilesMap, new PreprocessorService(),
				new CsvToArffConverter(), new FXMLLoader());
	}

	@Test
	public void should_populate_group_by_attribute_combo_box_properly() {
		ComboBox<String> comboBox = lookup("#groupByAttributeComboBox").query();

		assertThat(comboBox.getItems().size(), equalTo(2));
		assertThat(comboBox.getItems().contains("attributeOne"), equalTo(true));
		assertThat(comboBox.getItems().contains("attributeTwo"), equalTo(true));
	}

	@Test
	public void should_find_rules_with_apriori() {
		clickOn("#nextButton");

		VBox rulesVbox = lookup("#rulesVbox").query();
		List<String> rules = new ArrayList<String>();

		for (Node child : rulesVbox.getChildren()) {
			if (child instanceof Text) {
				rules.add(((Text) child).getText());
			}
		}

		assertThat(rules.isEmpty(), equalTo(false));
		assertThat(rules.get(0).contains("attributeTwo"), equalTo(true));
		assertThat(rules.get(0).contains("attributeOne"), equalTo(true));
	}

	@Test
	public void should_find_rules_with_filtered_associator() {
		File arffFile = new File("Data/TestArffOne.arff");
		controller.initDataFromSelectFiles(arffFile, new FXMLLoader());

		clickOn("#algorithmComboBox");
		clickOn("Filtered Associator");
		clickOn("#nextButton");

		VBox rulesVbox = lookup("#rulesVbox").query();
		List<String> rules = new ArrayList<String>();

		for (Node child : rulesVbox.getChildren()) {
			if (child instanceof Text) {
				rules.add(((Text) child).getText());
			}
		}

		assertThat(rules.isEmpty(), equalTo(false));
		assertThat(rules.get(0).contains("outlook=overcast"), equalTo(true));
		assertThat(rules.get(1).contains("temperature=cool"), equalTo(true));
	}

	@Test
	public void should_not_change_steps_when_continuing_with_invalid_config_and_cancelling_restart() {
		clickOn("#minimumConfidenceTextField").write("a");
		clickOn("#nextButton");
		clickOn("OK");
		clickOn("#restartButton");
		clickOn("Cancel");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 3/4:"));
	}

	@Test
	public void should_not_record_runtime() {
		clickOn("#recordRuntimeComboBox");
		clickOn("No");
		clickOn("#nextButton");

		Text text = lookup("#runTimeValue").query();

		assertThat(text.getText(), equalTo("Not Recorded"));
	}

	@Test
	public void should_be_able_to_continue_after_restart() {
		clickOn("#restartButton");
		clickOn("OK");
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 2/4:"));
	}

	@Test
	public void should_be_able_to_restart_after_continuing() {
		clickOn("#nextButton");
		clickOn("#restartButton");
		clickOn("OK");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 1/4:"));
	}

	@Test
	public void should_not_find_results_with_apriori() {
		doubleClickOn("#minimumConfidenceTextField").write("2");
		clickOn("#nextButton");
		clickOn("OK");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 3/4:"));
	}
	
	@Test
	public void should_continue_after_making_invalid_confidence_valid() {
		doubleClickOn("#minimumConfidenceTextField").write("a");
		clickOn("#nextButton");
		clickOn("OK");
		doubleClickOn("#minimumConfidenceTextField").write("0.9");
		clickOn("#nextButton");
		
		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 4/4:"));
	}
	
	@Test
	public void should_continue_after_multiple_making_invalid_support_lower_bound_valid() {
		doubleClickOn("#minimumSupportLowerBoundTextField").write("a");
		clickOn("#nextButton");
		clickOn("OK");
		doubleClickOn("#minimumSupportLowerBoundTextField").write("0.1");
		clickOn("#nextButton");
		
		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 4/4:"));
	}
	
	@Test
	public void should_continue_after_multiple_making_invalid_support_upper_bound_valid() {
		doubleClickOn("#minimumSupportUpperBoundTextField").write("a");
		clickOn("#nextButton");
		clickOn("OK");
		doubleClickOn("#minimumSupportUpperBoundTextField").write("1");
		clickOn("#nextButton");
		
		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 4/4:"));
	}
	
	@Test
	public void should_continue_after_multiple_making_invalid_support_delta_valid() {
		doubleClickOn("#minimumSupportDeltaTextField").write("a");
		clickOn("#nextButton");
		clickOn("OK");
		doubleClickOn("#minimumSupportDeltaTextField").write("0.05");
		clickOn("#nextButton");
		
		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 4/4:"));
	}
	
	@Test
	public void should_continue_after_multiple_making_invalid_number_of_rules_valid() {
		doubleClickOn("#numberOfRulesTextField").write("a");
		clickOn("#nextButton");
		clickOn("OK");
		doubleClickOn("#numberOfRulesTextField").write("10");
		clickOn("#nextButton");
		
		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 4/4:"));
	}

}
