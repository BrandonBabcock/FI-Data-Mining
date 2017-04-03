package controller;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Rule;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.PreprocessingService;

public class SelectWantedAttributesControllerIntegrationTest extends ApplicationTest {

	@Rule
	public EasyMockRule rule = new EasyMockRule(this);

	@TestSubject
	private SelectWantedAttributesController controller = new SelectWantedAttributesController();

	@Mock
	private PreprocessingService mockPreprocessor;

	@Override
	public void start(Stage primaryStage) {
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

	private void initializeController() {
		ArrayList<Path> inputtedFiles = new ArrayList<Path>();
		inputtedFiles.add(Paths.get("Data/TestXmlOne.xml"));
		inputtedFiles.add(Paths.get("Data/TestCsvOne.csv"));

		ArrayList<Path> convertedFIles = new ArrayList<Path>();
		convertedFIles.add(Paths.get("Data/TestXmlOne.csv"));
		convertedFIles.add(Paths.get("Data/TestCsvOne.csv"));

		HashMap<Path, ArrayList<String>> attributesToFilesMap = new HashMap<Path, ArrayList<String>>();

		ArrayList<String> firstFileValues = new ArrayList<String>();
		firstFileValues.add("author");
		firstFileValues.add("title");
		firstFileValues.add("genre");
		firstFileValues.add("price");
		firstFileValues.add("publish_date");
		firstFileValues.add("description");

		ArrayList<String> secondFileValues = new ArrayList<String>();
		secondFileValues.add("attributeOne");
		secondFileValues.add("attributeTwo");
		secondFileValues.add("attributeThree");

		attributesToFilesMap.put(Paths.get("Data/TestXmlOne.csv"), firstFileValues);
		attributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), secondFileValues);

		expect(mockPreprocessor.convertXmlToCsv(inputtedFiles)).andReturn(convertedFIles);
		expect(mockPreprocessor.mapAllAttributesToFiles(convertedFIles)).andReturn(attributesToFilesMap);
		replay(mockPreprocessor);

		controller.initData(mockPreprocessor, inputtedFiles);
	}

	@Test
	public void should_initialize_controller_with_correct_files() {
		Text firstFileText = lookup("#currentFileName").query();
		String firstFileName = firstFileText.getText();

		clickOn("author");
		clickOn("#nextButton");

		Text secondFileText = lookup("#currentFileName").query();
		String secondFileName = secondFileText.getText();

		assertThat(firstFileName, equalTo("TestXmlOne.csv"));
		assertThat(secondFileName, equalTo("TestCsvOne.csv"));
	}

}
