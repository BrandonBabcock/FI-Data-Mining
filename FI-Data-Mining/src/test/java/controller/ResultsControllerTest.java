package controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.DataMinerService;
import service.RuntimeRecorderService;
import weka.associations.Apriori;
import weka.associations.FilteredAssociator;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

@RunWith(MockitoJUnitRunner.class)
public class ResultsControllerTest extends ApplicationTest {

	private ResultsController controller;

	@Mock
	private DataMinerService dataMinerMock;

	@Mock
	private RuntimeRecorderService runtimeRecorderMock;

	@Mock
	SelectFilesController selectFilesControllerMock;

	@Spy
	private FXMLLoader fxmlLoaderSpy;

	private boolean rulesFoundWithApriori;

	private boolean rulesFoundWithFilteredAssociator;

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Results.fxml"));
			BorderPane screen = (BorderPane) loader.load();
			controller = loader.getController();
			initializeController();

			Scene scene = new Scene(screen);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Fischer International Data Mining");
			primaryStage.show();
		} catch (IOException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

	private void initializeController() {
		File arffFile = new File("Data/TestArffOne.arff");
		String[] dataMiningOptions = { "10", "0.9", "0.05", "1.0", "0.1" };

		doAnswer(invocation -> findAprioriRules("Data/TestArffOne.arff", dataMiningOptions)).when(dataMinerMock)
				.findAssociationRules(anyString(), anyString(), any());
		doNothing().when(runtimeRecorderMock).start();
		doNothing().when(runtimeRecorderMock).stop();
		doReturn(5.0).when(runtimeRecorderMock).getRunTime();

		rulesFoundWithApriori = controller.initData(arffFile, "Apriori", dataMiningOptions, true, dataMinerMock,
				runtimeRecorderMock, fxmlLoaderSpy);

		doAnswer(invocation -> findFilteredAssociatorRules("Data/TestArffOne.arff", dataMiningOptions))
				.when(dataMinerMock).findAssociationRules(anyString(), anyString(), any());

		rulesFoundWithFilteredAssociator = controller.initData(arffFile, "Filtered Associator", dataMiningOptions,
				false, dataMinerMock, runtimeRecorderMock, fxmlLoaderSpy);
	}

	@Test
	public void should_return_true_when_rules_are_found_with_apriori() {
		assertThat(rulesFoundWithApriori, equalTo(true));
	}

	@Test
	public void should_return_true_when_rules_are_found_with_filtered_associator() {
		assertThat(rulesFoundWithFilteredAssociator, equalTo(true));
	}

	@Test
	public void should_restart_to_step_one_if_user_accepts() {
		doReturn(selectFilesControllerMock).when(fxmlLoaderSpy).getController();
		doNothing().when(selectFilesControllerMock).initData(any(FXMLLoader.class));

		clickOn("#restartButton");
		clickOn("OK");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 1/4:"));
	}

	@Test
	public void should_not_restart_to_step_one_if_user_does_not_accept() {
		clickOn("#restartButton");
		clickOn("Cancel");

		Text text = lookup("#stepNumberText").query();

		assertThat(text.getText(), equalTo("Step 4/4:"));
	}

	private Apriori findAprioriRules(String filePath, String[] dataMiningOptions) {
		try {
			Instances data = DataSource.read(filePath);
			data.setClassIndex(data.numAttributes() - 1);

			String[] options = { "-N", dataMiningOptions[0], "-C", dataMiningOptions[1], "-D", dataMiningOptions[2],
					"-U", dataMiningOptions[3], "-M", dataMiningOptions[4] };

			Apriori apriori = new Apriori();
			apriori.setOptions(options);
			apriori.setClassIndex(data.classIndex());
			apriori.buildAssociations(data);

			return apriori;
		} catch (Exception e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

	private FilteredAssociator findFilteredAssociatorRules(String filePath, String[] dataMiningOptions) {
		try {
			Instances data = DataSource.read(filePath);
			data.setClassIndex(data.numAttributes() - 1);

			String[] options = { "-N", dataMiningOptions[0], "-C", dataMiningOptions[1], "-D", dataMiningOptions[2],
					"-U", dataMiningOptions[3], "-M", dataMiningOptions[4] };

			FilteredAssociator filteredAssociator = new FilteredAssociator();
			filteredAssociator.setOptions(options);
			filteredAssociator.setClassIndex(data.classIndex());
			filteredAssociator.buildAssociations(data);

			return filteredAssociator;
		} catch (Exception e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

}
