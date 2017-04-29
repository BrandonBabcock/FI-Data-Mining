package controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.PreprocessorService;

public class SelectWantedAttributesControllerIntegrationTest extends ApplicationTest {

	private SelectWantedAttributesController controller;

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
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
		inputtedFiles.add(Paths.get("Data/TestXmlOne.xml"));

		controller.initData(inputtedFiles, new PreprocessorService(), new FXMLLoader());
	}

	@Test
	public void should_display_correct_file_attributes() {
		ArrayList<String> firstFileAttributes = new ArrayList<String>();
		VBox attributesVbox = lookup("#attributesVbox").query();

		for (Node child : attributesVbox.getChildren()) {
			if (child instanceof CheckBox) {
				firstFileAttributes.add(((CheckBox) child).getText());
			}
		}

		clickOn("#selectAllButton");
		clickOn("#nextButton");

		ArrayList<String> secondFileAttributes = new ArrayList<String>();
		attributesVbox = lookup("#attributesVbox").query();

		for (Node child : attributesVbox.getChildren()) {
			if (child instanceof CheckBox) {
				secondFileAttributes.add(((CheckBox) child).getText());
			}
		}

		assertThat(firstFileAttributes.size(), equalTo(3));
		assertThat(firstFileAttributes.contains("attributeOne"), equalTo(true));
		assertThat(firstFileAttributes.contains("attributeTwo"), equalTo(true));
		assertThat(firstFileAttributes.contains("attributeThree"), equalTo(true));

		assertThat(secondFileAttributes.size(), equalTo(7));
		assertThat(secondFileAttributes.contains("author"), equalTo(true));
		assertThat(secondFileAttributes.contains("title"), equalTo(true));
		assertThat(secondFileAttributes.contains("genre"), equalTo(true));
		assertThat(secondFileAttributes.contains("price"), equalTo(true));
		assertThat(secondFileAttributes.contains("publish_date"), equalTo(true));
		assertThat(secondFileAttributes.contains("description"), equalTo(true));
		assertThat(secondFileAttributes.contains("attributeOne"), equalTo(true));
	}

	@Test
	public void should_display_correct_group_by_attribute_from_selected_attributes() {
		clickOn("attributeOne");
		clickOn("#nextButton");

		clickOn("attributeOne");
		clickOn("#nextButton");

		ComboBox<String> comboBox = lookup("#groupByAttributeComboBox").query();

		assertThat(comboBox.getItems().size(), equalTo(1));
		assertThat(comboBox.getItems().contains("attributeOne"), equalTo(true));
	}

	@Test
	public void should_not_change_steps_after_canceling_restart_and_continuing_with_no_selections() {
		clickOn("#restartButton");
		clickOn("Cancel");
		clickOn("#nextButton");
		clickOn("OK");

		Text text = lookup("#stepNumberText").query();
		Text firstFileName = lookup("#currentFileName").query();
		String firstFileNameString = firstFileName.getText();

		assertThat(firstFileNameString, equalTo("TestCsvOne.csv"));
		assertThat(text.getText(), equalTo("Step 2/4:"));
	}

	@Test
	public void should_not_be_able_to_continue_after_unselecting_attributes() {
		clickOn("#selectAllButton");
		clickOn("#unselectAllButton");
		clickOn("#nextButton");
		clickOn("OK");

		Text text = lookup("#stepNumberText").query();
		Text firstFileName = lookup("#currentFileName").query();
		String firstFileNameString = firstFileName.getText();

		assertThat(firstFileNameString, equalTo("TestCsvOne.csv"));
		assertThat(text.getText(), equalTo("Step 2/4:"));
	}

	@Test
	public void should_be_able_to_continue_to_configuration() {
		clickOn("#selectAllButton");
		clickOn("#nextButton");
		clickOn("#selectAllButton");
		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 3/4:"));
	}

	@Test
	public void should_be_able_to_continue_to_select_wanted_attributes_after_restarting_to_select_files() {
		clickOn("#restartButton");
		clickOn("OK");
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 2/4:"));
	}

	@Test
	public void should_find_rules_of_correct_attributes() {
		clickOn("attributeOne");
		clickOn("attributeTwo");
		clickOn("#nextButton");
		clickOn("attributeOne");
		clickOn("#nextButton");
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
	public void should_not_find_any_common_attributes() {
		clickOn("attributeOne");
		clickOn("#nextButton");
		clickOn("author");
		clickOn("#nextButton");
		clickOn("OK");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 2/4:"));
	}

}
