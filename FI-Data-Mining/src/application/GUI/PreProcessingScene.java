package application.GUI;

import application.DataMining.CsvToArff;
import application.DataMining.Preprocessor;
import application.DataMining.XmlToCsvConverter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by shawn on 3/5/2017.
 */
public class PreProcessingScene {

    Button previousButton = new Button("Previous");
    Button nextButton = new Button("Next");
    File dataFile;
    Preprocessor preprocessor;
    XmlToCsvConverter xmlConverter;
    CsvToArff csvConverter;
    VBox centerContent;

    public void setDataFile(File file){ this.dataFile = file; }

    public void processFile(){
        preprocessor = new Preprocessor(dataFile);
        preprocessor.getFileAttributes();

        ArrayList<String> attributes = preprocessor.getAllFileAttributesMap().get(dataFile.getName());

        VBox checkBoxes = new VBox();

        Text label = new Text("Select attributes to remove");
        checkBoxes.getChildren().addAll(label);
        for(String s : attributes){
            CheckBox box = new CheckBox(s);
            checkBoxes.getChildren().add(box);
        }

        BorderPane sp = new BorderPane();
        sp.setLeft(checkBoxes);

        centerContent.getChildren().addAll(sp);

    }

    public void fileAttributes(){
        System.out.print("File name" + dataFile.getName());
        ArrayList<String> list = preprocessor.getAllFileAttributesMap().get(dataFile.getName());
        for(String s : list){
            System.out.println(s);
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


        // Add navigation buttons to a horizontal box
        HBox navigationBox = new HBox(25);
        navigationBox.setAlignment(Pos.TOP_CENTER);
        navigationBox.setPadding(new Insets(100, 0, 0, 0));
        navigationBox.getChildren().addAll(previousButton, nextButton);

        // Add step info box, configuration grid pane, and navigation buttons
        // box to a vertical box container
        centerContent = new VBox(20);
        centerContent.getChildren().addAll(stepInfoBox,navigationBox);


        // Sets the border pane's center as centerContent
        borderPane.setCenter(centerContent);
        borderPane.setBottom(navigationBox);


        // Configure scene
        Scene scene = new Scene(borderPane, 1080, 720);
        return scene;
    }


}
