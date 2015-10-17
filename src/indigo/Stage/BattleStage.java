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
import indigo.Manager.ContentManager;
import indigo.Manager.SoundManager;
import indigo.Projectile.Projectile;

import java.awt.Graphics2D;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class BattleStage extends Stage
{
	private int enemiesToKill;
	private int enemiesKilled = 0;

	private Respawnable[] respawnables;
	private JSONObject[] respawnInfo;
	private int[] respawnTimers;

	public BattleStage(PlayState playState, JSONObject json)
	{
		super(playState);

		player = new Player(this, (int)(long)json.get("startingX"), (int)(long)json.get("startingY"),
				Player.BASE_HEALTH, Player.BASE_MANA, Player.BASE_STAMINA);
		entities.add(0, player);

		setOffsets((int)(long)json.get("mapX"), (int)(long)json.get("mapY"), BACKGROUND_X, BACKGROUND_Y);

		enemiesToKill = (int)(long)json.get("enemiesToDefeat");

		// Bounding walls
		walls.add(new Wall(0, SKY_LIMIT, 0, mapY));
		walls.add(new Wall(mapX, SKY_LIMIT, mapX, mapY));
		walls.add(new SkyBounds(0, SKY_LIMIT, mapX, SKY_LIMIT));

		JSONArray array = (JSONArray)json.get("walls");
		for(int count = 0; count < array.size(); count++)
		{
			JSONObject object = (JSONObject)array.get(count);
			Wall wall = new Wall((int)(long)object.get("x1"), (int)(long)object.get("y1"), (int)(long)object.get("x2"),
					(int)(long)object.get("y2"));
			walls.add(wall);
		}
		array = (JSONArray)json.get("spike pits");
		for(int count = 0; count < array.size(); count++)
		{
			JSONObject object = (JSONObject)array.get(count);
			SpikePit wall = new SpikePit((int)(long)object.get("x1"), (int)(long)object.get("y1"),
					(int)(long)object.get("x2"), (int)(long)object.get("y2"));
			walls.add(wall);
		}

		array = (JSONArray)json.get("platforms");
		for(int count = 0; count < array.size(); count++)
		{
			JSONObject object = (JSONObject)array.get(count);
			Platform plat = new Platform((int)(long)object.get("x1"), (int)(long)object.get("y1"),
					(int)(long)object.get("x2"), (int)(long)object.get("y2"));
			platforms.add(plat);
		}

		array = (JSONArray)json.get("respawnables");
		respawnables = new Respawnable[array.size()];
		respawnInfo = new JSONObject[array.size()];
		respawnTimers = new int[array.size()];
		for(int count = 0; count < array.size(); count++)
		{
			JSONObject object = (JSONObject)array.get(count);
			respawnInfo[count] = object;
			respawnables[count] = spawnObject(object);
		}

		// SoundManager.play(ContentManager.BACKGROUND_1);
	}

	public void update()
	{
		super.update();

		// Check for dead respawnables and respawn them when time is up
		for(int count = 0; count < respawnables.length; count++)
		{
			// If object dies, set the timer
			if(respawnables[count] != null && respawnables[count].isDead())
			{
				respawnables[count] = null;
				respawnTimers[count] = (int)(long)respawnInfo[count].get("respawnTime");
			}
			// If the timer hits zero, spawn the object
			if(respawnables[count] == null && respawnTimers[count] == 0)
			{
				respawnables[count] = spawnObject(respawnInfo[count]);
			}
			// Decrement the timer
			if(respawnTimers[count] > 0)
			{
				respawnTimers[count]--;
			}
		}
	}

	public void render(Graphics2D g)
	{
		g.translate(-camBackX, -camBackY);
		g.drawImage(ContentManager.getImage(ContentManager.BACKGROUND), 0, 0, backX, backY, null);
		g.translate(camBackX, camBackY);

		g.translate(-camForeX, -camForeY);
		g.drawImage(ContentManager.getImage(ContentManager.STAGE_BEACH), 0, 0, mapX, mapY, null);
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
		g.translate(camForeX, camForeY);
	}

	public void trackDeath(String killer, Entity killed)
	{
		if(killed.equals(player))
		{
			data.setKiller(killer);
			data.setVictory(false);
		}
		if(killed.isMarked())
		{
			enemiesKilled++;
			System.out.println("Enemies killed: " + enemiesKilled);
			// TODO Gain experienced - Add experience variable to Entity class
		}
		if(enemiesKilled >= enemiesToKill)
		{
			data.setVictory(true);
			playState.endGame();
		}
	}

	public Respawnable spawnObject(JSONObject info)
	{
		if(info.get("type").equals("Turret"))
		{
			Turret turret = new Turret(this, (int)(long)info.get("x"), (int)(long)info.get("y"), Turret.BASE_HEALTH);
			entities.add(turret);
			return turret;
		}
		else if(info.get("type").equals("FlyingBot"))
		{
			FlyingBot flyingBot = new FlyingBot(this, (int)(long)info.get("x"), (int)(long)info.get("y"),
					FlyingBot.BASE_HEALTH);
			entities.add(flyingBot);
			return flyingBot;
		}
		else if(info.get("type").equals("HealthPickup"))
		{
			HealthPickup healthPickup = new HealthPickup(this, (int)(long)info.get("x"), (int)(long)info.get("y"));
			items.add(healthPickup);
			return healthPickup;
		}
		return null;
	}
}