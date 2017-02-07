package application;

import java.io.File;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * TODO: Add class documentation
 */
public class GUI extends Application {

    private Stage stage;
    private Scene stepOneScene, stepTwoScene, stepThreeScene;
    private int currentStepNumber = 1;
    private String fileName;
    private boolean showMetrics = true;
    private boolean showBarGraph = true;
    private boolean showPieChart = true;


    /*
     * (non-Javadoc)
	 *
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;

            // Create step one's scene
            stepOneScene = createStepOneScene();

            // Configure and display the stage
            stage.setTitle("Fischer International Data Mining");
            stage.setScene(stepOneScene);
            stage.show();
        } catch (Exception e) {
            System.out.println("An exception has been thrown.");
            e.printStackTrace();
        }
    }

    /**
     * Temporary method to create the scene for step one until a proper project
     * structure is decided
     *
     * @return the first scene
     */
    private Scene createStepOneScene() {
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
        Button nextButton = new Button("Next");
        nextButton.setPrefSize(100, 20);

        // Add file inputting from File Explorer on file input field
        chooseFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    fileName = file.getName();
                    fileInputField.setText(fileName);
                }
            }
        });

        // Configure next button to go to next step
        nextButton.setOnAction(e -> goToNextStep());

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
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        return scene;
    }

    /**
     * Temporary method to create the scene for step two until a proper project
     * structure is decided
     *
     * @return the second scene
     */
    private Scene createStepTwoScene() {
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
        Text currentStepText = new Text("Step 2/3:");
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
        metricsComboBox.setOnAction(e -> showMetrics(metricsComboBox.getValue()));

        // Create graphs check boxes
        HBox graphChecks = new HBox(5);
        CheckBox pieChartCheck = new CheckBox("Pie Chart");
        CheckBox barGraphCheck = new CheckBox("Bar Graph");
        CheckBox graph3Check = new CheckBox("Graph 3");
        graphChecks.getChildren().addAll(pieChartCheck, barGraphCheck, graph3Check);

        showMetrics = true;
        showBarGraph = false;
        showPieChart = false;

        pieChartCheck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (pieChartCheck.isSelected())
                    showPieChart = true;
                else
                    showPieChart = false;
            }
        });

        barGraphCheck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (barGraphCheck.isSelected())
                    showBarGraph = true;
                else
                    showBarGraph = false;
            }
        });

        // Create previous and next buttons
        Button previousButton = new Button("Previous");
        previousButton.setPrefSize(100, 20);
        Button nextButton = new Button("Next");
        nextButton.setPrefSize(100, 20);

        // Configure next button to go to next step
        nextButton.setOnAction(e -> goToNextStep());
        previousButton.setOnAction(e -> goToPreviousStep());

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
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        return scene;
    }

    private Scene createStepThreeScene() {
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
        Text stepDescriptionText = new Text("Results");
        stepDescriptionText.setFont(stepInfoFont);

        // create pie chart
        Text pieChartText = new Text("Pie Chart:");
        pieChartText.setFont(stepInfoFont);
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(new PieChart.Data("ABC-Group", 13),
                new PieChart.Data("ACT-Group", 25), new PieChart.Data("Tec-Group", 10), new PieChart.Data("XYZ-Group", 22),
                new PieChart.Data("NET-Group", 30));
        PieChart chart = new PieChart(pieChartData);

        // create bar graph
        Text barGraphText = new Text("Bar Graph:");
        barGraphText.setFont(stepInfoFont);
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);
        xAxis.setLabel("Groups");
        yAxis.setLabel("Value");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Groups In Company X");
        series1.getData().add(new XYChart.Data("ABC-Group", 1000));
        series1.getData().add(new XYChart.Data("ACT-Group", 900));
        series1.getData().add(new XYChart.Data("Tec-Group", 700));
        series1.getData().add(new XYChart.Data("XYZ-Group", 500));
        series1.getData().add(new XYChart.Data("NET-Group", 300));
        barChart.getData().addAll(series1);

        // create metrics
        Text metrics = new Text("Metrics:");
        metrics.setFont(stepInfoFont);
        Text dataSize = new Text("Data Size: 20 MB");
        Text runTime = new Text("Run Time: 1 minute 30 Seconds");
        Text moreMetrics = new Text("Additional Metrics: 3400 samples");

        // add metrics to vertical box
        VBox metricsBox = new VBox();
        metricsBox.setPadding(new Insets(5,5,5,5));
        metricsBox.setAlignment(Pos.TOP_CENTER);
        metricsBox.getChildren().addAll(metrics, dataSize, runTime, moreMetrics);

        // Create previous and next buttons
        Button newDataSet = new Button("New Data Set");
        newDataSet.setPrefSize(100, 20);
        newDataSet.setOnAction(e -> goToPreviousStep());

        // Add navigation buttons to a horizontal box
        HBox navigationBox = new HBox();
        navigationBox.setPadding(new Insets(50,0,50,0));
        navigationBox.setAlignment(Pos.TOP_CENTER);
        navigationBox.getChildren().addAll(newDataSet);

        // add pie chart to vertical box
        VBox pieChartBox = new VBox();
        pieChartBox.autosize();
        pieChartBox.setAlignment(Pos.TOP_CENTER);
        pieChartBox.getChildren().addAll(pieChartText, chart);

        // add bar graph to vertical box
        VBox barGraphBox = new VBox();
        barChart.autosize();
        barGraphBox.setAlignment(Pos.TOP_CENTER);
        barGraphBox.getChildren().addAll(barGraphText, barChart);

        // data visual horizontal box
        HBox dataBox = new HBox();
        dataBox.setPadding(new Insets(50,100,0,100));
        dataBox.setAlignment(Pos.TOP_CENTER);
        if (showPieChart)
            dataBox.getChildren().addAll(pieChartBox);

        if (showBarGraph)
            dataBox.getChildren().addAll(barGraphBox);

        if (showMetrics)
            dataBox.getChildren().addAll(metricsBox);

        // Add step info to a vertical box
        VBox stepInfoBox = new VBox();
        stepInfoBox.setAlignment(Pos.TOP_CENTER);
        stepInfoBox.setPadding(new Insets(200, 0, 0, 0));
        stepInfoBox.getChildren().addAll(currentStepText, stepDescriptionText);

        // Add step info box, configuration grid pane, and navigation buttons
        // box to a vertical box container
        VBox centerContent = new VBox();
        centerContent.getChildren().addAll(stepInfoBox, dataBox, navigationBox);

        // Sets the border pane's center as centerContent
        borderPane.setCenter(centerContent);

        // Configure scene
        Scene scene = new Scene(borderPane, 1080, 720);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        return scene;
    }

    private void goToNextStep() {
        switch (currentStepNumber) {
            case 1:
                stepTwoScene = createStepTwoScene();
                stage.setScene(stepTwoScene);
                break;
            case 2:
                stepThreeScene = createStepThreeScene();
                stage.setScene(stepThreeScene);
                break;
            default:
                System.out.println("Next step does not exist.");
                break;
        }

        currentStepNumber++;
    }

    private void goToPreviousStep() {
        switch (currentStepNumber) {
            case 2:
                stage.setScene(stepOneScene);
                currentStepNumber--;
                break;
            case 3:
                stepOneScene = createStepOneScene();
                stage.setScene(stepOneScene);
                currentStepNumber = 1;
                break;
            default:
                System.out.println("Previous step does not exist.");
                break;
        }
    }

    private void showMetrics(String value) {
        if (value.equals("Yes")) {
            showMetrics = true;
        } else {
            showMetrics = false;
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
