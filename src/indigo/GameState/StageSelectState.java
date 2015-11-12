package indigo.GameState;

import indigo.Display.Book;
import indigo.Main.Game;
import indigo.Manager.ContentManager;
import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;
import indigo.Manager.Manager;

import java.awt.Graphics2D;

import org.json.simple.JSONObject;

/**
 * The state that allows the player to select a stage to play. Accessed when they player selects "play" from the main
 * menu.
 */
public class StageSelectState extends GameState
{
	private int bookIndex;

	private Book currentBook;
	private Book nextBook;

	private String[] levels;
	private int books;

	/**
	 * Sets up the stage selection state.
	 * 
	 * @param gsm The game state manager.
	 */
	public StageSelectState(GameStateManager gsm)
	{
		super(gsm);

		JSONObject index = ContentManager.load("/index.json");
		levels = (String[])index.values().toArray(new String[0]);
		books = (levels.length - 1) / Book.LEVELS_PER_BOOK + 1;

		bookIndex = 0;
		currentBook = new Book(Game.WIDTH / 2, 450, 0, bookIndex, levels);
	}

	@Override
	public void update()
	{
		currentBook.update();
		if(nextBook != null)
		{
			nextBook.update();
			if(currentBook.getX() > Game.WIDTH / 2 && nextBook.getX() > Game.WIDTH / 2)
			{
				bookIndex--;
				currentBook = nextBook;
				currentBook.setX(Game.WIDTH / 2);
				currentBook.setVelX(0);
				currentBook.setAccelX(0);
				nextBook = null;
			}
			else if(currentBook.getX() < Game.WIDTH / 2 && nextBook.getX() < Game.WIDTH / 2)
			{
				bookIndex++;
				currentBook = nextBook;
				currentBook.setX(Game.WIDTH / 2);
				currentBook.setVelX(0);
				currentBook.setAccelX(0);
				nextBook = null;
			}
		}

		handleInput();
	}

	@Override
	public void render(Graphics2D g)
	{
		// Background and back button
		g.drawImage(ContentManager.getImage(ContentManager.MENU_BACKGROUND), 0, 0, null);
		if(Manager.input.mouseInRect(100, 874, 358, 106))
		{
			g.drawImage(ContentManager.getImage(Manager.input.mouseLeftDown()? ContentManager.GLOW_RECTANGLE_CLICK
					: ContentManager.GLOW_RECTANGLE_HOVER), 70, 844, null);
		}
		g.drawImage(ContentManager.getImage(ContentManager.BUTTON_BACK), 100, 874, null);

		// Drawing books
		currentBook.render(g);
		if(nextBook != null)
		{
			nextBook.render(g);
		}
	}

	/**
	 * Handles the mouse interactions with the stage selections.
	 */
	public void handleInput()
	{
		if(nextBook == null)
		{
			// Level select
			if(Manager.input.mouseLeftRelease())
			{
				if(Manager.input.mouseInRect(510, 150, 450, 600))
				{
					String levelName = levels[bookIndex * 2].replace(" ", "_").toLowerCase();
					data.setStage(ContentManager.load("/levels/" + levelName + ".json"));
					gsm.setState(GameStateManager.PLAY);
				}
				else if(Manager.input.mouseInRect(960, 150, 450, 600) && bookIndex * 2 + 1 < levels.length)
				{
					String levelName = levels[bookIndex * 2 + 1].replace(" ", "_").toLowerCase();
					data.setStage(ContentManager.load("/levels/" + levelName + ".json"));
					gsm.setState(GameStateManager.PLAY);
				}
			}

			// Change book
			if(((Manager.input.mouseLeftDown() && Manager.input.mouseInRect(0, 0, 500, 800)) || Manager.input
					.keyDown(InputManager.A)) && bookIndex > 0)
			{
				currentBook.setAccelX(Book.ACCEL);
				nextBook = new Book(0 - Book.WIDTH / 2, 450, Book.ACCEL, bookIndex - 1, levels);
			}
			else if(((Manager.input.mouseLeftDown() && Manager.input.mouseInRect(1420, 0, 500, 800)) || Manager.input
					.keyDown(InputManager.D)) && bookIndex < books - 1)
			{
				currentBook.setAccelX(-Book.ACCEL);
				nextBook = new Book(Game.WIDTH + Book.WIDTH / 2, 450, -Book.ACCEL, bookIndex + 1,
						levels);
			}
		}

		// Back button
		if(Manager.input.mouseLeftRelease() && Manager.input.mouseInRect(100, 874, 358, 106))
		{
			gsm.setState(GameStateManager.MENU);
		}
	}
}