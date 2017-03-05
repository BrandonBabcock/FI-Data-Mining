package application.GUI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

/**
 * TODO: Add class documentation
 */
public class GUI extends Application {

    private Stage stage;
    private int currentStepNumber = 1;
    private Scene stepOneScene, stepTwoScene, getStepThreeScene;
    FileSelectionScene fileSelection;
    ConfigurationScene configuration;
    PreProcessingScene preprocess;
    File dataFile;

    /*
     * (non-Javadoc)
	 *
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
    @Override
    public void start(Stage primaryStage) {
        try {

            initComponents();

            stage = primaryStage;

            // Configure and display the stage
            stage.setTitle("Fischer International Data Mining");
            stage.setScene(stepOneScene);
            stage.show();

        } catch (Exception e) {
            System.out.println("An exception has been thrown.");
            e.printStackTrace();
        }
    }

    public void initComponents(){
        fileSelection = new FileSelectionScene();
        stepOneScene = fileSelection.fileSelection();
        fileSelection.nextButton.setOnAction(e -> goToNextStep());

        preprocess = new PreProcessingScene();
        stepTwoScene = preprocess.preProcessingScene();
        preprocess.nextButton.setOnAction(e -> goToNextStep());
        preprocess.previousButton.setOnAction(e -> goToPreviousStep());

        configuration = new ConfigurationScene();
        getStepThreeScene = configuration.configurationScene();
        configuration.nextButton.setOnAction(e -> goToNextStep());
        configuration.previousButton.setOnAction(e -> goToPreviousStep());
    }

    private void goToNextStep() {
        switch (currentStepNumber) {
            case 1:
                stage.setScene(stepTwoScene);
                currentStepNumber++;
                break;
            case 2:
                stage.setScene(getStepThreeScene);
                currentStepNumber++;
                break;
            default:
                System.out.println("Next step does not exist.");
                break;
        }
    }

    private void goToPreviousStep() {
        switch (currentStepNumber) {
            case 2:
                stage.setScene(stepOneScene);
                currentStepNumber--;
                break;
            case 3:
                stage.setScene(stepTwoScene);
                currentStepNumber--;
                break;
            default:
                System.out.println("Previous step does not exist.");
                break;
        }
    }

    /**
     * Launches the application
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
