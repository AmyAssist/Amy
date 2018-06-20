package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.data;

/**
 * In this class the data from a artist is stored. For example after a search
 * query
 * 
 * @author Lars Buttgereit
 */
public class ArtistData extends Item {
	private String genre;

	/**
	 * default constructor. no data is set
	 */
	public ArtistData() {

	}

	/**
	 * this constructor set all data Objects
	 * 
	 * @param name
	 * @param genre
	 * @param uri
	 */
	public ArtistData(String name, String genre, String uri) {
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

}
