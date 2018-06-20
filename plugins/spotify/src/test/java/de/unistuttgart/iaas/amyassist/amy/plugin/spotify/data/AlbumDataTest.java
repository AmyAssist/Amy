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
class AlbumDataTest {
	private static final String NAME = "Next to me";
	private static final String ARTIST = "Imagine Dragons";
	private static final String URI = "testuri123";

	@Reference
	private TestFramework testFramework;

	@Test
	public void testConstructorAndGetters() {
		AlbumData album = new AlbumData(NAME, ARTIST, URI);
		assertThat(album.getName(), equalTo(NAME));
		assertThat(album.getArtist(), equalTo(ARTIST));
		assertThat(album.getUri(), equalTo(URI));
	}

	@Test
	public void testSetter() {
		AlbumData album = new AlbumData();
		album.setName(NAME);
		album.setArtist(ARTIST);
		album.setUri(URI);
		assertThat(album.getName(), equalTo(NAME));
		assertThat(album.getArtist(), equalTo(ARTIST));
		assertThat(album.getUri(), equalTo(URI));
	}
}
