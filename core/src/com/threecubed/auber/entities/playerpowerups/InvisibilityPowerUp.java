package com.threecubed.auber.entities.playerpowerups;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class InvisibilityPowerUp extends PlayerPowerUp {

	/**
	 * Constructs the invisibility power up
	 * 
	 * @param sprite
	 * @param position
	 */
	public InvisibilityPowerUp(Sprite sprite, Vector2 position) {
		super("Invisibilty", sprite, position, 5000, 3000, Keys.X);
	}

	@Override
	protected void doAction() {
		player.isVisible = false;
	}

	@Override
	protected void reverseAction() {
		player.isVisible = true;
	}

}
