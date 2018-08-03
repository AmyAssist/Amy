package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities;

/**
 * In this class the data from a artist is stored. For example after a search
 * query
 * 
 * @author Lars Buttgereit
 */
public class ArtistEntity extends Item {
	private String genre;
	private String imageUrl;

	/**
	 * default constructor. no data is set
	 */
	public ArtistEntity() {

	}

	/**
	 * this constructor set all data Objects
	 * 
	 * @param name
	 * @param genre
	 * @param uri
	 */
	public ArtistEntity(String name, String genre, String uri) {
		super(name, uri);
		this.genre = genre;
	}

	/**
	 * get the genre from the artist
	 * 
	 * @return
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * set the genre from the artist
	 * 
	 * @param genre
	 */
	public void setGenre(String genre) {
		this.genre = genre;
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
