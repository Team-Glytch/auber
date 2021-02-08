package com.threecubed.auber.entities.playerpowerups;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.threecubed.auber.World;
import com.threecubed.auber.entities.GameEntity;
import com.threecubed.auber.entities.Player;

public abstract class PlayerPowerUp extends GameEntity {

	/**
	 * The timestamp of when the player can next use the power up
	 */
	protected long nextActivatableTimeMs;

	/**
	 * The millisecond cooldown of how long the player must wait between uses of the
	 * powerup
	 */
	protected int cooldownMs;

	/**
	 * The millisecond duration for the actions effects, -1 if there is no duration
	 */
	protected int durationMs;

	/**
	 * The code of the keyboard letter used to initiate the power up
	 */
	protected int keyCode;

	/**
	 * The player that has procured the power up
	 */
	protected Player player;

	/**
	 * True if the power up is currently active, False otherwise
	 */
	private boolean isActive = false;

	/**
	 * The name of the power up
	 */
	public String text;

	/**
	 * The power up's constructer
	 * 
	 * @param sprite
	 * @param position
	 * @param cooldownMs
	 * @param durationMs
	 * @param keyCode
	 */
	public PlayerPowerUp(String name, Sprite sprite, Vector2 position, int cooldownMs, int durationMs, int keyCode) {
		super(position.x, position.y, sprite);
		this.text = name + " " + Keys.toString(keyCode);
		this.sprite = sprite;
		this.position = position;
		this.cooldownMs = cooldownMs;
		this.durationMs = durationMs;
		this.keyCode = keyCode;
	}

	@Override
	public void render(Batch batch, Camera camera) {
		if (!isCollected()) {
			sprite.setPosition(position.x, position.y);
			sprite.draw(batch);
		}
	}

	@Override
	public void update(World world) {
		Circle collectArea = new Circle(position, 10f);

		if (!isCollected() && collectArea.contains(world.player.position)) {
			collect(world.player);
		}

	}

	public int getKeyCode() {
		return keyCode;
	}

	/**
	 * @return True if the player has collected the power up, False otherwise
	 */
	public boolean isCollected() {
		return player != null;
	}

	/**
	 * @return {@link #isActive}
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Fired when the player collects the power up
	 * 
	 * @param player
	 */
	public void collect(Player player) {
		this.player = player;
	}

	/**
	 * Activates the power up
	 */
	public void activate() {
		doAction();
		nextActivatableTimeMs = System.currentTimeMillis() + cooldownMs;

		if (durationMs != -1) {
			isActive = true;
			player.scheduleTask(new Runnable() {
				@Override
				public void run() {
					reverseAction();
					isActive = false;
				}
			}, durationMs);
		}
	}

	/**
	 * @param keyCode
	 * @return True if the key code supplied corresponds with the power up, False
	 *         otherwise
	 */
	public boolean isKey(int keyCode) {
		return this.keyCode == keyCode;
	}

	/**
	 * @return True if the cooldown is over
	 */
	public boolean canActivate() {
		return System.currentTimeMillis() > nextActivatableTimeMs;
	}

	/**
	 * Performs the action of the power up
	 */
	protected abstract void doAction();

	/**
	 * Retracts the actions performed by the power up
	 */
	protected abstract void reverseAction();

}
