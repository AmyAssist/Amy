package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities;

/**
 * In this class the data from a track is stored. For example after a search query
 * 
 * @author Lars Buttgereit
 */
public class TrackEntity extends Item {
	private String artists;
	private int durationInMs;

	/**
	 * default constructor. no data is set
	 */
	public TrackEntity() {

	}

	/**
	 * this constructor set all data Objects
	 * 
	 * @param name
	 * @param artists
	 * @param uri
	 */
	public TrackEntity(String name, String artists, String uri) {
		super(name, uri);
		this.artists = artists;
	}

	/**
	 * get the artists from the track
	 * 
	 * @return
	 */
	public String getArtist() {
		return artists;
	}

	/**
	 * set the artists from the track
	 * 
	 * @param playlistOwner
	 */
	public void setArtist(String artist) {
		this.artists = artist;
	}

	/**
	 * Get's {@link #durationInMs duration}
	 * 
	 * @return duration
	 */
	public int getDurationInMs() {
		return this.durationInMs;
	}

	/**
	 * Set's {@link #durationInMs duration}
	 * 
	 * @param duration
	 *            duration
	 */
	public void setDurationInMs(int duration) {
		this.durationInMs = duration;
	}

}
