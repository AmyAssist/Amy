package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import de.unistuttgart.iaas.amyassist.amy.test.FrameworkExtention;

@ExtendWith({ MockitoExtension.class, FrameworkExtention.class })
class PlayerlogicTest {

	PlayerLogic spyplayerLogic;
	
	@Mock
	Authorization auth;

	@BeforeEach
	public void init() {
		PlayerLogic playerLogic = new PlayerLogic();
		this.spyplayerLogic = spy(playerLogic);
		Field field;

		try {
			field = PlayerLogic.class.getDeclaredField("auth");
			field.setAccessible(true);
			field.set(playerLogic, auth);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void volume() {
		//doReturn(true).when(spyplayerLogic).checkPlayerState();
	  //  doNothing().when(spyplayerLogic).setVolume2(100);
	//	assertEquals(100, spyplayerLogic.setVolume("max"));
	}
}
