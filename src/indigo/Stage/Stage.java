package indigo.Stage;

import indigo.Display.HUD;
import indigo.Entity.Entity;
import indigo.Entity.Player;
import indigo.GameState.PlayState;
import indigo.Interactive.Interactive;
import indigo.Landscape.Land;
import indigo.Landscape.Platform;
import indigo.Landscape.Wall;
import indigo.Main.Game;
import indigo.Manager.Data;
import indigo.Manager.Manager;
import indigo.Projectile.Projectile;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

// Shows the map, including platforms, entities, and projectiles
public abstract class Stage
{
	protected PlayState playState;
	protected Data data;

	protected Player player;

	private boolean camUnlocked;

	private int camForeX;
	private int camForeY;
	private int camBackX;
	private int camBackY;

	private int maxOffsetX;
	private int maxOffsetY;
	private int minOffsetX;
	private int minOffsetY;

	private double startingX;
	private double startingY;

	protected int mapX;
	protected int mapY;
	protected int backX;
	protected int backY;

	protected BufferedImage background;
	protected BufferedImage foreground;

	protected ArrayList<Entity> entities;
	protected ArrayList<Interactive> interactives;
	protected ArrayList<Projectile> projectiles;
	protected ArrayList<Land> landscape;

	// Distance that entities are pushed when they collide with things - Fairly arbitrary
	public static final double PUSH_AMOUNT = 0.5;

	// Speed at which camera moves when unlocked
	public static final int CAMERA_SPEED = 80;

	public static final double GRAVITY = 3; // Non-flying entities and projectiles fall
	public static final double FRICTION = 2; // Entities have their velocities reduced towards zero
	public static final double TERMINAL_VELOCITY = 100; // Maximum value that x or y velocity can reach
	public static final double COLLISION_PROXIMITY = 250; // Maximum distance where collision is checked // TODO Revert
	public static final double SKY_LIMIT = -1000; // Upper boundary of the map

	public Stage(PlayState playState)
	{
		this.playState = playState;
		data = playState.getData();

		camUnlocked = false;

		entities = new ArrayList<Entity>();
		interactives = new ArrayList<Interactive>();
		projectiles = new ArrayList<Projectile>();
		landscape = new ArrayList<Land>();
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
					for(int itemCount = 0; itemCount < interactives.size(); itemCount++)
					{
						Interactive item = interactives.get(itemCount);
						item.update();

						if(item.isActive() && inProximity(ent, item) && ent.intersects(item))
						{
							item.activate(player);
						}

						if(item.isDead() || item.getX() < 0 || item.getX() > getMapX() || item.getY() < SKY_LIMIT
								|| item.getY() > getMapY())
						{
							item.setDead();
							interactives.remove(item);
							itemCount--;
						}
					}
				}

				// Entity-entity: Makes sure entities don't overlap
				for(int entCount = entities.indexOf(ent) + 1; entCount < entities.size(); entCount++)
				{
					Entity otherEnt = entities.get(entCount);

					if((otherEnt).isActive() && ent.isFriendly() != otherEnt.isFriendly() && inProximity(ent, otherEnt))
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
								if(!otherEnt.isActive())
								{
									trackDeath(ent.getName(), otherEnt);
								}
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
			if(ent.isGrounded())
			{
				Land prevGround = ent.getGround();
				if(ent.getX() + ent.getWidth() / 2 >= prevGround.getMinX()
						&& ent.getX() - ent.getWidth() / 2 <= prevGround.getMaxX())
				{
					ground = prevGround;
				}
			}

			// Entity-land: Colliding with and landing on land
			ArrayList<Land> intersectedLand = new ArrayList<Land>();
			for(Land land : landscape)
			{
				if(land instanceof Wall)
				{
					if(inProximity(ent, (Wall)land) && ent.intersects((Wall)land))
					{
						intersectedLand.add(land);
					}
				}
				else if(land instanceof Platform)
				{
					if(!ent.isFlying() && inProximity(ent, (Platform)land) && intersectsFeet(ent, land) && ent.feetAboveLand(land))
					{
						intersectedLand.add(land);
					}
				}
			}
			if(intersectedLand.size() > 0)
			{
				sortLandByDistance(ent, intersectedLand);

				for(Land land : intersectedLand)
				{
					if(land instanceof Wall && ((Wall)land).killsEntities() && ent.isActive())
					{
						ent.die();
						trackDeath(((Wall)land).getName(), ent);
					}
					if(land instanceof Platform || ((Wall)land).blocksEntities())
					{
						if(land instanceof Wall && !land.isHorizontal())
						{
							if(ent.isRightOfLand(land))
							{
								while(ent.intersects((Wall)land))
								{
									ent.setX(ent.getX() + PUSH_AMOUNT);
									ent.setVelX(Math.max(ent.getVelX(), 0));
								}
							}
							// Rightward collision into wall
							else
							{
								while(ent.intersects((Wall)land))
								{
									ent.setX(ent.getX() - PUSH_AMOUNT);
									ent.setVelX(Math.min(ent.getVelX(), 0));
								}
							}
						}
						else
						{
							// Downward collision into wall
							// Only the closest qualified wall is set as ground
							// Other downward collision walls afterwards are ignored to an extent
							if(land instanceof Platform || ent.isAboveLand(land))
							{
								if(ent.isFlying())
								{
									while(ent.intersects((Wall)land))
									{
										ent.setY(ent.getY() - PUSH_AMOUNT);
										ent.setVelY(Math.min(ent.getVelY(), 0));
									}
								}
								else if(intersectsFeet(ent, land))
								{
									ground = land;
									ent.setY(land.getSurface(ent.getX()) - ent.getHeight() / 2);
								}
							}
							// Upward collision into wall
							else if(!ent.isGrounded())
							{
								while(ent.intersects((Wall)land))
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
				if(count == 0) // Player dies
				{
					playState.endGame(false);
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
			ArrayList<Wall> intersectedWalls = new ArrayList<Wall>();
			for(Land land : landscape)
			{
				if(land instanceof Wall && inProximity(proj, land) && proj.intersects((Wall)land))
				{
					intersectedWalls.add((Wall)land);
				}
			}
			if(intersectedWalls.size() > 0)
			{
				sortWallsByDistance(proj, intersectedWalls);
				boolean collided = false;

				for(Wall wall : intersectedWalls)
				{
					if(proj.isActive() && !collided)
					{
						if((proj.isSolid() && wall.blocksSolidProjectiles())
								|| (!proj.isSolid() && wall.blocksNonsolidProjectiles()))
						{
							Point2D.Double intersection = wall.getIntersection(new Line2D.Double(proj.getPrevX(), proj
									.getPrevY(), proj.getX(), proj.getY()));

							proj.setX(intersection.getX());
							proj.setY(intersection.getY());
							proj.collide(wall);
							collided = true;
						}
						if((proj.isSolid() && wall.killsSolidProjectiles())
								|| (!proj.isSolid() && wall.killsNonsolidProjectiles()))
						{
							proj.die();
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

	public boolean inProximity(Entity ent, Interactive item)
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

	public boolean inProximity(Entity ent, Land land)
	{
		return land.getLine().ptSegDist(ent.getX(), ent.getY()) < COLLISION_PROXIMITY;
	}

	public boolean inProximity(Projectile proj, Land land)
	{
		return land.getLine().ptSegDist(proj.getX(), proj.getY()) < COLLISION_PROXIMITY;
	}

	// Used for entity-wall collision - Sorts walls from closest to furthest (uses previous position)
	public void sortLandByDistance(Entity ent, ArrayList<Land> landscape)
	{
		if(landscape.size() <= 1)
		{
			return;
		}

		for(int count = 0; count < landscape.size(); count++)
		{
			double length = landscape.get(count).getLine().ptSegDist(ent.getPrevX(), ent.getPrevY());

			for(int current = count + 1; current < landscape.size(); current++)
			{
				if(landscape.get(current).getLine().ptSegDist(ent.getPrevX(), ent.getPrevY()) < length)
				{
					Land temp = landscape.get(count);
					landscape.set(count, landscape.get(current));
					landscape.set(current, temp);
				}
			}
		}
	}

	// Used for entity-wall collision - Sorts walls from closest to furthest (uses previous position)
	public void sortWallsByDistance(Projectile proj, ArrayList<Wall> walls)
	{
		if(walls.size() < 2)
		{
			return;
		}

		for(int count = 0; count < walls.size(); count++)
		{
			double length = walls.get(count).getLine().ptSegDist(proj.getPrevX(), proj.getPrevY());

			for(int current = count + 1; current < walls.size(); current++)
			{
				if(walls.get(current).getLine().ptSegDist(proj.getPrevX(), proj.getPrevY()) < length)
				{
					Wall temp = walls.get(count);
					walls.set(count, walls.get(current));
					walls.set(current, temp);
				}
			}
		}
	}

	public boolean intersectsFeet(Entity ent, Land land)
	{
		if(ent.getVelY() <= 0)
		{
			Line2D.Double feetCenter = new Line2D.Double(ent.getPrevX(), ent.getPrevY() + ent.getHeight() / 2,
					ent.getX(), ent.getY() + ent.getHeight() / 2);

			return land.getLine().intersectsLine(feetCenter);
		}
		else
		{
			Line2D.Double feet = new Line2D.Double(ent.getX() - ent.getWidth() / 2, ent.getY() + ent.getHeight() / 2,
					ent.getX() + ent.getWidth() / 2, ent.getY() + ent.getHeight() / 2);
			Line2D.Double feetLeft = new Line2D.Double(ent.getPrevX() - ent.getWidth() / 2, ent.getPrevY()
					+ ent.getHeight() / 2, ent.getX() - ent.getWidth() / 2, ent.getY() + ent.getHeight() / 2);
			Line2D.Double feetRight = new Line2D.Double(ent.getPrevX() + ent.getWidth() / 2, ent.getPrevY()
					+ ent.getHeight() / 2, ent.getX() + ent.getWidth() / 2, ent.getY() + ent.getHeight() / 2);

			return land.getLine().intersectsLine(feet) || land.getLine().intersectsLine(feetLeft)
					|| land.getLine().intersectsLine(feetRight);
		}
	}

	public void trackDeath(String killer, Entity killed)
	{
		if(killed.equals(player))
		{
			data.setKiller(killer);
		}
		else if(killed.isMarked())
		{
			// TODO Gain experience - Add experience variable to Entity class
			// Consider doing after death animation
		}
	}

	// Updates the camera and renders everything
	public void render(Graphics2D g)
	{
		BufferedImage backgroundCrop = background.getSubimage(camBackX, camBackY, Game.WIDTH, Game.HEIGHT - HUD.HEIGHT);
		g.drawImage(backgroundCrop, 0, 0, Game.WIDTH, Game.HEIGHT, null);
		BufferedImage foregroundCrop = foreground.getSubimage(camForeX, camForeY, Game.WIDTH, Game.HEIGHT - HUD.HEIGHT);
		g.drawImage(foregroundCrop, 0, 0, Game.WIDTH, Game.HEIGHT - HUD.HEIGHT, null);

		g.translate(-camForeX, -camForeY);
		for(Projectile proj : projectiles)
		{
			proj.render(g);
		}
		for(Interactive interactive : interactives)
		{
			interactive.render(g);
		}
		for(int count = entities.size() - 1; count >= 0; count--)
		{
			// Render player last
			entities.get(count).render(g);
		}
		g.translate(camForeX, camForeY);
	}

	// Updates camera reference point based on player position
	public void updateCam(Graphics2D g)
	{
		if(camUnlocked)
		{
			if(Manager.input.mouseX() > Game.WIDTH * 0.9)
			{
				camForeX += CAMERA_SPEED;
			}
			else if(Manager.input.mouseX() < Game.WIDTH * 0.1)
			{
				camForeX -= CAMERA_SPEED;
			}
			if(camForeX > (int)(player.getX() - Game.WIDTH * 0.1))
			{
				camForeX = (int)(player.getX() - Game.WIDTH * 0.1);
			}
			else if(camForeX < (int)(player.getX() - Game.WIDTH * 0.9))
			{
				camForeX = (int)(player.getX() - Game.WIDTH * 0.9);
			}

			if(Manager.input.mouseY() > Game.HEIGHT * 0.9)
			{
				camForeY += CAMERA_SPEED;
			}
			else if(Manager.input.mouseY() < Game.HEIGHT * 0.1)
			{
				camForeY -= CAMERA_SPEED;
			}
			if(camForeY > (int)(player.getY() - Game.HEIGHT * 0.1))
			{
				camForeY = (int)(player.getY() - Game.HEIGHT * 0.1);
			}
			else if(camForeY < (int)(player.getY() - Game.HEIGHT * 0.9 + HUD.HEIGHT))
			{
				camForeY = (int)(player.getY() - Game.HEIGHT * 0.9 + HUD.HEIGHT);
			}
		}
		else
		{
			int newX = (int)player.getX() - Game.WIDTH / 2;
			int newY = (int)player.getY() - Game.HEIGHT / 2;

			camForeX = (int)(((double)camForeX + newX) / 2);
			camForeY = (int)(((double)camForeY + newY) / 2);
		}

		if(camForeX > maxOffsetX)
		{
			camForeX = maxOffsetX;
		}
		else if(camForeX < minOffsetX)
		{
			camForeX = minOffsetX;
		}
		if(camForeY > maxOffsetY)
		{
			camForeY = maxOffsetY;
		}
		else if(camForeY < minOffsetY)
		{
			camForeY = minOffsetY;
		}

		camBackX = (int)(((double)backX - Game.WIDTH) * camForeX / maxOffsetX);
		camBackY = (int)(((double)backY - Game.HEIGHT) * camForeY / maxOffsetY);
	}

	// Returns mouse x position in game coordinates
	public double getMouseX()
	{
		return Manager.input.mouseX() + camForeX;
	}

	// Returns mouse y position in game coordinates
	public double getMouseY()
	{
		return Manager.input.mouseY() + camForeY;
	}

	// Sets camera boundaries when initializing the class
	public void setOffsets(int mapX, int mapY)
	{
		this.mapX = mapX;
		this.mapY = mapY;
		maxOffsetX = mapX - Game.WIDTH;
		maxOffsetY = mapY - (Game.HEIGHT - HUD.HEIGHT);
		minOffsetX = 0;
		minOffsetY = 0;
		backX = Game.WIDTH + (maxOffsetX - minOffsetX) / 10;
		backY = Game.HEIGHT + (maxOffsetY - minOffsetY) / 10;

		camForeX = (int)player.getX() - Game.WIDTH / 2;
		camForeY = (int)player.getY() - Game.HEIGHT / 2;
		if(camForeX > maxOffsetX)
		{
			camForeX = maxOffsetX;
		}
		else if(camForeX < minOffsetX)
		{
			camForeX = minOffsetX;
		}
		if(camForeY > maxOffsetY)
		{
			camForeY = maxOffsetY;
		}
		else if(camForeY < minOffsetY)
		{
			camForeY = minOffsetY;
		}
		camBackX = (int)(((double)backX - Game.WIDTH) * camForeX / maxOffsetX);
		camBackY = (int)(((double)backY - Game.HEIGHT) * camForeY / maxOffsetY);

		// TODO Reconsider
		// Scales background image based on map size
		BufferedImage scaledBackground = new BufferedImage(backX, backY, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = scaledBackground.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(background, 0, 0, backX, backY, null);
		background = scaledBackground;

	}

	public void toggleCam()
	{
		camUnlocked = !camUnlocked;
	}

	public Entity getPlayer()
	{
		return entities.get(0);
	}

	public ArrayList<Entity> getEntities()
	{
		return entities;
	}

	public ArrayList<Interactive> getItems()
	{
		return interactives;
	}

	public ArrayList<Projectile> getProjectiles()
	{
		return projectiles;
	}

	public ArrayList<Land> getLandscape()
	{
		return landscape;
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