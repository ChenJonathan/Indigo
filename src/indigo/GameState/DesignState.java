package indigo.GameState;

import indigo.Main.Game;
import indigo.Manager.ContentManager;
import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;
import indigo.Manager.Manager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The state where levels can be created.
 */
public class DesignState extends GameState
{
	private JSONObject index;
	private JSONObject json;

	private ArrayList<WallData> walls;
	private ArrayList<PlatformData> platforms;
	private ArrayList<RespawnableData> respawnables;

	private ArrayList<Object> creationOrder; // Tracks the order in which objects were created

	private String name;
	private String type;

	// Map size - Measured in actual size
	private int mapX;
	private int mapY;

	// Starting player location - Measured in actual size
	private int startingX;
	private int startingY;

	private double scale = 1; // Factor by which the grid is scaled up to be easier to see
	private int pointRadius; // Radius of selected point
	private int respawnableRadius; // Radius of respawnable visual

	private int xMargin;
	private int yMargin;

	private boolean playerSet = false;
	private boolean objectiveSet = false;

	private int selectedTool = -1;
	private String selectedToolType;

	private Point2D selectedPoint;

	private List<String> objectives = Arrays.asList("Battle", "Defend", "Survive");

	private int hoverValue;
	private String[] hoverText = {"", "Sets the player location", "Sets the map objective",
			"Creates an entity spawn point", "Creates a projectile spawn point", "Creates an item spawn point",
			"Draws a wall", "Draws a platform", "Reverts the last action", "Saves the level", "Exits the level editor"};

	private static final int GRID_SPACE = 10; // Visual pixels per grid square
	private static final int GRID_SCALE = 100; // Actual pixels per grid square

	public static final int SET_PLAYER = 0;
	public static final int SET_OBJECTIVE = 1;
	public static final int SET_ENTITY = 2;
	public static final int SET_PROJECTILE = 3;
	public static final int SET_ITEM = 4;
	public static final int DRAW_WALL = 5;
	public static final int DRAW_PLATFORM = 6;

	/**
	 * Sets up the edit game state for level design.
	 * 
	 * @param gsm The game state manager.
	 */
	public DesignState(GameStateManager gsm)
	{
		super(gsm);

		name = JOptionPane.showInputDialog("Map name:");

		walls = new ArrayList<WallData>();
		platforms = new ArrayList<PlatformData>();
		respawnables = new ArrayList<RespawnableData>();

		creationOrder = new ArrayList<Object>();

		index = ContentManager.load("/index.json");
		if(index.get(name) == null)
		{
			json = new JSONObject();
			json.put("name", name);
			do
			{
				type = JOptionPane.showInputDialog("Map type (Battle / Defend / Survive):");
			}
			while(!objectives.contains(type));
			json.put("type", type);

			do
			{
				mapX = Integer.parseInt(JOptionPane.showInputDialog("Map width (100 to 16000):"));
			}
			while(mapX < 100 || mapX > 16000);
			do
			{
				mapY = Integer.parseInt(JOptionPane.showInputDialog("Map height (100 to 9000):"));
			}
			while(mapY < 100 || mapY > 9000);
			json.put("mapX", mapX);
			json.put("mapY", mapY);
		}
		else
		{
			json = ContentManager.load("/levels/" + name + ".json");

			playerSet = true;
			objectiveSet = true;

			startingX = (int)(long)json.get("startingX");
			startingY = (int)(long)json.get("startingY");

			mapX = (int)(long)json.get("mapX");
			mapY = (int)(long)json.get("mapY");

			for(Object obj : (JSONArray)json.get("walls"))
			{
				JSONObject wall = (JSONObject)obj;
				String type = (String)wall.get("type");
				int x1 = (int)(long)wall.get("x1");
				int y1 = (int)(long)wall.get("y1");
				int x2 = (int)(long)wall.get("x2");
				int y2 = (int)(long)wall.get("y2");
				walls.add(new WallData(type, x1, y1, x2, y2));
			}
			for(Object obj : (JSONArray)json.get("platforms"))
			{
				JSONObject plat = (JSONObject)obj;
				int x1 = (int)(long)plat.get("x1");
				int y1 = (int)(long)plat.get("y1");
				int x2 = (int)(long)plat.get("x2");
				int y2 = (int)(long)plat.get("y2");
				platforms.add(new PlatformData(x1, y1, x2, y2));
			}
			for(Object obj : (JSONArray)json.get("respawnables"))
			{
				JSONObject respawnable = (JSONObject)obj;
				String type = (String)respawnable.get("type");
				int x = (int)(long)respawnable.get("x");
				int y = (int)(long)respawnable.get("y");
				int respawnTime = (int)(long)respawnable.get("respawnTime");
				respawnables.add(new RespawnableData(type, x, y, respawnTime));
			}
		}

		scale = Math.min(1600.0 / scale(mapX), 900.0 / scale(mapY));
		pointRadius = (int)(scale * 2);
		respawnableRadius = (int)(scale * 3);

		xMargin = (int)(50);
		yMargin = (int)((Game.HEIGHT - scale(mapY)) / (2));
	}

	private class WallData
	{
		private String type;
		private int x1;
		private int y1;
		private int x2;
		private int y2;

		private WallData(String type, int x1, int y1, int x2, int y2)
		{
			this.type = type;
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		public String type()
		{
			return type;
		}

		public int x1()
		{
			return x1;
		}

		public int y1()
		{
			return y1;
		}

		public int x2()
		{
			return x2;
		}

		public int y2()
		{
			return y2;
		}
	}

	private class PlatformData
	{
		private int x1;
		private int y1;
		private int x2;
		private int y2;

		private PlatformData(int x1, int y1, int x2, int y2)
		{
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		public int x1()
		{
			return x1;
		}

		public int y1()
		{
			return y1;
		}

		public int x2()
		{
			return x2;
		}

		public int y2()
		{
			return y2;
		}
	}

	private class RespawnableData
	{
		private String type;
		private int x;
		private int y;
		private int respawnTime;

		private RespawnableData(String type, int x, int y, int respawnTime)
		{
			this.type = type;
			this.x = x;
			this.y = y;
			this.respawnTime = respawnTime;
		}

		public String type()
		{
			return type;
		}

		public int x()
		{
			return x;
		}

		public int y()
		{
			return y;
		}

		public int respawnTime()
		{
			return respawnTime;
		}
	}

	@Override
	public void update()
	{
		handleInput();
	}

	@Override
	public void render(Graphics2D g)
	{
		// Draw background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

		// Draw grid
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke((int)Math.sqrt(scale)));
		for(int count = xMargin; count <= scale(mapX) + xMargin; count += GRID_SPACE * scale)
		{
			g.drawLine(count, yMargin, count, (int)scale(mapY) + yMargin);
		}
		for(int count = yMargin; count <= scale(mapY) + yMargin; count += GRID_SPACE * scale)
		{
			g.drawLine(xMargin, count, (int)scale(mapX) + xMargin, count);
		}

		// Draw walls
		g.setColor(Color.BLUE);
		g.setStroke(new BasicStroke((int)(scale)));
		for(WallData wall : walls)
		{
			int x1 = (int)(xMargin + scale(wall.x1()));
			int y1 = (int)(yMargin + scale(wall.y1()));
			int x2 = (int)(xMargin + scale(wall.x2()));
			int y2 = (int)(yMargin + scale(wall.y2()));
			g.drawLine(x1, y1, x2, y2);
		}

		// Draw platforms
		g.setColor(Color.RED);
		for(PlatformData plat : platforms)
		{
			int x1 = (int)(xMargin + scale(plat.x1()));
			int y1 = (int)(yMargin + scale(plat.y1()));
			int x2 = (int)(xMargin + scale(plat.x2()));
			int y2 = (int)(yMargin + scale(plat.y2()));
			g.drawLine(x1, y1, x2, y2);
		}

		// Draw respawnables
		g.setColor(Color.GREEN);
		for(RespawnableData respawnable : respawnables)
		{
			int x = (int)(xMargin + scale(respawnable.x()));
			int y = (int)(yMargin + scale(respawnable.y()));
			g.fill(new Ellipse2D.Double(x - respawnableRadius, y - respawnableRadius, respawnableRadius * 2,
					respawnableRadius * 2));
		}

		// Draw player
		g.setColor(Color.MAGENTA);
		if(playerSet)
		{
			int x = (int)(xMargin + scale(startingX));
			int y = (int)(yMargin + scale(startingY));
			g.fill(new Ellipse2D.Double(x - respawnableRadius, y - respawnableRadius, respawnableRadius * 2,
					respawnableRadius * 2));
		}

		// Draw selected point
		g.setColor(Color.GRAY);
		if(selectedPoint != null)
		{
			int x = (int)(xMargin + selectedPoint.getX() * GRID_SPACE * scale);
			int y = (int)(yMargin + selectedPoint.getY() * GRID_SPACE * scale);
			g.fill(new Ellipse2D.Double(x - pointRadius, y - pointRadius, pointRadius * 2, pointRadius * 2));
		}

		// Draw toolbars
		g.drawImage(ContentManager.getImage(ContentManager.TOOLBAR), 1741, 45, 90, 990, null);

		// Draw hover text
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
		FontMetrics fontMetrics = g.getFontMetrics();
		g.drawString(hoverText[hoverValue], 1690 - fontMetrics.stringWidth(hoverText[hoverValue]), 74);
		if(hoverValue != 0)
		{
			g.drawRect(1670 - fontMetrics.stringWidth(hoverText[hoverValue]), 46,
					fontMetrics.stringWidth(hoverText[hoverValue]) + 40, 40);
		}
		
		// Draw selected tool box
		if(selectedTool != -1)
		{
			g.setColor(Color.YELLOW);
			g.setStroke(new BasicStroke(6));
			g.drawRect(1741, 100 * selectedTool + 45, 90, 90);
		}
	}

	@Override
	public void handleInput()
	{
		// Tool select
		if(Manager.input.keyPress(InputManager.K1))
		{
			selectTool(SET_PLAYER);
		}
		else if(Manager.input.keyPress(InputManager.K2))
		{
			selectTool(SET_OBJECTIVE);
		}
		else if(Manager.input.keyPress(InputManager.K3))
		{
			selectTool(SET_ENTITY);
		}
		else if(Manager.input.keyPress(InputManager.K4))
		{
			selectTool(SET_PROJECTILE);
		}
		else if(Manager.input.keyPress(InputManager.K5))
		{
			selectTool(SET_ITEM);
		}
		else if(Manager.input.keyPress(InputManager.K6))
		{
			selectTool(DRAW_WALL);
		}
		else if(Manager.input.keyPress(InputManager.K7))
		{
			selectTool(DRAW_PLATFORM);
		}
		else if(Manager.input.keyPress(InputManager.K8))
		{
			undo();
		}
		else if(Manager.input.keyPress(InputManager.ESCAPE))
		{
			// Quitting the level editor
			String save;
			do
			{
				save = JOptionPane.showInputDialog("Save? (Y / N):");
				if(save.equals("Y"))
				{
					save(true);
				}
				else if(save.equals("N"))
				{
					gsm.setState(GameStateManager.MENU);
				}
			}
			while(!save.equals("Y") && !save.equals("N"));
		}

		// Mouse clicking and hovering
		if(Manager.input.mouseInRect(xMargin - GRID_SPACE * scale / 2, yMargin - GRID_SPACE * scale / 2, scale(mapX)
				+ GRID_SPACE * scale, scale(mapY) + GRID_SPACE * scale))
		{
			if(Manager.input.mousePress())
			{
				int gridX = (int)Math.round((Manager.input.mouseX() - xMargin) / (GRID_SPACE * scale));
				int gridY = (int)Math.round((Manager.input.mouseY() - yMargin) / (GRID_SPACE * scale));
				selectPoint(gridX, gridY);
			}
		}
		for(int count = 1; count <= 10; count++)
		{
			if(Manager.input.mouseInRect(1741, 100 * count - 55, 90, 90))
			{
				hoverValue = count;
				if(Manager.input.mousePress() && count <= 7)
				{
					selectTool(count - 1);
				}
				break;
			}
			hoverValue = 0;
		}
	}

	/**
	 * Performs action corresponding to clicking on grid point.
	 * 
	 * @param x The x-position measured in grid rows.
	 * @param y The y-position measured in grid columns.
	 */
	public void selectPoint(int x, int y)
	{
		if(selectedTool == DRAW_WALL || selectedTool == DRAW_PLATFORM)
		{
			if(selectedPoint == null)
			{
				selectedPoint = new Point2D.Double(x, y);
			}
			else
			{
				if(x != selectedPoint.getX() || y != selectedPoint.getY()) // Length > 0
				{
					int x1 = (int)(selectedPoint.getX() * GRID_SCALE);
					int y1 = (int)(selectedPoint.getY() * GRID_SCALE);
					int x2 = x * GRID_SCALE;
					int y2 = y * GRID_SCALE;
					if(selectedTool == DRAW_WALL)
					{
						WallData wall = new WallData(selectedToolType, x1, y1, x2, y2);
						walls.add(wall);
						creationOrder.add(wall);
					}
					else
					{
						PlatformData plat = new PlatformData(x1, y1, x2, y2);
						platforms.add(plat);
						creationOrder.add(plat);
					}
				}
				selectedPoint = null;
			}
		}
		else
		{
			if(selectedTool == SET_PLAYER)
			{
				playerSet = true;
				startingX = x * GRID_SCALE;
				startingY = y * GRID_SCALE;
				json.put("startingX", startingX);
				json.put("startingY", startingY);
			}
		}
	}

	public void selectTool(int tool)
	{
		selectedPoint = null;
		if(tool == SET_OBJECTIVE)
		{
			if(type.equals("Battle"))
			{
				int enemiesToDefeat = Integer.parseInt(JOptionPane.showInputDialog("Number of enemies to defeat:"));
				json.put("enemiesToDefeat", enemiesToDefeat);
			}
			else if(type.equals("Defend"))
			{
				int coreX = Integer.parseInt(JOptionPane.showInputDialog("X-position of the core:"));
				int coreY = Integer.parseInt(JOptionPane.showInputDialog("Y-position of the core:"));
				json.put("coreX", coreX);
				json.put("coreY", coreY);
			}
			else if(type.equals("Survive"))
			{
				int surviveTime = Integer.parseInt(JOptionPane.showInputDialog("Survival duration:"));
				json.put("surviveTime", surviveTime);
			}
		}
		else
		{
			selectedTool = tool;

			if(selectedTool == SET_ENTITY || selectedTool == SET_PROJECTILE || selectedTool == SET_ITEM
					|| selectedTool == DRAW_WALL)
			{
				selectedToolType = JOptionPane.showInputDialog("Tool Type:");
			}
		}
	}

	/**
	 * Converts actual size to visual size.
	 * 
	 * @param value Value to be converted.
	 * @return Value in visual pixels.
	 */
	public double scale(double value)
	{
		return value / GRID_SCALE * GRID_SPACE * scale;
	}

	/**
	 * Reverts the last change.
	 */
	public void undo()
	{
		if(creationOrder.size() > 0)
		{
			selectedPoint = null;
			Object remove = creationOrder.get(creationOrder.size() - 1);
			walls.remove(remove);
			platforms.remove(remove);
			respawnables.remove(remove);
			creationOrder.remove(remove);
		}
	}

	/**
	 * Saves the designed level.
	 * 
	 * @param exit Whether to exit to the menu afterwards or not.
	 */
	public void save(boolean exit)
	{
		if(!playerSet)
		{
			JOptionPane.showMessageDialog(new JFrame(), "Player not set.");
		}
		else if(!objectiveSet)
		{
			JOptionPane.showMessageDialog(new JFrame(), "Objective not set.");
		}
		else
		{
			try
			{
				JSONObject index = ContentManager.load("/index.json");

				// Overwrite check
				if(index.get(name) != null)
				{
					String overwrite;
					do
					{
						overwrite = JOptionPane.showInputDialog("Overwrite existing file? (Y / N):");
						if(overwrite.equals("N"))
						{
							return;
						}
					}
					while(!overwrite.equals("Y") && !overwrite.equals("N"));
				}
				else
				{
					json.put("id", index.size());
					index.put(name, index.size());
					FileWriter file = new FileWriter(new File("").getAbsolutePath() + "/resources/data/index.json");
					file.write(index.toJSONString());
					file.flush();
					file.close();
				}

				// Packing objects into JSONObject
				for(WallData wall : walls)
				{
					addToJSON(wall);
				}
				for(PlatformData plat : platforms)
				{
					addToJSON(plat);
				}
				for(RespawnableData respawnable : respawnables)
				{
					addToJSON(respawnable);
				}

				// Saving and exiting
				String fileName = name.replace(" ", "_").toLowerCase();
				FileWriter file = new FileWriter(new File("").getAbsolutePath() + "/resources/data/levels/" + fileName
						+ ".json");
				file.write(json.toJSONString());
				file.flush();
				file.close();
				JOptionPane.showMessageDialog(new JFrame(), "Level saved!");
				if(exit)
				{
					gsm.setState(GameStateManager.MENU);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds the WallData object to the JSON.
	 */
	public void addToJSON(WallData wall)
	{
		JSONArray walls = (JSONArray)json.get("walls");
		if(walls == null)
		{
			walls = new JSONArray();
			json.put("walls", walls);
		}
		JSONObject obj = new JSONObject();
		obj.put("type", wall.type());
		obj.put("x1", wall.x1());
		obj.put("y1", wall.y1());
		obj.put("x2", wall.x2());
		obj.put("y2", wall.y2());
		walls.add(obj);
	}

	/**
	 * Adds the PlatformData object to the JSON.
	 */
	public void addToJSON(PlatformData plat)
	{
		JSONArray platforms = (JSONArray)json.get("platforms");
		if(platforms == null)
		{
			platforms = new JSONArray();
			json.put("platforms", platforms);
		}
		JSONObject obj = new JSONObject();
		obj.put("x1", plat.x1());
		obj.put("y1", plat.y1());
		obj.put("x2", plat.x2());
		obj.put("y2", plat.y2());
		platforms.add(obj);
	}

	/**
	 * Adds the RespawnableData object to the JSON.
	 */
	public void addToJSON(RespawnableData respawnable)
	{
		JSONArray respawnables = (JSONArray)json.get("respawnables");
		if(respawnables == null)
		{
			respawnables = new JSONArray();
			json.put("respawnables", respawnables);
		}
		JSONObject obj = new JSONObject();
		obj.put("type", respawnable.type());
		obj.put("x", respawnable.x());
		obj.put("y", respawnable.y());
		obj.put("respawnTime", respawnable.respawnTime());
		respawnables.add(obj);
	}
}