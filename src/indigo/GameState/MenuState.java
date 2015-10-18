package indigo.GameState;

import indigo.Manager.ContentManager;
import indigo.Manager.GameStateManager;
import indigo.Manager.Manager;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.json.simple.JSONObject;

/**
 * The state where the main menu is displayed. Present at game startup.
 */
public class MenuState extends GameState implements ActionListener
{
	private JFrame frame; // For level selection
	
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

		gsm.setCursor(ContentManager.getImage(ContentManager.CURSOR));

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
			g.drawImage(ContentManager.getImage(ContentManager.INSTRUCTIONS_BACKGROUND), 0, 0, 1920, 1080, null);
			g.drawImage(ContentManager.getImage(ContentManager.BACK_BUTTON), 180, 800, 200, 60, null);
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
		// Instruction button functionality goes here
		if(instructions)
		{
			if(Manager.input.mouseLeftRelease() && Manager.input.mouseX() >= 180 && Manager.input.mouseX() <= 380 && Manager.input.mouseY() >= 800
					&& Manager.input.mouseY() <= 860)
			{
				instructions = false;
			}
		}
		// Credits button functionality goes here
		else if(credits)
		{
			if(Manager.input.mouseLeftRelease() && Manager.input.mouseX() >= 180 && Manager.input.mouseX() <= 380 && Manager.input.mouseY() >= 800
					&& Manager.input.mouseY() <= 860)
			{
				credits = false;
			}
		}
		// Main menu button functionality goes here
		else
		{
			if(Manager.input.mouseX() >= 1235 && Manager.input.mouseX() <= 1385 && Manager.input.mouseY() >= 285 && Manager.input.mouseY() <= 385)
			{
				buttonState[PLAY] = HOVER;

				if(Manager.input.mouseLeftDown())
				{
					buttonState[PLAY] = CLICKED;
				}
				if(Manager.input.mouseLeftRelease())
				{
					JSONObject index = ContentManager.load("/index.json");
					String[] levels = (String[])index.keySet().toArray(new String[0]);
					JComboBox levelSelect = new JComboBox(levels);
					levelSelect.addActionListener(this);
					
					frame = new JFrame("");
			        frame.setSize(new Dimension(120, 100));
			        frame.setLayout(null);
			        frame.setResizable(false);
			        frame.setLocationRelativeTo(null);
			        frame.setVisible(true);
			        
			        levelSelect.setBounds(10, 10, 100, 30);
			        frame.add(levelSelect);
				}
			}
			else
			{
				buttonState[PLAY] = NORMAL;
			}
			if(Manager.input.mouseX() >= 1235 && Manager.input.mouseX() <= 1395 && Manager.input.mouseY() >= 385 && Manager.input.mouseY() <= 485)
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
			if(Manager.input.mouseX() >= 1235 && Manager.input.mouseX() <= 1515 && Manager.input.mouseY() >= 485 && Manager.input.mouseY() <= 585)
			{
				buttonState[OPTIONS] = HOVER;

				if(Manager.input.mouseLeftDown())
				{
					buttonState[OPTIONS] = CLICKED;
				}
				if(Manager.input.mouseLeftRelease())
				{
					gsm.setState(GameStateManager.DESIGN);
				}
			}
			else
			{
				buttonState[OPTIONS] = NORMAL;
			}
			if(Manager.input.mouseX() >= 1235 && Manager.input.mouseX() <= 1485 && Manager.input.mouseY() >= 585 && Manager.input.mouseY() <= 685)
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
			if(Manager.input.mouseX() >= 1235 && Manager.input.mouseX() <= 1365 && Manager.input.mouseY() >= 685 && Manager.input.mouseY() <= 785)
			{
				buttonState[EXIT] = HOVER;

				if(Manager.input.mouseLeftDown())
				{
					buttonState[EXIT] = CLICKED;
				}
				if(Manager.input.mouseLeftRelease())
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
	
	public void actionPerformed(ActionEvent e)
	{
		JComboBox levelSelect = (JComboBox)e.getSource();
		String level = (String)levelSelect.getSelectedItem();
		level = level.replace(" ","_").toLowerCase();
		if(!level.equals(""))
		{
			System.out.println(level);
			data.setStage(ContentManager.load("/levels/" + level + ".json"));
			gsm.setState(GameStateManager.PLAY);
			frame.dispose();
		}
	}
}