/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information see notice.md
 */

package io.github.amyassist.amy.plugin.spotify.logic;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;

import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.messagehub.MessageHub;
import io.github.amyassist.amy.plugin.spotify.SpotifyAPICalls;
import io.github.amyassist.amy.plugin.spotify.entities.AlbumEntity;
import io.github.amyassist.amy.plugin.spotify.entities.ArtistEntity;
import io.github.amyassist.amy.plugin.spotify.entities.PlaylistEntity;
import io.github.amyassist.amy.plugin.spotify.entities.TrackEntity;

/**
 * This class have methods to control a spotify client from a user. For examlpe play, pause playback or search for music
 * tracks etc.
 * 
 * @author Lars Buttgereit
 */
@Service(PlayerLogic.class)
public class PlayerLogic {
	@Reference
	private SpotifyAPICalls spotifyAPICalls;
	@Reference
	private Search search;
	@Reference
	private Logger logger;
	@Reference
	private MessageHub messageHub;

	private boolean suppressed = false;
	private PostSuppressionAction postSuppressionAction = PostSuppressionAction.NONE;

	/**
	 * save a album or playlist uri to play after the speech output
	 */
	private String collectionUriToPlay;
	/**
	 * save a track uri to play after the speech output
	 */
	private String trackUriToPlay;

	/**
	 * actions to be performed after the mute
	 */
	private enum PostSuppressionAction {
		NONE, PAUSE, PLAY_COLLECTION, PLAY_TRACK, SKIP, BACK
	}

	private static final int VOLUME_MUTE_VALUE = 0;
	private static final int VOLUME_MAX_VALUE = 100;
	private static final int VOLUME_UPDOWN_VALUE = 10;

	private static final String ITME_NOT_FOUND = "Item not found";

	/**
	 * needed for the first init. need the clientID and the clientSecret form a spotify devloper account
	 * 
	 * @param clientID
	 *            from spotify developer account
	 * @param clientSecret
	 *            spotify developer account
	 * @return login link to a personal spotify account
	 */
	public URI firstTimeInit(String clientID, String clientSecret) {
		this.spotifyAPICalls.setClientID(clientID);
		this.spotifyAPICalls.setClientSecret(clientSecret);
		return this.spotifyAPICalls.authorizationCodeUri();
	}

	/**
	 * needed for the first init. this method need the properties file in apikeys
	 * 
	 * @return login link to a personal spotify account
	 */
	public URI firstTimeInit() {
		return this.spotifyAPICalls.authorizationCodeUri();
	}

	/**
	 * create the refresh token in he authorization object with the authCode
	 * 
	 * @param authCode
	 *            Callback from the login link
	 */
	public void inputAuthCode(String authCode) {
		this.spotifyAPICalls.createRefreshToken(authCode);
	}

	/**
	 * this play method play a featured playlist from spotify
	 * 
	 * @return a Playlist object. can be null
	 */
	public PlaylistEntity play() {
		List<PlaylistEntity> playLists;
		playLists = this.search.searchFeaturedPlaylists(5);
		if (!playLists.isEmpty()) {
			if (this.suppressed) {
				this.collectionUriToPlay = playLists.get(0).getUri();
				this.postSuppressionAction = PostSuppressionAction.PLAY_COLLECTION;
			} else {
				this.spotifyAPICalls.playListFromUri(playLists.get(0).getUri());
			}
			return playLists.get(0);
		}
		this.logger.warn("no featured playlist found");
		return null;
	}

	/**
	 * this method play a playlist from a playlist search. Use only after a search
	 * 
	 * @param playlistNumber
	 *            number of the item form the search before
	 * @param type
	 *            to find the right search Results
	 * @return a PlaylistEntity
	 */
	public PlaylistEntity playPlaylist(int playlistNumber, SearchTypes type) {
		PlaylistEntity selectedPlaylist = null;
		if (type.equals(SearchTypes.FEATURED_PLAYLISTS) && this.search.getFeaturedPlaylists().size() > playlistNumber) {
			selectedPlaylist = this.search.getFeaturedPlaylists().get(playlistNumber);
		} else if (type.equals(SearchTypes.USER_PLAYLISTS) && this.search.getOwnPlaylists().size() > playlistNumber) {
			selectedPlaylist = this.search.getOwnPlaylists().get(playlistNumber);
		} else if (type.equals(SearchTypes.SEARCH_PLAYLISTS)
				&& this.search.getPlaylistSearchResults().size() > playlistNumber) {
			selectedPlaylist = this.search.getPlaylistSearchResults().get(playlistNumber);
		}
		if (selectedPlaylist != null) {
			if (this.suppressed) {
				this.postSuppressionAction = PostSuppressionAction.PLAY_COLLECTION;
				this.collectionUriToPlay = selectedPlaylist.getUri();
			} else {
				this.spotifyAPICalls.playListFromUri(selectedPlaylist.getUri());
			}
			return selectedPlaylist;
		}
		this.logger.warn(ITME_NOT_FOUND);
		return null;
	}

	/**
	 * this method play a track from a track search. Use only after a search
	 * 
	 * @param trackNumber
	 *            number of the item form the search before
	 * @return a TrackEntity
	 */
	public TrackEntity playTrack(int trackNumber) {
		if (this.search.getTrackSearchResults().size() > trackNumber) {
			if (this.suppressed) {
				this.trackUriToPlay = this.search.getTrackSearchResults().get(trackNumber).getUri();
				this.postSuppressionAction = PostSuppressionAction.PLAY_TRACK;
			} else {
				this.spotifyAPICalls.playSongFromUri(this.search.getTrackSearchResults().get(trackNumber).getUri());
			}
			return this.search.getTrackSearchResults().get(trackNumber);
		}
		this.logger.warn(ITME_NOT_FOUND);
		return null;
	}

	/**
	 * this method play a album from a album search. Use only after a search
	 * 
	 * @param albumNumber
	 *            number of the item form the search before
	 * @return a AlbumEntity
	 */
	public AlbumEntity playAlbum(int albumNumber) {
		if (this.search.getAlbumSearchResults().size() > albumNumber) {
			if (this.suppressed) {
				this.postSuppressionAction = PostSuppressionAction.PLAY_COLLECTION;
				this.collectionUriToPlay = this.search.getAlbumSearchResults().get(albumNumber).getUri();
			} else {
				this.spotifyAPICalls.playListFromUri(this.search.getAlbumSearchResults().get(albumNumber).getUri());
			}
			return this.search.getAlbumSearchResults().get(albumNumber);
		}
		this.logger.warn(ITME_NOT_FOUND);
		return null;
	}

	/**
	 * this method play a artist from a artist search. Use only after a search
	 * 
	 * @param artistNumber
	 *            number of the item form the search before
	 * @return a ArtistEntity
	 */
	public ArtistEntity playArtist(int artistNumber) {
		if (this.search.getArtistSearchResults().size() > artistNumber) {
			if (this.suppressed) {
				this.postSuppressionAction = PostSuppressionAction.PLAY_COLLECTION;
				this.collectionUriToPlay = this.search.getArtistSearchResults().get(artistNumber).getUri();
			} else {
				this.spotifyAPICalls.playListFromUri(this.search.getArtistSearchResults().get(artistNumber).getUri());
			}
			return this.search.getArtistSearchResults().get(artistNumber);
		}
		this.logger.warn(ITME_NOT_FOUND);
		return null;
	}

	/**
	 * resume the actual playback
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean resume() {
		if (this.suppressed) {
			this.postSuppressionAction = PostSuppressionAction.NONE;
			return true;
		}
		return this.spotifyAPICalls.resume();
	}

	/**
	 * pause the actual playback
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean pause() {
		if (this.suppressed) {
			this.postSuppressionAction = PostSuppressionAction.PAUSE;
			return true;
		}
		return this.spotifyAPICalls.pause();
	}

	/**
	 * goes one song forward in the playlist or album
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean skip() {
		if (this.suppressed) {
			this.postSuppressionAction = PostSuppressionAction.SKIP;
			return true;
		}
		return this.spotifyAPICalls.skip();
	}

	/**
	 * goes one song back in the playlist or album
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean back() {
		if (this.suppressed) {
			this.postSuppressionAction = PostSuppressionAction.BACK;
			return true;
		}
		return this.spotifyAPICalls.back();
	}

	/**
	 * gives the actual played song in the spotify client back
	 * 
	 * @return a hashMap with the keys name and artist
	 */
	public TrackEntity getCurrentSong() {
		CurrentlyPlayingContext currentlyPlayingContext = this.spotifyAPICalls.getCurrentPlayingContext();
		if (currentlyPlayingContext != null && currentlyPlayingContext.getItem() != null) {
			Track[] track = { currentlyPlayingContext.getItem() };
			return this.search.createTrackData(new Paging.Builder<Track>().setItems(track).build()).get(0);
		}
		return null;

	}

	/**
	 * this method controls the volume of the player
	 * 
	 * @param volumeString
	 *            allowed strings: mute, max, up, down
	 * @return a int from 0-100. This represent the Volume in percent. if the volume is unknown the return value is -1
	 */
	public int setVolume(String volumeString) {
		int volume = getVolume();
		if (volume != -1) {
			switch (volumeString) {
			case "mute":
				return setVolume(VOLUME_MUTE_VALUE);
			case "max":
			case "full":
				return setVolume(VOLUME_MAX_VALUE);
			case "up":
				volume = Math.min(VOLUME_MAX_VALUE, volume + VOLUME_UPDOWN_VALUE);
				return setVolume(volume);
			case "down":
				volume = Math.max(VOLUME_MUTE_VALUE, volume - VOLUME_UPDOWN_VALUE);
				return setVolume(volume);
			default:
				this.logger.warn("Incorrect volume command");
				return -1;
			}
		}
		return volume;
	}

	/**
	 * set the volume direct with an integer
	 * 
	 * @param volume
	 *            a interger between 0 and 100
	 * @return the new volume. -1 the volume was not between 0 and 100
	 */
	public int setVolume(int volume) {
		if (volume >= VOLUME_MUTE_VALUE && volume <= VOLUME_MAX_VALUE) {
			this.spotifyAPICalls.setVolume(volume);
			return volume;
		}
		return -1;
	}

	/**
	 * get actual volume from 0-100, -1 if no volume available
	 * 
	 * @return the volume
	 */
	public int getVolume() {
		return this.spotifyAPICalls.getVolume();
	}

	/**
	 * Suppress music playback temporarily and wait with playback start until the unmute is triggered
	 * 
	 * @param suppressed
	 *            'true' to suppress playback or wait with playing a new song or skip a song, 'false' to restore it or
	 *            start playback
	 */
	void setSuppressed(boolean suppressed) {
		if (suppressed != this.suppressed) {

			boolean isPlaying = this.spotifyAPICalls.getIsPlaying();

			if (!suppressed && !isPlaying) {
				this.suppressed = false;
				// Consider resuming playback
				if (this.postSuppressionAction == PostSuppressionAction.PAUSE) {
					// Nothing to do here. is already paused
				} else if (this.postSuppressionAction == PostSuppressionAction.PLAY_TRACK) {
					this.spotifyAPICalls.playSongFromUri(this.trackUriToPlay);
				} else if (this.postSuppressionAction == PostSuppressionAction.PLAY_COLLECTION) {
					this.spotifyAPICalls.playListFromUri(this.collectionUriToPlay);
				} else if (this.postSuppressionAction == PostSuppressionAction.BACK) {
					back();
				} else if (this.postSuppressionAction == PostSuppressionAction.SKIP) {
					skip();
				} else {
					// resume if was not paused and no other action is executed
					resume();
				}
				this.postSuppressionAction = PostSuppressionAction.NONE;
			} else if (suppressed) {
				if (isPlaying) {
					// only pause if playing
					pause();
				} else {
					// is already paused, may be overwritten from other methods while suppressed
					this.postSuppressionAction = PostSuppressionAction.PAUSE;
				}
				this.suppressed = true;
			}
		}
	}

}
