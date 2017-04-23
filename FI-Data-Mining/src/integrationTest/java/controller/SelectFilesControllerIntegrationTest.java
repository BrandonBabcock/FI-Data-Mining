//package controller;
//
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.junit.Assert.assertThat;
//
//import java.io.IOException;
//
//import org.junit.Test;
//import org.testfx.framework.junit.ApplicationTest;
//
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//
//public class SelectFilesControllerIntegrationTest extends ApplicationTest {
//
//	@Override
//	public void start(Stage primaryStage) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SelectFiles.fxml"));
//			BorderPane screen = (BorderPane) loader.load();
//			Scene scene = new Scene(screen);
//			primaryStage.setScene(scene);
//			primaryStage.setTitle("Fischer International Data Mining");
//			primaryStage.show();
//		} catch (IOException e) {
//			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
//		}
//	}
//
//	@Test
//	public void should_use_inputted_files_for_next_screen() {
//		clickOn("#fileTextField").write("Data/TestCsvOne.csv");
//		clickOn("#addFileButton");
//
//		clickOn("#fileTextField").write("Data/TestXmlOne.xml");
//		clickOn("#addFileButton");
//
//		clickOn("#nextButton");
//
//		Text firstFileName = lookup("#currentFileName").query();
//		String firstFileNameString = firstFileName.getText();
//
//		clickOn("#selectAllButton");
//		clickOn("#nextButton");
//
//		Text secondFileName = lookup("#currentFileName").query();
//		String secondFileNameString = secondFileName.getText();
//
//		assertThat(firstFileNameString, equalTo("TestCsvOne.csv"));
//		assertThat(secondFileNameString, equalTo("TestXmlOne.xml"));
//	}
//
//}
