package controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
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

public class SelectFilesControllerIntegrationTest extends ApplicationTest {

	private SelectFilesController controller;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SelectFiles.fxml"));
			BorderPane screen = (BorderPane) loader.load();
			controller = loader.getController();
			controller.initData(new FXMLLoader());
			Scene scene = new Scene(screen);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Fischer International Data Mining");
			primaryStage.show();
		} catch (IOException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

	@Test
	public void should_use_inputted_files_for_select_wanted_attributes_screen() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");

		clickOn("#fileTextField").write("Data/TestXmlOne.xml");
		clickOn("#addFileButton");

		clickOn("#nextButton");

		Text firstFileName = lookup("#currentFileName").query();
		String firstFileNameString = firstFileName.getText();

		clickOn("#selectAllButton");
		clickOn("#nextButton");

		Text secondFileName = lookup("#currentFileName").query();
		String secondFileNameString = secondFileName.getText();

		assertThat(firstFileNameString, equalTo("TestCsvOne.csv"));
		assertThat(secondFileNameString, equalTo("TestXmlOne.xml"));
	}

	@Test
	public void should_load_attributes_from_user_inputted_file() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#nextButton");

		VBox attributesVbox = lookup("#attributesVbox").query();
		List<String> attributes = new ArrayList<String>();

		for (Node child : attributesVbox.getChildren()) {
			if (child instanceof CheckBox) {
				attributes.add(((CheckBox) child).getText());
			}
		}

		assertThat(attributes.get(0), equalTo("attributeOne"));
		assertThat(attributes.get(1), equalTo("attributeTwo"));
		assertThat(attributes.get(2), equalTo("attributeThree"));
	}
	
	@Test
	public void should_load_correct_attributes_for_xml_file() {
		clickOn("#fileTextField").write("Data/bio.xml");
		clickOn("#addFileButton");
		clickOn("#nextButton");

		VBox attributesVbox = lookup("#attributesVbox").query();
		List<String> attributes = new ArrayList<String>();

		for (Node child : attributesVbox.getChildren()) {
			if (child instanceof CheckBox) {
				attributes.add(((CheckBox) child).getText());
			}
		}

		assertThat(attributes.get(0).contains("BANNER_ID"), equalTo(true));
		assertThat(attributes.get(1).contains("DEPT"), equalTo(true));
		assertThat(attributes.get(2).contains("DEPT_FILE_SERVER"), equalTo(true));
	}

	@Test
	public void should_skip_to_the_configuration_screen_if_user_accepts_when_adding_arff_file() {
		clickOn("#fileTextField").write("Data/TestArffOne.arff");
		clickOn("#addFileButton");
		clickOn("OK");

		Text text = lookup("#stepNumberText").query();
		ComboBox<String> groupByAttributeComboBox = lookup("#groupByAttributeComboBox").query();

		assertThat(groupByAttributeComboBox.isDisabled(), equalTo(true));
		assertThat(text.getText(), equalTo("Step 3/4:"));
	}

	@Test
	public void should_use_inputted_arff_file_when_doing_data_mininig() {
		clickOn("#fileTextField").write("Data/TestArffOne.arff");
		clickOn("#addFileButton");
		clickOn("OK");
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
	}

	@Test
	public void should_not_allow_user_to_continue_after_removing_last_file_with_one_file_inputted() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#removeLastFileButton");
		clickOn("OK");
		clickOn("#nextButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));

		clickOn("OK");
	}

	@Test
	public void should_not_allow_user_to_continue_after_removing_all_file_with_more_than_one_file_inputted() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#fileTextField").write("Data/TestCsvTwo.csv");
		clickOn("#addFileButton");
		clickOn("#removeAllFilesButton");
		clickOn("OK");
		clickOn("#nextButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));

		clickOn("OK");
	}

	@Test
	public void should_be_able_to_restart_when_skipping_to_configuration_screen() {
		clickOn("#fileTextField").write("Data/TestArffOne.arff");
		clickOn("#addFileButton");
		clickOn("OK");
		clickOn("#restartButton");
		clickOn("OK");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 1/4:"));
	}

	@Test
	public void should_be_able_to_continue_to_results_screen_after_skipping_to_configuration_screen() {
		clickOn("#fileTextField").write("Data/TestArffOne.arff");
		clickOn("#addFileButton");
		clickOn("OK");
		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 4/4:"));
	}

	@Test
	public void should_not_add_file_after_clicking_clear_field_button() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#clearFieldButton");
		clickOn("#addFileButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));
		assertThat(controller.getInputtedFiles().isEmpty(), equalTo(true));

		clickOn("OK");
	}

	@Test
	public void should_not_have_files_to_remove_after_removing_all_files() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#fileTextField").write("Data/TestCsvTwo.csv");
		clickOn("#addFileButton");
		clickOn("#removeAllFilesButton");
		clickOn("OK");
		clickOn("#removeAllFilesButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));

		clickOn("OK");
	}

	@Test
	public void should_not_have_files_to_remove_after_removing_last_file_with_one_file_inputted() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#removeLastFileButton");
		clickOn("OK");
		clickOn("#removeLastFileButton");

		Node errorDialog = lookup(".alert").query();

		assertThat(errorDialog.isVisible(), equalTo(true));

		clickOn("OK");
	}

	@Test
	public void should_have_not_files_added_when_adding_invalid_file_type() {
		clickOn("#fileTextField").write("Data/InvalidFileType.txt");
		clickOn("#addFileButton");
		clickOn("OK");

		assertThat(controller.getInputtedFiles().isEmpty(), equalTo(true));
	}

	@Test
	public void should_have_one_file_added_when_adding_duplicate_file() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("OK");

		assertThat(controller.getInputtedFiles().size(), equalTo(1));
	}

	@Test
	public void should_stay_on_select_files_screen_and_allow_adding_of_files_after_declining_to_continue_to_configuration() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#fileTextField").write("Data/TestArffOne.arff");
		clickOn("#addFileButton");
		clickOn("Cancel");
		clickOn("#fileTextField").write("Data/TestCsvTwo.csv");
		clickOn("#addFileButton");

		assertThat(controller.getInputtedFiles().size(), equalTo(2));
	}

	@Test
	public void should_be_able_to_restart_after_continuing_to_select_wanted_attributes_screen() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#nextButton");
		clickOn("#restartButton");
		clickOn("OK");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 1/4:"));
	}

	@Test
	public void should_be_able_to_continue_to_the_configuration_screen_after_continuing_to_select_wanted_attributes_screen() {
		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
		clickOn("#addFileButton");
		clickOn("#nextButton");
		clickOn("#selectAllButton");
		clickOn("#nextButton");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 3/4:"));
	}

	@Test
	public void should_have_correct_attributes_when_using_xml_file() {
		clickOn("#fileTextField").write("Data/TestXmlOne.xml");
		clickOn("#addFileButton");
		clickOn("#nextButton");

		VBox attributesVbox = lookup("#attributesVbox").query();
		List<String> attributes = new ArrayList<String>();

		for (Node child : attributesVbox.getChildren()) {
			if (child instanceof CheckBox) {
				attributes.add(((CheckBox) child).getText());
			}
		}

		assertThat(attributes.get(0), equalTo("author"));
		assertThat(attributes.get(1), equalTo("title"));
		assertThat(attributes.get(2), equalTo("genre"));
		assertThat(attributes.get(3), equalTo("price"));
		assertThat(attributes.get(4), equalTo("publish_date"));
		assertThat(attributes.get(5), equalTo("description"));
		assertThat(attributes.get(6), equalTo("attributeOne"));
	}

}
