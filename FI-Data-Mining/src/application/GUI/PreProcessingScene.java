package application.GUI;

import application.DataMining.Preprocessor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by shawn on 3/5/2017.
 */
public class PreProcessingScene {

    Button previousButton;
    Button nextButton;
    File dataFile;
    VBox centerContent;
    Preprocessor preprocessor;
    ArrayList<RadioButton> radiobuttons;
    ArrayList<CheckBox> checkBoxes;

    public PreProcessingScene(){
        previousButton = new Button("Previous");
        nextButton = new Button("Next");
        nextButton.setDisable(true);
    }

    public void setDataFile(File file){
        this.dataFile = file;
        preprocessor = new Preprocessor(file);
        fileAttributes();
        centerContent.getChildren().add(createGroupByPanel());
        centerContent.getChildren().add(createWantedAttributePanel());
        centerContent.getChildren().add(createNavigationBox());
    }

    public void fileAttributes() {
        try {
            Scanner scan = new Scanner(dataFile);
            String[] attributes = scan.nextLine().split(",");
            preprocessor.getAllFileAttributesMap().put(dataFile.getName(), new ArrayList<String>(Arrays.asList(attributes)));
        }catch (IOException e){

        }
    }

    public Scene preProcessingScene(){

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20, 5, 5, 5));

        // Create the header and add it to the border pane
        Text headerText = new Text("Fischer International Data Mining");
        Font headerFont = Font.font("Times New Roman", FontWeight.BOLD, 48);
        headerText.setFont(headerFont);
        borderPane.setTop(headerText);
        BorderPane.setAlignment(headerText, Pos.TOP_CENTER);

        // build navigation box and add it
        VBox stepInfoBox = createStepInfoBox();

        // Add step info box, configuration grid pane, and navigation buttons
        // box to a vertical box container
        centerContent = new VBox(20);
        centerContent.getChildren().add(stepInfoBox);

        // Sets the border pane's center as centerContent
        borderPane.setCenter(centerContent);


        // Configure scene
        Scene scene = new Scene(borderPane, 1080, 720);
        return scene;
    }

    private BorderPane createGroupByPanel() {
        BorderPane pane = new BorderPane();
        radiobuttons = new ArrayList<RadioButton>();
        TitledPane groupByBox = new TitledPane();
        groupByBox.setText("Group by Attribute");

        VBox radionbuttonVB = new VBox();

        for(String s : preprocessor.getAllFileAttributesMap().get(dataFile.getName())){
            RadioButton radiobutton = new RadioButton(s);
            radiobuttons.add(radiobutton);
            radionbuttonVB.getChildren().add(radiobutton);
            radiobutton.setOnAction(e -> radioButtonHandler(radiobutton));
        }

        groupByBox.setContent(radionbuttonVB);
        groupByBox.setAlignment(Pos.TOP_CENTER);
        groupByBox.setExpanded(false);

        pane.setCenter(groupByBox);
        return pane;
    }

    private BorderPane createWantedAttributePanel() {
        BorderPane pane = new BorderPane();
        checkBoxes = new ArrayList<CheckBox>();
        TitledPane groupByBox = new TitledPane();
        groupByBox.setText("Select wanted attributes");

        VBox checkBoxVB = new VBox();

        for(String s : preprocessor.getAllFileAttributesMap().get(dataFile.getName())){
            CheckBox newCheckBox = new CheckBox(s);
            checkBoxes.add(newCheckBox);
            checkBoxVB.getChildren().add(newCheckBox);
            newCheckBox.setOnAction(e -> enableNextButton());
        }
        groupByBox.setContent(checkBoxVB);
        groupByBox.setAlignment(Pos.TOP_CENTER);
        groupByBox.setExpanded(false);

        pane.setCenter(groupByBox);
        return pane;
    }

    private VBox createStepInfoBox(){
        // Create step info nodes
        Font stepInfoFont = Font.font("Times New Roman", FontWeight.BOLD, 28);
        Text currentStepText = new Text("Step 2/3:");
        currentStepText.setFont(stepInfoFont);
        Text stepDescriptionText = new Text("Pre-process");
        stepDescriptionText.setFont(stepInfoFont);

        VBox stepInfoBox = new VBox();
        stepInfoBox.setAlignment(Pos.TOP_CENTER);
        stepInfoBox.setPadding(new Insets(200, 0, 25, 0));
        stepInfoBox.getChildren().addAll(currentStepText, stepDescriptionText);

        return stepInfoBox;
    }

    private HBox createNavigationBox(){
        HBox navigationBox = new HBox(25);
        navigationBox.setAlignment(Pos.TOP_CENTER);
        navigationBox.setPadding(new Insets(100, 0, 0, 0));
        navigationBox.getChildren().addAll(previousButton, nextButton);
        return navigationBox;
    }

    private void radioButtonHandler(RadioButton button){
        for(RadioButton rb : radiobuttons){
            rb.setSelected(false); // turn off all button
        }
        button.setSelected(true); // turn on the one clicked
        enableNextButton();
    }

    public void processFile() {
        String groupBy = "";
        for(RadioButton rb : radiobuttons) {
            if (rb.isSelected()) {
                groupBy = rb.getText();
            }
        }
        preprocessor.setGroupByAttribute(groupBy);

        ArrayList<String> wantedAttributes = new ArrayList<>();
        for(CheckBox cb : checkBoxes){
            if(cb.isSelected()) {
                wantedAttributes.add(cb.getText());
            }
        }
        HashMap<String, ArrayList<String>> wantedAttributesMap = new HashMap<String, ArrayList<String>>();
        wantedAttributesMap.put(dataFile.getName(), wantedAttributes);
        preprocessor.setWantedFileAttributesMap(wantedAttributesMap);

        preprocessor.removeGroupByAttributeFromWantedMap();
        preprocessor.mapAttributeLocations();
        preprocessor.mapUserAttributes();
        preprocessor.createPreprocessedFile();

    }

    public void enableNextButton(){
        boolean isGroupBySelected = false;
        boolean isWantedAttributes = false;

        for(RadioButton rb : radiobuttons){
            isGroupBySelected = (rb.isSelected()) ? true : false;
            if(isGroupBySelected) {break;}
        }

        for(CheckBox cb : checkBoxes){
            isWantedAttributes = (cb.isSelected());
            if(isWantedAttributes) {break;}
        }

        nextButton.setDisable((isGroupBySelected && isWantedAttributes) ? false : true);
    }
}
