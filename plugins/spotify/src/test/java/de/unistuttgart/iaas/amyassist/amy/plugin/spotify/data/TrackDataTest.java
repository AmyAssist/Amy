package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtention;
import de.unistuttgart.iaas.amyassist.amy.test.TestFramework;

@ExtendWith({ MockitoExtension.class, FrameworkExtention.class })
class TrackDataTest {
	private static final String NAME = "Next to me";
	private static final String ARTIST = "Imagine Dragons";
	private static final String URI = "testuri123";

	@Reference
	private TestFramework testFramework;

	@Test
	public void testConstructorAndGetters() {
		TrackData track = new TrackData(NAME, ARTIST, URI);
		assertThat(track.getName(), equalTo(NAME));
		assertThat(track.getArtist(), equalTo(ARTIST));
		assertThat(track.getUri(), equalTo(URI));
	}

	@Test
	public void testSetter() {
		TrackData track = new TrackData();
		track.setName(NAME);
		track.setArtist(ARTIST);
		track.setUri(URI);
		assertThat(track.getName(), equalTo(NAME));
		assertThat(track.getArtist(), equalTo(ARTIST));
		assertThat(track.getUri(), equalTo(URI));
	}
}
