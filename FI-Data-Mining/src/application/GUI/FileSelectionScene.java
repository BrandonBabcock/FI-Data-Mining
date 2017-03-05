package application.GUI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Created by shawn on 3/5/2017.
 */
public class FileSelectionScene {

    String fileName;
    Button nextButton;
    File selectedFile;

    public FileSelectionScene(){
        nextButton = new Button();
    }

    /**
     * Temporary method to create the scene for step one until a proper project
     * structure is decided
     *
     * @return the first scene
     */
    public Scene fileSelection() {
        // Configure the border pane
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20, 5, 5, 5));

        // Create the header and add it to the border pane
        Text headerText = new Text("Fischer International Data Mining");
        Font headerFont = Font.font("Times New Roman", FontWeight.BOLD, 48);
        headerText.setFont(headerFont);
        borderPane.setTop(headerText);
        BorderPane.setAlignment(headerText, Pos.TOP_CENTER);

        // Create elements and add them to border pane's center
        Font stepInfoFont = Font.font("Times New Roman", FontWeight.BOLD, 28);
        Text currentStepText = new Text("Step 1/3:");
        currentStepText.setFont(stepInfoFont);
        Text stepDescriptionText = new Text("Choose a File");
        stepDescriptionText.setFont(stepInfoFont);
        TextField fileInputField = new TextField();
        Button chooseFileButton = new Button("Choose File");
        nextButton = new Button("Next");
        nextButton.setDisable(true);
        nextButton.setPrefSize(100, 20);

        // Add file inputting from File Explorer on file input field
        chooseFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                selectedFile = fileChooser.showOpenDialog(null);
                if (selectedFile != null && checkFileType(selectedFile)) {
                    fileName = selectedFile.getAbsolutePath();
                    fileInputField.setText(fileName);
                    nextButton.setDisable(false);
                }
            }
        });

        // Add step info to a vertical box
        VBox stepInfoBox = new VBox();
        stepInfoBox.setAlignment(Pos.TOP_CENTER);
        stepInfoBox.setPadding(new Insets(200, 0, 25, 0));
        stepInfoBox.getChildren().addAll(currentStepText, stepDescriptionText);

        // Add file input pieces to a horizontal box
        HBox fileInputBox = new HBox(5);
        fileInputBox.setAlignment(Pos.TOP_CENTER);
        fileInputBox.getChildren().addAll(fileInputField, chooseFileButton);

        // Add navigation buttons to a horizontal box
        HBox navigationBox = new HBox();
        navigationBox.setAlignment(Pos.TOP_CENTER);
        navigationBox.setPadding(new Insets(100, 0, 0, 0));
        navigationBox.getChildren().add(nextButton);

        // Add step info box, file input pieces, and navigation buttons box
        // to a vertical box container
        VBox centerContent = new VBox();
        centerContent.getChildren().addAll(stepInfoBox, fileInputBox, navigationBox);

        // Sets the border pane's center as centerContent
        borderPane.setCenter(centerContent);

        // Configure scene
        Scene scene = new Scene(borderPane, 1080, 720);

        return scene;
    }

    /**
     * Method used to check file type
     * @param file file we wish to check
     * @return true if the file type is supported and false if the file is not supported
     */
    public boolean checkFileType(File file){

        String fileName = file.getName().toLowerCase();

        if(fileName.contains(".csv")){
            return true;
        } else if (fileName.contains(".xml")){
            return true;
        } else if (fileName.contains(".arff")){
            return true;
        }

        return false;
    }
}
