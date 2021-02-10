package com.threecubed.auber.screens;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.threecubed.auber.AuberGame;
import com.threecubed.auber.World;
import com.threecubed.auber.files.FileHandler;
import com.threecubed.auber.ui.Button;

/**
 * The menu screen is the first screen that shows in the game and is responsible
 * for controlling when the game begins, or reloading saved game data.
 *
 * @author Joseph Krystek-Walton
 * @author Joshua Cottrell
 * @version 2.0
 * @since 1.0
 */
public class MenuScreen extends ScreenAdapter {
	World world;
	AuberGame game;

	Button playButton;
	Button loadButton;
	Button demoButton;
	Button difficultyButton;
	OrthogonalTiledMapRenderer renderer;
	Sprite background;
	Sprite instructions;
	Sprite title;
	SpriteBatch spriteBatch;
	String difficulty;

	/**
	 * Instantiate the screen with the {@link AuberGame} object. Set the title and
	 * button up to be rendered.
	 *
	 * @param game The game object
	 */
	public MenuScreen(final AuberGame game) {
		this.game = game;
		difficulty = "easyButton";
		spriteBatch = new SpriteBatch();

		background = game.atlas.createSprite("stars");
		instructions = game.atlas.createSprite("instructions");
		title = game.atlas.createSprite("auber_logo");

		Runnable onPlayClick = new Runnable() {
			@Override
			public void run() {
				game.setScreen(new GameScreen(game, false, difficulty));
			}
		};

		playButton = new Button(new Vector2(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 2), 1f,
				game.atlas.createSprite("playButton"), game, onPlayClick);

		Runnable onLoadClick = new Runnable() {

			@Override
			public void run() {
				try {
					FileHandler.load(game);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		loadButton = new Button(new Vector2(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 2 - 300f), 1f,
				game.atlas.createSprite("loadButton"), game, onLoadClick);

		Runnable onDemoClick = new Runnable() {
			@Override
			public void run() {
				game.setScreen(new GameScreen(game, true, difficulty));
			}
		};

		demoButton = new Button(new Vector2(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 2 - 150f), 1f,
				game.atlas.createSprite("demoButton"), game, onDemoClick);

		Runnable onDifficultyClick = new Runnable() {
			@Override
			public void run() {
				switch (difficulty) {
				case "easyButton":
					difficulty = "mediumButton";
					break;
				case "mediumButton":
					difficulty = "hardButton";
					break;
				case "hardButton":
					difficulty = "easyButton";
					break;
				}
				difficultyButton.setSprite(game.atlas.createSprite(difficulty));
			}

		};
		difficultyButton = new Button(new Vector2(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 2 - 450f), 1f,
				game.atlas.createSprite(difficulty), game, onDifficultyClick);
	}

	@Override
	public void render(float deltaTime) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
			DisplayMode currentDisplayMode = Gdx.graphics.getDisplayMode();
			Gdx.graphics.setFullscreenMode(currentDisplayMode);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
			game.setScreen(new GameScreen(game, true, difficulty));
		}

		// Set the background color
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();

		background.setPosition(0f, 0f);
		background.draw(spriteBatch);

		instructions.setPosition(900f, 125f);
		instructions.draw(spriteBatch);

		title.setScale(0.5f);
		title.setPosition(0f, 550f);
		title.draw(spriteBatch);

		playButton.render(spriteBatch);
		loadButton.render(spriteBatch);
		demoButton.render(spriteBatch);
		difficultyButton.render(spriteBatch);

		spriteBatch.end();
	}
}
