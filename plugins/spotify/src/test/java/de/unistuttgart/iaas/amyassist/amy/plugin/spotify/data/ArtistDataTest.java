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
class ArtistDataTest {
	private static final String NAME = "Next to me";
	private static final String GENRE = "Rock";
	private static final String URI = "testuri123";

	@Reference
	private TestFramework testFramework;

	@Test
	public void testConstructorAndGetters() {
		ArtistData artist = new ArtistData(NAME, GENRE, URI);
		assertThat(artist.getName(), equalTo(NAME));
		assertThat(artist.getGenre(), equalTo(GENRE));
		assertThat(artist.getUri(), equalTo(URI));
	}

	@Test
	public void testSetter() {
		ArtistData artist = new ArtistData();
		artist.setName(NAME);
		artist.setGenre(GENRE);
		artist.setUri(URI);
		assertThat(artist.getName(), equalTo(NAME));
		assertThat(artist.getGenre(), equalTo(GENRE));
		assertThat(artist.getUri(), equalTo(URI));
	}
}
