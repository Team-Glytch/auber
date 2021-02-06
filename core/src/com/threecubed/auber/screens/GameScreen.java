package com.threecubed.auber.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.threecubed.auber.AuberGame;
import com.threecubed.auber.World;
import com.threecubed.auber.entities.Civilian;
import com.threecubed.auber.entities.GameEntity;
import com.threecubed.auber.entities.Infiltrator;
import com.threecubed.auber.entities.Player;
import com.threecubed.auber.entities.playerpowerups.ExposePowerUp;
import com.threecubed.auber.entities.playerpowerups.InvisibilityPowerUp;
import com.threecubed.auber.entities.playerpowerups.ShieldPowerUp;
import com.threecubed.auber.entities.playerpowerups.SpeedPowerUp;
import com.threecubed.auber.entities.playerpowerups.StunShotPowerUp;
import com.threecubed.auber.ui.GameUi;

/**
 * The main screen of the game, responsible for rendering entities and executing
 * their functions.
 *
 * @author Daniel O'Brien
 * @version 1.0
 * @since 1.0
 */
public class GameScreen extends ScreenAdapter {
	public World world;
	public AuberGame game;
	Sprite stars;

	SpriteBatch screenBatch = new SpriteBatch();
	GameUi ui;

	int workingSystems = 0;

	/**
	 * Initialise the game screen with the {@link AuberGame} object and add a few
	 * entities.
	 *
	 * @param game     The game object
	 * @param demoMode Whether the game should run in demo mode
	 */
	public GameScreen(AuberGame game, boolean demoMode, String difficulty) {
		this.game = game;
		ui = new GameUi(game);

		world = new World(game, demoMode, difficulty);

		for (int i = 0; i < World.MAX_INFILTRATORS_IN_GAME; i++) {
			world.queueEntityAdd(new Infiltrator(world));
			world.infiltratorsAddedCount++;
		}
		for (int i = 0; i < World.NPC_COUNT; i++) {
			world.queueEntityAdd(new Civilian(world));
		}

		List<Vector2> systemPos = getInteractablesPos();
		
		world.queueEntityAdd(new ExposePowerUp(world.atlas.createSprite("ExposePU"), world, systemPos.get(0)));
		world.queueEntityAdd(new InvisibilityPowerUp(world.atlas.createSprite("InvisibilityPU"), systemPos.get(1)));
		world.queueEntityAdd(new ShieldPowerUp(world.atlas.createSprite("ShieldPU"), systemPos.get(2)));
		world.queueEntityAdd(new SpeedPowerUp(world.atlas.createSprite("SpeedPU"), systemPos.get(3)));
		world.queueEntityAdd(new StunShotPowerUp(world.atlas.createSprite("StunShotPU"), systemPos.get(4)));

		stars = game.atlas.createSprite("stars");
	}

	/**
	 * @return The positions of the interactables in the map
	 */
	private List<Vector2> getInteractablesPos() {
		List<Vector2> positions = new ArrayList<Vector2>();

		MapLayer interactionLayer = World.map.getLayers().get("object_layer");
		MapObjects objects = interactionLayer.getObjects();

		for (MapObject object : objects) {
			if (object instanceof RectangleMapObject) {
		        Rectangle rect = ((RectangleMapObject) object).getRectangle();
				positions.add(new Vector2(rect.x, rect.y));
			}
		}

		return positions;
	}

	@Override
	public void render(float delta) {
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			game.setScreen(new MenuScreen(game));
		}
		// Add any queued entities
		world.updateEntities();

		// Set the background color
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		screenBatch.begin();
		screenBatch.draw(stars, 0, 0);
		screenBatch.end();

		OrthogonalTiledMapRenderer renderer = world.renderer;
		renderer.setView(world.camera);
		renderer.render(world.backgroundLayersIds);

		Batch batch = renderer.getBatch();
		// Iterate over all entities. Perform movement logic and render them.
		batch.begin();
		world.infiltratorCount = 0;
		for (GameEntity entity : world.getEntities()) {
			entity.update(world);
			entity.render(batch, world.camera);

			if (entity instanceof Player) {
				world.camera.position.set(entity.position.x, entity.position.y, 0);
				world.camera.update();
			} else if (entity instanceof Infiltrator) {
				Infiltrator infiltrator = (Infiltrator) entity;
				if (infiltrator.aiEnabled) {
					world.infiltratorCount += 1;
				}
			}
		}
		batch.end();
		renderer.render(world.foregroundLayersIds);

		if (world.infiltratorCount < World.MAX_INFILTRATORS_IN_GAME
				&& world.infiltratorsAddedCount < World.MAX_INFILTRATORS) {
			Infiltrator newInfiltrator = new Infiltrator(world);
			while (newInfiltrator.entityOnScreen(world)) {
				newInfiltrator.moveToRandomLocation(world);
			}
			world.queueEntityAdd(newInfiltrator);
			world.infiltratorsAddedCount++;
		}

		// Draw the UI
		ui.render(world, screenBatch);
		world.checkForEndState();
	}

	@Override
	public void dispose() {
		world.renderer.dispose();
	}
}
