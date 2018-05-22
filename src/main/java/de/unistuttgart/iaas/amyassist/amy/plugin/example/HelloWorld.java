package de.unistuttgart.iaas.amyassist.amy.plugin.example;

import de.unistuttgart.iaas.amyassist.amy.core.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.ICore;
import de.unistuttgart.iaas.amyassist.amy.core.Init;
import de.unistuttgart.iaas.amyassist.amy.core.SpeechCommand;

@SpeechCommand("Hello world")
public class HelloWorld {
	private static final String KEY = "hellocount";

	protected ICore core;

	@Grammar("say hello")
	public String say(String... say) {
		int count = Integer.parseInt(this.core.read(KEY));
		count++;

		String countString = String.valueOf(count);
		this.core.store(KEY, countString);

		return "hello" + countString;
	}

	@Init
	public void init(ICore core) {
		this.core = core;

		if (!core.has(KEY)) {
			core.store(KEY, "0");
		}
	}
}
