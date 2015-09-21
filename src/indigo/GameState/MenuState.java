package indigo.GameState;

import indigo.Manager.Content;
import indigo.Manager.GameStateManager;

import java.awt.Graphics2D;

/**
 * The state where the main menu is displayed. Present at game startup.
 */
public class MenuState extends GameState
{
	private boolean instructions; // Whether the instructions are open or not
	private boolean credits; // Whether the credits are open or not

	int[] buttonState;

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

		gsm.setCursor(Content.CURSOR);

		buttonState = new int[5];
		for(int i = 0; i < buttonState.length; i++)
		{
			buttonState[i] = NORMAL;
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
			g.drawImage(Content.INSTRUCTIONS_BACKGROUND, 0, 0, 1920, 1080, null);
			g.drawImage(Content.BACK_BUTTON, 180, 800, 200, 60, null);
		}
		else if(credits)
		{
			// Draw credits
			g.drawImage(Content.CREDITS_BACKGROUND, 0, 0, 1920, 1080, null);
			g.drawImage(Content.BACK_BUTTON, 180, 800, 200, 60, null);
		}
		else
		{
			// Draw main menu
			g.drawImage(Content.MENU_BACKGROUND, 0, 0, 1920, 1080, null);
			g.drawImage(Content.TITLE, 200, 200, 800, 382, null);
			g.drawImage(Content.PLAY_BUTTON, 1235, 285, 150, 100, null);
			g.drawImage(Content.HELP_BUTTON, 1235, 385, 160, 100, null);
			g.drawImage(Content.OPTIONS_BUTTON, 1235, 485, 280, 100, null);
			g.drawImage(Content.CREDITS_BUTTON, 1235, 585, 250, 100, null);
			g.drawImage(Content.EXIT_BUTTON, 1235, 685, 130, 100, null);
			g.drawImage(Content.SELECT_BAR, 1235, 345, 268, 46, null);
			g.drawImage(Content.SELECT_BAR, 1235, 445, 268, 46, null);
			g.drawImage(Content.SELECT_BAR, 1235, 545, 268, 46, null);
			g.drawImage(Content.SELECT_BAR, 1235, 645, 268, 46, null);
			if(buttonState[PLAY] == CLICKED)
			{
				g.drawImage(Content.GLOW, 1100, 240, 500, 160, null);
			}
			if(buttonState[PLAY] == HOVER || buttonState[PLAY] == CLICKED)
			{
				g.drawImage(Content.PLAY_BUTTON_HOVER, 1235, 285, 150, 100, null);
			}
			if(buttonState[HELP] == CLICKED)
			{
				g.drawImage(Content.GLOW, 1100, 340, 500, 160, null);
			}
			if(buttonState[HELP] == HOVER || buttonState[HELP] == CLICKED)
			{
				g.drawImage(Content.HELP_BUTTON_HOVER, 1235, 385, 160, 100, null);
			}
			if(buttonState[OPTIONS] == CLICKED)
			{
				g.drawImage(Content.GLOW, 1100, 440, 500, 160, null);
			}
			if(buttonState[OPTIONS] == HOVER || buttonState[OPTIONS] == CLICKED)
			{
				g.drawImage(Content.OPTIONS_BUTTON_HOVER, 1235, 485, 280, 100, null);
			}
			if(buttonState[CREDITS] == CLICKED)
			{
				g.drawImage(Content.GLOW, 1100, 540, 500, 160, null);
			}
			if(buttonState[CREDITS] == HOVER || buttonState[CREDITS] == CLICKED)
			{
				g.drawImage(Content.CREDITS_BUTTON_HOVER, 1235, 585, 250, 100, null);
			}
			if(buttonState[EXIT] == CLICKED)
			{
				g.drawImage(Content.GLOW, 1100, 640, 500, 160, null);
			}
			if(buttonState[EXIT] == HOVER || buttonState[EXIT] == CLICKED)
			{
				g.drawImage(Content.EXIT_BUTTON_HOVER, 1235, 685, 130, 100, null);
			}
		}
	}

	/**
	 * Handles mouse interactions with the menu buttons.
	 */
	@Override
	public void handleInput()
	{
		// Instruction button functionality goes here
		if(instructions)
		{
			if(input.mouseLeftRelease() && input.mouseX() >= 180 && input.mouseX() <= 380 && input.mouseY() >= 800
					&& input.mouseY() <= 860)
			{
				instructions = false;
			}
		}
		// Credits button functionality goes here
		else if(credits)
		{
			if(input.mouseLeftRelease() && input.mouseX() >= 180 && input.mouseX() <= 380 && input.mouseY() >= 800
					&& input.mouseY() <= 860)
			{
				credits = false;
			}
		}
		// Main menu button functionality goes here
		else
		{
			if(input.mouseX() >= 1235 && input.mouseX() <= 1385 && input.mouseY() >= 285 && input.mouseY() <= 385)
			{
				buttonState[PLAY] = HOVER;

				if(input.mouseLeftDown())
				{
					buttonState[PLAY] = CLICKED;
				}
				if(input.mouseLeftRelease())
				{
					gsm.setState(GameStateManager.SELECT);
				}
			}
			else
			{
				buttonState[PLAY] = NORMAL;
			}
			if(input.mouseX() >= 1235 && input.mouseX() <= 1395 && input.mouseY() >= 385 && input.mouseY() <= 485)
			{
				buttonState[HELP] = HOVER;

				if(input.mouseLeftDown())
				{
					buttonState[HELP] = CLICKED;
				}
				if(input.mouseLeftRelease())
				{
					instructions = true;
				}
			}
			else
			{
				buttonState[HELP] = NORMAL;
			}
			if(input.mouseX() >= 1235 && input.mouseX() <= 1515 && input.mouseY() >= 485 && input.mouseY() <= 585)
			{
				buttonState[OPTIONS] = HOVER;

				if(input.mouseLeftDown())
				{
					buttonState[OPTIONS] = CLICKED;
				}
				if(input.mouseLeftRelease())
				{
					gsm.setOptions(true);
				}
			}
			else
			{
				buttonState[OPTIONS] = NORMAL;
			}
			if(input.mouseX() >= 1235 && input.mouseX() <= 1485 && input.mouseY() >= 585 && input.mouseY() <= 685)
			{
				buttonState[CREDITS] = HOVER;

				if(input.mouseLeftDown())
				{
					buttonState[CREDITS] = CLICKED;
				}
				if(input.mouseLeftRelease())
				{
					credits = true;
				}
			}
			else
			{
				buttonState[CREDITS] = NORMAL;
			}
			if(input.mouseX() >= 1235 && input.mouseX() <= 1365 && input.mouseY() >= 685 && input.mouseY() <= 785)
			{
				buttonState[EXIT] = HOVER;

				if(input.mouseLeftDown())
				{
					buttonState[EXIT] = CLICKED;
				}
				if(input.mouseLeftRelease())
				{
					System.exit(0);
				}
			}
			else
			{
				buttonState[EXIT] = NORMAL;
			}
		}
	}
}