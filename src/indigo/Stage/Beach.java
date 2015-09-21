package indigo.Stage;

import indigo.Entity.Entity;
import indigo.Entity.Player;
import indigo.Entity.SmallBot;
import indigo.Entity.Turret;
import indigo.GameState.PlayState;
import indigo.Landscape.Platform;
import indigo.Landscape.SkyBounds;
import indigo.Landscape.SpikePit;
import indigo.Landscape.Wall;
import indigo.Manager.Content;
import indigo.Projectile.Projectile;

import java.awt.Graphics2D;
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
		walls.add(new SkyBounds(0, SKY_LIMIT, mapX, SKY_LIMIT));

		// Spike pits
		walls.add(new Wall(2116, 960, 2116, 1180));
		walls.add(new SpikePit(2116, 1180, 2353, 1180));
		walls.add(new Wall(2353, 1180, 2353, 960));
		walls.add(new Wall(5651, 960, 5651, 1180));
		walls.add(new SpikePit(5651, 1180, 5888, 1180));
		walls.add(new Wall(5888, 1180, 5888, 960));

		// Bottom level
		walls.add(new Wall(0, 960, 2116, 960));
		walls.add(new Wall(3100, 960, 3100, 1139)); // Swapped with below // TODO Temporary fix
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
		platforms.add(new Platform(3349, 732, 3600, 732));
		;
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
		super.update();

		if(enemiesKilled >= enemiesToKill)
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
				if(r == 0)
				{
					entities.add(new SmallBot(this, Math.random() * 5000 + 700, Math.random() * 300 + 350,
							SmallBot.BASE_HEALTH));
				}
			}
		}
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
			enemiesKilled++;
			System.out.println("Enemies killed: " + enemiesKilled);
			// TODO Gain experienced - Add experience variable to Entity class
		}
	}

	public void render(Graphics2D g)
	{
		g.drawImage(Content.STAGE_BEACH, 0, 0, 6400, 1200, null);

		for(Entity ent : entities)
		{
			// Don't render if in pipe
			if(!(ent.getX() > 3834 && ent.getX() < 4122 && ent.getY() > 995 && ent.getY() < 1140))
			{
				ent.render(g);
			}
		}
		for(Projectile proj : projectiles)
		{
			// Don't render if in pipe
			if(!(proj.isSolid() && proj.getX() > 3834 && proj.getX() < 4122 && proj.getY() > 995 && proj.getY() < 1140))
			{
				proj.render(g);
			}
		}
	}
}