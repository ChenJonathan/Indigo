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
import java.util.List;

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

	private ArrayList<Object> creationOrder; // Tracks the order in which objects were created
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

	private int xMargin;
	private int yMargin;

	private boolean playerSet = false;
	private boolean objectiveSet = false;

	private int selectedTool = -1;
	private String selectedToolType;

	private Point2D selectedPoint;

	// Tool types
	private List<String> player = Arrays.asList("Player");
	private List<String> objectives = Arrays.asList("Battle", "Defend", "Survival", "Travel");
	private List<String> lands = Arrays.asList("Platform", "Wall", "Spike Pit");
	private List<String> entities = Arrays.asList("Flying Bot", "Turret");
	private List<String> projectiles = Arrays.asList("Steel Beam");
	private List<String> interactives = Arrays.asList("Health Pickup");

	private int hoverValue;
	private String[] hoverText = {"Sets the player location", "Sets the map objective",
			"Creates an entity spawn point", "Creates a projectile spawn point",
			"Creates an interactive object spawn point", "Draws a wall", "Draws a platform", "Reverts the last action",
			"Saves the level", "Exits the level editor"};

	private static final int GRID_SPACE = 10; // Visual pixels per grid square
	private static final int GRID_SCALE = 100; // Actual pixels per grid square

	public static final int SET_PLAYER = 0;
	public static final int SET_OBJECTIVE = 1;
	public static final int SET_ENTITY = 2;
	public static final int SET_PROJECTILE = 3;
	public static final int SET_INTERACTIVE = 4;
	public static final int DRAW_LAND = 5;
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

		landscape = new ArrayList<LandData>();
		spawns = new ArrayList<SpawnData>();

		creationOrder = new ArrayList<Object>();

		index = ContentManager.load("/index.json");
		if(index.get(name) == null)
		{
			json = new JSONObject();
			json.put("name", name);

			do
			{
				mapX = Integer.parseInt(JOptionPane.showInputDialog("Map width (2000 to 16000):"));
				mapX = mapX / GRID_SCALE * GRID_SCALE;
			}
			while(mapX < 2000 || mapX > 16000);
			do
			{
				mapY = Integer.parseInt(JOptionPane.showInputDialog("Map height (1000 to 9000):"));
				mapY = mapY / GRID_SCALE * GRID_SCALE;
			}
			while(mapY < 1000 || mapY > 9000);
			json.put("mapX", mapX);
			json.put("mapY", mapY);

			spawnsGrid = new SpawnData[mapX / GRID_SCALE + 1][mapY / GRID_SCALE + 1];
			landscapeGrid = new LandData[mapX / GRID_SCALE + 1][mapY / GRID_SCALE + 1][mapX / GRID_SCALE + 1][mapY
					/ GRID_SCALE + 1];
		}
		else
		{
			json = ContentManager.load("/levels/" + name + ".json");

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

			spawnsGrid = new SpawnData[mapX / GRID_SCALE + 1][mapY / GRID_SCALE + 1];
			landscapeGrid = new LandData[mapX / GRID_SCALE + 1][mapY / GRID_SCALE + 1][mapX / GRID_SCALE + 1][mapY
					/ GRID_SCALE + 1];

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

			creationOrder.clear(); // Prevents undoing elements from loaded file
		}

		scale = Math.min(1600.0 / scale(mapX), 900.0 / scale(mapY));
		selectedPointRadius = (int)(scale * 3);
		spawnRadius = (int)(scale * 4);

		xMargin = (int)(50);
		yMargin = (int)((Game.HEIGHT - scale(mapY)) / (2));
	}

	private class LandData
	{
		private String type;
		private int x1;
		private int y1;
		private int x2;
		private int y2;

		private LandData(String type, int x1, int y1, int x2, int y2)
		{
			this.type = type;
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
	}

	private class SpawnData
	{
		private String category;
		private String type;
		private int x;
		private int y;
		private int respawnTime;

		private SpawnData(String category, String type, int x, int y, int respawnTime)
		{
			this.category = category;
			this.type = type;
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
		g.setStroke(new BasicStroke((int)(scale)));
		for(LandData land : landscape)
		{
			g.setColor(Color.BLUE);
			if(land.type.equals("Platform"))
			{
				g.setColor(Color.RED);
			}
			int x1 = (int)(xMargin + scale(land.x1));
			int y1 = (int)(yMargin + scale(land.y1));
			int x2 = (int)(xMargin + scale(land.x2));
			int y2 = (int)(yMargin + scale(land.y2));
			g.drawLine(x1, y1, x2, y2);
		}

		// Draw spawns
		g.setColor(Color.GREEN);
		for(SpawnData spawn : spawns)
		{
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

		// Draw core (Map type Defend) or destination (Map type Travel)
		g.setColor(Color.PINK);
		if(objectiveSet)
		{
			if(type.equals("Defend"))
			{
				int x = (int)(xMargin + scale(Integer.parseInt(json.get("coreX") + "")));
				int y = (int)(yMargin + scale(Integer.parseInt(json.get("coreY") + "")));
				g.fill(new Ellipse2D.Double(x - spawnRadius, y - spawnRadius, spawnRadius * 2, spawnRadius * 2));
			}
			else if(type.equals("Travel"))
			{
				int x = (int)(xMargin + scale((int)json.get("destinationX")));
				int y = (int)(yMargin + scale((int)json.get("destinationY")));
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

		// Draw toolbars
		g.drawImage(ContentManager.getImage(ContentManager.TOOLBAR), 1741, 45, 90, 990, null);

		// Draw hover text
		if(hoverValue != -1)
		{
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
			FontMetrics fontMetrics = g.getFontMetrics();

			g.setColor(Color.GRAY);
			g.setStroke(new BasicStroke(6));
			g.drawRect(1720 - fontMetrics.stringWidth(hoverText[hoverValue]) - 90, 100 * hoverValue + 45,
					fontMetrics.stringWidth(hoverText[hoverValue]) + 90, 90);

			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(1720 - fontMetrics.stringWidth(hoverText[hoverValue]) - 90, 100 * hoverValue + 45,
					fontMetrics.stringWidth(hoverText[hoverValue]) + 90, 90);

			g.setColor(Color.BLACK);
			g.drawString(hoverText[hoverValue], 1720 - fontMetrics.stringWidth(hoverText[hoverValue]) - 45, 100
					* hoverValue + 90 + fontMetrics.getHeight() / 4);
		}

		// Draw hover box
		if(hoverValue != -1)
		{
			g.setColor(Color.GRAY);
			g.setStroke(new BasicStroke(6));
			g.drawRect(1741, 100 * hoverValue + 45, 90, 90);
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
			selectTool(SET_INTERACTIVE);
		}
		else if(Manager.input.keyPress(InputManager.K6))
		{
			selectTool(DRAW_LAND);
		}
		else if(Manager.input.keyPress(InputManager.K7))
		{
			selectTool(DRAW_PLATFORM);
		}
		else if(Manager.input.keyPress(InputManager.K8))
		{
			undo();
		}
		else if(Manager.input.keyPress(InputManager.K9))
		{
			save(false);
		}
		else if(Manager.input.keyPress(InputManager.ESCAPE))
		{
			String exit;
			do
			{
				exit = JOptionPane.showInputDialog("Exit? (Y / N):");
				if(exit.equals("Y"))
				{
					exit();
				}
				else if(exit.equals("N"))
				{
					gsm.setState(GameStateManager.MENU);
				}
			}
			while(!exit.equals("Y") && !exit.equals("N"));
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
		for(int count = 0; count < 10; count++)
		{
			if(Manager.input.mouseInRect(1741, 100 * count + 45, 90, 90))
			{
				hoverValue = count;
				break;
			}
			hoverValue = -1;
		}
		if(Manager.input.mousePress())
		{
			if(hoverValue >= 0 && hoverValue <= 6)
			{
				selectTool(hoverValue);
			}
			else if(hoverValue == 7)
			{
				undo();
			}
			else if(hoverValue == 8)
			{
				save(false);
			}
			else if(hoverValue == 9)
			{
				exit();
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
		// TODO Return if selectedToolType is ""

		if(selectedTool == DRAW_LAND)
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
					LandData land = new LandData(selectedToolType, x1, y1, x2, y2);
					addToList(land);
				}
				selectedPoint = null;
			}
		}
		else
		{
			if(selectedTool == SET_ENTITY || selectedTool == SET_PROJECTILE || selectedTool == SET_INTERACTIVE)
			{
				int respawnTime = Integer.parseInt(JOptionPane.showInputDialog("Respawn time (In frames):"));
				SpawnData spawn = null;
				if(selectedTool == SET_ENTITY)
				{
					spawn = new SpawnData("Entity", selectedToolType, x * GRID_SCALE, y * GRID_SCALE, respawnTime);
				}
				else if(selectedTool == SET_PROJECTILE)
				{
					spawn = new SpawnData("Projectile", selectedToolType, x * GRID_SCALE, y * GRID_SCALE, respawnTime);
				}
				else if(selectedTool == SET_INTERACTIVE)
				{
					spawn = new SpawnData("Interactive", selectedToolType, x * GRID_SCALE, y * GRID_SCALE, respawnTime);
				}
				addToList(spawn);
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
	}

	public void selectTool(int tool)
	{
		selectedPoint = null;
		if(tool == SET_OBJECTIVE)
		{
			while(!objectives.contains(type))
			{
				type = JOptionPane.showInputDialog("Map type (Battle / Defend / Survival / Travel):");
				json.put("type", type);
			}

			if(type.equals("Battle"))
			{
				int enemiesToDefeat = Integer.parseInt(JOptionPane.showInputDialog("Number of enemies to defeat:"));
				json.put("enemiesToDefeat", enemiesToDefeat);
				objectiveSet = true;
			}
			else if(type.equals("Defend"))
			{
				selectedTool = tool;
				selectedToolType = "Core";
				int survivalTime = Integer.parseInt(JOptionPane.showInputDialog("Survival duration (In frames):"));
				json.put("survivalTime", survivalTime);
			}
			else if(type.equals("Survival"))
			{
				int survivalTime = Integer.parseInt(JOptionPane.showInputDialog("Survival duration (In frames):"));
				json.put("survivalTime", survivalTime);
				objectiveSet = true;
			}
			else if(type.equals("Travel"))
			{
				selectedTool = tool;
				selectedToolType = "Destination";
				int timeLimit = Integer.parseInt(JOptionPane.showInputDialog("Time limit (In frames):"));
				json.put("timeLimit", timeLimit);
			}
		}
		else
		{
			selectedTool = tool;

			if(selectedTool == SET_ENTITY || selectedTool == SET_PROJECTILE || selectedTool == SET_INTERACTIVE
					|| selectedTool == DRAW_LAND)
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
			landscape.remove(remove);
			spawns.remove(remove);
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
				FileWriter file = new FileWriter(new File("").getAbsolutePath() + "/resources/data/levels/" + fileName
						+ ".json");
				file.write(json.toJSONString());
				file.flush();
				file.close();

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
	 * Attempts to quit the level editor
	 */
	public void exit()
	{
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

	/**
	 * Adds the LandData object to the landscape list.
	 */
	public void addToList(LandData land)
	{
		if(land.x1 % GRID_SCALE == 0 && land.y1 % GRID_SCALE == 0 && land.x2 % GRID_SCALE == 0
				&& land.y2 % GRID_SCALE == 0)
		{
			if(landscapeGrid[land.x1 / GRID_SCALE][land.y1 / GRID_SCALE][land.x2 / GRID_SCALE][land.y2 / GRID_SCALE] != null)
			{
				landscape.remove(landscapeGrid[land.x1][land.y1][land.x2][land.y2]);
				creationOrder.remove(landscapeGrid[land.x1][land.y1][land.x2][land.y2]);
			}
			landscape.add(land);
			creationOrder.add(land);
		}
		else
		{
			landscape.add(land);
			creationOrder.add(land);
		}
	}

	/**
	 * Adds the SpawnData object to the spawns list.
	 */
	public void addToList(SpawnData spawn)
	{
		if(spawn.x % GRID_SCALE == 0 && spawn.y % GRID_SCALE == 0)
		{
			if(spawnsGrid[spawn.x / GRID_SCALE][spawn.y / GRID_SCALE] != null)
			{
				spawns.remove(spawnsGrid[spawn.x][spawn.y]);
				creationOrder.remove(spawnsGrid[spawn.x][spawn.y]);
			}
			spawns.add(spawn);
			creationOrder.add(spawn);
		}
		else
		{
			spawns.add(spawn);
			creationOrder.add(spawn);
		}
	}

	/**
	 * Adds the LandData object to the JSON.
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

	public BufferedImage createImage()
	{
		BufferedImage stage = new BufferedImage(mapX, mapY, BufferedImage.TYPE_INT_ARGB);
		try
		{
			Graphics2D stageGraphics = stage.createGraphics();
			stageGraphics.setStroke(new BasicStroke(10));

			// Draw landscape
			for(LandData land : landscape)
			{
				stageGraphics.setColor(Color.BLUE);
				if(land.type.equals("Platform"))
				{
					stageGraphics.setColor(Color.RED);
				}
				int x1 = (int)land.x1;
				int y1 = (int)land.y1;
				int x2 = (int)land.x2;
				int y2 = (int)land.y2;
				stageGraphics.drawLine(x1, y1, x2, y2);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return stage;
	}
}