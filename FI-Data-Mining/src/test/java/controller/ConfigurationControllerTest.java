package controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.PreprocessorService;

@PrepareForTest(ConfigurationController.class)
public class ConfigurationControllerTest extends ApplicationTest {

	private ConfigurationController controller;

	@Spy
	private final PreprocessorService preprocessorSpy = new PreprocessorService();

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			MockitoAnnotations.initMocks(this);
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

		ArrayList<String> wantedAttributesList = new ArrayList<String>();
		wantedAttributesList.add("attributeOne");
		wantedAttributesList.add("attributeTwo");

		ArrayList<String> allAttributesList = new ArrayList<String>();
		allAttributesList.add("attributeOne");
		allAttributesList.add("attributeTwo");
		allAttributesList.add("attributeThree");

		wantedAttributesToFileMap.put(Paths.get("Data/TestCsvOne.csv"), wantedAttributesList);
		allAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), allAttributesList);

		whenNew(PreprocessorService.class).withNoArguments().thenReturn(preprocessorSpy);
		doReturn(wantedAttributesList).when(preprocessorSpy).findCommonAttributesInMap(isA(HashMap.class));

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
		doNothing().when(preprocessorSpy).createPreprocessedFile(isA(HashMap.class), isA(HashMap.class),
				isA(String.class));

		clickOn("#groupByAttributeComboBox");
		clickOn("attributeOne");

		clickOn("#algorithmComboBox");
		clickOn("Apriori");

		clickOn("#recordRuntimeComboBox");
		clickOn("Yes");

		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 4/4:"));
	}

	@Test
	public void should_allow_selection_of_enabling_recording_of_runtime() {
		clickOn("#recordRuntimeComboBox");
		clickOn("Yes");

		ComboBox<String> comboBox = lookup("#recordRuntimeComboBox").query();

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
