package controller;

import java.io.IOException;

import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SelectFilesControllerIntegrationTest extends ApplicationTest {

	private SelectFilesController controller;

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
	public void should_test() {

	}

}
