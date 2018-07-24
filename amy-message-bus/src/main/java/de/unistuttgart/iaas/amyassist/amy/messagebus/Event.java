package de.unistuttgart.iaas.amyassist.amy.messagebus;

/**
 * 
 * @author Leon Kiefer
 */
public class Event<T> {
	private final String topic;
	private final T data;

	public Event(String topic, T data) {
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
