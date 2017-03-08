package application.GUI;

import javafx.application.Application;
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
    private FileSelectionScene fileSelectionScene;
    private ConfigurationScene configuration;
    private PreProcessingScene preprocessScene;
    private File dataFile;

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
        fileSelectionScene = new FileSelectionScene();
        stepOneScene = fileSelectionScene.fileSelection();
        fileSelectionScene.nextButton.setOnAction(e -> goToNextStep());

        preprocessScene = new PreProcessingScene();
        stepTwoScene = preprocessScene.preProcessingScene();
        preprocessScene.nextButton.setOnAction(e -> {
            goToNextStep();
            preprocessScene.processFile();
        });
        preprocessScene.previousButton.setOnAction(e -> goToPreviousStep());

        configuration = new ConfigurationScene();
        getStepThreeScene = configuration.configurationScene();
        configuration.nextButton.setOnAction(e -> goToNextStep());
        configuration.previousButton.setOnAction(e -> goToPreviousStep());
    }

    private void goToNextStep() {
        switch (currentStepNumber) {
            case 1:
                preprocessScene.setDataFile(fileSelectionScene.selectedFile);
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
