package com.threecubed.auber.entities;

import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.threecubed.auber.Utils;
import com.threecubed.auber.World;
import com.threecubed.auber.entities.playerpowerups.PlayerPowerUp;
import com.threecubed.auber.files.FileHandler;
import com.threecubed.auber.files.SaveCategory;
import com.threecubed.auber.pathfinding.NavigationMesh;

/**
 * The player entity that the user controls. Handles keyboard input, and
 * interaction with other entities and tiles in the game world.
 *
 * @author Daniel O'Brien
 * @author Joshua Cottrell
 * @version 2.0
 * @since 1.0
 */
public class Player extends GameEntity {
	private Vector2 teleporterRayCoordinates = new Vector2();

	/** Health of Auber - varies between 1 and 0. */
	public float health = 1;

	public boolean confused = false;
	public boolean slowed = false;
	public boolean blinded = false;

	public boolean isStunShot = false;

	/**
	 * True if the player is visible to the user and enemies, False otherwise
	 */
	public boolean isVisible = true;

	private ShapeRenderer rayRenderer;

	public Player(float x, float y, Sprite sprite) {
		super(x, y, sprite);
		FileHandler.addSaveable(this);

		if (sprite != null) {
			rayRenderer = new ShapeRenderer();
		}
	}

	public Player(float x, float y, World world) {
		this(x, y, world.atlas.createSprite("player"));
	}

	public void scheduleTask(final Runnable task, long ms) {
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				task.run();
				timer.cancel();
			}
		}, ms);
	}

	/**
	 * Handle player controls such as movement, interaction and firing the
	 * teleporing gun.
	 *
	 * @param world The game world
	 */
	@Override
	public void update(World world) {
		if (!world.demoMode) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || health <= 0) {
				position.set(World.MEDBAY_COORDINATES[0], World.MEDBAY_COORDINATES[1]);
				confused = false;
				slowed = false;
				teleporterRayCoordinates.setZero();
			}

			// Increment Auber's health if in medbay
			if (world.medbay.getRectangle().contains(position.x, position.y)) {
				health += World.AUBER_HEAL_RATE;
				health = Math.min(1f, health);
			}
			// Slow down Auber when they charge their weapon. Should be stopped when weapon
			// half charged,
			// hence the * 2
			float speedModifier = Math.min(world.auberTeleporterCharge * speed * 2, speed);
			if (slowed) {
				velocity.scl(0.5f);
			}

			// Flip the velocity before new velocity calculated if confused. Otherwise,
			// second iteration
			// of flipped velocity will cancel out the first
			if (confused) {
				velocity.set(-velocity.x, -velocity.y);
			}

			if (Gdx.input.isKeyPressed(Input.Keys.W)) {
				velocity.y = Math.min(velocity.y + speed - speedModifier, maxSpeed);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.A)) {
				velocity.x = Math.max(velocity.x - speed + speedModifier, -maxSpeed);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.S)) {
				velocity.y = Math.max(velocity.y - speed + speedModifier, -maxSpeed);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.D)) {
				velocity.x = Math.min(velocity.x + speed - speedModifier, maxSpeed);
			}

			for (GameEntity entity : world.getEntities()) {
				if (entity instanceof PlayerPowerUp) {
					PlayerPowerUp powerup = (PlayerPowerUp) entity;

					if (powerup.isCollected() && powerup.canActivate()) {
						if (Gdx.input.isKeyJustPressed(powerup.getKeyCode())) {
							powerup.activate();
						}
					}
				}
			}

			if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && teleporterRayCoordinates.isZero()) {
				world.auberTeleporterCharge = Math.min(world.auberTeleporterCharge + World.AUBER_CHARGE_RATE, 1f);
			} else {
				if (world.auberTeleporterCharge > 0.95f) {
					world.auberTeleporterCharge = 0;

					// Scare entities
					teleporterRayCoordinates = handleRayCollisions(world);
					for (GameEntity entity : world.getEntities()) {
						float entityDistance = NavigationMesh.getEuclidianDistance(
								new float[] { teleporterRayCoordinates.x, teleporterRayCoordinates.y },
								new float[] { entity.position.x, entity.position.y });
						if (entityDistance < World.NPC_EAR_STRENGTH && entity instanceof Npc) {
							if (entity instanceof Infiltrator) {
								Infiltrator infiltrator = (Infiltrator) entity;

								// Exposed infiltrators shouldn't flee
								if (infiltrator.exposed) {
									continue;
								}
							}
							Npc npc = (Npc) entity;
							npc.navigateToNearestFleepoint(world);
						}
					}

					scheduleTask(new Runnable() {
						@Override
						public void run() {
							teleporterRayCoordinates.setZero();
						}
					}, (long) World.AUBER_RAY_TIME);
				} else {
					world.auberTeleporterCharge = Math.max(world.auberTeleporterCharge - World.AUBER_CHARGE_RATE, 0f);
				}
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
				// Interact with an object
				RectangleMapObject nearbyObject = getNearbyObjects(World.map);

				if (nearbyObject != null) {
					MapProperties properties = nearbyObject.getProperties();
					String type = properties.get("type", String.class);

					switch (type) {
					case "teleporter":
						MapObjects objects = World.map.getLayers().get("object_layer").getObjects();

						String linkedTeleporterId = properties.get("linked_teleporter", String.class);
						RectangleMapObject linkedTeleporter = (RectangleMapObject) objects.get(linkedTeleporterId);
						velocity.setZero();
						position.x = linkedTeleporter.getRectangle().getX();
						position.y = linkedTeleporter.getRectangle().getY();
						break;

					default:
						break;
					}
				}
			}

			Vector2 mousePosition = Utils.getMouseCoordinates(world.camera);

			// Set the rotation to the angle theta where theta is the angle between the
			// mouse cursor and
			// player position. Correct the player position to be measured from the centre
			// of the sprite.
			rotation = (float) (Math
					.toDegrees(Math.atan2((mousePosition.y - getCenterY()), (mousePosition.x - getCenterX()))) - 90f);

			// Handle the confused debuff
			if (confused) {
				velocity.set(-velocity.x, -velocity.y);
			}

			move(velocity, World.map);
		}
	}

	/**
	 * Overrides the GameEntity render method to render the player's teleporter
	 * raygun, as well as the player itself.
	 *
	 * @param batch  The batch to draw to
	 * @param camera The world's camera
	 */
	@Override
	public void render(Batch batch, Camera camera) {
		if (!teleporterRayCoordinates.isZero()) {
			batch.end();
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			rayRenderer.setProjectionMatrix(camera.combined);
			rayRenderer.begin(ShapeType.Filled);
			rayRenderer.rectLine(getCenterX(), getCenterY(), teleporterRayCoordinates.x, teleporterRayCoordinates.y,
					0.5f, World.rayColorA, World.rayColorB);
			rayRenderer.end();

			batch.begin();
		}

		if (isVisible) {
			super.render(batch, camera);
		}

	}

	/**
	 * Handle teleporter ray collisions and return the coordinates of the object it
	 * collides with.
	 *
	 * @param world The game world
	 * @return The coordinates the ray hit
	 */
	private Vector2 handleRayCollisions(World world) {
		Vector2 output = new Vector2();

		Vector2 targetCoordinates = new Vector2(Utils.getMouseCoordinates(world.camera));
		float alpha = 0.1f;
		boolean rayIntersected = false;
		// Allow the ray to go 20x the distance between the mouse and player,
		// prevents game from hanging if ray escapes map
		while (!rayIntersected && alpha < 20) {
			output.x = getCenterX();
			output.y = getCenterY();

			output.lerp(targetCoordinates, alpha);

			// Check for entity collisions
			for (GameEntity entity : world.getEntities()) {
				if (!(entity instanceof Player)) {
					if (entity.sprite.getBoundingRectangle().contains(output)) {
						rayIntersected = true;
						if (entity instanceof Npc) {
							Npc npc = (Npc) entity;
							npc.handleTeleporterShot(world);

							if (entity instanceof Infiltrator && isStunShot) {
								handleStunShot((Infiltrator) entity);
								isStunShot = false;
							}
						}
						break;
					}
				}
			}

			// Check for tile collisions
			TiledMapTileLayer collisionLayer = (TiledMapTileLayer) World.map.getLayers().get("collision_layer");
			Cell targetCell = collisionLayer.getCell((int) output.x / collisionLayer.getTileWidth(),
					(int) output.y / collisionLayer.getTileHeight());
			if (targetCell != null) {
				rayIntersected = true;
			}
			alpha += 0.1f;
		}
		return output;
	}

	/**
	 * Handles when the player stun shoots the infiltrator
	 * 
	 * @param infiltrator
	 */
	private void handleStunShot(final Infiltrator infiltrator) {
		infiltrator.speed = 0f;
		infiltrator.maxSpeed = 0f;

		scheduleTask(new Runnable() {
			@Override
			public void run() {
				infiltrator.speed = 0.4f;
				infiltrator.maxSpeed = 2f;
			}
		}, 5000);
	}

	@Override
	public String getSaveData() {
		int confused = this.confused ? 1 : 0;
		int slowed = this.slowed ? 1 : 0;
		int blinded = this.blinded ? 1 : 0;
		return super.getSaveData() + "," + health + "," + confused + "," + slowed + "," + blinded;
	}

	@Override
	public void loadSaveData(String data) {
		super.loadSaveData(data);

		String[] atomicData = data.split(",");

		float health = Float.parseFloat(atomicData[3]);
		boolean confused = atomicData[4].equalsIgnoreCase("1");
		boolean slowed = atomicData[5].equalsIgnoreCase("1");
		boolean blinded = atomicData[6].equalsIgnoreCase("1");

		this.health = health;
		this.confused = confused;
		this.slowed = slowed;
		this.blinded = blinded;

	}

	@Override
	public SaveCategory getCategory() {
		return SaveCategory.PLAYER;
	}

}
