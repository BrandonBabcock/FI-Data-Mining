package service;

import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

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
	public void should_record_runtime() throws InterruptedException {
		runtimeRecorder.start();
		TimeUnit.SECONDS.sleep(2);
		runtimeRecorder.stop();

		assertThat(runtimeRecorder.getRunTime() > 0, equalTo(true));
	}

}
