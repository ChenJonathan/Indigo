package indigo.Stage;

import java.lang.reflect.Constructor;

import javax.imageio.ImageIO;

import indigo.Entity.Core;
import indigo.Entity.Entity;
import indigo.Entity.Player;
import indigo.GameState.PlayState;
import indigo.Interactive.Interactive;
import indigo.Landscape.Land;
import indigo.Landscape.Platform;
import indigo.Landscape.SkyBounds;
import indigo.Landscape.Wall;
import indigo.Manager.ContentManager;
import indigo.Projectile.Projectile;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DefendStage extends Stage
{
	private Core core;

	private int survivalDuration;

	private Respawnable[] respawnables;
	private JSONObject[] respawnInfo;
	private int[] respawnTimers;

	public DefendStage(PlayState playState, JSONObject json)
	{
		super(playState);

		startingX = (int)(long)json.get("startingX");
		startingY = (int)(long)json.get("startingY");
		int health = Player.BASE_HEALTH + (data.getLevel() - 1) * 10;
		int mana = Player.BASE_MANA + (data.getLevel() - 1) * 10;
		player = new Player(this, startingX, startingY, health, mana, Player.BASE_STAMINA);
		entities.add(0, player);
		name = json.get("name") + "";

		background = ContentManager.getImage(ContentManager.BACKGROUND);
		try
		{
			String fileName = ((String)json.get("name")).replace(" ", "_").toLowerCase();
			foreground = ImageIO.read(getClass().getResourceAsStream("/images/stages/" + fileName + ".png"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		setOffsets((int)(long)json.get("mapX"), (int)(long)json.get("mapY"));

		core = new Core(this, (int)(long)json.get("coreX"), (int)(long)json.get("coreY"), Core.BASE_HEALTH);
		entities.add(1, core);
		survivalDuration = (int)(long)json.get("survivalDuration");
		suddenDeath = false;

		walls.add(new SkyBounds(this, -Land.THICKNESS / 2, SKY_LIMIT - Land.THICKNESS / 2, -Land.THICKNESS / 2, mapY
				- Land.THICKNESS / 2));
		walls.add(new SkyBounds(this, mapX + Land.THICKNESS / 2, SKY_LIMIT - Land.THICKNESS / 2, mapX + Land.THICKNESS
				/ 2, mapY - Land.THICKNESS / 2));
		walls.add(new SkyBounds(this, -Land.THICKNESS / 2, SKY_LIMIT - Land.THICKNESS / 2, mapX + Land.THICKNESS / 2,
				SKY_LIMIT - Land.THICKNESS / 2));

		JSONArray array = (JSONArray)json.get("landscape");
		if(array == null)
		{
			array = new JSONArray();
		}
		for(int count = 0; count < array.size(); count++)
		{
			JSONObject object = (JSONObject)array.get(count);
			createLand(object);
		}

		array = (JSONArray)json.get("spawns");
		if(array == null)
		{
			array = new JSONArray();
		}
		respawnables = new Respawnable[array.size()];
		respawnInfo = new JSONObject[array.size()];
		respawnTimers = new int[array.size()];
		for(int count = 0; count < array.size(); count++)
		{
			JSONObject object = (JSONObject)array.get(count);
			respawnInfo[count] = object;
			respawnables[count] = spawnObject(object);
		}
	}

	public void update()
	{
		if(!entities.contains(core))
		{
			data.setDeathMessage("The core was destroyed by _");
			playState.endGame(false);
		}

		if(playState.getTime() == survivalDuration)
		{
			playState.endGame(true);
		}

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

		super.update();
	}

	public void trackDeath(String killer, Entity killed)
	{
		if((killed.equals(player) & suddenDeath) || killed.equals(core))
		{
			data.setKiller(killer);
		}
		else if(killed.isMarked())
		{
			data.setExperience(data.getExperience() + killed.getExperience());
		}
	}

	public int getCoreHealth()
	{
		return core.getHealth();
	}

	public int getCoreMaxHealth()
	{
		return core.getMaxHealth();
	}

	public int getSurvivalDuration()
	{
		return survivalDuration;
	}

	public Entity getPlayer()
	{
		return core;
	}

	public void createLand(JSONObject info)
	{
		Land object = null;
		String type = ((String)info.get("type")).replace(" ", "");
		double x1 = Integer.parseInt(info.get("x1") + "");
		double y1 = Integer.parseInt(info.get("y1") + "");
		double x2 = Integer.parseInt(info.get("x2") + "");
		double y2 = Integer.parseInt(info.get("y2") + "");

		try
		{
			String className = "indigo.Landscape." + type;
			Class<?> varClass = Class.forName(className);
			Constructor<?> varConstructor = varClass.getConstructor(Stage.class, double.class, double.class,
					double.class, double.class);
			object = (Land)varConstructor.newInstance(new Object[] {this, x1, y1, x2, y2});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(object instanceof Wall)
		{
			walls.add((Wall)object);
		}
		else if(object instanceof Platform)
		{
			platforms.add((Platform)object);
		}
	}

	public Respawnable spawnObject(JSONObject info)
	{
		Respawnable object = null;
		String category = (String)info.get("category");
		String type = ((String)info.get("type")).replace(" ", "");
		double x = Integer.parseInt(info.get("x") + "");
		double y = Integer.parseInt(info.get("y") + "");

		try
		{
			String className = "indigo." + info.get("category") + "." + type;
			Class<?> varClass = Class.forName(className);
			Constructor<?> varConstructor = varClass.getConstructor(Stage.class, double.class, double.class);
			object = (Respawnable)varConstructor.newInstance(new Object[] {this, x, y});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		switch(category)
		{
			case "Entity":
				entities.add((Entity)object);
				break;
			case "Projectile":
				projectiles.add((Projectile)object);
				break;
			case "Interactive":
				interactives.add((Interactive)object);
				break;
		}

		return object;
	}
}