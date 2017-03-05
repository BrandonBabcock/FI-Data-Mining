package application.GUI;

import application.DataMining.CsvToArff;
import application.DataMining.Preprocessor;
import application.DataMining.XmlToCsvConverter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    public void setDataFile(File file){
        this.dataFile = file;
    }

    public void fileHandler(){
        String fileName = dataFile.getName();

        if(fileName.contains(".xml")){
            try {
                xmlConverter = new XmlToCsvConverter(dataFile);
                xmlConverter.convertToCsv();
                dataFile = xmlConverter.getCSVFile();
                preprocessor = new Preprocessor(dataFile);
                preprocessor.getFileAttributes();

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            preprocessor = new Preprocessor(dataFile);
            preprocessor.getFileAttributes();
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

        VBox attributes = fileAttributes();

        // Add navigation buttons to a horizontal box
        HBox navigationBox = new HBox(25);
        navigationBox.setAlignment(Pos.TOP_CENTER);
        navigationBox.setPadding(new Insets(100, 0, 0, 0));
        navigationBox.getChildren().addAll(previousButton, nextButton);

        // Add step info box, configuration grid pane, and navigation buttons
        // box to a vertical box container
        VBox centerContent = new VBox(20);
        centerContent.getChildren().addAll(stepInfoBox,attributes,navigationBox);

        // Sets the border pane's center as centerContent
        borderPane.setCenter(centerContent);

        // Configure scene
        Scene scene = new Scene(borderPane, 1080, 720);
        return scene;
    }

    public VBox fileAttributes(){
        VBox box = new VBox();



        return box;
    }

}
