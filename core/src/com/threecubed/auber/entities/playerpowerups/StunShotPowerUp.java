package com.threecubed.auber.entities.playerpowerups;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class StunShotPowerUp extends PlayerPowerUp {

	/**
	 * Constructs the stun shot power up
	 * 
	 * @param sprite
	 * @param position
	 */
	public StunShotPowerUp(Sprite sprite, Vector2 position) {
		super("Stun Shot", sprite, position, 5000, -1, Keys.B);
	}

	@Override
	protected void doAction() {
		player.isStunShot = true;
	}

	@Override
	protected void reverseAction() {
	}

}
