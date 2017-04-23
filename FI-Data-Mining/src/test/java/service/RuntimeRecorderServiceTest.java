package service;

import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.Test;

public class RuntimeRecorderServiceTest {

	RuntimeRecorderService runtimeRecorder;

	@Before
	public void setUp() {
		runtimeRecorder = new RuntimeRecorderService();
	}

	@After
	public void tearDown() {
		runtimeRecorder = null;
	}

	@Test
	public void should_record_runtime() {
		runtimeRecorder.start();
		runtimeRecorder.stop();

		assertThat(runtimeRecorder.getRunTime() > 0, equalTo(true));
	}

}
