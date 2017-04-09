package controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@PrepareForTest(SelectFilesController.class)
public class SelectFilesControllerTest extends ApplicationTest {

	private SelectFilesController controller;

	@Spy
	private FXMLLoader fxmlLoaderSpy = new FXMLLoader(getClass().getResource("/view/SelectWantedAttributes.fxml"));

	@Spy
	private SelectWantedAttributesController selectWantedAttributesControllerSpy = new SelectWantedAttributesController();

	@Override
	public void start(Stage primaryStage) {
		try {
			MockitoAnnotations.initMocks(this);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SelectFiles.fxml"));
			BorderPane screen = (BorderPane) loader.load();
			controller = loader.getController();
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
	public void should_allow_multiple_files_to_be_inputted() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#fileTextField").write("Data/TestCsvTwo.csv");
		clickOn("#addFileButton");

		assertThat(controller.getInputtedFiles(), hasItem(Paths.get("Data/TestCsvOne.csv")));
		assertThat(controller.getInputtedFiles(), hasItem(Paths.get("Data/TestCsvTwo.csv")));
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

		clickOn("OK");
	}

	@Test
	public void should_remove_last_added_file_path_from_list_of_inputted_files() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#fileTextField").write("Data/TestCsvTwo.csv");
		clickOn("#addFileButton");
		clickOn("#removeLastFileButton");

		assertThat(controller.getInputtedFiles().contains(Paths.get("Data/TestCsvTwo.csv")), equalTo(false));
		assertThat(controller.getInputtedFiles(), hasItem(Paths.get("Data/TestCsvOne.csv")));
		assertThat(controller.getInputtedFiles().size(), equalTo(1));
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

		assertThat(controller.getInputtedFiles().size(), equalTo(0));
	}

	@Test
	public void should_continue_to_next_screen() throws Exception {
		whenNew(FXMLLoader.class).withArguments(getClass().getResource("/view/SelectWantedAttributes.fxml"))
				.thenReturn(fxmlLoaderSpy);
		doReturn(selectWantedAttributesControllerSpy).when(fxmlLoaderSpy).getController();
		doNothing().when(selectWantedAttributesControllerSpy).initData(isA(ArrayList.class));

		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 2/4:"));
	}

	@Test
	public void should_show_error_when_clicking_next_without_adding_any_files() {
		clickOn("#nextButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));

		clickOn("OK");
	}

}
