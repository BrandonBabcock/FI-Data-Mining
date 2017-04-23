package application;

import java.io.IOException;

import controller.SelectFilesController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Main class to start the application and GUI
 */
public class Main extends Application {

	/**
	 * Launches the GUI to the first screen
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SelectFiles.fxml"));
			BorderPane screen = (BorderPane) loader.load();
			
			SelectFilesController controller = loader.getController();
			controller.initData(new FXMLLoader());
			
			Scene scene = new Scene(screen);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Fischer International Data Mining");
			primaryStage.show();
		} catch (IOException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

	/**
	 * Starts the application
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}