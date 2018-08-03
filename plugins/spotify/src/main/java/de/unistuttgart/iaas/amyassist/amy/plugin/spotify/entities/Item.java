package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities;

import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.iaas.amyassist.amy.utility.rest.Entity;

/**
 * in this abstract class are all attributes from Data object that are the same
 * in all four different types. For example after a search
 * 
 * @author Lars Buttgereit
 *
 */
@XmlRootElement
public abstract class Item extends Entity{
	private String uri;
	private String name;

	/**
	 * default constructor. no data is set
	 */
	public Item() {

	}

	/**
	 * this constructor set all data Objects
	 * 
	 * @param name
	 * @param uri
	 */
	public Item(String name, String uri) {
		this.uri = uri;
		this.name = name;
	}

	/**
	 * get the name from the item
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * set the name form the item
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * get the uri from the item
	 * 
	 * @return
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * set the uri form the item
	 * 
	 * @param uri
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
}
