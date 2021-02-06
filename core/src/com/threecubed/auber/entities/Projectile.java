package com.threecubed.auber.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer.Task;
import com.threecubed.auber.World;


public class Projectile extends GameEntity {
  CollisionActions collisionAction;
  GameEntity originEntity;

  public static enum CollisionActions {
    CONFUSE,
    SLOW,
    BLIND;

    public static CollisionActions randomAction() {
      // Int rounds down so no need to sub 1 from length
      return values()[(int) (Math.random() * values().length)];
    }
  }

  /**
   * Initialise a projectile.
   *
   * @param x The x coordinate to initialise at
   * @param y The y coordinate to initialise at
   * @param velocity A {@link Vector2} representing the velocity of the projectile
   * @param originEntity The entity that the projectile originated from
   * @param action The effect the projectile should have on the player
   * @param sprite The sprite of the projectile
   * */
  public Projectile(float x, float y, Vector2 velocity, GameEntity originEntity,
      CollisionActions action, Sprite sprite) {
    super(x, y, sprite);
    collisionAction = action;
    this.originEntity = originEntity;
    this.velocity = velocity;
  }

  /**
   * Initialise a projectile.
   *
   * @param x The x coordinate to initialise at
   * @param y The y coordinate to initialise at
   * @param velocity A {@link Vector2} representing the velocity of the projectile
   * @param originEntity The entity that the projectile originated from
   * @param action The effect the projectile should have on the player
   * @param world the game world
   * */
  public Projectile(float x, float y, Vector2 velocity, GameEntity originEntity, CollisionActions action, World world) {
	  this(x, y, velocity, originEntity, action, world.atlas.createSprite("projectile"));
  }

  /**
   * Step the projectile in its target direction, execute the collision handler if it hits the
   * {@link Player}, destroy if it hits anything else.
   *
   * @param world The game world
   * */
  public void update(World world) {
    position.add(velocity);
    for (GameEntity entity : world.getEntities()) {
      if (Intersector.overlaps(entity.sprite.getBoundingRectangle(),
            sprite.getBoundingRectangle())
          && entity != originEntity && entity != this) {
        if (entity instanceof Player) {
          handleCollisionWithPlayer(world.player);
        } 
        world.queueEntityRemove(this);
        return;
      }
    }

    TiledMapTileLayer collisionLayer = (TiledMapTileLayer)
        World.map.getLayers().get("collision_layer");

    int[] cellCoordinates = world.navigationMesh.getTilemapCoordinates(getCenterX(), getCenterY());

    if (collisionLayer.getCell(cellCoordinates[0], cellCoordinates[1]) != null) {
      world.queueEntityRemove(this);
    }
  }

  private void handleCollisionWithPlayer(Player player) {
    switch (collisionAction) {
      case CONFUSE:
        confusePlayer(player);
        break;
      case SLOW:
        slowPlayer(player);
        break;
      case BLIND:
        blindPlayer(player);
        break;
      default:
        break;
    }
    player.health -= World.INFILTRATOR_PROJECTILE_DAMAGE;
  }

  private void confusePlayer(final Player player) {
    player.confused = true;
    player.playerTimer.scheduleTask(new Task() {
      @Override
      public void run() {
        player.confused = false;
      }
    }, World.AUBER_DEBUFF_TIME);
  }

  private void slowPlayer(final Player player) {
    player.slowed = true;
    player.playerTimer.scheduleTask(new Task() {
      @Override
      public void run() {
        player.slowed = false;
      }
    }, World.AUBER_DEBUFF_TIME);
  }

  private void blindPlayer(final Player player) {
    player.blinded = true;
    player.playerTimer.scheduleTask(new Task() {
      @Override
      public void run() {
        player.blinded = false;
      }
    }, World.AUBER_DEBUFF_TIME - 3f);
  }
}
