//package controller;
//
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.junit.Assert.assertThat;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Test;
//import org.testfx.framework.junit.ApplicationTest;
//
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.layout.BorderPane;
//import javafx.stage.Stage;
//
//public class ConfigurationControllerIntegrationTest extends ApplicationTest {
//
//	private ConfigurationController controller;
//
//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Configuration.fxml"));
//			BorderPane screen = (BorderPane) loader.load();
//			controller = loader.getController();
//			initializeController();
//
//			Scene scene = new Scene(screen);
//			primaryStage.setScene(scene);
//			primaryStage.setTitle("Fischer International Data Mining");
//			primaryStage.show();
//		} catch (IOException e) {
//			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
//		}
//	}
//
//	private void initializeController() throws Exception {
//		Map<Path, List<String>> wantedAttributesToFileMap = new HashMap<Path, List<String>>();
//		Map<Path, List<String>> allAttributesToFilesMap = new HashMap<Path, List<String>>();
//
//		List<String> wantedAttributesList = new ArrayList<String>();
//		wantedAttributesList.add("attributeOne");
//		wantedAttributesList.add("attributeTwo");
//
//		ArrayList<String> allAttributesList = new ArrayList<String>();
//		allAttributesList.add("attributeOne");
//		allAttributesList.add("attributeTwo");
//		allAttributesList.add("attributeThree");
//
//		wantedAttributesToFileMap.put(Paths.get("Data/TestCsvOne.csv"), wantedAttributesList);
//		allAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), allAttributesList);
//
//		controller.initData(wantedAttributesToFileMap, allAttributesToFilesMap);
//	}
//
//	@Test
//	public void should_create_preprocessed_file() {
//		File file = new File("Data/PreprocessedFile.csv");
//		file.delete();
//
//		clickOn("#groupByAttributeComboBox");
//		clickOn("attributeOne");
//
//		clickOn("#algorithmComboBox");
//		clickOn("Apriori");
//
//		clickOn("#recordRuntimeComboBox");
//		clickOn("Yes");
//
//		clickOn("#nextButton");
//
//		boolean fileCreated = new File("Data/PreprocessedFile.csv").exists();
//
//		assertThat(fileCreated, equalTo(true));
//
//		file.delete();
//	}
//
//}
