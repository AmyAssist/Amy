package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities;

/**
 * In this class the data from a album is stored. For example after a search
 * query
 * 
 * @author Lars Buttgereit
 */
public class AlbumEntity extends Item {
	private String artists;
	private String imageUrl;

	/**
	 * default constructor. no data is set
	 */
	public AlbumEntity() {

	}

	/**
	 * this constructor set all data Objects
	 * 
	 * @param name
	 * @param artists
	 * @param uri
	 */
	public AlbumEntity(String name, String artists, String uri) {
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
	
	/**
	 * Get's {@link #imageUrl imageUrl}
	 * @return  imageUrl
	 */
	public String getImageUrl() {
		return this.imageUrl;
	}

	/**
	 * Set's {@link #imageUrl imageUrl}
	 * @param imageUrl  imageUrl
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
