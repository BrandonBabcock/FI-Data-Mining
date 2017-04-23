package controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.PreprocessorService;

@RunWith(MockitoJUnitRunner.class)
public class SelectFilesControllerTest extends ApplicationTest {

	private SelectFilesController controller;

	@Spy
	private FXMLLoader fxmlLoaderSpy;

	@Mock
	private SelectWantedAttributesController selectWantedAttributesControllerMock;

	@Mock
	private ConfigurationController configurationControllerMock;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SelectFiles.fxml"));
			BorderPane screen = (BorderPane) loader.load();
			controller = loader.getController();
			controller.initData(fxmlLoaderSpy);
			Scene scene = new Scene(screen);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Fischer International Data Mining");
			primaryStage.show();
		} catch (IOException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

	@Test
	public void should_clear_text_field() {
		clickOn("#fileTextField").write("test text");
		clickOn("#clearFieldButton");

		verifyThat("#fileTextField", hasText(""));
	}

	@Test
	public void should_allow_adding_of_a_csv_file() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");

		assertThat(controller.getInputtedFiles(), hasItem(Paths.get("Data/TestCsvOne.csv")));
		assertThat(controller.getInputtedFiles().size(), equalTo(1));
	}

	@Test
	public void should_allow_adding_of_a_xml_file() {
		clickOn("#fileTextField").write("Data/TestXmlOne.xml");
		clickOn("#addFileButton");

		assertThat(controller.getInputtedFiles(), hasItem(Paths.get("Data/TestXmlOne.xml")));
		assertThat(controller.getInputtedFiles().size(), equalTo(1));
	}

	@Test
	public void should_allow_adding_of_an_arff_file() {
		clickOn("#fileTextField").write("Data/TestArffOne.arff");
		clickOn("#addFileButton");

		Node confirmationDialog = lookup(".alert").query();

		assertThat(confirmationDialog.isVisible(), equalTo(true));

		clickOn("Cancel");
	}

	@Test
	public void should_allow_multiple_files_to_be_inputted() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#fileTextField").write("Data/TestXmlOne.xml");
		clickOn("#addFileButton");

		assertThat(controller.getInputtedFiles(), hasItem(Paths.get("Data/TestCsvOne.csv")));
		assertThat(controller.getInputtedFiles(), hasItem(Paths.get("Data/TestXmlOne.xml")));
		assertThat(controller.getInputtedFiles().size(), equalTo(2));
	}

	@Test
	public void should_show_error_dialog_when_adding_file_with_invalid_type() {
		clickOn("#fileTextField").write("Data/InvalidFileType.txt");
		clickOn("#addFileButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));
		assertThat(controller.getInputtedFiles().isEmpty(), equalTo(true));

		clickOn("OK");
	}

	@Test
	public void should_show_error_dialog_when_adding_nonexisting_file() {
		clickOn("#fileTextField").write("Data/NonexistingFile.csv");
		clickOn("#addFileButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));
		assertThat(controller.getInputtedFiles().isEmpty(), equalTo(true));

		clickOn("OK");
	}

	@Test
	public void should_show_error_dialog_when_adding_same_file_twice() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));
		assertThat(controller.getInputtedFiles().size(), equalTo(1));

		clickOn("OK");
	}

	@Test
	public void should_remove_last_added_file_path_from_list_of_inputted_files() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#fileTextField").write("Data/TestCsvTwo.csv");
		clickOn("#addFileButton");
		clickOn("#removeLastFileButton");

		Node confirmationDialog = lookup(".alert").query();

		assertThat(confirmationDialog.isVisible(), equalTo(true));
		assertThat(controller.getInputtedFiles().contains(Paths.get("Data/TestCsvTwo.csv")), equalTo(false));
		assertThat(controller.getInputtedFiles(), hasItem(Paths.get("Data/TestCsvOne.csv")));
		assertThat(controller.getInputtedFiles().size(), equalTo(1));

		clickOn("OK");
	}

	@Test
	public void should_show_error_when_removing_last_file_when_no_files_have_been_added() {
		clickOn("#removeLastFileButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));

		clickOn("OK");
	}

	@Test
	public void should_remove_all_added_file_paths_from_list_of_inputted_files() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#fileTextField").write("Data/TestCsvTwo.csv");
		clickOn("#addFileButton");
		clickOn("#removeAllFilesButton");

		Node confirmationDialog = lookup(".alert").query();

		assertThat(confirmationDialog.isVisible(), equalTo(true));
		assertThat(controller.getInputtedFiles().size(), equalTo(0));

		clickOn("OK");
	}

	@Test
	public void should_show_error_when_removing_all_files_when_no_files_have_been_added() {
		clickOn("#removeAllFilesButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));

		clickOn("OK");
	}

	@Test
	public void should_show_error_when_clicking_next_without_adding_any_files() {
		clickOn("#nextButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));

		clickOn("OK");
	}

	@Test
	public void should_continue_to_next_screen() throws Exception {
		doReturn(selectWantedAttributesControllerMock).when(fxmlLoaderSpy).getController();
		doNothing().when(selectWantedAttributesControllerMock).initData(anyList(), any(PreprocessorService.class),
				any(FXMLLoader.class));

		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 2/4:"));
	}

	@Test
	public void should_continue_to_configuration_screen_when_adding_arff_file() throws Exception {
		doReturn(configurationControllerMock).when(fxmlLoaderSpy).getController();
		doNothing().when(configurationControllerMock).initDataFromSelectFiles(any(File.class),
				any(PreprocessorService.class), any(CsvToArffConverter.class), any(FXMLLoader.class));

		clickOn("#fileTextField").write("Data/TestArffOne.arff");
		clickOn("#addFileButton");
		clickOn("OK");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 3/4:"));
	}

}
