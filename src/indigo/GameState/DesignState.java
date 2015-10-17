package indigo.GameState;

import indigo.Main.Game;
import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;
import indigo.Manager.Manager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;

/**
 * The state where levels can be created or modified.
 */
public class DesignState extends GameState
{
	private JSONObject json;
	
	private String name;
	private String type;

	private int mapX;
	private int mapY;

	private double scale;

	private int xMargin;
	private int yMargin;

	/**
	 * Sets up the edit game state for level design.
	 * 
	 * @param gsm The game state manager.
	 */
	public DesignState(GameStateManager gsm)
	{
		super(gsm);
		json = new JSONObject();

		name = JOptionPane.showInputDialog("Map name:");
		json.put("name", name);
		type = JOptionPane.showInputDialog("Map type (battle / protect / survive):");
		json.put("type", type);

		switch(type)
		{
			case "battle":
				int enemiesToDefeat = Integer.parseInt(JOptionPane.showInputDialog("Number of enemies to defeat:"));
				json.put("enemiesToDefeat", enemiesToDefeat);
		}

		do
		{
			mapX = Integer.parseInt(JOptionPane.showInputDialog("Map width (100 to 16000):"));
			mapX = mapX / 10;
		}
		while(mapX < 10 || mapX > 1600);
		do
		{
			mapY = Integer.parseInt(JOptionPane.showInputDialog("Map height (100 to 9000):"));
			mapY = mapY / 10;
		}
		while(mapY < 10 || mapY > 900);

		scale = Math.min(1600.0 / mapX, 900.0 / mapY);

		xMargin = (int)(50 / scale);
		yMargin = (int)((Game.HEIGHT - mapY * scale) / (2 * scale));

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
		g.scale(scale, scale);
		g.setColor(Color.BLACK);
		for(int count = xMargin; count <= mapX + xMargin; count += 10)
		{
			g.drawLine(count, yMargin, count, mapY + yMargin);
		}
		for(int count = yMargin; count <= mapY + yMargin; count += 10)
		{
			g.drawLine(xMargin, count, mapX + xMargin, count);
		}
		g.scale(1 / scale, 1 / scale);
	}

	@Override
	public void handleInput()
	{
		// Quit
		if(Manager.input.keyPress(InputManager.ESCAPE))
		{
			String save;
			do
			{
				save = JOptionPane.showInputDialog("Save? (Y / N):");
				if(save.equals("Y"))
				{
					save();
				}
			}
			while(save.equals("N"));
			gsm.setState(GameStateManager.MENU);
		}
	}
	
	public void save()
	{
		try
		{
			FileWriter file = new FileWriter(new File("").getAbsolutePath() + "/resources/stages/" + name + ".json");
			file.write(json.toJSONString());
			file.flush();
			file.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}