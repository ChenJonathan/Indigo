package indigo.Stage;

import indigo.Entity.Entity;
import indigo.Entity.Player;
import indigo.Entity.SmallBot;
import indigo.Entity.Turret;
import indigo.GameState.PlayState;
import indigo.Landscape.Land;
import indigo.Landscape.Platform;
import indigo.Landscape.SpikePit;
import indigo.Landscape.Wall;
import indigo.Manager.Content;
import indigo.Projectile.Projectile;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.Random;

public class Beach extends Stage
{
	private Random generator = new Random();
	
	private final int maxEnemies = 8;
	private final int enemiesToKill = 25;
	private int enemiesKilled = 0;
	
	private final double startingX = 150;
	private final double startingY = 900;
	
	private Turret turretCenter;
	private Turret turretFlag;
	
	public Beach(PlayState playState)
	{
		super(playState);
		
		player = new Player(this, startingX, startingY, Player.BASE_HEALTH, Player.BASE_MANA);
		entities.add(0, player);
		
		mapX = 6400;
		mapY = 1200;
		setOffsets();
		
		// Boundaries
		walls.add(new Wall(0, SKY_LIMIT, 0, mapY));
		walls.add(new Wall(mapX, SKY_LIMIT, mapX, mapY));
		
		// Spike pits
		walls.add(new Wall(2116, 960, 2116, 1180));
		walls.add(new SpikePit(2116, 1180, 2353, 1180));
		walls.add(new Wall(2353, 1180, 2353, 960));
		walls.add(new Wall(5651, 960, 5651, 1180));
		walls.add(new SpikePit(5651, 1180, 5888, 1180));
		walls.add(new Wall(5888, 1180, 5888, 960));
		
		// Bottom level
		walls.add(new Wall(0, 960, 2116, 960));
		walls.add(new Wall(3100, 960, 3100, 1139)); // Swapped with below
		walls.add(new Wall(2353, 960, 3100, 960)); // Swapped with above
		walls.add(new Wall(3100, 1139, 4220, 1139));
		walls.add(new Wall(4220, 1139, 4220, 1000));
		walls.add(new Wall(3843, 1000, 4220, 1000));
		walls.add(new Wall(3843, 1000, 3924, 960));
		walls.add(new Wall(3924, 960, 5651, 960));
		walls.add(new Wall(5888, 960, 6400, 960));
		
		// Platforms
		platforms.add(new Platform(3218, 960, 3735, 960));
		platforms.add(new Platform(1169, 850, 1420, 850));
		platforms.add(new Platform(1398, 736, 1649, 736));
		platforms.add(new Platform(1736, 842, 1980, 842));
		platforms.add(new Platform(3349, 732, 3600, 732));;
		platforms.add(new Platform(6318, 355, 6400, 355));
		
		// Wood structure
		walls.add(new Wall(4025, 960, 4357, 693));
		walls.add(new Wall(4357, 693, 4753, 693));
		walls.add(new Wall(4753, 693, 5280, 960));
		platforms.add(new Platform(4753, 693, 5137, 467));
		platforms.add(new Platform(4848, 467, 5246, 467));
		
		// Clouds
		platforms.add(new Platform(1727, 618, 2190, 618));
		platforms.add(new Platform(2247, 515, 2704, 515));
		platforms.add(new Platform(2816, 413, 3272, 413));
		platforms.add(new Platform(3388, 341, 3850, 341));
		platforms.add(new Platform(5334, 571, 5793, 571));
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
				// Entity-entity: Makes sure entities don't overlap
				for(int entCount = entities.indexOf(ent) + 1; entCount < entities.size(); entCount++)
				{
					Entity otherEnt = entities.get(entCount);
					
					if((otherEnt).isActive())
					{
						if(otherEnt.isSolid() && ent.intersects(otherEnt))
						{
							// May be subject to change depending on how the interaction works out
							if(ent.getX() < otherEnt.getX())
							{
								ent.setX(ent.getX() - ent.getMovability());
								otherEnt.setX(otherEnt.getX() + otherEnt.getMovability());
							}
							else
							{
								ent.setX(ent.getX() + ent.getMovability());
								otherEnt.setX(otherEnt.getX() - otherEnt.getMovability());
							}
						}
						
						// Entity-melee: Melee weapon interactions
						if(ent.hasWeapon())
						{
							if(otherEnt.intersects(ent.getWeapon().getHitbox()))
							{
								ent.getWeapon().collide(otherEnt);
							}
							if(otherEnt.getHealth() == 0) // Change to !isActive() call when player death animation is done
							{
								if(otherEnt.equals(player))
								{
									data.setKiller(ent.getName());
								}
								// Else gain experience
							}
						}
					}
				}
				
				// Entity-platform: Landing on platforms
				if(ent.isSolid())
				{
					Land ground = null;
					
					// Checks if the entity is eligible to land on a platform
					// Change 0 to platform's velocity if implementing vertically moving platforms
					if(ent.getVelY() >= 0 && !ent.isFlying())
					{
						for (Platform plat: platforms)
						{
							Line2D.Double feetTravel = new Line2D.Double(ent.getPrevX(), ent.getPrevY() + ent.getHeight() / 2, ent.getX(), ent.getY() + ent.getHeight() / 2);
							
							if(feetTravel.intersectsLine(plat.getLine()))
							{
								ent.setY(plat.getSurface(ent.getX()) - ent.getHeight() / 2);
							}
							if(Math.round(ent.getY() + ent.getHeight() / 2) == Math.round(plat.getSurface(ent.getX())) && ent.getX() > plat.getMinX() && ent.getX() < plat.getMaxX())
							{
								ent.setVelY(0);
								ground = plat;
							}
						}
					}
					
					// Entity-wall: Colliding with and landing on walls
					for(Wall wall: walls)
					{
						if(wall.killsEntities() || (wall.killsSolidEntities() && ent.isSolid()))
						{
							if(ent.intersects(wall.getLine()))
							{
								if(ent.equals(player))
								{
									data.setKiller(wall.getName());
								}
								// Else add experience?
								ent.die();
							}
						}
						else if(!wall.isHorizontal())
						{
							// Leftward collision into wall
							if(ent.isRightOfLine(wall.getLine()))
							{
								while(ent.intersects(wall.getLine()))
								{
									ent.setX(ent.getX() + PUSH_AMOUNT);
									ent.setVelX(Math.max(ent.getVelX(), 0));
								}
							}
							// Rightward collision into wall
							else
							{
								while(ent.intersects(wall.getLine()))
								{
									ent.setX(ent.getX() - PUSH_AMOUNT);
									ent.setVelX(Math.min(ent.getVelX(), 0));
								}
							}
						}
						else
						{
							// Downward collision into wall
							if(ent.isAboveLine(wall.getLine()))
							{
								if(ent.isFlying())
								{
									while(ent.intersects(wall.getLine()))
									{
										ent.setY(ent.getY() - PUSH_AMOUNT);
										ent.setVelY(Math.min(ent.getVelY(), 0));
									}
								}
								else
								{
									Line2D.Double line = new Line2D.Double(ent.getX(), ent.getY() - ent.getHeight() / 2, ent.getX(), ent.getY() + ent.getHeight() / 2);
									
									if(line.intersectsLine(wall.getLine()))
									{
										ent.setY(wall.getSurface(ent.getX()) - ent.getHeight() / 2);
									}
									if(Math.round(ent.getY() + ent.getHeight() / 2) == Math.round(wall.getSurface(ent.getX())) && ent.getX() > wall.getMinX() && ent.getX() < wall.getMaxX())
									{
										ent.setVelY(Math.min(ent.getVelY(), 0));
										ground = wall;
									}
								}
							}
							// Upward collision into wall
							else
							{
								while(ent.intersects(wall.getLine()))
								{
									ent.setY(ent.getY() + PUSH_AMOUNT);
									ent.setVelY(Math.max(ent.getVelY(), 0));
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
				}
				
				// Entity-projectile: Taking damage and tracking kills
				for(int projCount = 0; projCount < projectiles.size(); projCount++)
				{
					Projectile proj = projectiles.get(projCount);
					// Consider setting projectile location to intersection
					if((proj.isFriendly() != ent.isFriendly()) && proj.isActive() && ent.intersects(proj))
					{
						proj.collide(ent);
						if(ent.getHealth() == 0) // TODO Change to !isActive() call when player death animation is done
						{
							if(ent.equals(player))
							{
								data.setKiller(proj.getName());
							}
							// Else gain experience
						}
					}
				}
			}
			
			if(ent.isDead() || ent.getX() < 0 || ent.getX() > getMapX() || ent.getY() < SKY_LIMIT || ent.getY() > getMapY())
			{
				if(count == 0)
				{
					data.setVictory(false);
					playState.endGame();
				}
				else
				{
					enemiesKilled++;
					System.out.println("Enemies killed: " + enemiesKilled);
				}
				entities.remove(entities.get(count));
				count--;
			}
		}
		
		for(int count = 0; count < projectiles.size(); count++)
		{
			Projectile proj = projectiles.get(count);
			proj.update();
			
			// Projectile-wall: Solid projectiles die after collision
			if(proj.isActive() && proj.isSolid())
			{
				for (Wall wall: walls)
				{
					if(proj.intersects(wall.getLine()))
					{
						if(wall.killsProjectiles() || (wall.killsSolidProjectiles() && proj.isSolid()))
						{
							proj.die(); // Considering simply removing the projectile
						}
						else
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
							proj.collide(wall);
							break;
						}
					}
				}
			}

			if(proj.isDead() || proj.getX() < 0 || proj.getX() > getMapX() || proj.getY() < SKY_LIMIT || proj.getY() > getMapY())
			{
				projectiles.remove(proj);
				count--;
			}
		}
		
		if (enemiesKilled >= enemiesToKill)
		{
			data.setVictory(true);
			playState.endGame();
		}
		else if(entities.size() - 1 < maxEnemies)
		{
			if((turretCenter == null || turretCenter.isDead()) && playState.getTime() % 300 == 0)
			{
				turretCenter = new Turret(this, 3470, 665, Turret.BASE_HEALTH);
				entities.add(turretCenter);
			}
			else if((turretFlag == null || turretFlag.isDead()) && playState.getTime() % 300 == 0)
			{
				turretFlag = new Turret(this, 6335, 285, Turret.BASE_HEALTH);
				entities.add(turretFlag);
			}
			else
			{
				int r = generator.nextInt(200);
				if (r == 0)
				{
					entities.add(new SmallBot(this, Math.random() * 5000 + 700, Math.random() * 300 + 350, SmallBot.BASE_HEALTH));
				}
			}
		}
	}
	
	public void render(Graphics2D g)
	{
		g.drawImage(Content.STAGE_BEACH, 0, 0, 6400, 1200, null);
		
		for(Entity ent: entities)
		{
			// Don't render if in pipe
			if(!(ent.isSolid() && ent.getX() > 3834 && ent.getX() < 4122 && ent.getY() > 995 && ent.getY() < 1140))
			{
				ent.render(g);
			}
		}
		for(Projectile proj: projectiles)
		{
			// Don't render if in pipe
			if(!(proj.isSolid() && proj.getX() > 3834 && proj.getX() < 4122 && proj.getY() > 995 && proj.getY() < 1140))
			{
				proj.render(g);
			}
		}
	}
}