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
class PlaylistDataTest {
	private static final String NAME = "Next to me";
	private static final String PLAYLISTOWNER = "Imagine Dragons";
	private static final String URI = "testuri123";

	@Reference
	private TestFramework testFramework;

	@Test
	public void testConstructorAndGetters() {
		PlaylistData playlist = new PlaylistData(NAME, PLAYLISTOWNER, URI);
		assertThat(playlist.getName(), equalTo(NAME));
		assertThat(playlist.getPlaylistOwner(), equalTo(PLAYLISTOWNER));
		assertThat(playlist.getUri(), equalTo(URI));
	}

	@Test
	public void testSetter() {
		PlaylistData playlist = new PlaylistData();
		playlist.setName(NAME);
		playlist.setPlaylistOwner(PLAYLISTOWNER);
		playlist.setUri(URI);
		assertThat(playlist.getName(), equalTo(NAME));
		assertThat(playlist.getPlaylistOwner(), equalTo(PLAYLISTOWNER));
		assertThat(playlist.getUri(), equalTo(URI));
	}
}
