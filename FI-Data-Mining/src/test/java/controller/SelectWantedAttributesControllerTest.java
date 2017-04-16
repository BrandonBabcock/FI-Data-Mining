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

import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.PreprocessorService;

@PrepareForTest(SelectWantedAttributesController.class)
public class SelectWantedAttributesControllerTest extends ApplicationTest {

	private SelectWantedAttributesController controller;

	@Spy
	private final PreprocessorService preprocessorSpy = new PreprocessorService();

	@Spy
	private final FXMLLoader fxmlLoaderSpy = new FXMLLoader(getClass().getResource("/view/Configuration.fxml"));

	@Spy
	private final ConfigurationController configurationControllerSpy = new ConfigurationController();

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			MockitoAnnotations.initMocks(this);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SelectWantedAttributes.fxml"));
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
		ArrayList<Path> inputtedFiles = new ArrayList<Path>();
		inputtedFiles.add(Paths.get("Data/TestCsvOne.csv"));
		inputtedFiles.add(Paths.get("Data/TestCsvTwo.csv"));

		HashMap<Path, ArrayList<String>> allAttributesToFilesMap = new HashMap<Path, ArrayList<String>>();
		ArrayList<String> firstFileAttributes = new ArrayList<String>();
		ArrayList<String> secondFileAttributes = new ArrayList<String>();

		firstFileAttributes.add("attributeOne");
		firstFileAttributes.add("attributeTwo");
		firstFileAttributes.add("attributeThree");

		secondFileAttributes.add("attributeOne");
		secondFileAttributes.add("attributeTwo");
		secondFileAttributes.add("attributeThree");
		secondFileAttributes.add("attributeFour");

		allAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), firstFileAttributes);
		allAttributesToFilesMap.put(Paths.get("Data/TestCsvTwo.csv"), secondFileAttributes);

		whenNew(PreprocessorService.class).withNoArguments().thenReturn(preprocessorSpy);
		doReturn(inputtedFiles).when(preprocessorSpy).convertXmlToCsv(isA(ArrayList.class));
		doReturn(allAttributesToFilesMap).when(preprocessorSpy).mapAllAttributesToFiles(isA(ArrayList.class));

		controller.initData(inputtedFiles);
	}

	@Test
	public void should_have_first_file_in_list_as_current_file_when_screen_is_loaded() {
		Text text = lookup("#currentFileName").query();

		assertThat(text.getText(), equalTo("TestCsvOne.csv"));
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

		assertThat(text.getText(), equalTo("Step 2/4:"));
	}

	@Test
	public void should_continue_to_next_file_in_list_on_same_step() {
		clickOn("attributeOne");
		clickOn("#nextButton");

		Text fileName = lookup("#currentFileName").query();

		Text stepNumber = lookup("#stepNumberText").query();

		assertThat(fileName.getText(), equalTo("TestCsvTwo.csv"));
		assertThat(stepNumber.getText(), equalTo("Step 2/4:"));
	}

	@Test
	public void should_continue_to_next_screen() throws Exception {
		whenNew(FXMLLoader.class).withArguments(getClass().getResource("/view/Configuration.fxml"))
				.thenReturn(fxmlLoaderSpy);
		doReturn(configurationControllerSpy).when(fxmlLoaderSpy).getController();
		doNothing().when(configurationControllerSpy).initData(isA(HashMap.class), isA(HashMap.class));

		clickOn("attributeOne");
		clickOn("#nextButton");

		clickOn("attributeOne");
		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 3/4:"));
	}

	@Test
	public void should_show_error_when_clicking_next_without_selecting_any_attributes() {
		clickOn("#nextButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));

		clickOn("OK");
	}

	@Test()
	public void should_select_all_attributes() {
		clickOn("#selectAllButton");

		VBox attributesVbox = lookup("#attributesVbox").query();
		boolean allChecked = true;

		for (Node child : attributesVbox.getChildren()) {
			if (child instanceof CheckBox) {
				if (!((CheckBox) child).isSelected()) {
					allChecked = false;
				}
			}
		}

		assertThat(allChecked, equalTo(true));
	}

	@Test
	public void should_unselect_all_attributes() {
		clickOn("#selectAllButton");
		clickOn("#unselectAllButton");

		VBox attributesVbox = lookup("#attributesVbox").query();
		boolean allUnchecked = true;

		for (Node child : attributesVbox.getChildren()) {
			if (child instanceof CheckBox) {
				if (((CheckBox) child).isSelected()) {
					allUnchecked = false;
				}
			}
		}

		assertThat(allUnchecked, equalTo(true));
	}

}
