package controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SelectWantedAttributesControllerIntegrationTest extends ApplicationTest {

	private SelectWantedAttributesController controller;

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

		controller.initData(inputtedFiles);
	}

	@Test
	public void should_test() {

	}

}
