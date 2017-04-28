package controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.testfx.framework.junit.ApplicationTest;

import converter.CsvToArffConverter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.DataMinerService;
import service.PreprocessorService;
import service.RuntimeRecorderService;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationControllerTest extends ApplicationTest {

	private ConfigurationController controller;

	@Mock
	private PreprocessorService preprocessorMock;

	@Mock
	private CsvToArffConverter csvToArffConverterMock;

	@Mock
	private ResultsController resultsControllerMock;

	@Spy
	private FXMLLoader fxmlLoaderSpy;

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

		List<String> allAttributesList = new ArrayList<String>();
		allAttributesList.add("attributeOne");
		allAttributesList.add("attributeTwo");
		allAttributesList.add("attributeThree");

		wantedAttributesToFileMap.put(Paths.get("Data/TestCsvOne.csv"), wantedAttributesList);
		allAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), allAttributesList);

		doReturn(wantedAttributesList).when(preprocessorMock).findCommonAttributesInMap(anyMap());

		controller.initData(wantedAttributesToFileMap, allAttributesToFilesMap, preprocessorMock,
				csvToArffConverterMock, fxmlLoaderSpy);
	}

	@Test
	public void should_populate_and_set_group_by_attribute_combo_box_from_select_wanted_attributes() {
		ComboBox<String> groupByAttributeComboBox = lookup("#groupByAttributeComboBox").query();

		assertThat(groupByAttributeComboBox.getItems().contains("attributeOne"), equalTo(true));
		assertThat(groupByAttributeComboBox.getItems().contains("attributeTwo"), equalTo(true));
		assertThat(groupByAttributeComboBox.getValue(), equalTo("attributeOne"));
	}

	@Test
	public void should_disable_group_by_attribute_combo_box_from_select_files() {
		File arffFile = new File("Data/TestArffOne.arff");
		controller.initDataFromSelectFiles(arffFile, preprocessorMock, csvToArffConverterMock, fxmlLoaderSpy);

		ComboBox<String> groupByAttributeComboBox = lookup("#groupByAttributeComboBox").query();

		assertThat(groupByAttributeComboBox.isDisabled(), equalTo(true));
	}

	@Test
	public void should_populate_and_set_algorithm_combo_box_from_select_wanted_attributes() {
		ComboBox<String> algorithmComboBox = lookup("#algorithmComboBox").query();

		assertThat(algorithmComboBox.getItems().contains("Apriori"), equalTo(true));
		assertThat(algorithmComboBox.getItems().contains("Filtered Associator"), equalTo(true));
		assertThat(algorithmComboBox.getValue(), equalTo("Apriori"));
	}

	@Test
	public void should_populate_and_set_algorithm_combo_box_from_select_select_files() {
		File arffFile = new File("Data/TestArffOne.arff");
		controller.initDataFromSelectFiles(arffFile, preprocessorMock, csvToArffConverterMock, fxmlLoaderSpy);

		ComboBox<String> algorithmComboBox = lookup("#algorithmComboBox").query();

		assertThat(algorithmComboBox.getItems().contains("Apriori"), equalTo(true));
		assertThat(algorithmComboBox.getItems().contains("Filtered Associator"), equalTo(true));
		assertThat(algorithmComboBox.getValue(), equalTo("Apriori"));
	}

	@Test
	public void should_populate_and_set_record_runtime_combo_box_from_select_wanted_attributes() {
		ComboBox<String> recordRuntimeComboBox = lookup("#recordRuntimeComboBox").query();

		assertThat(recordRuntimeComboBox.getItems().contains("Yes"), equalTo(true));
		assertThat(recordRuntimeComboBox.getItems().contains("No"), equalTo(true));
		assertThat(recordRuntimeComboBox.getValue(), equalTo("Yes"));
	}

	@Test
	public void should_populate_and_set_record_runtime_combo_box_from_select_files() {
		File arffFile = new File("Data/TestArffOne.arff");
		controller.initDataFromSelectFiles(arffFile, preprocessorMock, csvToArffConverterMock, fxmlLoaderSpy);

		ComboBox<String> recordRuntimeComboBox = lookup("#recordRuntimeComboBox").query();

		assertThat(recordRuntimeComboBox.getItems().contains("Yes"), equalTo(true));
		assertThat(recordRuntimeComboBox.getItems().contains("No"), equalTo(true));
		assertThat(recordRuntimeComboBox.getValue(), equalTo("Yes"));
	}

	@Test
	public void should_return_true_if_all_data_can_be_initialized_from_select_wanted_attributes() throws Exception {
		Map<Path, List<String>> wantedAttributesToFileMap = new HashMap<Path, List<String>>();
		Map<Path, List<String>> allAttributesToFilesMap = new HashMap<Path, List<String>>();

		List<String> wantedAttributesList = new ArrayList<String>();
		wantedAttributesList.add("attributeOne");

		List<String> allAttributesList = new ArrayList<String>();
		allAttributesList.add("attributeOne");

		wantedAttributesToFileMap.put(Paths.get("Data/TestCsvOne.csv"), wantedAttributesList);
		allAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), allAttributesList);

		doReturn(wantedAttributesList).when(preprocessorMock).findCommonAttributesInMap(anyMap());

		boolean actualValue = controller.initData(wantedAttributesToFileMap, allAttributesToFilesMap, preprocessorMock,
				csvToArffConverterMock, fxmlLoaderSpy);

		assertThat(actualValue, equalTo(true));
	}

	@Test
	public void should_return_false_if_there_are_no_common_attributes_from_select_wanted_attributes() throws Exception {
		Map<Path, List<String>> wantedAttributesToFileMap = new HashMap<Path, List<String>>();
		Map<Path, List<String>> allAttributesToFilesMap = new HashMap<Path, List<String>>();

		List<String> wantedAttributesList = new ArrayList<String>();
		wantedAttributesList.add("attributeOne");

		List<String> allAttributesList = new ArrayList<String>();
		allAttributesList.add("attributeTwo");

		wantedAttributesToFileMap.put(Paths.get("Data/TestCsvOne.csv"), wantedAttributesList);
		allAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), allAttributesList);

		doReturn(new ArrayList<String>()).when(preprocessorMock).findCommonAttributesInMap(anyMap());

		boolean actualValue = controller.initData(wantedAttributesToFileMap, allAttributesToFilesMap, preprocessorMock,
				csvToArffConverterMock, fxmlLoaderSpy);

		assertThat(actualValue, equalTo(false));
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
	public void should_continue_to_next_screen_when_not_using_arff_file_or_performance_metrics() {
		doReturn(new File("Data/TestCsvOne.csv")).when(preprocessorMock).createPreprocessedFile(anyMap(), anyMap(),
				anyString());
		doReturn(new File("Data/TestArffOne.arff")).when(csvToArffConverterMock).convertToArff(any(File.class));
		doReturn(resultsControllerMock).when(fxmlLoaderSpy).getController();
		doReturn(true).when(resultsControllerMock).initData(any(File.class), anyString(), any(), anyBoolean(),
				any(DataMinerService.class), any(RuntimeRecorderService.class), any(FXMLLoader.class));

		clickOn("#recordRuntimeComboBox");
		clickOn("No");
		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 4/4:"));
	}

	@Test
	public void should_continue_to_next_screen_when_not_using_arff_file_and_using_performance_metrics() {
		doReturn(new File("Data/TestCsvOne.csv")).when(preprocessorMock).createPreprocessedFile(anyMap(), anyMap(),
				anyString());
		doReturn(new File("Data/TestArffOne.arff")).when(csvToArffConverterMock).convertToArff(any(File.class));
		doReturn(resultsControllerMock).when(fxmlLoaderSpy).getController();
		doReturn(true).when(resultsControllerMock).initData(any(File.class), anyString(), any(), anyBoolean(),
				any(DataMinerService.class), any(RuntimeRecorderService.class), any(FXMLLoader.class));

		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 4/4:"));
	}

	@Test
	public void should_continue_to_next_screen_when_using_arff_file() {
		doReturn(resultsControllerMock).when(fxmlLoaderSpy).getController();
		doReturn(true).when(resultsControllerMock).initData(any(File.class), anyString(), any(), anyBoolean(),
				any(DataMinerService.class), any(RuntimeRecorderService.class), any(FXMLLoader.class));

		File arffFile = new File("Data/TestArffOne.arff");
		controller.initDataFromSelectFiles(arffFile, preprocessorMock, csvToArffConverterMock, fxmlLoaderSpy);

		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 4/4:"));

	}

	@Test
	public void should_alert_user_that_no_rules_were_found() {
		doReturn(new File("Data/TestCsvOne.csv")).when(preprocessorMock).createPreprocessedFile(anyMap(), anyMap(),
				anyString());
		doReturn(new File("Data/TestArffOne.arff")).when(csvToArffConverterMock).convertToArff(any(File.class));
		doReturn(resultsControllerMock).when(fxmlLoaderSpy).getController();
		doReturn(false).when(resultsControllerMock).initData(any(File.class), anyString(), any(), anyBoolean(),
				any(DataMinerService.class), any(RuntimeRecorderService.class), any(FXMLLoader.class));

		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();
		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));
		assertThat(text.getText(), equalTo("Step 3/4:"));

		clickOn("OK");
	}

	@Test
	public void should_show_error_when_clicking_next_without_proper_configuration() {
		clickOn("#minimumConfidenceTextField").write("a");
		clickOn("#nextButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));

		clickOn("OK");
	}

}
