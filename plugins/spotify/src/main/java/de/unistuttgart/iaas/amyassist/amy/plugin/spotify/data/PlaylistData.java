package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.data;

/**
 * In this class the data from a playist is stored. For example after a search
 * query
 * 
 * @author Lars Buttgereit
 */
public class PlaylistData extends Item {
	private String playlistOwners;

	/**
	 * default constructor. no data is set
	 */
	public PlaylistData() {

	}

	/**
	 * this constructor set all data Objects
	 * 
	 * @param name
	 * @param playlistOwners
	 * @param uri
	 */
	public PlaylistData(String name, String playlistOwners, String uri) {
		super(name, uri);
		this.playlistOwners = playlistOwners;
	}

	/**
	 * get the playlist owners
	 * 
	 * @return
	 */
	public String getPlaylistOwner() {
		return playlistOwners;
	}

	/**
	 * set the playlist owners
	 * 
	 * @param playlistOwner
	 */
	public void setPlaylistOwner(String playlistOwner) {
		this.playlistOwners = playlistOwner;
	}

}
