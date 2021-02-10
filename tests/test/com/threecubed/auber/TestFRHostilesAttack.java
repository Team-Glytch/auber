package com.threecubed.auber.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.threecubed.auber.entities.Projectile.CollisionActions;

class TestFRHostilesAttack {
	/*
	 * Requirement(s): FR_HOSTILES_ATTACK
	 * A hostile's attack should deduct 10% of maximum player health from the player's current health.
	 */
	@Test
	void test() {
		Projectile proj = new Projectile(0, 0, null, null, CollisionActions.BLIND, (Sprite) null);
		Player player = new Player(0, 0, (Sprite) null);
		float initialHealth = player.health;
		System.out.println("init health: " + Float.toString(initialHealth));
		proj.handleCollisionWithPlayer(player);
		float expectedHealth = (initialHealth / 100) * 90;
		System.out.println("expected health: " + Float.toString(expectedHealth));
		System.out.println("final health: " + Float.toString(player.health));
		assertEquals(player.health, expectedHealth);
	}

}
