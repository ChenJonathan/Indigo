package indigo.GameState;

import indigo.Main.Game;
import indigo.Manager.ContentManager;
import indigo.Manager.GameStateManager;
import indigo.Manager.Manager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The state where the main menu is displayed. Present at game startup.
 */
public class MenuState extends GameState
{
	private JFrame frame; // For level selection

	// Whether certain subsections of the main menu are open or not
	private boolean instructions;
	private boolean saveLoad;
	private boolean credits;

	private int[] buttonState;

	// TEMP SOUND SLIDE VARIABLE
	public int soundVolume = 0;

	// TODO Consider replacing
	private int transitionCount;
	private int destination;
	private final int TRANSITION_DURATION = 6;

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

		gsm.setCursor(ContentManager.getImage(ContentManager.CURSOR));

		buttonState = new int[5];
		for(int i = 0; i < buttonState.length; i++)
		{
			buttonState[i] = NORMAL;
		}

		transitionCount = -1;
		destination = -1;
	}

	@Override
	public void update()
	{
		if(transitionCount > -1)
		{
			if(transitionCount == 0)
			{
				if(destination == -1)
				{
					System.exit(0);
				}
				gsm.setState(destination);
			}
			transitionCount--;
			return;
		}

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
		if(transitionCount > -1)
		{
			g.drawImage(ContentManager.getImage(ContentManager.TRANSITION), (int)((double)Game.WIDTH
					* transitionCount / TRANSITION_DURATION), 0, Game.WIDTH, Game.HEIGHT, null);
			return;
		}

		if(instructions)
		{
			// Draw instructions
			g.drawImage(ContentManager.getImage(ContentManager.INSTRUCTIONS_BACKGROUND), 0, 0, 1920, 1080, null);
			g.drawImage(ContentManager.getImage(ContentManager.BACK_BUTTON), 180, 800, 200, 60, null);
		}
		else if(saveLoad)
		{
			g.drawImage(ContentManager.getImage(ContentManager.CREDITS_BACKGROUND), 0, 0, 1920, 1080, null);
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(100, 100, 400, 200);
			g.fillRect(600, 100, 400, 200);
			g.fillRect(1100, 100, 400, 200);
			g.setColor(Color.BLACK);
			g.drawLine(100, 200, 1500, 200);
		}
		else if(credits)
		{
			// Draw credits
			g.drawImage(ContentManager.getImage(ContentManager.CREDITS_BACKGROUND), 0, 0, 1920, 1080, null);
			g.drawImage(ContentManager.getImage(ContentManager.BACK_BUTTON), 180, 800, 200, 60, null);
		}
		else
		{
			// Draw main menu
			g.drawImage(ContentManager.getImage(ContentManager.MENU_BACKGROUND), 0, 0, 1920, 1080, null);
			g.drawImage(ContentManager.getImage(ContentManager.TITLE), 200, 200, 800, 382, null);
			g.drawImage(ContentManager.getImage(ContentManager.PLAY_BUTTON), 1235, 285, 150, 100, null);
			g.drawImage(ContentManager.getImage(ContentManager.HELP_BUTTON), 1235, 385, 160, 100, null);
			g.drawImage(ContentManager.getImage(ContentManager.OPTIONS_BUTTON), 1235, 485, 280, 100, null);
			g.drawImage(ContentManager.getImage(ContentManager.CREDITS_BUTTON), 1235, 585, 250, 100, null);
			g.drawImage(ContentManager.getImage(ContentManager.EXIT_BUTTON), 1235, 685, 130, 100, null);
			g.drawImage(ContentManager.getImage(ContentManager.SELECT_BAR), 1235, 345, 268, 46, null);
			g.drawImage(ContentManager.getImage(ContentManager.SELECT_BAR), 1235, 445, 268, 46, null);
			g.drawImage(ContentManager.getImage(ContentManager.SELECT_BAR), 1235, 545, 268, 46, null);
			g.drawImage(ContentManager.getImage(ContentManager.SELECT_BAR), 1235, 645, 268, 46, null);
			if(buttonState[PLAY] == CLICKED)
			{
				g.drawImage(ContentManager.getImage(ContentManager.GLOW), 1100, 240, 500, 160, null);
			}
			if(buttonState[PLAY] == HOVER || buttonState[PLAY] == CLICKED)
			{
				g.drawImage(ContentManager.getImage(ContentManager.PLAY_BUTTON_HOVER), 1235, 285, 150, 100, null);
			}
			if(buttonState[HELP] == CLICKED)
			{
				g.drawImage(ContentManager.getImage(ContentManager.GLOW), 1100, 340, 500, 160, null);
			}
			if(buttonState[HELP] == HOVER || buttonState[HELP] == CLICKED)
			{
				g.drawImage(ContentManager.getImage(ContentManager.HELP_BUTTON_HOVER), 1235, 385, 160, 100, null);
			}
			if(buttonState[OPTIONS] == CLICKED)
			{
				g.drawImage(ContentManager.getImage(ContentManager.GLOW), 1100, 440, 500, 160, null);
			}
			if(buttonState[OPTIONS] == HOVER || buttonState[OPTIONS] == CLICKED)
			{
				g.drawImage(ContentManager.getImage(ContentManager.OPTIONS_BUTTON_HOVER), 1235, 485, 280, 100, null);
			}
			if(buttonState[CREDITS] == CLICKED)
			{
				g.drawImage(ContentManager.getImage(ContentManager.GLOW), 1100, 540, 500, 160, null);
			}
			if(buttonState[CREDITS] == HOVER || buttonState[CREDITS] == CLICKED)
			{
				g.drawImage(ContentManager.getImage(ContentManager.CREDITS_BUTTON_HOVER), 1235, 585, 250, 100, null);
			}
			if(buttonState[EXIT] == CLICKED)
			{
				g.drawImage(ContentManager.getImage(ContentManager.GLOW), 1100, 640, 500, 160, null);
			}
			if(buttonState[EXIT] == HOVER || buttonState[EXIT] == CLICKED)
			{
				g.drawImage(ContentManager.getImage(ContentManager.EXIT_BUTTON_HOVER), 1235, 685, 130, 100, null);
			}
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
			if(Manager.input.mouseLeftRelease() && Manager.input.mouseX() >= 180 && Manager.input.mouseX() <= 380
					&& Manager.input.mouseY() >= 800 && Manager.input.mouseY() <= 860)
			{
				instructions = false;
			}
		}
		else if(saveLoad)
		{
			if(Manager.input.mouseLeftRelease())
			{
				if(Manager.input.mouseInRect(100, 100, 400, 100))
				{
					data.save(1);
				}
				else if(Manager.input.mouseInRect(600, 100, 400, 100))
				{
					data.save(2);
				}
				else if(Manager.input.mouseInRect(1100, 100, 400, 100))
				{
					data.save(3);
				}
				else if(Manager.input.mouseInRect(100, 200, 400, 100))
				{
					data.load(1);
					saveLoad = false;
				}
				else if(Manager.input.mouseInRect(600, 200, 400, 100))
				{
					data.load(2);
					saveLoad = false;
				}
				else if(Manager.input.mouseInRect(1100, 200, 400, 100))
				{
					data.load(3);
					saveLoad = false;
				}
				else if(Manager.input.mouseInRect(0, 400, 1920, 680))
				{
					saveLoad = false;
				}
			}
		}
		else if(credits)
		{
			if(Manager.input.mouseLeftRelease() && Manager.input.mouseX() >= 180 && Manager.input.mouseX() <= 380
					&& Manager.input.mouseY() >= 800 && Manager.input.mouseY() <= 860)
			{
				credits = false;
			}
		}
		else
		{
			// TEMPORARY SOUND CONTROL BUTTON
			if(Manager.input.mouseX() >= 0 && Manager.input.mouseX() <= 200 && Manager.input.mouseY() >= 0
					&& Manager.input.mouseY() <= 200 && Manager.input.mouseLeftRelease())
			{
				JSlider volumeSlider = new JSlider(-50, 10, soundVolume);
				volumeSlider.setMajorTickSpacing(10);
				volumeSlider.setMinorTickSpacing(1);
				volumeSlider.setPaintTicks(true);
				volumeSlider.addChangeListener(new ChangeListener()
				{
					@Override
					public void stateChanged(ChangeEvent E)
					{
						JSlider Source = (JSlider)E.getSource();
						soundVolume = Source.getValue();
						if(!Source.getValueIsAdjusting())
						{
							data.setVolume(soundVolume);
							frame.dispose();
						}
					}
				});

				frame = new JFrame("");
				frame.setSize(new Dimension(500, 100));
				frame.setLayout(null);
				frame.setResizable(false);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);

				volumeSlider.setBounds(10, 10, 480, 50);
				frame.add(volumeSlider);
			}

			if(Manager.input.mouseX() >= 1235 && Manager.input.mouseX() <= 1385 && Manager.input.mouseY() >= 285
					&& Manager.input.mouseY() <= 385)
			{
				buttonState[PLAY] = HOVER;

				if(Manager.input.mouseLeftDown())
				{
					buttonState[PLAY] = CLICKED;
				}
				if(Manager.input.mouseLeftRelease())
				{
					gsm.setState(GameStateManager.SELECT);
				}
			}
			else
			{
				buttonState[PLAY] = NORMAL;
			}
			if(Manager.input.mouseX() >= 1235 && Manager.input.mouseX() <= 1395 && Manager.input.mouseY() >= 385
					&& Manager.input.mouseY() <= 485)
			{
				buttonState[HELP] = HOVER;

				if(Manager.input.mouseLeftDown())
				{
					buttonState[HELP] = CLICKED;
				}
				if(Manager.input.mouseLeftRelease())
				{
					instructions = true;
				}
			}
			else
			{
				buttonState[HELP] = NORMAL;
			}
			if(Manager.input.mouseX() >= 1235 && Manager.input.mouseX() <= 1515 && Manager.input.mouseY() >= 485
					&& Manager.input.mouseY() <= 585)
			{
				buttonState[OPTIONS] = HOVER;

				if(Manager.input.mouseLeftDown())
				{
					buttonState[OPTIONS] = CLICKED;
				}
				if(Manager.input.mouseLeftRelease())
				{
					transitionCount = TRANSITION_DURATION;
					destination = GameStateManager.DESIGN;
				}
			}
			else
			{
				buttonState[OPTIONS] = NORMAL;
			}
			if(Manager.input.mouseInRect(200, 200, 800, 382) && Manager.input.mouseLeftRelease())
			{
				saveLoad = true;
			}
			if(Manager.input.mouseX() >= 1235 && Manager.input.mouseX() <= 1485 && Manager.input.mouseY() >= 585
					&& Manager.input.mouseY() <= 685)
			{
				buttonState[CREDITS] = HOVER;

				if(Manager.input.mouseLeftDown())
				{
					buttonState[CREDITS] = CLICKED;
				}
				if(Manager.input.mouseLeftRelease())
				{
					credits = true;
				}
			}
			else
			{
				buttonState[CREDITS] = NORMAL;
			}
			if(Manager.input.mouseX() >= 1235 && Manager.input.mouseX() <= 1365 && Manager.input.mouseY() >= 685
					&& Manager.input.mouseY() <= 785)
			{
				buttonState[EXIT] = HOVER;

				if(Manager.input.mouseLeftDown())
				{
					buttonState[EXIT] = CLICKED;
				}
				if(Manager.input.mouseLeftRelease())
				{
					transitionCount = TRANSITION_DURATION;
				}
			}
			else
			{
				buttonState[EXIT] = NORMAL;
			}
		}
	}
}