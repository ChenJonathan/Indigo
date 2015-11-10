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
			g.drawImage(ContentManager.getImage(ContentManager.MENU_BACKGROUND), 0, 0, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_BACK), 100, 874, null);
		}
		else if(saveLoad)
		{
			// Draw save and load menu
			g.drawImage(ContentManager.getImage(ContentManager.MENU_BACKGROUND), 0, 0, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_BACK), 100, 874, null);
			for(int slot = 0; slot < saves.length; slot++)
			{
				g.drawImage(ContentManager.getImage(ContentManager.SAVE_LOAD_BAR), 100, 100 + 258 * slot, null);
				g.drawImage(ContentManager.getImage(ContentManager.BUTTON_SAVE), 920, 126 + 258 * slot, null);
				g.drawImage(ContentManager.getImage(ContentManager.BUTTON_LOAD), 1304, 126 + 258 * slot, null);
				g.drawImage(ContentManager.getImage(ContentManager.BUTTON_CLEAR), 1688, 126 + 258 * slot, null);

				if(saves[slot] != null)
				{
					// TODO g.setColor(new Color(75, 94, 112));
					g.setColor(Color.LIGHT_GRAY);
					g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
					FontMetrics fontMetrics = g.getFontMetrics();
					String name = saves[slot].get("name").equals("")? "Empty" : saves[slot].get("name") + "";
					String level = saves[slot].get("name").equals("")? "" : saves[slot].get("level") + "";
					g.drawString("Name: " + name, 152, 179 + slot * 258 + fontMetrics.getHeight() / 4);
					g.drawString("Level: " + level, 620, 179 + slot * 258 + fontMetrics.getHeight() / 4);
				}
			}
		}
		else if(credits)
		{
			// Draw credits
			g.drawImage(ContentManager.getImage(ContentManager.MENU_BACKGROUND), 0, 0, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_BACK), 100, 874, null);
		}
		else
		{
			// Draw main menu
			g.drawImage(ContentManager.getImage(ContentManager.TITLE_BACKGROUND), 0, 0, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_PLAY), 353, 720, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_INSTRUCTIONS), 781, 720, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_LEVEL_EDITOR), 1209, 720, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_OPTIONS), 353, 876, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_CREDITS), 781, 876, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_SAVE_LOAD), 1209, 876, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_LEVEL), 20, 940, null);
			g.drawImage(ContentManager.getImage(ContentManager.BUTTON_EXIT), 1780, 940, null);

			g.setColor(new Color(75, 94, 112));
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
			FontMetrics fontMetrics = g.getFontMetrics();
			g.drawString(data.getLevel() + "", 79 - fontMetrics.stringWidth(data.getLevel() + "") / 2,
					1000 + fontMetrics.getHeight() / 4);
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
				if(Manager.input.mouseInRect(100, 874, 358, 106))
				{
					instructions = false;
				}
			}
		}
		else if(saveLoad)
		{
			if(Manager.input.mouseLeftRelease())
			{
				if(Manager.input.mouseInRect(920, 126, 358, 106))
				{
					data.save(1);
					saves[0] = ContentManager.load("/saves/slot1.json");
				}
				else if(Manager.input.mouseInRect(1304, 126, 358, 106))
				{
					data.load(1);
					saveLoad = false;
				}
				else if(Manager.input.mouseInRect(1688, 126, 106, 106))
				{
					data.clear(1);
					saves[0] = ContentManager.load("/saves/slot1.json");
				}
				else if(Manager.input.mouseInRect(920, 384, 358, 106))
				{
					data.save(2);
					saves[1] = ContentManager.load("/saves/slot2.json");
				}
				else if(Manager.input.mouseInRect(1304, 384, 358, 106))
				{
					data.load(2);
					saveLoad = false;
				}
				else if(Manager.input.mouseInRect(1688, 384, 106, 106))
				{
					data.clear(2);
					saves[1] = ContentManager.load("/saves/slot2.json");
				}
				else if(Manager.input.mouseInRect(920, 642, 358, 106))
				{
					data.save(3);
					saves[2] = ContentManager.load("/saves/slot3.json");
				}
				else if(Manager.input.mouseInRect(1304, 642, 358, 106))
				{
					data.load(3);
					saveLoad = false;
				}
				else if(Manager.input.mouseInRect(1688, 642, 106, 106))
				{
					data.clear(3);
					saves[2] = ContentManager.load("/saves/slot3.json");
				}
				else if(Manager.input.mouseInRect(100, 874, 358, 106))
				{
					saveLoad = false;
				}
			}
		}
		else if(credits)
		{
			if(Manager.input.mouseLeftRelease())
			{
				if(Manager.input.mouseInRect(100, 874, 358, 106))
				{
					credits = false;
				}
			}
		}
		else
		{
			if(Manager.input.mouseLeftRelease())
			{
				if(Manager.input.mouseInRect(353, 720, 358, 106))
				{
					gsm.setState(GameStateManager.SELECT);
				}
				else if(Manager.input.mouseInRect(781, 720, 358, 106))
				{
					instructions = true;
				}
				else if(Manager.input.mouseInRect(1209, 720, 358, 106))
				{
					gsm.setState(GameStateManager.DESIGN);
				}
				else if(Manager.input.mouseInRect(353, 876, 358, 106))
				{
					gsm.setOptions(true);
				}
				else if(Manager.input.mouseInRect(781, 876, 358, 106))
				{
					credits = true;
				}
				else if(Manager.input.mouseInRect(1209, 876, 358, 106))
				{
					saveLoad = true;
				}
				else if(Manager.input.mouseInCirc(1840, 1000, 60))
				{
					System.exit(0);
				}
			}
		}
	}
}