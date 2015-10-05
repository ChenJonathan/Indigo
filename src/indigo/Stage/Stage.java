package indigo.Stage;

import indigo.Entity.Entity;
import indigo.Entity.Player;
import indigo.GameState.PlayState;
import indigo.Item.Item;
import indigo.Landscape.Land;
import indigo.Landscape.Platform;
import indigo.Landscape.Wall;
import indigo.Main.Game;
import indigo.Manager.Data;
import indigo.Projectile.Projectile;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

// Shows the map, including platforms, entities, and projectiles
// Subclasses - When two walls are connected, initialize the vertical one before the horizontal one
public abstract class Stage
{
	protected PlayState playState;
	protected Data data;

	protected Player player;

	private int camX;
	private int camY;

	private int maxOffsetX;
	private int maxOffsetY;
	private int minOffsetX;
	private int minOffsetY;

	private double startingX;
	private double startingY;

	protected int mapX;
	protected int mapY;

	protected ArrayList<Entity> entities;
	protected ArrayList<Item> items;
	protected ArrayList<Projectile> projectiles;
	protected ArrayList<Platform> platforms;
	protected ArrayList<Wall> walls;

	// Distance that entities are pushed when they collide with things - Fairly arbitrary
	public static final double PUSH_AMOUNT = 0.5;

	public static final double GRAVITY = 3; // Non-flying entities and projectiles fall
	public static final double FRICTION = 2; // Entities have their velocities reduced towards zero
	public static final double TERMINAL_VELOCITY = 100; // Maximum value that x or y velocity can reach
	public static final double COLLISION_PROXIMITY = 250; // Maximum distance where collision is checked
	public static final double SKY_LIMIT = -1000; // Upper boundary of the map

	public Stage(PlayState playState)
	{
		this.playState = playState;
		data = playState.getData();

		entities = new ArrayList<Entity>();
		items = new ArrayList<Item>();
		projectiles = new ArrayList<Projectile>();
		platforms = new ArrayList<Platform>();
		walls = new ArrayList<Wall>();
	}

	public void update()
	{
		for(int count = 0; count < entities.size(); count++)
		{
			Entity ent = entities.get(count);
			ent.update();

			// Collision loops
			if(ent.isActive())
			{
				// Entity-item: Allows player to use items
				if(ent.equals(player))
				{
					for(int itemCount = 0; itemCount < items.size(); itemCount++)
					{
						Item item = items.get(count);
						item.update();

						if(item.isActive() && inProximity(ent, item) && ent.intersects(item))
						{
							item.activate(player);
						}

						if(item.isDead() || item.getX() < 0 || item.getX() > getMapX() || item.getY() < SKY_LIMIT
								|| item.getY() > getMapY())
						{
							item.setDead();
							items.remove(item);
							itemCount--;
						}
					}
				}

				// Entity-entity: Makes sure entities don't overlap
				for(int entCount = entities.indexOf(ent) + 1; entCount < entities.size(); entCount++)
				{
					Entity otherEnt = entities.get(entCount);

					if((otherEnt).isActive() && inProximity(ent, otherEnt))
					{
						if(ent.intersects(otherEnt))
						{
							// Entities are pushed horizontally when colliding with each other
							if(ent.getX() < otherEnt.getX())
							{
								ent.setVelX(ent.getVelX() - ent.getPushability());
								otherEnt.setVelX(otherEnt.getVelX() + otherEnt.getPushability());
							}
							else
							{
								ent.setVelX(ent.getVelX() + ent.getPushability());
								otherEnt.setVelX(otherEnt.getVelX() - otherEnt.getPushability());
							}
							// Flying entities are also pushed vertically
							if(ent.isFlying())
							{
								if(ent.getY() < otherEnt.getY())
								{
									ent.setVelY(ent.getVelY() - ent.getPushability());
								}
								else
								{
									ent.setVelY(ent.getVelY() + ent.getPushability());
								}
							}
							if(otherEnt.isFlying())
							{
								if(ent.getY() < otherEnt.getY())
								{
									otherEnt.setVelY(otherEnt.getVelY() + otherEnt.getPushability());
								}
								else
								{
									otherEnt.setVelY(otherEnt.getVelY() - otherEnt.getPushability());
								}
							}
						}

						// Entity-melee: Melee weapon interactions
						if(ent.hasWeaponHitbox())
						{
							if(otherEnt.intersects(ent.getWeapon()))
							{
								ent.getWeapon().collide(otherEnt);
							}
							if(!otherEnt.isActive())
							{
								trackDeath(ent.getName(), otherEnt);
							}
						}
					}
				}

				// Entity-projectile: Taking damage and tracking kills
				for(int projCount = 0; projCount < projectiles.size(); projCount++)
				{
					Projectile proj = projectiles.get(projCount);
					// Consider setting projectile location to intersection
					if(inProximity(ent, proj) && proj.isFriendly() != ent.isFriendly() && proj.isActive()
							&& ent.intersects(proj))
					{
						proj.collide(ent);
						if(!ent.isActive())
						{
							trackDeath(proj.getName(), ent);
						}
					}
				}
			}

			Land ground = null;

			Line2D.Double feetTravel = new Line2D.Double(ent.getPrevX(), ent.getPrevY() + ent.getHeight() / 2,
					ent.getX(), ent.getY() + ent.getHeight() / 2);

			// Entity-platform: Landing on platforms
			if(!ent.isFlying())
			{
				for(Platform plat : platforms)
				{
					if(inProximity(ent, plat))
					{
						if(feetTravel.intersectsLine(plat.getLine()) && ent.feetIsAbovePlatform(plat))
						{
							ent.setY(plat.getSurface(ent.getX()) - ent.getHeight() / 2);
						}
						if(ent.getX() >= plat.getMinX()
								&& ent.getX() <= plat.getMaxX()
								&& ent.getVelY() >= 0
								&& Math.round(ent.getY() + ent.getHeight() / 2) == Math.round(plat.getSurface(ent
										.getX())))
						{
							ground = plat;
						}
					}
				}
			}

			ArrayList<Wall> intersectedWalls = new ArrayList<Wall>();

			// Entity-wall: Colliding with and landing on walls
			for(Wall wall : walls)
			{
				if(inProximity(ent, wall)
						&& (ent.intersects(wall) || (ent.isGrounded() && ent.getGround().equals(wall))))
				{
					intersectedWalls.add(wall);
				}
			}
			if(intersectedWalls.size() > 0)
			{
				ent.sortWallsByDistance(intersectedWalls);

				for(Wall intersectedWall : intersectedWalls)
				{
					if(intersectedWall.killsEntities() && ent.isActive())
					{
						ent.die();
						trackDeath(intersectedWall.getName(), ent);
					}
					if(intersectedWall.blocksEntities())
					{
						if(!intersectedWall.isHorizontal())
						{
							// Leftward collision into wall
							if(ent.isRightOfWall(intersectedWall))
							{
								while(ent.intersects(intersectedWall))
								{
									ent.setX(ent.getX() + PUSH_AMOUNT);
									ent.setVelX(Math.max(ent.getVelX(), 0));
								}
							}
							// Rightward collision into wall
							else
							{
								while(ent.intersects(intersectedWall))
								{
									ent.setX(ent.getX() - PUSH_AMOUNT);
									ent.setVelX(Math.min(ent.getVelX(), 0));
								}
							}
						}
						else
						{
							// Downward collision into wall
							// Only the closest wall is set as ground; other downward collision walls are ignored
							if(ent.isAboveWall(intersectedWall))
							{
								if(!ent.isFlying() && ground == null)
								{
									if(ent.getX() >= intersectedWall.getMinX()
											&& ent.getX() <= intersectedWall.getMaxX() && ent.isGrounded()
											&& ent.getGround().equals(intersectedWall))
									{
										ground = intersectedWall;
									}
									else if(feetTravel.intersectsLine(intersectedWall.getLine()))
									{
										ground = intersectedWall;
										ent.setY(intersectedWall.getSurface(ent.getX()) - ent.getHeight() / 2);
									}
								}
								else if(ground == null)
								{
									while(ent.intersects(intersectedWall))
									{
										ent.setY(ent.getY() - PUSH_AMOUNT);
										ent.setVelY(Math.min(ent.getVelY(), 0));
									}
								}
							}
							// Upward collision into wall
							else
							{
								while(ent.intersects(intersectedWall))
								{
									ent.setY(ent.getY() + PUSH_AMOUNT);
									ent.setVelY(Math.max(ent.getVelY(), 0));
								}
							}
						}
					}
				}
			}

			if(ground != null)
			{
				ent.setGround(ground);
			}
			else
			{
				ent.removeGround();
			}

			if(ent.isDead() || ent.getX() < 0 || ent.getX() > getMapX() || ent.getY() < SKY_LIMIT
					|| ent.getY() > getMapY())
			{
				if(count == 0)
				{
					playState.endGame();
				}
				ent.setDead();
				entities.remove(entities.get(count));
				count--;
			}
		}

		for(int count = 0; count < projectiles.size(); count++)
		{
			Projectile proj = projectiles.get(count);
			proj.update();

			// Projectile-wall: Walls may block the projectile, kill the projectile, or both
			for(Wall wall : walls)
			{
				if(proj.isActive() && inProximity(proj, wall))
				{
					if(wall.killsNonsolidProjectiles() && !proj.isSolid() && proj.intersects(wall.getLine()))
					{
						proj.die();
					}
					else if(wall.killsSolidProjectiles() && proj.isSolid() && proj.intersects(wall.getLine()))
					{
						proj.die();
					}
					if(wall.blocksNonsolidProjectiles() && !proj.isSolid() && proj.intersects(wall.getLine()))
					{
						double xInt = 0;
						double yInt = 0;

						// Calculate intersection point
						if(proj.getPrevX() != proj.getX())
						{
							double slope = (proj.getY() - proj.getPrevY()) / (proj.getX() - proj.getPrevX());
							double wallYInt = wall.getSlope() * -wall.getLine().getX1() + wall.getLine().getY1();
							double projYInt = -proj.getX() * slope + proj.getY();
							xInt = -(wallYInt - projYInt) / (wall.getSlope() - slope);
							yInt = xInt * slope + projYInt;
						}
						else
						{
							xInt = proj.getX();
							yInt = wall.getSlope() * (proj.getX() - wall.getLine().getX1()) + wall.getLine().getY1();
						}

						proj.setX(xInt);
						proj.setY(yInt);
						if(proj.isActive())
						{
							proj.collide(wall);
						}
					}
					else if(wall.blocksSolidProjectiles() && proj.isSolid() && proj.intersects(wall.getLine()))
					{
						double xInt = 0;
						double yInt = 0;

						// Calculate intersection point
						if(proj.getPrevX() != proj.getX())
						{
							double slope = (proj.getY() - proj.getPrevY()) / (proj.getX() - proj.getPrevX());
							double wallYInt = wall.getSlope() * -wall.getLine().getX1() + wall.getLine().getY1();
							double projYInt = -proj.getX() * slope + proj.getY();
							xInt = -(wallYInt - projYInt) / (wall.getSlope() - slope);
							yInt = xInt * slope + projYInt;
						}
						else
						{
							xInt = proj.getX();
							yInt = wall.getSlope() * (proj.getX() - wall.getLine().getX1()) + wall.getLine().getY1();
						}

						proj.setX(xInt);
						proj.setY(yInt);
						if(proj.isActive())
						{
							proj.collide(wall);
						}
					}
				}
			}

			if(proj.isDead() || proj.getX() < 0 || proj.getX() > getMapX() || proj.getY() < SKY_LIMIT
					|| proj.getY() > getMapY())
			{
				proj.setDead();
				projectiles.remove(proj);
				count--;
			}
		}
	}

	public boolean inProximity(Entity ent, Item item)
	{
		return Math.sqrt(Math.pow(ent.getX() - item.getX(), 2) + Math.pow(ent.getY() - item.getY(), 2)) < COLLISION_PROXIMITY;
	}

	public boolean inProximity(Entity ent, Entity otherEnt)
	{
		return Math.sqrt(Math.pow(ent.getX() - otherEnt.getX(), 2) + Math.pow(ent.getY() - otherEnt.getY(), 2)) < COLLISION_PROXIMITY;
	}

	public boolean inProximity(Entity ent, Projectile proj)
	{
		return Math.sqrt(Math.pow(ent.getX() - proj.getX(), 2) + Math.pow(ent.getY() - proj.getY(), 2)) < COLLISION_PROXIMITY;
	}

	public boolean inProximity(Entity ent, Platform plat)
	{
		return plat.getLine().ptSegDist(ent.getX(), ent.getY()) < COLLISION_PROXIMITY;
	}

	public boolean inProximity(Entity ent, Wall wall)
	{
		return wall.getLine().ptSegDist(ent.getX(), ent.getY()) < COLLISION_PROXIMITY;
	}

	public boolean inProximity(Projectile proj, Wall wall)
	{
		return wall.getLine().ptSegDist(proj.getX(), proj.getY()) < COLLISION_PROXIMITY;
	}

	public void trackDeath(String killer, Entity killed)
	{
		if(killed.equals(player))
		{
			data.setKiller(killer);
			data.setVictory(false);
		}
		else if(killed.isMarked())
		{
			// TODO Gain experience - Add experience variable to Entity class
			// Consider doing after death animation
		}
	}

	// Updates the camera and renders everything - Subclasses need to render background, render targeting reticles,
	// render projectiles, render items, and render entities (in that order)
	public abstract void render(Graphics2D g);

	// Updates camera reference point based on player position
	public void updateCam(Graphics2D g)
	{
		camX = (int)Math.round(player.getX()) - Game.DEFAULT_WIDTH / 2;
		camY = (int)Math.round(player.getY()) - Game.DEFAULT_HEIGHT / 2;
		if(camX > maxOffsetX)
		{
			camX = maxOffsetX;
		}
		else if(camX < minOffsetX)
		{
			camX = minOffsetX;
		}
		if(camY > maxOffsetY)
		{
			camY = maxOffsetY;
		}
		else if(camY < minOffsetY)
		{
			camY = minOffsetY;
		}
		g.translate(-camX, -camY);
	}

	// Consider changing - Inefficient
	public void resetCam(Graphics2D g)
	{
		g.translate(camX, camY);
	}

	// Returns mouse x position in game coordinates
	public double getMouseX()
	{
		return playState.getInput().mouseX() + camX;
	}

	// Returns mouse y position in game coordinates
	public double getMouseY()
	{
		return playState.getInput().mouseY() + camY;
	}

	// Sets camera boundaries when initializing the class
	public void setOffsets(int mapX, int mapY)
	{
		this.mapX = mapX;
		this.mapY = mapY;
		maxOffsetX = mapX - Game.DEFAULT_WIDTH;
		maxOffsetY = mapY - Game.DEFAULT_HEIGHT;
		minOffsetX = 0;
		minOffsetY = 0;
	}

	public Entity getPlayer()
	{
		return entities.get(0);
	}

	public ArrayList<Entity> getEntities()
	{
		return entities;
	}

	public ArrayList<Item> getItems()
	{
		return items;
	}

	public ArrayList<Projectile> getProjectiles()
	{
		return projectiles;
	}

	public ArrayList<Platform> getPlatforms()
	{
		return platforms;
	}

	public ArrayList<Wall> getWalls()
	{
		return walls;
	}

	public int getTime()
	{
		return playState.getTime();
	}

	public double getStartingX()
	{
		return startingX;
	}

	public double getStartingY()
	{
		return startingY;
	}

	public double getMapX()
	{
		return mapX;
	}

	public double getMapY()
	{
		return mapY;
	}
}