package com.threecubed.auber;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AuberGameTest {
	@Test
	void test() {
		AuberGame testGame = new AuberGame();
		GameStartedRunnable gameStartedRunnable = new GameStartedRunnable();
		
		LwjglApplicationConfiguration testConfig = new LwjglApplicationConfiguration();
		testConfig.forceExit = false;
		
		LwjglApplication testApplication = new LwjglApplication(testGame, testConfig);
		
		testApplication.postRunnable(gameStartedRunnable);

		while(!gameStartedRunnable.runnableCalled) {}
		
		assertTrue(testGame.getScreen() instanceof com.threecubed.auber.screens.MenuScreen);

		testApplication.stop();
	}

}
