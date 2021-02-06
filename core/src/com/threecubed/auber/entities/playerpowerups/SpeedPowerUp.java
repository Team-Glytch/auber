package com.threecubed.auber.entities.playerpowerups;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class SpeedPowerUp extends PlayerPowerUp {

	/**
	 * Constructs the speed power up
	 * 
	 * @param sprite
	 * @param position
	 */
	public SpeedPowerUp(Sprite sprite, Vector2 position) {
		super("Speed", sprite, position, 5000, 3000, Keys.V);
	}

	@Override
	protected void doAction() {
		player.maxSpeed = 5f;
		player.speed = 5f;
	}

	@Override
	protected void reverseAction() {
		player.maxSpeed = 2f;
		player.speed = 0.4f;
	}

}
