package de.unistuttgart.iaas.amyassist.amy.messagehub;

/**
 * 
 * @author Leon Kiefer
 */
public class Message<T> {
	private final String topic;
	private final T data;

	public Message(String topic, T data) {
		this.topic = topic;
		this.data = data;
	}

	public String getTopic() {
		return topic;
	}

	public T getData() {
		return data;
	}
}
