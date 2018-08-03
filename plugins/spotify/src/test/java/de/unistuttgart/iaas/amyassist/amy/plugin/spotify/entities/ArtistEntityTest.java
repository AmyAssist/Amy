package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;



public class ArtistEntityTest {
	private static final String NAME = "Next to me";
	private static final String GENRE = "Rock";
	private static final String URI = "testuri123";

	@Test
	public void testConstructorAndGetters() {
		ArtistEntity artist = new ArtistEntity(NAME, GENRE, URI);
		assertThat(artist.getName(), equalTo(NAME));
		assertThat(artist.getGenre(), equalTo(GENRE));
		assertThat(artist.getUri(), equalTo(URI));
	}

	@Test
	public void testSetter() {
		ArtistEntity artist = new ArtistEntity();
		artist.setName(NAME);
		artist.setGenre(GENRE);
		artist.setUri(URI);
		assertThat(artist.getName(), equalTo(NAME));
		assertThat(artist.getGenre(), equalTo(GENRE));
		assertThat(artist.getUri(), equalTo(URI));
	}
}
