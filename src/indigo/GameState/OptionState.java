package indigo.GameState;

import indigo.Manager.ContentManager;
import indigo.Manager.GameStateManager;
import indigo.Manager.Manager;
import indigo.Manager.SoundManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileWriter;

import org.json.simple.JSONObject;

/**
 * The state where the game options are displayed. Accessible through the game menu (MenuState).
 */
public class OptionState extends GameState
{
	private int resolutionWidth;
	private int resolutionHeight;
	private boolean autosave;
	private float soundVolume;

	private int resolutionIndex;
	private final int[] resolutionWidths = {800, 1280, 1366, 1600, 1920};
	private final int[] resolutionHeights = {600, 720, 768, 900, 1080};
	
	/**
	 * Sets up the options menu.
	 * 
	 * @param gsm The game state manager.
	 */
	public OptionState(GameStateManager gsm)
	{
		super(gsm);

		JSONObject settings = ContentManager.load("/settings.json");
		resolutionWidth = Integer.parseInt(settings.get("resolutionWidth") + "");
		resolutionHeight = Integer.parseInt(settings.get("resolutionHeight") + "");
		switch(resolutionWidth)
		{
			case 800:
				resolutionIndex = 0;
				break;
			case 1280:
				resolutionIndex = 1;
				break;
			case 1366:
				resolutionIndex = 2;
				break;
			case 1600:
				resolutionIndex = 3;
				break;
			case 1920:
				resolutionIndex = 4;
				break;
		}
		autosave = Boolean.parseBoolean(settings.get("autosave") + "");
		soundVolume = Float.parseFloat(settings.get("soundVolume") + "f");
	}

	@Override
	public void update()
	{
		handleInput();
	}

	/**
	 * Renders the options menu.
	 * 
	 * @param g The graphics to be rendered.
	 */
	@Override
	public void render(Graphics2D g)
	{
		// Draw menu and buttons
		g.drawImage(ContentManager.getImage(ContentManager.MENU_BACKGROUND), 0, 0, null);
		if(Manager.input.mouseInRect(100, 874, 400, 106))
		{
			g.drawImage(ContentManager.getImage(Manager.input.mouseLeftDown()? ContentManager.GLOW_RECTANGLE_CLICK
					: ContentManager.GLOW_RECTANGLE_HOVER), 70, 844, null);
		}
		g.drawImage(ContentManager.getImage(ContentManager.BUTTON_BACK), 100, 874, null);

		for(int option = 0; option < 3; option++)
		{
			g.drawImage(ContentManager.getImage(ContentManager.OPTION_BAR), 100, 100 + 258 * option, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_BLANK), 1394, 126 + 258 * option, null);
		}
		
		// Draw arrows
		if(resolutionIndex > 0)
		{
			g.drawImage(ContentManager.getImage(ContentManager.ARROW_LEFT), 1409, 149, null);
		}
		if(resolutionIndex < resolutionWidths.length - 1)
		{
			g.drawImage(ContentManager.getImage(ContentManager.ARROW_RIGHT), 1739, 149, null);
		}
		if(soundVolume > -50)
		{
			g.drawImage(ContentManager.getImage(ContentManager.ARROW_LEFT), 1409, 665, null);
		}
		if(soundVolume < 10)
		{
			g.drawImage(ContentManager.getImage(ContentManager.ARROW_RIGHT), 1739, 665, null);
		}

		g.setColor(Color.LIGHT_GRAY);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
		FontMetrics fontMetrics = g.getFontMetrics();
		g.drawString("Resolution", 152, 179 + fontMetrics.getHeight() / 4);
		g.drawString("Autosave", 152, 437 + fontMetrics.getHeight() / 4);
		g.drawString("Sound volume", 152, 695 + fontMetrics.getHeight() / 4);
		String text = resolutionWidth + " x " + resolutionHeight;
		g.drawString(text, 1594 - fontMetrics.stringWidth(text) / 2, 179 + fontMetrics.getHeight() / 4);
		text = autosave? "On" : "Off";
		g.drawString(text, 1594 - fontMetrics.stringWidth(text) / 2, 437 + fontMetrics.getHeight() / 4);
		text = (soundVolume * 2 + 100) + "";
		g.drawString(text, 1594 - fontMetrics.stringWidth(text) / 2, 695 + fontMetrics.getHeight() / 4);
	}

	/**
	 * Handles mouse mouse input.
	 */
	@Override
	public void handleInput()
	{
		if(Manager.input.mouseLeftRelease())
		{
			if(Manager.input.mouseInRect(1394, 126, 60, 106))
			{
				if(resolutionIndex > 0)
				{
					resolutionIndex--;
					resolutionWidth = resolutionWidths[resolutionIndex];
					resolutionHeight = resolutionHeights[resolutionIndex];
				}
			}
			else if(Manager.input.mouseInRect(1734, 126, 60, 106))
			{
				if(resolutionIndex < resolutionWidths.length - 1)
				{
					resolutionIndex++;
					resolutionWidth = resolutionWidths[resolutionIndex];
					resolutionHeight = resolutionHeights[resolutionIndex];
				}
			}
			else if(Manager.input.mouseInRect(1394, 384, 400, 106))
			{
				autosave = !autosave;
			}
			else if(Manager.input.mouseInRect(1394, 642, 60, 106))
			{
				if(soundVolume > -50)
				{
					soundVolume -= 2.5;
				}
			}
			else if(Manager.input.mouseInRect(1734, 642, 60, 106))
			{
				if(soundVolume < 0)
				{
					soundVolume +=2.5;
				}
			}
			else if(Manager.input.mouseInRect(100, 874, 360, 106))
			{
				save();
				gsm.setOptions(false);
			}
		}
	}

	/**
	 * Checks if a String can be converted to an integer.
	 * 
	 * @param str String to be checked.
	 * 
	 * @return Whether the String can be converted or not.
	 */
	public boolean isInteger(String str)
	{
		if(str == null)
		{
			return false;
		}
		int length = str.length();
		if(length == 0)
		{
			return false;
		}
		int i = 0;
		if(str.charAt(0) == '-')
		{
			if(length == 1)
			{
				return false;
			}
			i = 1;
		}
		for(; i < length; i++)
		{
			char c = str.charAt(i);
			if(c < '0' || c > '9')
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Saves current settings.
	 */
	public void save()
	{
		data.setAutosave(autosave);
		SoundManager.changeVolume(soundVolume);
		
		String string = "";
		string += "    \"resolutionWidth\":" + resolutionWidth + ",\n";
		string += "    \"resolutionHeight\":" + resolutionHeight + ",\n";
		string += "    \"autosave\":" + autosave + ",\n";
		string += "    \"soundVolume\":" + soundVolume;
		string = "{\n" + string + "\n}";

		try
		{
			String filePath = new File("").getAbsolutePath() + "/resources/data/settings.json";
			FileWriter fileWriter = new FileWriter(filePath);
			fileWriter.write(string);
			fileWriter.flush();
			fileWriter.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Reverts any unsaved changes.
	 */
	public void revert()
	{
		JSONObject settings = ContentManager.load("/settings.json");
		resolutionWidth = Integer.parseInt(settings.get("resolutionWidth") + "");
		resolutionHeight = Integer.parseInt(settings.get("resolutionHeight") + "");
		switch(resolutionWidth)
		{
			case 800:
				resolutionIndex = 0;
				break;
			case 1280:
				resolutionIndex = 1;
				break;
			case 1366:
				resolutionIndex = 2;
				break;
			case 1600:
				resolutionIndex = 3;
				break;
			case 1920:
				resolutionIndex = 4;
				break;
		}
		autosave = Boolean.parseBoolean(settings.get("autosave") + "");
		soundVolume = Float.parseFloat(settings.get("soundVolume") + "f");
	}
}