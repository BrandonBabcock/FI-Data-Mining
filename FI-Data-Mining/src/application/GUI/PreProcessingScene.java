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
    private File dataFile;
    private VBox centerContent;
    private Preprocessor preprocessor;
    private ArrayList<RadioButton> radiobuttons;
    private ArrayList<CheckBox> checkBoxes;

    /**
     * Constructor init the buttons and disable the next button
     */
    public PreProcessingScene(){
        previousButton = new Button("Previous");
        nextButton = new Button("Next");
        nextButton.setDisable(true);
    }

    /**
     * Purpose: Used to set the data file
     * @param file file we wish to preprocess
     */
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

    /**
     * Purpose: Used to build the scene
     * @return
     */
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

    /**
     * Purpose: Used to create the wanted attributes drop down
     */
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

    /**
     * Purpose: Used to create the step info box
     * @return Vbox containing the step information
     */
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

    /**
     * Purposed: Used to build the navigation box the user will use to change scene
     * @return Hbox containing the previous and next buttons
     */
    private HBox createNavigationBox(){
        HBox navigationBox = new HBox(25);
        navigationBox.setAlignment(Pos.TOP_CENTER);
        navigationBox.setPadding(new Insets(100, 0, 0, 0));
        navigationBox.getChildren().addAll(previousButton, nextButton);
        return navigationBox;
    }

    /**
     * Purposed: Used to control the radio buttons. Allows only one button to be selected at a given time
     * @param button
     */
    private void radioButtonHandler(RadioButton button){
        for(RadioButton rb : radiobuttons){
            rb.setSelected(false); // turn off all button
        }
        button.setSelected(true); // turn on the one clicked
        enableNextButton();
    }

    /**
     * Purpose: Used to drive the preprocessor to build the preprocessed CSV
     */
    public void processFile() {
        // get the name of the group by attribute
        String groupBy = "";
        for(RadioButton rb : radiobuttons) {
            if (rb.isSelected()) {
                groupBy = rb.getText();
            }
        }
        preprocessor.setGroupByAttribute(groupBy); // set the attribute

        // get the name of the check boxes that are selected
        ArrayList<String> wantedAttributes = new ArrayList<>();
        for(CheckBox cb : checkBoxes){
            if(cb.isSelected()) {
                wantedAttributes.add(cb.getText());
            }
        }

        // build a map with the file name and selected attributes
        HashMap<String, ArrayList<String>> wantedAttributesMap = new HashMap<String, ArrayList<String>>();
        wantedAttributesMap.put(dataFile.getName(), wantedAttributes);

        preprocessor.setWantedFileAttributesMap(wantedAttributesMap); // set the wanted attribute map
        preprocessor.removeGroupByAttributeFromWantedMap(); // removes unwanted attributes
        preprocessor.mapAttributeLocations(); // map attribute locations
        preprocessor.mapUserAttributes(); // map attributes
        preprocessor.createPreprocessedFile(); // create preprocessed file

    }

    /**
     * Purpose: To check if the next button should be enabled
     *
     * Condition to enable: The group by attribute is selected and at least one wanted attribute is selected
     */
    private void enableNextButton(){
        boolean isGroupBySelected = false;
        boolean isWantedAttributes = false;

        // check to see if a group by attribute is selected
        for(RadioButton rb : radiobuttons){
            isGroupBySelected = (rb.isSelected()) ? true : false;
            if(isGroupBySelected) {break;}
        }

        // check to see if at least one check box is selected
        for(CheckBox cb : checkBoxes){
            isWantedAttributes = (cb.isSelected());
            if(isWantedAttributes) {break;}
        }

        // if the conditions are meet enable the next button
        nextButton.setDisable((isGroupBySelected && isWantedAttributes) ? false : true);
    }
}
