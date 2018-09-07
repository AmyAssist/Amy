package de.unistuttgart.iaas.amyassist.amy.core.natlang;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import de.unistuttgart.iaas.amyassist.amy.core.configuration.ConfigurationManager;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.io.Environment;
import de.unistuttgart.iaas.amyassist.amy.natlang.Dialog;
import de.unistuttgart.iaas.amyassist.amy.natlang.NLProcessingManagerImpl;
import de.unistuttgart.iaas.amyassist.amy.natlang.aim.XMLAIMIntent;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtension;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

@ExtendWith(FrameworkExtension.class)
public class NLProcessingManagerTest {

	@Reference
	private TestFramework testFramework;

	private NLProcessingManagerImpl manager;

	private List<String> failedToUnderstand = new ArrayList<>();

	private List<String> quitIntent = new ArrayList<>();

	String check;

	@BeforeEach
	public void setup() {
		Environment env = this.testFramework.mockService(Environment.class);
		ConfigurationManager loader = this.testFramework.mockService(ConfigurationManager.class);

		Properties prop = new Properties();
		prop.setProperty("enableStemmer", "false");

		when(loader.getConfigurationWithDefaults(Mockito.anyString())).thenReturn(prop);

		this.failedToUnderstand.add("I did not understand that");
		this.failedToUnderstand.add("Sorry, could you repeat that?");
		this.failedToUnderstand.add("I don't know what you mean");
		this.failedToUnderstand.add("No idea what you are talking about");
		this.failedToUnderstand.add("My plugin developers did not teach me this yet");

		this.quitIntent.addAll(Arrays.asList(new String[] { "ok", "sure", "what else can i do for you?" }));

		this.manager = this.testFramework.setServiceUnderTest(NLProcessingManagerImpl.class);

	}

	@Test
	public void test() {
		Dialog dialog = new Dialog(this::consumerMethodForDialog);
		XMLAIMIntent intent = new XMLAIMIntent();
		this.manager.decideIntent(dialog, "test");
		assertEquals(true, this.failedToUnderstand.contains(this.check));

		this.manager.processIntent(dialog, "never mind");
		assertEquals(true, this.quitIntent.contains(this.check));
	}

	private void consumerMethodForDialog(String s) {
		this.check = s;
	}

}
