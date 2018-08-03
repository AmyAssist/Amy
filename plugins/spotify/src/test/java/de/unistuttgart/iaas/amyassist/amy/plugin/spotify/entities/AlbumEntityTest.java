package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;


class AlbumEntityTest {
	private static final String NAME = "Next to me";
	private static final String ARTIST = "Imagine Dragons";
	private static final String URI = "testuri123";

	@Test
	public void testConstructorAndGetters() {
		AlbumEntity album = new AlbumEntity(NAME, ARTIST, URI);
		assertThat(album.getName(), equalTo(NAME));
		assertThat(album.getArtist(), equalTo(ARTIST));
		assertThat(album.getUri(), equalTo(URI));
	}

	@Test
	public void testSetter() {
		AlbumEntity album = new AlbumEntity();
		album.setName(NAME);
		album.setArtist(ARTIST);
		album.setUri(URI);
		assertThat(album.getName(), equalTo(NAME));
		assertThat(album.getArtist(), equalTo(ARTIST));
		assertThat(album.getUri(), equalTo(URI));
	}
}
