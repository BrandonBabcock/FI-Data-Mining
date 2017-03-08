package application.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Created by shawn on 3/5/2017.
 */
public class ConfigurationScene {

    CheckBox pieChartCheck = new CheckBox("Pie Chart");
    CheckBox barGraphCheck = new CheckBox("Bar Graph");
    CheckBox graph3Check = new CheckBox("Graph 3");
    Button previousButton = new Button("Previous");
    Button nextButton = new Button("Next");

    /**
     * Temporary method to create the scene for step two until a proper project
     * structure is decided
     *
     * @return the second scene
     */
    public Scene configurationScene() {
        // Configure the border pane
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
        Text currentStepText = new Text("Step 3/3:");
        currentStepText.setFont(stepInfoFont);
        Text stepDescriptionText = new Text("Configure");
        stepDescriptionText.setFont(stepInfoFont);

        // Create configuration text nodes
        Font configFont = Font.font("Times New Roman", FontWeight.NORMAL, 20);
        Text algorithmText = new Text("Algorithm:");
        algorithmText.setFont(configFont);
        Text metricsText = new Text("Performance Metrics:");
        metricsText.setFont(configFont);
        Text graphsText = new Text("Graphs:");
        graphsText.setFont(configFont);

        // Create configuration combo boxes
        ObservableList<String> algorithmOptions = FXCollections.observableArrayList("Algorithm 1", "Algorithm 2",
                "Algorithm 3");
        ObservableList<String> metricsOptions = FXCollections.observableArrayList("Yes", "No");
        ComboBox<String> algorithmComboBox = new ComboBox<String>(algorithmOptions);
        algorithmComboBox.setPrefSize(200, 20);
        algorithmComboBox.setValue(algorithmOptions.get(0));
        ComboBox<String> metricsComboBox = new ComboBox<String>(metricsOptions);
        metricsComboBox.setPrefSize(200, 20);
        metricsComboBox.setValue(metricsOptions.get(0));
      //  metricsComboBox.setOnAction(e -> showMetrics(metricsComboBox.getValue()));

        // Create graphs check boxes
        HBox graphChecks = new HBox(5);

        graphChecks.getChildren().addAll(pieChartCheck, barGraphCheck, graph3Check);

        // Create previous and next buttons
        previousButton.setPrefSize(100, 20);
        nextButton.setPrefSize(100, 20);


        // Add step info to a vertical box
        VBox stepInfoBox = new VBox();
        stepInfoBox.setAlignment(Pos.TOP_CENTER);
        stepInfoBox.setPadding(new Insets(200, 0, 0, 0));
        stepInfoBox.getChildren().addAll(currentStepText, stepDescriptionText);

        // Configure grid pane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(0, 0, 0, 0));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Add column constraints to grid pane
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(HPos.RIGHT);
        gridPane.getColumnConstraints().add(column1);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHalignment(HPos.LEFT);
        gridPane.getColumnConstraints().add(column2);

        // Add configurations to grid pane
        gridPane.add(algorithmText, 0, 0);
        gridPane.add(algorithmComboBox, 1, 0);
        gridPane.add(metricsText, 0, 1);
        gridPane.add(metricsComboBox, 1, 1);
        gridPane.add(graphsText, 0, 2);
        gridPane.add(graphChecks, 1, 2);

        // Add navigation buttons to a horizontal box
        HBox navigationBox = new HBox(25);
        navigationBox.setAlignment(Pos.TOP_CENTER);
        navigationBox.setPadding(new Insets(100, 0, 0, 0));
        navigationBox.getChildren().addAll(previousButton, nextButton);

        // Add step info box, configuration grid pane, and navigation buttons
        // box to a vertical box container
        VBox centerContent = new VBox(20);
        centerContent.getChildren().addAll(stepInfoBox, gridPane, navigationBox);

        // Sets the border pane's center as centerContent
        borderPane.setCenter(centerContent);

        // Configure scene
        Scene scene = new Scene(borderPane, 1080, 720);

        return scene;
    }
}
