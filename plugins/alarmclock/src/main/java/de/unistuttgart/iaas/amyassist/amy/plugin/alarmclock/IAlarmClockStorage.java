package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

/**
 * Interface that defines the methods the alarmclock logic needs to store the
 * alarms and timers
 * 
 * @author padyf
 *
 */
public interface IAlarmClockStorage {

	public void storeAlarm(Alarm alarm);

	public void storeTimer(Timer timer);

	public int getAlarmCounter();

	public int getTimerCounter();

	public void putAlarmCounter(int number);

	public void putTimerCounter(int number);

	public int incrementAlarmCounter();

	public int incrementTimerCounter();

	public boolean hasKey(String key);

	public void deleteKey(String key);

	public Alarm getAlarm(int id);

	public Timer getTimer(int id);
}
