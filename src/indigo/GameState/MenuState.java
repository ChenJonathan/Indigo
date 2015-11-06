package indigo.GameState;

import indigo.Manager.ContentManager;
import indigo.Manager.GameStateManager;
import indigo.Manager.Manager;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import org.json.simple.JSONObject;

/**
 * The state where the main menu is displayed. Present at game startup.
 */
public class MenuState extends GameState
{
	JSONObject[] saves;

	// Whether certain subsections of the main menu are open or not
	private boolean instructions;
	private boolean credits;
	private boolean saveLoad;

	public final int NORMAL = 0;
	public final int HOVER = 1;
	public final int CLICKED = 2;

	public final int PLAY = 0;
	public final int HELP = 1;
	public final int OPTIONS = 2;
	public final int CREDITS = 3;
	public final int EXIT = 4;

	/**
	 * Sets up the menu and initializes the button states.
	 * 
	 * @param gsm The game state manager.
	 */
	public MenuState(GameStateManager gsm)
	{
		super(gsm);
		instructions = false;
		credits = false;
		saveLoad = false;

		// Loading save information
		saves = new JSONObject[3];
		for(int slot = 1; slot <= 3; slot++)
		{
			String fileName = "slot" + slot;
			saves[slot - 1] = ContentManager.load("/saves/" + fileName + ".json");
		}
	}

	@Override
	public void update()
	{
		handleInput();
	}

	/**
	 * Displays the background and buttons. Also handles the visual response to button events.
	 * 
	 * @param g The graphics to be rendered.
	 */
	@Override
	public void render(Graphics2D g)
	{
		if(instructions)
		{
			// Draw instructions
			g.drawImage(ContentManager.getImage(ContentManager.MENU_BACKGROUND), 0, 0, 1920, 1080, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_BACK), 100, 873, 360, 107, null);
		}
		else if(saveLoad)
		{
			// Draw save and load menu
			g.drawImage(ContentManager.getImage(ContentManager.MENU_BACKGROUND), 0, 0, 1920, 1080, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_BACK), 100, 873, 360, 107, null);
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(100, 100, 1720, 158);
			g.fillRect(100, 358, 1720, 158);
			g.fillRect(100, 616, 1720, 158);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_SAVE), 1050, 125, 360, 107, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_LOAD), 1435, 125, 360, 107, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_SAVE), 1050, 383, 360, 107, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_LOAD), 1435, 383, 360, 107, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_SAVE), 1050, 641, 360, 107, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_LOAD), 1435, 641, 360, 107, null);

			for(int slot = 0; slot < saves.length; slot++)
			{
				if(saves[slot] != null)
				{
					g.setColor(Color.BLACK);
					g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
					FontMetrics fontMetrics = g.getFontMetrics();
					g.drawString(saves[slot].get("name") + "", 125, 179 + slot * 258 + fontMetrics.getHeight() / 4);
				}
			}
		}
		else if(credits)
		{
			// Draw credits
			g.drawImage(ContentManager.getImage(ContentManager.MENU_BACKGROUND), 0, 0, 1920, 1080, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_BACK), 100, 873, 360, 107, null);
		}
		else
		{
			// Draw main menu
			g.drawImage(ContentManager.getImage(ContentManager.TITLE_BACKGROUND), 0, 0, 1920, 1080, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_PLAY), 360, 720, 360, 107, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_INSTRUCTIONS), 780, 720, 360, 107, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_LEVEL_EDITOR), 1193, 720, 360, 107, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_OPTIONS), 360, 869, 360, 107, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_CREDITS), 780, 869, 360, 107, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_SAVE_LOAD), 1193, 869, 360, 107, null);
		}
	}

	/**
	 * Handles mouse interactions with the menu buttons.
	 */
	@Override
	public void handleInput()
	{
		if(instructions)
		{
			if(Manager.input.mouseLeftRelease())
			{
				if(Manager.input.mouseInRect(100, 873, 360, 107))
				{
					instructions = false;
				}
			}
		}
		else if(saveLoad)
		{
			if(Manager.input.mouseLeftRelease())
			{
				if(Manager.input.mouseInRect(1050, 125, 360, 107))
				{
					data.save(1);
				}
				else if(Manager.input.mouseInRect(1435, 125, 360, 107))
				{
					data.load(1);
					saveLoad = false;
				}
				else if(Manager.input.mouseInRect(1050, 383, 360, 107))
				{
					data.save(2);
				}
				else if(Manager.input.mouseInRect(1435, 383, 360, 107))
				{
					data.load(2);
					saveLoad = false;
				}
				else if(Manager.input.mouseInRect(1050, 641, 360, 107))
				{
					data.save(3);
				}
				else if(Manager.input.mouseInRect(1435, 641, 360, 107))
				{
					data.load(3);
					saveLoad = false;
				}
				else if(Manager.input.mouseInRect(100, 873, 360, 107))
				{
					saveLoad = false;
				}
			}
		}
		else if(credits)
		{
			if(Manager.input.mouseLeftRelease())
			{
				if(Manager.input.mouseInRect(100, 873, 360, 107))
				{
					credits = false;
				}
			}
		}
		else
		{
			if(Manager.input.mouseLeftRelease())
			{
				if(Manager.input.mouseInRect(360, 720, 360, 108))
				{
					gsm.setState(GameStateManager.SELECT);
				}
				else if(Manager.input.mouseInRect(780, 720, 360, 108))
				{
					instructions = true;
				}
				else if(Manager.input.mouseInRect(1193, 720, 360, 108))
				{
					gsm.setState(GameStateManager.DESIGN);
				}
				else if(Manager.input.mouseInRect(360, 869, 360, 108))
				{
					gsm.setOptions(true);
				}
				else if(Manager.input.mouseInRect(780, 869, 360, 108))
				{
					credits = true;
				}
				else if(Manager.input.mouseInRect(1193, 869, 360, 108))
				{
					saveLoad = true;
				}
			}
		}
	}
}