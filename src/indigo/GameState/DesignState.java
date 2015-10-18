package indigo.GameState;

import indigo.Main.Game;
import indigo.Manager.ContentManager;
import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;
import indigo.Manager.Manager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

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

	private ArrayList<Line2D> walls;
	private ArrayList<Line2D> platforms;
	private ArrayList<Point2D> respawnables;

	private ArrayList<Object> creationOrder; // Tracks the order in which objects were created

	private String name;
	private String type;

	// Measured in actual size
	private int mapX;
	private int mapY;

	// Measured in visual size
	private int scaledMapX;
	private int scaledMapY;

	private double scale = 1; // Factor by which the grid is scaled up to be easier to see

	private int xMargin;
	private int yMargin;

	private boolean playerSet = false;
	private boolean objectiveSet = false;

	private int selectedTool;
	private String selectedToolType;

	private Point2D selectedPoint;
	private int pointRadius;

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

		walls = new ArrayList<Line2D>();
		platforms = new ArrayList<Line2D>();
		respawnables = new ArrayList<Point2D>();

		creationOrder = new ArrayList<Object>();

		index = ContentManager.load("/index.json");
		if(index.get(name) == null)
		{
			json = new JSONObject();
			json.put("name", name);
			type = JOptionPane.showInputDialog("Map type (battle / protect / survive):");
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
			json.put("mapX", mapY);
		}
		else
		{
			json = ContentManager.load("/levels/" + name + ".json");

			for(Object obj : (JSONArray)json.get("walls"))
			{
				JSONObject wall = (JSONObject)obj;
				int x1 = (int)(long)wall.get("x1");
				int y1 = (int)(long)wall.get("y1");
				int x2 = (int)(long)wall.get("x2");
				int y2 = (int)(long)wall.get("y2");
				walls.add(new Line2D.Double(x1, y1, x2, y2));
			}
			for(Object obj : (JSONArray)json.get("platforms"))
			{
				JSONObject plat = (JSONObject)obj;
				int x1 = (int)(long)plat.get("x1");
				int y1 = (int)(long)plat.get("y1");
				int x2 = (int)(long)plat.get("x2");
				int y2 = (int)(long)plat.get("y2");
				platforms.add(new Line2D.Double(x1, y1, x2, y2));
			}
			for(Object obj : (JSONArray)json.get("respawnables"))
			{
				JSONObject respawnable = (JSONObject)obj;
				int x = (int)(long)respawnable.get("x");
				int y = (int)(long)respawnable.get("y");
				respawnables.add(new Point2D.Double(x, y));
			}

			mapX = (int)(long)json.get("mapX");
			mapY = (int)(long)json.get("mapY");
		}

		scale = Math.min(1600.0 / scale(mapX), 900.0 / scale(mapY));
		pointRadius = (int)(scale * 3);

		xMargin = (int)(50);
		yMargin = (int)((Game.HEIGHT - scale(mapY)) / (2));
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
			g.drawLine(count, yMargin, count, scale(mapY) + yMargin);
		}
		for(int count = yMargin; count <= scale(mapY) + yMargin; count += GRID_SPACE * scale)
		{
			g.drawLine(xMargin, count, scale(mapX) + xMargin, count);
		}

		// Draw walls
		g.setColor(Color.BLUE);
		g.setStroke(new BasicStroke((int)(scale)));
		for(Line2D wall : walls)
		{
			int x1 = (int)(xMargin + scale(wall.getX1()));
			int y1 = (int)(yMargin + scale(wall.getY1()));
			int x2 = (int)(xMargin + scale(wall.getX2()));
			int y2 = (int)(yMargin + scale(wall.getY2()));
			g.drawLine(x1, y1, x2, y2);
		}

		// Draw platforms
		g.setColor(Color.RED);
		for(Line2D plat : platforms)
		{
			int x1 = (int)(xMargin + scale(plat.getX1()));
			int y1 = (int)(yMargin + scale(plat.getY1()));
			int x2 = (int)(xMargin + scale(plat.getX2()));
			int y2 = (int)(yMargin + scale(plat.getY2()));
			g.drawLine(x1, y1, x2, y2);
		}

		// Draw selected point
		g.setColor(Color.CYAN);
		if(selectedPoint != null)
		{
			int x = (int)(xMargin + selectedPoint.getX() * GRID_SPACE * scale);
			int y = (int)(yMargin + selectedPoint.getY() * GRID_SPACE * scale);
			g.fill(new Ellipse2D.Double(x - pointRadius, y - pointRadius, pointRadius * 2, pointRadius * 2));
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
					saveAndExit();
				}
				else if(save.equals("N"))
				{
					gsm.setState(GameStateManager.MENU);
				}
			}
			while(!save.equals("Y") && !save.equals("N"));
		}

		// Mouse clicking and hovering
		if(Manager.input.mouseInRect(xMargin, yMargin, scale(mapX), scale(mapY)))
		{
			if(Manager.input.mousePress())
			{
				int gridX = (int)Math.round((Manager.input.mouseX() - xMargin) / (GRID_SPACE * scale));
				int gridY = (int)Math.round((Manager.input.mouseY() - yMargin) / (GRID_SPACE * scale));
				selectPoint(gridX, gridY);
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
					double x1 = selectedPoint.getX() * GRID_SCALE;
					double y1 = selectedPoint.getY() * GRID_SCALE;
					double x2 = x * GRID_SCALE;
					double y2 = y * GRID_SCALE;
					if(selectedTool == DRAW_WALL)
					{
						Line2D.Double line = new Line2D.Double(x1, y1, x2, y2);
						walls.add(line);
						creationOrder.add(line);
					}
					else
					{
						Line2D.Double line = new Line2D.Double(x1, y1, x2, y2);
						platforms.add(line);
						creationOrder.add(line);
					}
				}
				selectedPoint = null;
			}
		}
		else
		{
			if(selectedTool == SET_PLAYER)
			{

			}
			else if(selectedTool == SET_OBJECTIVE)
			{

			}
		}
	}

	public void selectTool(int tool)
	{
		selectedPoint = null;
		if(tool == SET_OBJECTIVE && type.equals("battle"))
		{
			int enemiesToDefeat = Integer.parseInt(JOptionPane.showInputDialog("Number of enemies to defeat:"));
			json.put("enemiesToDefeat", enemiesToDefeat); // TODO Move to set objective
		}
		else
		{
			selectedTool = tool;
		}
	}

	/**
	 * Reverts the last change
	 */
	public void undo()
	{
		if(creationOrder.size() > 0)
		{
			Object remove = creationOrder.get(creationOrder.size() - 1);
			walls.remove(remove);
			platforms.remove(remove);
			respawnables.remove(remove);
			creationOrder.remove(remove);
		}
	}

	/**
	 * Saves the designed level.
	 */
	public void save()
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
					index.put(name, index.size());
					FileWriter file = new FileWriter(new File("").getAbsolutePath() + "/resources/data/index.json");
					file.write(index.toJSONString());
					file.flush();
					file.close();
				}

				// Saving and exiting
				String fileName = name.replace(" ", "_").toLowerCase();
				FileWriter file = new FileWriter(new File("").getAbsolutePath() + "/resources/data/levels/" + fileName
						+ ".json");
				file.write(json.toJSONString());
				file.flush();
				file.close();
				JOptionPane.showMessageDialog(new JFrame(), "Level saved!");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Saves and exits the designed level.
	 */
	public void saveAndExit()
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
					FileWriter file = new FileWriter(new File("").getAbsolutePath() + "/resources/data/index.json");
					file.write(index.toJSONString());
					file.flush();
					file.close();
				}

				// Saving and exiting
				String fileName = name.replace(" ", "_").toLowerCase();
				FileWriter file = new FileWriter(new File("").getAbsolutePath() + "/resources/data/levels/" + fileName
						+ ".json");
				file.write(json.toJSONString());
				file.flush();
				file.close();
				JOptionPane.showMessageDialog(new JFrame(), "Level saved!");
				gsm.setState(GameStateManager.MENU);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Converts actual size to visual size.
	 * 
	 * @param value Value to be converted
	 * @return Value in visual pixels
	 */
	public int scale(double value)
	{
		return (int)(value / GRID_SCALE * GRID_SPACE * scale);
	}
}