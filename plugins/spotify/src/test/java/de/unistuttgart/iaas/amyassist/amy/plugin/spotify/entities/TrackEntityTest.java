package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.entities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class TrackEntityTest {
	private static final String NAME = "Next to me";
	private static final String ARTIST = "Imagine Dragons";
	private static final String URI = "testuri123";

	@Test
	public void testConstructorAndGetters() {
		TrackEntity track = new TrackEntity(NAME, ARTIST, URI);
		assertThat(track.getName(), equalTo(NAME));
		assertThat(track.getArtist(), equalTo(ARTIST));
		assertThat(track.getUri(), equalTo(URI));
	}

	@Test
	public void testSetter() {
		TrackEntity track = new TrackEntity();
		track.setName(NAME);
		track.setArtist(ARTIST);
		track.setUri(URI);
		assertThat(track.getName(), equalTo(NAME));
		assertThat(track.getArtist(), equalTo(ARTIST));
		assertThat(track.getUri(), equalTo(URI));
	}
}
