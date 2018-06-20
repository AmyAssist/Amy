package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.data;

/**
 * In this class the data from a track is stored. For example after a search
 * query
 * 
 * @author Lars Buttgereit
 */
public class TrackData extends Item {
	private String artists;

	/**
	 * default constructor. no data is set
	 */
	public TrackData() {

	}

	/**
	 * this constructor set all data Objects
	 * 
	 * @param name
	 * @param artists
	 * @param uri
	 */
	public TrackData(String name, String artists, String uri) {
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

}
