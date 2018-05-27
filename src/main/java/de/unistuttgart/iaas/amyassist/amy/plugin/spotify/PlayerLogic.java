/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.requests.data.player.GetUsersAvailableDevicesRequest;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * TODO: Description
 * 
 * @author Lars Buttgereit
 */
@Service(PlayerLogic.class)
public class PlayerLogic {
	private Authorization auth;
	private String deviceID = null;

	public void init() {
		this.auth = new Authorization();
		this.auth.init();
	}

	/**
	 * needed for the first init. need the clientID and the clientSecret form a
	 * spotify devloper account
	 * 
	 * @param clientID
	 * @param clientSecret
	 * @return login link to a personal spotify account
	 */
	public URI firstTimeInit(String clientID, String clientSecret) {
		this.auth = new Authorization();
		this.auth.setClientID(clientID);
		this.auth.setClientSecret(clientSecret);
		return this.auth.authorizationCodeUri();
	}

	/**
	 * create the refresh token in he authorization object with the authCode
	 * 
	 * @param authCode
	 *            Callback from the login link
	 */
	public void inputAuthCode(String authCode) {
		this.auth.createRefreshToken(authCode);
		this.auth.init();
	}

	/**
	 * get all devices that logged in at the moment
	 * 
	 * @return empty ArrayList if no device available else the name of the devices
	 */
	public ArrayList<String> getDevices() {

		ArrayList<String> deviceNames = new ArrayList<>();
		if (this.auth.getSpotifyApi() != null) {
			GetUsersAvailableDevicesRequest getUsersAvailableDevicesRequest = this.auth.getSpotifyApi()
					.getUsersAvailableDevices().build();

			try {
				Device[] devices = getUsersAvailableDevicesRequest.execute();
				for (int i = 0; i < devices.length; i++) {
					deviceNames.add(devices[i].getName());
				}
			} catch (SpotifyWebApiException | IOException e) {
				System.err.println(e);
				e.printStackTrace();
			}

			return deviceNames;
		}
		System.err.println("please init the authorization object");
		return deviceNames;
	}

	/**
	 * set the given device as acutal active device for playing music
	 * 
	 * @param deviceNumber
	 *            index of the device array. Order is the same as in the output in
	 *            getDevices
	 * @return selected device
	 */
	public String setDevice(int deviceNumber) {
		if(this.auth.getSpotifyApi() != null) {
			GetUsersAvailableDevicesRequest getUsersAvailableDevicesRequest = this.auth.getSpotifyApi()
					.getUsersAvailableDevices().build();
			try {
				Device[] devices = getUsersAvailableDevicesRequest.execute();
				if(deviceNumber < devices.length) {
					this.deviceID = devices[deviceNumber].getId();
					return devices[deviceNumber].getName();
				}
				return "This device was not found";
			} catch (SpotifyWebApiException | IOException e) {
				e.printStackTrace();
				System.err.println(e);
				return "A problem has occurred";
			}
			
		}
		return "Please init the spotify API";
	}

	// testing
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String clientId;
		String clientSecret;
		PlayerLogic pc = new PlayerLogic();
		System.out.println("first? (y/n)");
		if (sc.nextLine().equals("y")) {
			System.out.println("Input ClientId:");
			clientId = sc.nextLine();
			System.out.println("Input ClientSecret");
			clientSecret = sc.nextLine();
			System.out.println("Copy this to your browser:");
			System.out.println(pc.firstTimeInit(clientId, clientSecret));
			System.out.println("copy result code from browser in the console");
			pc.inputAuthCode(sc.nextLine());
		} else {
			pc.init();
		}
		pc.getDevices();
		for (int i = 0; i < pc.getDevices().size(); i++) {
			System.out.println(pc.getDevices().get(i));
		}
	}
}
