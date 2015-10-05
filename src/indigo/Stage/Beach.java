package indigo.Stage;

import indigo.Entity.Entity;
import indigo.Entity.Player;
import indigo.Entity.FlyingBot;
import indigo.Entity.Turret;
import indigo.GameState.PlayState;
import indigo.Item.HealthPickup;
import indigo.Item.Item;
import indigo.Landscape.Platform;
import indigo.Landscape.SkyBounds;
import indigo.Landscape.SpikePit;
import indigo.Landscape.Wall;
import indigo.Manager.Content;
import indigo.Projectile.Projectile;

import java.awt.Graphics2D;

public class Beach extends Stage
{
	private final int maxEnemies = 8;
	private final int enemiesToKill = 25;
	private int enemiesKilled = 0;

	private final double startingX = 150;
	private final double startingY = 900;

	private Turret turretCenter;
	private Turret turretFlag;

	private HealthPickup pickup;

	public Beach(PlayState playState)
	{
		super(playState);

		player = new Player(this, startingX, startingY, Player.BASE_HEALTH, Player.BASE_MANA, Player.BASE_STAMINA);
		entities.add(0, player);

		setOffsets(6400, 1200);

		// Boundaries
		walls.add(new Wall(0, SKY_LIMIT, 0, mapY));
		walls.add(new Wall(mapX, SKY_LIMIT, mapX, mapY));
		walls.add(new SkyBounds(0, SKY_LIMIT, mapX, SKY_LIMIT));

		// Spike pits
		walls.add(new Wall(2116, 961, 2116, 1180));
		walls.add(new SpikePit(2116, 1180, 2353, 1180));
		walls.add(new Wall(2353, 1180, 2353, 961));
		walls.add(new Wall(5651, 961, 5651, 1180));
		walls.add(new SpikePit(5651, 1180, 5888, 1180));
		walls.add(new Wall(5888, 1180, 5888, 961));

		// Bottom level
		walls.add(new Wall(0, 961, 2116, 961));
		walls.add(new Wall(2353, 961, 3100, 961));
		walls.add(new Wall(3100, 961, 3100, 1139));
		walls.add(new Wall(3100, 1139, 4220, 1139));
		walls.add(new Wall(4220, 1139, 4220, 1000));
		walls.add(new Wall(3843, 1000, 4220, 1000));
		walls.add(new Wall(3843, 1000, 3924, 961));
		walls.add(new Wall(3924, 961, 5651, 961));
		walls.add(new Wall(5888, 961, 6400, 961));

		// Platforms
		platforms.add(new Platform(1169, 850, 1420, 850));
		platforms.add(new Platform(1398, 736, 1649, 736));
		platforms.add(new Platform(1736, 842, 1980, 842));
		platforms.add(new Platform(3349, 732, 3600, 732));
		platforms.add(new Platform(3218, 961, 3735, 961));
		platforms.add(new Platform(6318, 355, 6400, 355));

		// Wood structure
		walls.add(new Wall(4025, 961, 4357, 693));
		walls.add(new Wall(4357, 693, 4753, 693));
		walls.add(new Wall(4753, 693, 5280, 961));
		platforms.add(new Platform(4753, 693, 5137, 467));
		platforms.add(new Platform(4848, 467, 5246, 467));

		// Clouds
		platforms.add(new Platform(1727, 618, 2190, 618));
		platforms.add(new Platform(2247, 515, 2704, 515));
		platforms.add(new Platform(2816, 413, 3272, 413));
		platforms.add(new Platform(3388, 341, 3850, 341));
		platforms.add(new Platform(5334, 571, 5793, 571));
		
		turretCenter = new Turret(this, 3470, 665, Turret.BASE_HEALTH);
		entities.add(turretCenter);
		
		turretFlag = new Turret(this, 6335, 285, Turret.BASE_HEALTH);
		entities.add(turretFlag);
		
		pickup = new HealthPickup(this, 2000, 920);
		items.add(pickup);
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
			if((!entities.contains(turretCenter) || turretCenter.isDead()) && (int)(Math.random() * 200) == 0)
			{
				turretCenter = new Turret(this, 3470, 665, Turret.BASE_HEALTH);
				entities.add(turretCenter);
			}
			else if((!entities.contains(turretFlag) || turretFlag.isDead()) && (int)(Math.random() * 200) == 0)
			{
				turretFlag = new Turret(this, 6335, 285, Turret.BASE_HEALTH);
				entities.add(turretFlag);
			}
			else
			{
				if((int)(Math.random() * 200) == 0)
				{
					entities.add(new FlyingBot(this, Math.random() * 5000 + 700, Math.random() * 300 + 350,
							FlyingBot.BASE_HEALTH));
				}
			}
		}
		
		if((!items.contains(pickup) || pickup.isDead()) && (int)(Math.random() * 200) == 0)
		{
			pickup = new HealthPickup(this, 2000, 920);
			items.add(pickup);
		}
	}

	public void render(Graphics2D g)
	{
		g.drawImage(Content.STAGE_BEACH, 0, 0, 6400, 1200, null);

		for(Projectile proj : projectiles)
		{
			// Don't render if in pipe
			if(!(proj.isSolid() && proj.getX() > 3834 && proj.getX() < 4122 && proj.getY() > 995 && proj.getY() < 1140))
			{
				proj.render(g);
			}
		}
		for(Item item : items)
		{
			item.render(g);
		}
		for(Entity ent : entities)
		{
			// Don't render if in pipe
			if(!(ent.getX() > 3834 && ent.getX() < 4122 && ent.getY() > 995 && ent.getY() < 1140))
			{
				ent.render(g);
			}
		}
		
		// Render player on top
		if(!(player.getX() > 3834 && player.getX() < 4122 && player.getY() > 995 && player.getY() < 1140))
		{
			player.render(g); // TODO Don't render player twice
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
}