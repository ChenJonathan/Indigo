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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;
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

	private ArrayList<LandData> landscape;
	private ArrayList<SpawnData> spawns;

	private enum Action
	{
		ADD, REMOVE, OVERWRITE
	}

	// Tracks the order in which objects were created / deleted
	private ArrayList<Action> actionOrder;
	private ArrayList<MapData> creationOrder;
	private ArrayList<MapData> deletionOrder;

	// Tracks position in above arrays - Used for undo / redo
	private int[] indices = {-1, -1, -1};
	private int ACTION = 0;
	private int CREATION = 1;
	private int DELETION = 2;

	// Grids of objects
	LandData[][][][] landscapeGrid;
	SpawnData[][] spawnsGrid;

	private String name = "";
	private String type = "";

	// Map size - Measured in actual size
	private int mapX;
	private int mapY;

	// Starting player location - Measured in actual size
	private int startingX;
	private int startingY;

	private double scale = 1; // Factor by which the grid is scaled up to be easier to see
	private int selectedPointRadius; // Radius of selected point
	private int spawnRadius; // Radius of respawnable visual

	// Map margins
	private int xMargin = 50;
	private int yMargin = 50;

	// Whether the player and objective is set or not
	private boolean playerSet = false;
	private boolean objectiveSet = false;

	// Selected toolbar item and subtype
	private int selectedTool = 0;
	private int selectedToolType = 0;

	// Toolbar item or spawn object that the mouse is hovering over
	private int hoverValue;
	private SpawnData hoverSpawn;

	// Whether certain UI features should be shown or not
	private boolean confirmBox = false;
	private boolean arrowLeft = false;
	private boolean arrowRight = false;

	// Point selected if the current tool requires two points
	private Point2D selectedPoint;

	// Tools and tool types - Display text on selection box
	private HashMap<Integer, String> tools = new HashMap<Integer, String>();
	private HashMap<Integer, String[]> toolTypes = new HashMap<Integer, String[]>();
	private HashMap<String, String> descriptionText = new HashMap<String, String>();

	// Used to format the JSON String
	private String[] formatMaster = {"name", "type", "", "mapX", "mapY", "", "startingX", "startingY", "",
			"objectives", "", "landscape", "", "spawns"};
	private String[][] formatObjective = { {"enemiesToDefeat"}, {"coreX", "coreY", "", "survivalDuration"},
			{"survivalDuration"}, {"destinationX", "destinationY", "", "timeLimit"}};
	private String[] formatLand = {"type", "x1", "y1", "x2", "y2"};
	private String[] formatSpawn = {"category", "type", "x", "y", "respawnTime"};
	private String[] formatIndex = {};

	private static final int GRID_SPACE = 10; // Visual pixels per grid square
	private static final int GRID_SCALE = 100; // Actual pixels per grid square

	private static final int DEFAULT_MARGIN_X = 40;
	private static final int DEFAULT_MARGIN_Y = 40;

	public static final int NUM_TOOLS = 12;

	public static final int SET_PLAYER = 0;
	public static final int SET_OBJECTIVE = 1;
	public static final int SET_LAND = 2;
	public static final int SET_ENTITY = 3;
	public static final int SET_PROJECTILE = 4;
	public static final int SET_INTERACTIVE = 5;
	public static final int UNDO = 6;
	public static final int DELETE = 7;
	public static final int CLEAR = 8;
	public static final int SAVE = 9;
	public static final int LOAD = 10;
	public static final int EXIT = 11;

	/**
	 * Sets up the edit game state for level design.
	 * 
	 * @param gsm The game state manager.
	 */
	public DesignState(GameStateManager gsm)
	{
		super(gsm);

		// Initialize tools
		tools.put(SET_PLAYER, "Set Player");
		tools.put(SET_OBJECTIVE, "Set Objective");
		tools.put(SET_LAND, "Set Land");
		tools.put(SET_ENTITY, "Set Entity");
		tools.put(SET_PROJECTILE, "Set Projectile");
		tools.put(SET_INTERACTIVE, "Set Interactive");
		tools.put(UNDO, "Undo / Redo");
		tools.put(DELETE, "Delete Tool");
		tools.put(CLEAR, "Reset / Clear Map");
		tools.put(SAVE, "Save Map");
		tools.put(LOAD, "Load Map");
		tools.put(EXIT, "Exit");

		// Initialize tool types
		toolTypes.put(SET_PLAYER, new String[] {"Player"});
		toolTypes.put(SET_OBJECTIVE, new String[] {"Battle", "Defend", "Survival", "Travel"});
		toolTypes.put(SET_LAND, new String[] {"Platform", "Wall", "Spike Pit", "Force Field"});
		toolTypes.put(SET_ENTITY, new String[] {"Flying Bot", "Turret", "Harvester", "Tree"});
		toolTypes.put(SET_PROJECTILE, new String[] {"Steel Beam"});
		toolTypes.put(SET_INTERACTIVE, new String[] {"Health Pickup"});
		toolTypes.put(UNDO, new String[] {"Undo", "Redo"});
		toolTypes.put(DELETE, new String[] {"Delete Land", "Delete Spawn"});
		toolTypes.put(CLEAR, new String[] {"Reset Map", "Clear Map"});
		toolTypes.put(SAVE, new String[] {"Save Map"});
		toolTypes.put(LOAD, new String[] {"Load Map"});
		toolTypes.put(EXIT, new String[] {"Exit"});

		// Initialize tool hover description text
		descriptionText.put("Set Player", "Set the player's starting location.");
		descriptionText.put("Set Objective", "Set the level objective.");
		descriptionText.put("Set Land", "Create walls or platforms.");
		descriptionText.put("Set Entity", "Create an entity spawn point.");
		descriptionText.put("Set Projectile", "Create a projectile spawn point.");
		descriptionText.put("Set Interactive", "Create an interactive object spawn point.");
		descriptionText.put("Undo / Redo", "Undo or redo an action.");
		descriptionText.put("Delete Tool", "Delete an object.");
		descriptionText.put("Reset / Clear Map", "Reset or clear the level.");
		descriptionText.put("Save Map", "Save the level.");
		descriptionText.put("Load Map", "Load a level.");
		descriptionText.put("Exit", "Exit to the main menu.");

		// Initialize tool type description text
		descriptionText.put("Player", "The player's starting location.");
		descriptionText.put("Battle", "Defeat a specified number of enemies.");
		descriptionText.put("Defend", "Defend a core from enemy attack for a specified duration.");
		descriptionText.put("Survival", "Survive for a specified duration.");
		descriptionText.put("Travel", "Reach a specified destination.");
		descriptionText.put("Platform", "A nonsolid platform that can be both jumped through and landed on. "
				+ "Must be more horizontal than vertical or the platform will not be created.");
		descriptionText.put("Wall", "An impassable wall.");
		descriptionText.put("Spike Pit", "A wall that instantly kills solid entities upon contact.");
		descriptionText.put("Force Field", "A wall that lets entities through but destroys all projectiles.");
		descriptionText.put("Flying Bot", "A flying robot that can shoot left or right.");
		descriptionText.put("Turret", "A stationary turret that can rotate its arm towards its target. "
				+ "Attaches itself to the nearest wall or platform upon map creation. "
				+ "Cannot aim towards its base.");
		descriptionText.put("Harvester", "A tree harvesting robot. "
				+ "Moves towards the nearest tree and attempts to saw it down");
		descriptionText.put("Tree", "A tree that spawns branches which can be jumped on. "
				+ "Branches break after a short duration."
				+ "Attaches itself to the nearest perfectly horizontal wall or platform upon map creation. "
				+ "The tree is vulnerable to enemy attack and its size is around 1.5 by 6 grid spaces.");
		descriptionText.put("Steel Beam", "A falling steel beam that breaks on contact.");
		descriptionText.put("Health Pickup", "An item that replenishes player health when collected.");
		descriptionText.put("Undo", "Reverts the last action. Player and objective changes are not reverted.");
		descriptionText.put("Redo", "Reverts the last undo.");
		descriptionText.put("Delete Land", "Deletes a wall or platform from the map.");
		descriptionText.put("Delete Spawn", "Deletes an entity, projectile, or interactive object from the map.");
		descriptionText.put("Reset Map", "Deletes all contents of the map but does not save or modify the file.");
		descriptionText.put("Clear Map", "Deletes the map from the index and returns to the main menu. "
				+ "The file still exists but the program will overwrite it automatically.");

		name = JOptionPane.showInputDialog("Map name:");

		landscape = new ArrayList<LandData>();
		spawns = new ArrayList<SpawnData>();

		actionOrder = new ArrayList<Action>();
		creationOrder = new ArrayList<MapData>();
		deletionOrder = new ArrayList<MapData>();

		index = ContentManager.load("/index.json");
		if(index.get(name) == null)
		{
			json = new JSONObject();
			json.put("name", name);

			do
			{
				mapX = Integer.parseInt(JOptionPane.showInputDialog("Map width (2400 to 12000):"));
				mapX = mapX / GRID_SCALE * GRID_SCALE;
			}
			while(mapX < 2400 || mapX > 12000);
			do
			{
				mapY = Integer.parseInt(JOptionPane.showInputDialog("Map height (1600 to 8000):"));
				mapY = mapY / GRID_SCALE * GRID_SCALE;
			}
			while(mapY < 1600 || mapY > 8000);
			json.put("mapX", mapX);
			json.put("mapY", mapY);

			landscapeGrid = new LandData[mapX / GRID_SCALE + 1][mapY / GRID_SCALE + 1][mapX / GRID_SCALE + 1][mapY
					/ GRID_SCALE + 1];
			spawnsGrid = new SpawnData[mapX / GRID_SCALE + 1][mapY / GRID_SCALE + 1];
		}
		else
		{
			String fileName = name.replace(" ", "_").toLowerCase();
			json = ContentManager.load("/levels/" + fileName + ".json");

			if(json.get("type") != null)
			{
				type = (String)json.get("type");
				objectiveSet = true;
			}

			if(json.get("startingX") != null && json.get("startingY") != null)
			{
				startingX = (int)(long)json.get("startingX");
				startingY = (int)(long)json.get("startingY");
				playerSet = true;
			}

			mapX = (int)(long)json.get("mapX");
			mapY = (int)(long)json.get("mapY");

			landscapeGrid = new LandData[mapX / GRID_SCALE + 1][mapY / GRID_SCALE + 1][mapX / GRID_SCALE + 1][mapY
					/ GRID_SCALE + 1];
			spawnsGrid = new SpawnData[mapX / GRID_SCALE + 1][mapY / GRID_SCALE + 1];

			if(json.get("landscape") != null)
			{
				for(Object obj : (JSONArray)json.get("landscape"))
				{
					JSONObject land = (JSONObject)obj;
					String type = (String)land.get("type");
					int x1 = (int)(long)land.get("x1");
					int y1 = (int)(long)land.get("y1");
					int x2 = (int)(long)land.get("x2");
					int y2 = (int)(long)land.get("y2");
					LandData landData = new LandData(type, x1, y1, x2, y2);
					addToList(landData);
				}
			}
			if(json.get("spawns") != null)
			{
				for(Object obj : (JSONArray)json.get("spawns"))
				{
					JSONObject spawn = (JSONObject)obj;
					String category = (String)spawn.get("category");
					String type = (String)spawn.get("type");
					int x = (int)(long)spawn.get("x");
					int y = (int)(long)spawn.get("y");
					int respawnTime = (int)(long)spawn.get("respawnTime");
					SpawnData spawnData = new SpawnData(category, type, x, y, respawnTime);
					addToList(spawnData);
				}
			}

			// Prevents modifying elements from loaded file
			actionOrder.clear();
			deletionOrder.clear();
			creationOrder.clear();
			indices = new int[] {-1, -1, -1};
		}

		scale = Math.min(1500.0 / scale(mapX), 1000.0 / scale(mapY));
		selectedPointRadius = (int)(scale * 3);
		spawnRadius = (int)(scale * 4);

		xMargin = (int)((1500 - scale(mapX)) / 2) + DEFAULT_MARGIN_X;
		yMargin = (int)((1000 - scale(mapY)) / 2) + DEFAULT_MARGIN_Y;
	}

	private abstract class MapData
	{
		protected String category;
		protected String type;

		private MapData(String category, String type)
		{
			this.category = category;
			this.type = type;
		}
	}

	private class LandData extends MapData
	{
		private int x1;
		private int y1;
		private int x2;
		private int y2;

		private double slope;
		private boolean horizontal;

		private LandData(String type, int x1, int y1, int x2, int y2)
		{
			super("Land", type);
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;

			slope = (y2 - y1) / ((x2 - x1) == 0? 0.0000001 : (x2 - x1));
			horizontal = (Math.abs(x2 - x1) >= Math.abs(y2 - y1))? true : false;
		}
	}

	private class SpawnData extends MapData
	{
		private int x;
		private int y;
		private int respawnTime;

		private SpawnData(String category, String type, int x, int y, int respawnTime)
		{
			super(category, type);
			this.x = x;
			this.y = y;
			this.respawnTime = respawnTime;
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
		for(double count = xMargin; count < scale(mapX) + xMargin; count += GRID_SPACE * scale)
		{
			g.drawLine((int)count, yMargin, (int)count, (int)scale(mapY) + yMargin);
		}
		g.drawLine((int)scale(mapX) + xMargin, yMargin, (int)scale(mapX) + xMargin, (int)scale(mapY) + yMargin);
		for(double count = yMargin; count < scale(mapY) + yMargin; count += GRID_SPACE * scale)
		{
			g.drawLine(xMargin, (int)count, (int)scale(mapX) + xMargin, (int)count);
		}
		g.drawLine(xMargin, (int)scale(mapY) + yMargin, (int)scale(mapX) + xMargin, (int)scale(mapY) + yMargin);

		// Draw landscape
		g.setStroke(new BasicStroke((int)(scale * 1.5)));
		for(LandData land : landscape)
		{
			switch(land.type)
			{
				case "Platform":
					g.setColor(Color.GREEN);
					break;
				case "Spike Pit":
					g.setColor(Color.RED);
					break;
				case "Force Field":
					g.setColor(Color.CYAN);
					break;
				default:
					g.setColor(Color.BLUE);
					break;
			}
			int x1 = (int)(xMargin + scale(land.x1));
			int y1 = (int)(yMargin + scale(land.y1));
			int x2 = (int)(xMargin + scale(land.x2));
			int y2 = (int)(yMargin + scale(land.y2));
			g.drawLine(x1, y1, x2, y2);
		}

		// Draw spawns
		for(SpawnData spawn : spawns)
		{
			switch(spawn.category)
			{
				case "Entity":
					g.setColor(Color.RED);
					break;
				case "Projectile":
					g.setColor(Color.ORANGE);
					break;
				case "Interactive":
					g.setColor(Color.PINK);
					break;
			}
			int x = (int)(xMargin + scale(spawn.x));
			int y = (int)(yMargin + scale(spawn.y));
			g.fill(new Ellipse2D.Double(x - spawnRadius, y - spawnRadius, spawnRadius * 2, spawnRadius * 2));
		}

		// Draw player
		g.setColor(Color.MAGENTA);
		if(playerSet)
		{
			int x = (int)(xMargin + scale(startingX));
			int y = (int)(yMargin + scale(startingY));
			g.fill(new Ellipse2D.Double(x - spawnRadius, y - spawnRadius, spawnRadius * 2, spawnRadius * 2));
		}

		// Draw objectives
		if(objectiveSet)
		{
			g.setColor(Color.CYAN);
			if(type.equals("Defend"))
			{
				int x = (int)(xMargin + scale(Integer.parseInt(json.get("coreX") + "")));
				int y = (int)(yMargin + scale(Integer.parseInt(json.get("coreY") + "")));
				g.fill(new Ellipse2D.Double(x - spawnRadius, y - spawnRadius, spawnRadius * 2, spawnRadius * 2));
			}
			else if(type.equals("Travel"))
			{
				int x = (int)(xMargin + scale(Integer.parseInt(json.get("destinationX") + "")));
				int y = (int)(yMargin + scale(Integer.parseInt(json.get("destinationY") + "")));
				g.fill(new Ellipse2D.Double(x - spawnRadius, y - spawnRadius, spawnRadius * 2, spawnRadius * 2));
			}
		}

		// Draw selected point
		g.setColor(Color.GRAY);
		if(selectedPoint != null)
		{
			int x = (int)(xMargin + selectedPoint.getX() * GRID_SPACE * scale);
			int y = (int)(yMargin + selectedPoint.getY() * GRID_SPACE * scale);
			g.fill(new Ellipse2D.Double(x - selectedPointRadius, y - selectedPointRadius, selectedPointRadius * 2,
					selectedPointRadius * 2));
		}

		// Draw toolbar
		g.drawImage(ContentManager.getImage(ContentManager.TOOLBAR), 1579, 39, 302, 402, null);

		// Draw hover box
		if(hoverValue != -1)
		{
			g.setColor(Color.GRAY);
			g.setStroke(new BasicStroke(6));
			g.drawRect(1585 + (hoverValue % 3) * 100, 45 + (hoverValue / 3) * 100, 92, 92);
		}

		// Draw tool selection box
		g.setColor(Color.YELLOW);
		g.setStroke(new BasicStroke(6));
		g.drawRect(1585 + (selectedTool % 3) * 100, 45 + (selectedTool / 3) * 100, 92, 92);

		// Draw selection box
		g.drawImage(ContentManager.getImage(ContentManager.SELECTION_BOX), 1580, 480, 300, 110, null);
		if(arrowLeft)
		{
			g.drawImage(ContentManager.getImage(ContentManager.ARROW_LEFT_ACTIVE), 1580, 480, 60, 110, null);
		}
		else
		{
			g.drawImage(ContentManager.getImage(ContentManager.ARROW_LEFT_INACTIVE), 1580, 480, 60, 110, null);
		}
		if(arrowRight)
		{
			g.drawImage(ContentManager.getImage(ContentManager.ARROW_RIGHT_ACTIVE), 1820, 480, 60, 110, null);
		}
		else
		{
			g.drawImage(ContentManager.getImage(ContentManager.ARROW_RIGHT_INACTIVE), 1820, 480, 60, 110, null);
		}

		// Draw tool type text
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
		FontMetrics fontMetrics = g.getFontMetrics();
		String text = toolTypes.get(selectedTool)[selectedToolType];
		g.drawString(text, 1730 - fontMetrics.stringWidth(text) / 2, 535 + fontMetrics.getHeight() / 4);

		// Draw description box
		g.drawImage(ContentManager.getImage(ContentManager.DESCRIPTION_BOX), 1580, 630, 300, 410, null);

		// Draw description text
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
		fontMetrics = g.getFontMetrics();
		String title;
		String[] words;
		int word = 0;
		if(hoverValue == -1)
		{
			title = "Description";
			words = descriptionText.get(toolTypes.get(selectedTool)[selectedToolType]).split(" ");
		}
		else
		{
			title = tools.get(hoverValue);
			words = descriptionText.get(tools.get(hoverValue)).split(" ");
		}
		g.drawString(title, 1730 - fontMetrics.stringWidth(title) / 2, 650 + fontMetrics.getHeight() / 2);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
		fontMetrics = g.getFontMetrics();
		int lineY = 695 + fontMetrics.getHeight() / 2;
		while(word < words.length)
		{
			String line = "";
			int lineWidth = 0;

			while(word < words.length && lineWidth + fontMetrics.stringWidth(" " + words[word]) < 280)
			{
				line += " " + words[word];
				lineWidth = fontMetrics.stringWidth(line);
				word++;
			}

			g.drawString(line, 1590, lineY);
			lineY += fontMetrics.getHeight() / 2 + 10;
		}

		// Draw confirm button
		if(confirmBox)
		{
			g.drawImage(ContentManager.getImage(ContentManager.CONFIRM_BUTTON), 1642, 970, 175, 50, null);
		}

		// Draw hover tooltip
		if(hoverSpawn != null)
		{
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
			fontMetrics = g.getFontMetrics();
			String typeText = "Type: " + hoverSpawn.type;
			String respawnText = "Respawn time: " + (hoverSpawn.respawnTime / 30) + "s";
			int stringWidth = Math.max(fontMetrics.stringWidth(typeText), fontMetrics.stringWidth(respawnText));
			int stringHeight = fontMetrics.getHeight();
			int tooltipX = Manager.input.mouseX() + 20;
			int tooltipY = Manager.input.mouseY();

			if(tooltipX < xMargin)
			{
				tooltipX = xMargin;
			}
			else if(tooltipX + stringWidth + 20 > xMargin + scale(mapX))
			{
				tooltipX = (int)(xMargin + scale(mapX)) - (stringWidth + 20);
			}
			if(tooltipY < yMargin)
			{
				tooltipY = yMargin;
			}
			else if(tooltipY + stringHeight + 30 > yMargin + scale(mapY))
			{
				tooltipY = (int)(yMargin + scale(mapY)) - (stringHeight + 30);
			}

			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(tooltipX, tooltipY, stringWidth + 20, stringHeight + 30);
			g.setStroke(new BasicStroke(3));
			g.setColor(Color.GRAY);
			g.drawRect(tooltipX, tooltipY, stringWidth + 20, stringHeight + 30);

			g.setColor(Color.BLACK);
			g.drawString(typeText, tooltipX + 10, tooltipY + fontMetrics.getHeight() / 2 + 10);
			g.drawString(respawnText, tooltipX + 10, tooltipY + fontMetrics.getHeight() + 20);
		}
	}

	@Override
	public void handleInput()
	{
		// Tool select
		if(Manager.input.keyPress(InputManager.ESCAPE))
		{
			exit();
		}

		// Mouse clicking and hovering
		hoverSpawn = null;
		if(Manager.input.mouseInRect(xMargin - GRID_SPACE * scale / 2, yMargin - GRID_SPACE * scale / 2, scale(mapX)
				+ GRID_SPACE * scale, scale(mapY) + GRID_SPACE * scale))
		{
			if(Manager.input.mousePress())
			{
				double gridX = Math.round((Manager.input.mouseX() - xMargin) / (GRID_SPACE * scale));
				double gridY = Math.round((Manager.input.mouseY() - yMargin) / (GRID_SPACE * scale));
				selectPoint((int)Math.round(gridX), (int)Math.round(gridY));
			}

			double minDistance = spawnRadius;
			SpawnData minDistanceSpawn = null;
			for(SpawnData spawn : spawns)
			{
				double distance = Math.sqrt(Math.pow(Manager.input.mouseX() - (xMargin + scale(spawn.x)), 2)
						+ Math.pow(Manager.input.mouseY() - (yMargin + scale(spawn.y)), 2));
				if(distance < minDistance)
				{
					minDistance = distance;
					minDistanceSpawn = spawn;
				}
			}
			hoverSpawn = minDistance < 50? minDistanceSpawn : null;
		}
		else if(Manager.input.mouseInRect(1580, 485, 300, 110))
		{
			if(Manager.input.mousePress())
			{
				// Clicking on left arrow
				if(arrowLeft && Manager.input.mouseX() <= 1640)
				{
					selectedToolType--;
					arrowLeft = (selectedToolType != 0);
					arrowRight = (selectedToolType != toolTypes.get(selectedTool).length - 1);
				}
				// Clicking on right arrow
				else if(arrowRight && Manager.input.mouseX() >= 1820)
				{
					selectedToolType++;
					arrowLeft = (selectedToolType != 0);
					arrowRight = (selectedToolType != toolTypes.get(selectedTool).length - 1);
				}
			}
		}
		else if(Manager.input.mouseInRect(1642, 940, 175, 75))
		{
			if(confirmBox && Manager.input.mousePress())
			{
				confirm();
			}
		}
		else
		{
			hoverValue = -1;
			for(int row = 0; row < 4; row++)
			{
				for(int col = 0; col < 3; col++)
				{
					if(Manager.input.mouseInRect(1580 + col * 105, 40 + row * 105, 90, 90))
					{
						hoverValue = row * 3 + col;
					}
				}
			}
			if(Manager.input.mousePress())
			{
				if(hoverValue >= 0 && hoverValue < NUM_TOOLS)
				{
					selectTool(hoverValue);
				}
			}
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
		if(selectedTool == SET_LAND)
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
					LandData land = new LandData(toolTypes.get(SET_LAND)[selectedToolType], x1, y1, x2, y2);
					if(!land.type.equals("Platform") || land.horizontal)
					{
						addToList(land);
					}
				}
				selectedPoint = null;
			}
		}
		else if(selectedTool == SET_ENTITY || selectedTool == SET_PROJECTILE || selectedTool == SET_INTERACTIVE)
		{
			int respawnTime = Integer.parseInt(JOptionPane.showInputDialog("Respawn time (In seconds):")) * Game.FPS;
			String category = "";
			switch(selectedTool)
			{
				case SET_ENTITY:
					category = "Entity";
					break;
				case SET_PROJECTILE:
					category = "Projectile";
					break;
				case SET_INTERACTIVE:
					category = "Interactive";
					break;
			}
			addToList(new SpawnData(category, toolTypes.get(selectedTool)[selectedToolType], x * GRID_SCALE, y
					* GRID_SCALE, respawnTime));
		}
		else if(selectedTool == DELETE)
		{
			if(toolTypes.get(selectedTool)[selectedToolType] == "Delete Land")
			{
				if(selectedPoint == null)
				{
					selectedPoint = new Point2D.Double(x, y);
				}
				else
				{
					if(x != selectedPoint.getX() || y != selectedPoint.getY()) // Length > 0
					{
						System.out.println("gg");
						LandData land = landscapeGrid[(int)selectedPoint.getX()][(int)selectedPoint.getY()][x][y];
						if(land == null)
						{
							land = landscapeGrid[x][y][(int)selectedPoint.getX()][(int)selectedPoint.getY()];
						}
						if(land != null)
						{
							removeFromList(land);
						}
					}
					selectedPoint = null;
				}
			}
			else if(toolTypes.get(selectedTool)[selectedToolType] == "Delete Spawn")
			{
				SpawnData spawn = spawnsGrid[x][y];
				if(spawn != null)
				{
					removeFromList(spawn);
				}
			}
		}
		else if(selectedTool == SET_PLAYER)
		{
			startingX = x * GRID_SCALE;
			startingY = y * GRID_SCALE;
			json.put("startingX", startingX);
			json.put("startingY", startingY);
			playerSet = true;
		}
		else if(selectedTool == SET_OBJECTIVE)
		{
			if(type.equals("Defend"))
			{
				int coreX = x * GRID_SCALE;
				int coreY = y * GRID_SCALE;
				json.put("coreX", coreX);
				json.put("coreY", coreY);
				objectiveSet = true;
			}
			else if(type.equals("Travel"))
			{
				int destinationX = x * GRID_SCALE;
				int destinationY = y * GRID_SCALE;
				json.put("destinationX", destinationX);
				json.put("destinationY", destinationY);
				objectiveSet = true;
			}
		}
	}

	public void selectTool(int tool)
	{
		// TODO May or may not be temporary
		if(selectedTool == SET_OBJECTIVE && !objectiveSet)
		{
			return;
		}

		selectedTool = tool;
		selectedToolType = 0;
		arrowLeft = false;
		arrowRight = (0 != toolTypes.get(selectedTool).length - 1);
		confirmBox = (tool == SET_OBJECTIVE || (tool != DELETE && tool >= 6));
		selectedPoint = null;
	}

	/**
	 * Confirms the selection
	 */
	public void confirm()
	{
		switch(selectedTool)
		{
			case SET_OBJECTIVE:
				type = toolTypes.get(SET_OBJECTIVE)[selectedToolType];
				json.put("type", type);
				objectiveSet = false;
				switch(type)
				{
					case "Battle":
						int enemiesToDefeat = Integer.parseInt(JOptionPane
								.showInputDialog("Number of enemies to defeat:"));
						json.put("enemiesToDefeat", enemiesToDefeat);
						objectiveSet = true;
						break;
					case "Defend":
						int survivalDuration = Integer.parseInt(JOptionPane
								.showInputDialog("Survival duration (In seconds):")) * Game.FPS;
						json.put("survivalDuration", survivalDuration);
						break;
					case "Survival":
						survivalDuration = Integer.parseInt(JOptionPane
								.showInputDialog("Survival duration (In seconds):")) * Game.FPS;
						json.put("survivalDuration", survivalDuration);
						objectiveSet = true;
						break;
					case "Travel":
						int timeLimit = Integer.parseInt(JOptionPane.showInputDialog("Time limit (In seconds):"))
								* Game.FPS;
						json.put("timeLimit", timeLimit);
						break;
				}
				break;
			case UNDO:
				switch(selectedToolType)
				{
					case 0:
						undo();
						break;
					case 1:
						redo();
						break;
				}
				break;
			case CLEAR:
				switch(selectedToolType)
				{
					case 0:
						reset();
						break;
					case 1:
						clear();
						break;
				}
				break;
			case SAVE:
				save();
				break;
			case LOAD:
				load();
				break;
			case EXIT:
				exit();
				break;
		}
	}

	/**
	 * Converts actual size to visual size.
	 * 
	 * @param value Value to be converted.
	 * 
	 * @return Value in visual pixels.
	 */
	public double scale(double value)
	{
		return value / GRID_SCALE * GRID_SPACE * scale;
	}

	/**
	 * Checks if the object is on the grid lines.
	 * 
	 * @param The object to be checked.
	 * 
	 * @return Whether the object is on the grid or not.
	 */
	public boolean onGrid(MapData data)
	{
		if(data instanceof LandData)
		{
			LandData land = (LandData)data;
			return land.x1 % GRID_SCALE == 0 && land.y1 % GRID_SCALE == 0 && land.x2 % GRID_SCALE == 0
					&& land.y2 % GRID_SCALE == 0;
		}
		else if(data instanceof SpawnData)
		{
			SpawnData spawn = (SpawnData)data;
			return spawn.x % GRID_SCALE == 0 && spawn.y % GRID_SCALE == 0;
		}
		return false;
	}

	/**
	 * Checks if the object is overlapping another object.
	 * 
	 * @param The object to be checked.
	 * 
	 * @return Whether the object is overlapping another object or not.
	 */
	public MapData overlapExists(MapData data)
	{
		if(data instanceof LandData)
		{
			LandData land = (LandData)data;
			for(LandData otherLand : landscape)
			{
				if(land.equals(otherLand))
				{
					continue;
				}
				else if(land.x1 == otherLand.x1 && land.y1 == otherLand.y1 && land.x2 == otherLand.x2
						&& land.y2 == otherLand.y2)
				{
					return otherLand;
				}
				else if(land.x1 == otherLand.x2 && land.y1 == otherLand.y2 && land.x2 == otherLand.x1
						&& land.y2 == otherLand.y1)
				{
					return otherLand;
				}
			}
		}
		else if(data instanceof SpawnData)
		{
			SpawnData spawn = (SpawnData)data;
			for(SpawnData otherSpawn : spawns)
			{
				if(spawn.equals(otherSpawn))
				{
					continue;
				}
				else if(spawn.x == otherSpawn.x && spawn.y == otherSpawn.y)
				{
					return otherSpawn;
				}
			}
		}
		return null;
	}

	/**
	 * Reverts the last action.
	 */
	public void undo()
	{
		selectedPoint = null;
		if(actionOrder.isEmpty() || indices[ACTION] == -1)
		{
			return;
		}

		Action action = actionOrder.get(indices[ACTION]);
		switch(action)
		{
			case ADD:
				removeFromList(creationOrder.get(indices[CREATION]));
				break;
			case REMOVE:
				addToList(deletionOrder.get(indices[DELETION]));
				break;
			case OVERWRITE:
				overwriteToList(deletionOrder.get(indices[DELETION]), creationOrder.get(indices[CREATION]));
				break;

		}
	}

	/**
	 * Reverts the last undo.
	 */
	public void redo()
	{
		selectedPoint = null;
		if(actionOrder.isEmpty() || indices[ACTION] + 1 == actionOrder.size())
		{
			return;
		}

		Action action = actionOrder.get(indices[ACTION] + 1);
		switch(action)
		{
			case ADD:
				addToList(creationOrder.get(indices[CREATION] + 1));
				break;
			case REMOVE:
				removeFromList(deletionOrder.get(indices[DELETION] + 1));
				break;
			case OVERWRITE:
				overwriteToList(creationOrder.get(indices[CREATION] + 1), deletionOrder.get(indices[DELETION] + 1));
				break;
		}
	}

	/**
	 * Attempts to reset the level.
	 */
	public void reset()
	{
		String resetLevel;
		do
		{
			resetLevel = JOptionPane.showInputDialog("Reset the level? (Y / N):");
			if(resetLevel.equals("Y"))
			{
				landscape.clear();
				spawns.clear();

				actionOrder.clear();
				creationOrder.clear();
				deletionOrder.clear();
				indices = new int[] {-1, -1, -1};

				landscapeGrid = new LandData[mapX / GRID_SCALE + 1][mapY / GRID_SCALE + 1][mapX / GRID_SCALE + 1][mapY
						/ GRID_SCALE + 1];
				spawnsGrid = new SpawnData[mapX / GRID_SCALE + 1][mapY / GRID_SCALE + 1];

				playerSet = false;
				objectiveSet = false;

				selectedPoint = null;
			}
		}
		while(!resetLevel.equals("Y") && !resetLevel.equals("N"));
	}

	/**
	 * Attempts to delete the level permanently.
	 */
	public void clear()
	{
		String clearFile;
		do
		{
			clearFile = JOptionPane.showInputDialog("Delete the level permanently? (Y / N):");
			if(clearFile.equals("Y"))
			{
				if(index.get(name) == null)
				{
					JOptionPane.showMessageDialog(new JFrame(), "Level was not saved yet.");
				}
				else
				{
					try
					{
						int currentId = Integer.parseInt(index.get(name) + "");
						index.remove(name);
						for(Object level : index.entrySet())
						{
							String[] pair = (level + "").split("=");
							int id = Integer.parseInt(pair[1]);
							if(id > currentId)
							{
								index.put(pair[0], id - 1);
							}
						}
						FileWriter indexWriter = new FileWriter(new File("").getAbsolutePath()
								+ "/resources/data/index.json");
						indexWriter.write(toJSONString(index, formatIndex));
						indexWriter.flush();
						indexWriter.close();

						String clearImage;
						do
						{
							clearImage = JOptionPane.showInputDialog("Delete the associated image? (Y / N):");
							if(clearImage.equals("Y"))
							{
								String fileName = name.replace(" ", "_").toLowerCase();
								String filePath = new File("").getAbsolutePath() + "/resources/images/stages/"
										+ fileName + ".png";
								File file = new File(filePath);
								file.delete();
								JOptionPane.showMessageDialog(new JFrame(), "Level data and image deleted!");
							}
							else if(clearImage.equals("N"))
							{
								JOptionPane.showMessageDialog(new JFrame(), "Level data deleted!");
							}
						}
						while(!clearImage.equals("Y") && !clearImage.equals("N"));

						gsm.setState(GameStateManager.MENU);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		while(!clearFile.equals("Y") && !clearFile.equals("N"));
	}

	/**
	 * Saves the designed level.
	 */
	public void save()
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
				index.put(name, index.size());
				FileWriter indexWriter = new FileWriter(new File("").getAbsolutePath() + "/resources/data/index.json");
				indexWriter.write(toJSONString(index, formatIndex));
				indexWriter.flush();
				indexWriter.close();
			}

			// Packing objects into JSONObject
			json.remove("landscape");
			json.remove("spawns");
			for(LandData land : landscape)
			{
				addToJSON(land);
			}
			for(SpawnData spawn : spawns)
			{
				addToJSON(spawn);
			}

			// Saving and exiting
			String fileName = name.replace(" ", "_").toLowerCase();
			String filePath = new File("").getAbsolutePath() + "/resources/data/levels/" + fileName + ".json";
			FileWriter fileWriter = new FileWriter(filePath);
			fileWriter.write(toJSONString(json, formatMaster));
			fileWriter.flush();
			fileWriter.close();

			// Generating image
			String generate;
			do
			{
				generate = JOptionPane.showInputDialog("Generate image? (Y / N):");
				if(generate.equals("Y"))
				{
					ImageIO.write(createImage(), "PNG", new File(new File("").getAbsolutePath()
							+ "/resources/images/stages/" + fileName + ".png"));
				}
			}
			while(!generate.equals("Y") && !generate.equals("N"));

			JOptionPane.showMessageDialog(new JFrame(), "Level saved!");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Attempts to load another level.
	 */
	public void load()
	{
		String save;
		do
		{
			save = JOptionPane.showInputDialog("Save? (Y / N):");
			if(save.equals("Y"))
			{
				if(!playerSet)
				{
					JOptionPane.showMessageDialog(new JFrame(), "Player not set.");
					return;
				}
				else if(!objectiveSet)
				{
					JOptionPane.showMessageDialog(new JFrame(), "Objective not set.");
					return;
				}
				else
				{
					save();
				}
			}
		}
		while(!save.equals("Y") && !save.equals("N"));
		gsm.setState(GameStateManager.DESIGN);
	}

	/**
	 * Attempts to quit the level editor.
	 */
	public void exit()
	{
		String save;
		do
		{
			save = JOptionPane.showInputDialog("Save? (Y / N):");
			if(save.equals("Y"))
			{
				if(!playerSet)
				{
					JOptionPane.showMessageDialog(new JFrame(), "Player not set.");
					return;
				}
				else if(!objectiveSet)
				{
					JOptionPane.showMessageDialog(new JFrame(), "Objective not set.");
					return;
				}
				else
				{
					save();
				}
			}
		}
		while(!save.equals("Y") && !save.equals("N"));
		gsm.setState(GameStateManager.MENU);
	}

	/**
	 * Adds the MapData Object to its corresponding list.
	 * 
	 * @param add The object to be added.
	 */
	public void addToList(MapData add)
	{
		// Overwrite check
		if(onGrid(add))
		{
			MapData overlap = overlapExists(add);
			if(overlap != null)
			{
				overwriteToList(add, overlap);
				return;
			}
			else if(add instanceof LandData)
			{
				LandData land = (LandData)add;
				landscapeGrid[land.x1 / GRID_SCALE][land.y1 / GRID_SCALE][land.x2 / GRID_SCALE][land.y2 / GRID_SCALE] = land;
			}
			else if(add instanceof SpawnData)
			{
				SpawnData spawn = (SpawnData)add;
				spawnsGrid[spawn.x / GRID_SCALE][spawn.y / GRID_SCALE] = spawn;
			}
		}

		if(add instanceof LandData)
		{
			landscape.add((LandData)add);
		}
		else if(add instanceof SpawnData)
		{
			spawns.add((SpawnData)add);
		}
		if(selectedTool == UNDO)
		{
			if(toolTypes.get(selectedTool)[selectedToolType] == "Undo")
			{
				indices[ACTION]--;
				indices[DELETION]--;
			}
			else if(toolTypes.get(selectedTool)[selectedToolType] == "Redo")
			{
				indices[ACTION]++;
				indices[CREATION]++;
			}
		}
		else
		{
			indices[ACTION]++;
			indices[CREATION]++;
			actionOrder.subList(indices[ACTION], actionOrder.size()).clear();
			creationOrder.subList(indices[CREATION], creationOrder.size()).clear();
			actionOrder.add(Action.ADD);
			creationOrder.add(add);
		}
	}

	/**
	 * Removes the MapData object from the list.
	 * 
	 * @param remove The object to be removed.
	 */
	public void removeFromList(MapData remove)
	{
		if(onGrid(remove))
		{
			if(remove instanceof LandData)
			{
				LandData land = (LandData)remove;
				landscapeGrid[land.x1 / GRID_SCALE][land.y1 / GRID_SCALE][land.x2 / GRID_SCALE][land.y2 / GRID_SCALE] = null;
			}
			else if(remove instanceof SpawnData)
			{
				SpawnData spawn = (SpawnData)remove;
				spawnsGrid[spawn.x / GRID_SCALE][spawn.y / GRID_SCALE] = null;
			}
		}

		landscape.remove(remove);
		spawns.remove(remove);
		if(selectedTool == UNDO)
		{
			if(toolTypes.get(selectedTool)[selectedToolType] == "Undo")
			{
				indices[ACTION]--;
				indices[CREATION]--;
			}
			else if(toolTypes.get(selectedTool)[selectedToolType] == "Redo")
			{
				indices[ACTION]++;
				indices[DELETION]++;
			}
		}
		else
		{
			indices[ACTION]++;
			indices[DELETION]++;
			actionOrder.subList(indices[ACTION], actionOrder.size()).clear();
			deletionOrder.subList(indices[DELETION], deletionOrder.size()).clear();
			actionOrder.add(Action.REMOVE);
			deletionOrder.add(remove);
		}
	}

	/**
	 * Overwrites the MapData object with another one.
	 * 
	 * @param add The object to be added.
	 * @param remove The object to be removed.
	 */
	public void overwriteToList(MapData add, MapData remove)
	{
		if(onGrid(add))
		{
			if(add instanceof LandData)
			{
				LandData land = (LandData)add;
				landscapeGrid[land.x1 / GRID_SCALE][land.y1 / GRID_SCALE][land.x2 / GRID_SCALE][land.y2 / GRID_SCALE] = land;
			}
			else if(add instanceof SpawnData)
			{
				SpawnData spawn = (SpawnData)add;
				spawnsGrid[spawn.x / GRID_SCALE][spawn.y / GRID_SCALE] = spawn;
			}
		}

		landscape.remove(remove);
		spawns.remove(remove);
		if(add instanceof LandData)
		{
			landscape.add((LandData)add);
		}
		else if(add instanceof SpawnData)
		{
			spawns.add((SpawnData)add);
		}
		if(selectedTool == UNDO)
		{
			if(toolTypes.get(selectedTool)[selectedToolType] == "Undo")
			{
				indices[ACTION]--;
				indices[CREATION]--;
				indices[DELETION]--;
			}
			else if(toolTypes.get(selectedTool)[selectedToolType] == "Redo")
			{
				indices[ACTION]++;
				indices[CREATION]++;
				indices[DELETION]++;
			}
		}
		else
		{
			indices[ACTION]++;
			indices[CREATION]++;
			indices[DELETION]++;
			actionOrder.subList(indices[ACTION], actionOrder.size()).clear();
			creationOrder.subList(indices[CREATION], creationOrder.size()).clear();
			deletionOrder.subList(indices[DELETION], deletionOrder.size()).clear();
			actionOrder.add(Action.OVERWRITE);
			creationOrder.add(add);
			deletionOrder.add(remove);
		}
	}

	/**
	 * Adds the LandData object to the JSON.
	 * 
	 * @param land The object to be added.
	 */
	public void addToJSON(LandData land)
	{
		JSONArray landscape = (JSONArray)json.get("landscape");
		if(landscape == null)
		{
			landscape = new JSONArray();
			json.put("landscape", landscape);
		}
		JSONObject obj = new JSONObject();
		obj.put("type", land.type);
		obj.put("x1", land.x1);
		obj.put("y1", land.y1);
		obj.put("x2", land.x2);
		obj.put("y2", land.y2);
		landscape.add(obj);
	}

	/**
	 * Adds the SpawnData object to the JSON.
	 * 
	 * @param spawn The object to be added.
	 */
	public void addToJSON(SpawnData spawn)
	{
		JSONArray spawns = (JSONArray)json.get("spawns");
		if(spawns == null)
		{
			spawns = new JSONArray();
			json.put("spawns", spawns);
		}
		JSONObject obj = new JSONObject();
		obj.put("category", spawn.category);
		obj.put("type", spawn.type);
		obj.put("x", spawn.x);
		obj.put("y", spawn.y);
		obj.put("respawnTime", spawn.respawnTime);
		spawns.add(obj);
	}

	/**
	 * Formats the JSON String to be more readable. Do yourself a favor and don't look through this.
	 * 
	 * @param obj The JSON object to be formatted.
	 * @param format The format to be used.
	 * 
	 * @return The formatted String.
	 */
	public String toJSONString(JSONObject obj, String[] format)
	{
		String string = "";

		// Index does not require recursion
		if(format.equals(formatIndex))
		{
			for(int count = 0; count < obj.size(); count++)
			{
				for(Object name : obj.entrySet())
				{
					String[] pair = (name + "").split("=");
					int id = Integer.parseInt(pair[1]);
					if(id == count)
					{
						string += "    \"" + pair[0] + "\":" + id + ",\n";
					}
				}
			}

			return "{\n" + string.substring(0, string.length() - 2) + "\n}";
		}

		for(String line : format)
		{
			if(!line.equals(""))
			{
				if(line.equals("objectives"))
				{
					int objective = Arrays.asList(toolTypes.get(SET_OBJECTIVE)).indexOf(type);
					string += toJSONString(obj, formatObjective[objective]);
				}
				else if(obj.get(line) instanceof JSONArray)
				{
					string += "    \"" + line + "\":[\n";

					JSONArray array = (JSONArray)obj.get(line);
					String[] subFormat = null;
					switch(line)
					{
						case "landscape":
							subFormat = formatLand;
							break;
						case "spawns":
							subFormat = formatSpawn;
							break;
					}
					for(Object subObj : array)
					{
						string += "        " + toJSONString((JSONObject)subObj, subFormat) + "\n";
					}

					string += "    ],";
				}
				else
				{
					if(!format.equals(formatLand) && !format.equals(formatSpawn))
					{
						string += "    ";
					}
					string += "\"" + line + "\":";
					string += obj.get(line) instanceof String? "\"" + obj.get(line) + "\"" : obj.get(line);
					string += ",";
				}
			}
			if(format.equals(formatLand) || format.equals(formatSpawn))
			{
				string += " ";
			}
			else if(!line.equals("objectives"))
			{
				string += "\n";
			}
		}

		// Remove last comma
		if(format.equals(formatMaster))
		{
			string = "{\n" + string.substring(0, string.length() - 2) + "\n}";
		}
		else if(format.equals(formatLand) || format.equals(formatSpawn))
		{
			string = "{" + string.substring(0, string.length() - 2) + "}";
		}

		return string;
	}

	public BufferedImage createImage()
	{
		BufferedImage stage = new BufferedImage(mapX, mapY, BufferedImage.TYPE_INT_ARGB);
		try
		{
			Graphics2D g = stage.createGraphics();
			g.setStroke(new BasicStroke(10));

			// Draw landscape
			for(LandData land : landscape)
			{
				switch(land.type)
				{
					case "Platform":
						g.setColor(Color.GREEN);
						break;
					case "Spike Pit":
						g.setColor(Color.RED);
						break;
					case "Force Field":
						g.setColor(Color.CYAN);
						break;
					default:
						g.setColor(Color.BLUE);
						break;
				}
				int x1 = (int)land.x1;
				int y1 = (int)land.y1;
				int x2 = (int)land.x2;
				int y2 = (int)land.y2;
				g.drawLine(x1, y1, x2, y2);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return stage;
	}
}