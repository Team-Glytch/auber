package com.threecubed.auber.entities.playerpowerups;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.threecubed.auber.World;
import com.threecubed.auber.entities.GameEntity;
import com.threecubed.auber.entities.Projectile;

public class ShieldPowerUp extends PlayerPowerUp {

	float range;

	/**
	 * Creates a shield power up
	 * 
	 * @param sprite
	 * @param position
	 */
	public ShieldPowerUp(Sprite sprite, Vector2 position) {
		super("Shield", sprite, position, 5000, 3000, Keys.C);
		this.range = 1;
	}

	@Override
	protected void doAction() {
	}

	@Override
	protected void reverseAction() {
	}

	@Override
	public void update(World world) {
		super.update(world);

		if (isActive()) {
			Circle exposeArea = new Circle(player.position, range);

			for (GameEntity entity : world.getEntities()) {
				if (entity instanceof Projectile && exposeArea.contains(entity.position)) {
					world.queueEntityRemove(entity);
				}
			}
		}
	}

}
