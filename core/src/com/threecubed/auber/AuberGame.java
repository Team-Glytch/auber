package com.threecubed.auber;

import java.io.IOException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.threecubed.auber.files.FileHandler;
import com.threecubed.auber.screens.MenuScreen;

public class AuberGame extends Game {
	public boolean renderWasCalled = false;
	
	public TextureAtlas atlas;

	@Override
	public void create() {
		atlas = new TextureAtlas("auber.atlas");
		Gdx.graphics.setWindowedMode(1920, 1080);
		setScreen(new MenuScreen(this));
	}
	
	@Override
	public void render() {
		renderWasCalled = true;
		super.render();
	}

	@Override
	public void dispose() {
		atlas.dispose();
		try {
			FileHandler.save("s" + System.currentTimeMillis());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
