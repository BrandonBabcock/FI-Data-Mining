package controller;

import java.io.IOException;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;

public class SelectFilesControllerTest extends ApplicationTest {

	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane screen = (BorderPane) FXMLLoader.load(getClass().getResource("/view/SelectFiles.fxml"));
			Scene scene = new Scene(screen);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Fischer International Data Mining");
			primaryStage.show();
		} catch (IOException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

	@Test
	public void should_clear_text_field_when_clear_button_is_pressed() {
		clickOn("#fileTextField").write("test text");
		clickOn("#clearFieldButton");

		verifyThat("#fileTextField", hasText(""));
	}

}
