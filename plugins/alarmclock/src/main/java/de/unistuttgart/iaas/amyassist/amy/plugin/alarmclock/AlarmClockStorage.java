package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;

/**
 * Implements the IAlarmStorage
 * 
 * @author padyf
 *
 */
public class AlarmClockStorage implements IAlarmClockStorage {

	@Reference
	IStorage storage;

	protected static final String ALARMCOUNTER = "alarmCounter";

	protected static final String TIMERCOUNTER = "timerCounter";

	@Override
	public void storeAlarm(Alarm alarm) {
		this.storage.put("alarm" + alarm.getId(), alarm.convertToString());
	}

	@Override
	public void storeTimer(Timer timer) {
		this.storage.put("timer" + timer.getId() + "", timer.convertToString());
	}

	@Override
	public int getAlarmCounter() {
		return Integer.parseInt(this.storage.get(ALARMCOUNTER));

	}

	@Override
	public int getTimerCounter() {
		return Integer.parseInt(this.storage.get(TIMERCOUNTER));

	}

	@Override
	public void putAlarmCounter(int number) {
		this.storage.put(ALARMCOUNTER, number + "");
	}

	@Override
	public void putTimerCounter(int number) {
		this.storage.put(TIMERCOUNTER, number + "");
	}

	@Override
	public int incrementAlarmCounter() {
		int counter = Integer.parseInt(this.storage.get(ALARMCOUNTER));
		counter++;
		this.storage.put(ALARMCOUNTER, Integer.toString(counter));
		return counter;
	}

	@Override
	public int incrementTimerCounter() {
		int counter = Integer.parseInt(this.storage.get(TIMERCOUNTER));
		counter++;
		this.storage.put(TIMERCOUNTER, Integer.toString(counter));
		return counter;
	}

	@Override
	public boolean hasKey(String key) {
		return this.storage.has(key);
	}

	@Override
	public void deleteKey(String key) {
		this.storage.delete(key);
	}

	@Override
	public Alarm getAlarm(int id) {
		String alarmString = this.storage.get("alarm" + id);
		String[] params = alarmString.split(":");

		return new Alarm(id, Integer.parseInt(params[0]), Integer.parseInt(params[1]), Boolean.valueOf(params[2]));
	}

	@Override
	public Timer getTimer(int id) {
		String timerString = this.storage.get("timer" + id);
		String[] params = timerString.split(":");

		return new Timer(id, Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]),
				Boolean.valueOf(params[3]));
	}

	/**
	 *
	 * Initialization method for logic class.
	 */
	@PostConstruct
	public void init() {
		if (!this.storage.has("ALARMCOUNTER"))
			this.storage.put(ALARMCOUNTER, "0");
		if (!this.storage.has(TIMERCOUNTER))
			this.storage.put(TIMERCOUNTER, "0");
	}

}
