package controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ConfigurationControllerTest extends ApplicationTest {

	private ConfigurationController controller;

	@Override
	public void start(Stage primaryStage) {
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

	private void initializeController() {
		HashMap<Path, ArrayList<String>> wantedAttributesToFileMap = new HashMap<Path, ArrayList<String>>();
		HashMap<Path, ArrayList<String>> allAttributesToFilesMap = new HashMap<Path, ArrayList<String>>();

		ArrayList<String> wantedAttributesList = new ArrayList<String>();
		wantedAttributesList.add("attributeOne");
		wantedAttributesList.add("attributeTwo");

		ArrayList<String> allAttributesList = new ArrayList<String>();
		allAttributesList.add("attributeOne");
		allAttributesList.add("attributeTwo");
		allAttributesList.add("attributeThree");

		wantedAttributesToFileMap.put(Paths.get("Data/TestCsvOne.csv"), wantedAttributesList);
		allAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), allAttributesList);

		controller.initData(wantedAttributesToFileMap, allAttributesToFilesMap);
	}

	@Test
	public void should_restart_to_step_one_if_user_accepts() {
		clickOn("#restartButton");
		clickOn("OK");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 1/4:"));
	}

	@Test
	public void should_not_restart_to_step_one_if_user_does_not_accept() {
		clickOn("#restartButton");
		clickOn("Cancel");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 3/4:"));
	}

	@Test
	public void should_continue_to_next_screen() {
		clickOn("#groupByAttributeComboBox");
		clickOn("attributeOne");

		clickOn("#algorithmComboBox");
		clickOn("Apriori");

		clickOn("#performanceMetricsComboBox");
		clickOn("Yes");

		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 4/4:"));
	}
	
	@Test
	public void should_allow_selection_of_enabling_performance_metrics() {
		clickOn("#performanceMetricsComboBox");
		clickOn("Yes");
		
		ComboBox<String> comboBox = lookup("#performanceMetricsComboBox").query();
		
		assertThat(comboBox.getValue(), equalTo("Yes"));
	}
	
	@Test
	public void should_allow_the_selection_of_a_group_by_attribute() {
		clickOn("#groupByAttributeComboBox");
		clickOn("attributeOne");

		
		ComboBox<String> comboBox = lookup("#groupByAttributeComboBox").query();
		
		assertThat(comboBox.getValue(), equalTo("attributeOne"));
	}
	
	@Test
	public void should_allow_the_selection_of_a_data_mining_algorithm() {
		clickOn("#algorithmComboBox");
		clickOn("Apriori");
		
		ComboBox<String> comboBox = lookup("#algorithmComboBox").query();
		
		assertThat(comboBox.getValue(), equalTo("Apriori"));
	}

	@Test
	public void should_show_error_when_clicking_next_without_finishing_configuration() {
		clickOn("#nextButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));

		clickOn("OK");
	}

}
