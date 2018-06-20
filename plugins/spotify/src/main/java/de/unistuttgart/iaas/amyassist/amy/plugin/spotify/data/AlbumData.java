package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.data;

/**
 * In this class the data from a album is stored. For example after a search
 * query
 * 
 * @author Lars Buttgereit
 */
public class AlbumData extends Item {
	private String artists;

	/**
	 * default constructor. no data is set
	 */
	public AlbumData() {

	}

	/**
	 * this constructor set all data Objects
	 * 
	 * @param name
	 * @param artists
	 * @param uri
	 */
	public AlbumData(String name, String artists, String uri) {
		super(name, uri);
		this.artists = artists;
	}

	/**
	 * get the artists from the album
	 * 
	 * @return
	 */
	public String getArtist() {
		return artists;
	}

	/**
	 * set the artists from the album
	 * 
	 * @param artists
	 */
	public void setArtist(String artists) {
		this.artists = artists;
	}

}
