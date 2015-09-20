package indigo.Manager;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * Handles data files (i.e. images):  their paths, usage, and general info.
 */
public class Content
{
	// Cursor
	public static BufferedImage CURSOR = load("/cursor/crosshair.png", 32, 32);

	// HUD
	public static BufferedImage INDICATOR = load("/hud/indicator.png", 100, 100);
	public static BufferedImage PLAYER_HUD = load("/hud/player_hud.png", 350, 100);
	public static BufferedImage POINTER = load("/hud/pointer.png", 100, 7);
	
	// Menus
	public static BufferedImage BACK_BUTTON = load("/menus/back_button.png", 200, 60);
	public static BufferedImage CONFIRM_BUTTON = load("/menus/confirm_button.png", 200, 60);
	public static BufferedImage CREDITS_BACKGROUND = load("/menus/credits_background.png", 1920, 1080);
	public static BufferedImage CREDITS_BUTTON = load("/menus/credits.png", 250, 100);
	public static BufferedImage CREDITS_BUTTON_HOVER = load("/menus/credits_hover.png", 250, 100);
	public static BufferedImage EXIT_BUTTON_HOVER = load("/menus/exit_hover.png", 130, 100);
	public static BufferedImage EXIT_BUTTON = load("/menus/exit.png", 130, 100);
	public static BufferedImage GLOW = load("/menus/glow.png", 500, 160);
	public static BufferedImage HELP_BUTTON = load("/menus/help.png", 160, 100);
	public static BufferedImage HELP_BUTTON_HOVER = load("/menus/help_hover.png", 160, 100);
	public static BufferedImage INSTRUCTIONS_BACKGROUND = load("/menus/instructions_background.png", 1920, 1080);
	public static BufferedImage MENU_BACKGROUND = load("/menus/menu_background.png", 1920, 1080);
	public static BufferedImage OPTIONS_BACKGROUND = load("/menus/options_background.png", 1920, 1080);
	public static BufferedImage OPTIONS_BUTTON = load("/menus/options.png", 280, 100);
	public static BufferedImage OPTIONS_BUTTON_HOVER = load("/menus/options_hover.png", 280, 100);
	public static BufferedImage PLAY_BUTTON = load("/menus/play.png", 150, 100);
	public static BufferedImage PLAY_BUTTON_HOVER = load("/menus/play_hover.png", 150, 100);
	public static BufferedImage SELECT_BAR = load("/menus/select_bar.png", 268, 46);
	public static BufferedImage STAGE_SELECT_BACKGROUND = load("/menus/stage_select_background.png", 1920, 1080);
	public static BufferedImage TALENTS_BACKGROUND = load("/menus/talents.png", 1920, 1080);
	public static BufferedImage TITLE = load("/menus/title.png", 800, 382);
	
	// Projectiles
	public static BufferedImage[] ELECTRIC_BALL = loadArray("/projectiles/electric_ball.png", 100, 100, 1);
	public static BufferedImage[] ELECTRIC_SPARK = loadArray("/projectiles/electric_spark.png", 100, 100, 1);
	public static BufferedImage[] GEYSER_BASE = loadArray("/projectiles/geyser_base.png", 100, 100, 1);
	public static BufferedImage[] GEYSER = loadArray("/projectiles/geyser_particle.png", 80, 50, 1);
	public static BufferedImage[] MORTAR_DEATH = loadArray("/projectiles/mortar_death.png", 100, 100, 9);
	public static BufferedImage[] MORTAR = loadArray("/projectiles/mortar.png", 50, 50, 1);
	public static BufferedImage[] WATER_BALL_DEATH = loadArray("/projectiles/water_ball_death.png", 100, 100, 2);
	public static BufferedImage[] WATER_BALL = loadArray("/projectiles/water_ball.png", 100, 100, 1);
	//public static BufferedImage[] ICECHAIN = loadArray("/projectiles/icechain.png", 100, 100, 1);
	
	// Sprites
	public static BufferedImage[] PLAYER_CROUCH_LEFT = loadArray("/sprites/player_crouch_left.png", 64, 111, 1);
	public static BufferedImage[] PLAYER_CROUCH_RIGHT = loadArray("/sprites/player_crouch_right.png", 64, 111, 1);
	public static BufferedImage[] PLAYER_IDLE_LEFT = loadArray("/sprites/player_idle_left.png", 64, 111, 6);
	public static BufferedImage[] PLAYER_IDLE_RIGHT = loadArray("/sprites/player_idle_right.png", 64, 111, 6);
	public static BufferedImage[] PLAYER_JUMP_LEFT = loadArray("/sprites/player_jump_left.png", 64, 111, 1);
	public static BufferedImage[] PLAYER_JUMP_RIGHT = loadArray("/sprites/player_jump_right.png", 64, 111, 1);
	public static BufferedImage[] PLAYER_MIST = loadArray("/sprites/player_mist.png", 80, 120, 4);
	public static BufferedImage[] PLAYER_MOVE_LEFT = loadArray("/sprites/player_move_left.png", 64, 111, 8);
	public static BufferedImage[] PLAYER_MOVE_RIGHT = loadArray("/sprites/player_move_right.png", 64, 111, 8);
	public static BufferedImage[] SMALL_BOT_DEATH = loadArray("/sprites/small_bot_death.png", 60, 60, 7);
	public static BufferedImage[] SMALL_BOT_IDLE = loadArray("/sprites/small_bot_idle.png", 60, 60, 1);
	public static BufferedImage[] TURRET_DEATH = loadArray("/sprites/turret_death.png", 100, 130, 31);
	public static BufferedImage[] TURRET_IDLE = loadArray("/sprites/turret_idle.png", 100, 130, 1);
	
	// Stages
	public static BufferedImage STAGE_BEACH = load("/stages/beach.png", 6400, 1200);

    /**
     * Loads an image.
     * @param s The path to the file.
     * @param w The width of the image.
     * @param h The height of the image.
     * @return The loaded image.
     */
	private static BufferedImage load(String s, int w, int h)
	{
		BufferedImage img;
		try
		{
			img = ImageIO.read(Content.class.getResourceAsStream(s));
			img = img.getSubimage(0, 0, w, h);
			return img;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Error loading graphics.");
			System.exit(0);
		}
		return null;
	}

    /**
     * Loads an array of images used together.
     * @param s The path to the file.
     * @param w The width of the images.
     * @param h The height of the images.
     * @param frames The number of frames in the array.
     * @return The array of loaded images.
     */
	private static BufferedImage[] loadArray(String s, int w, int h, int frames)
	{
		BufferedImage[] img = new BufferedImage[frames];
		BufferedImage sheet = load(s, w * frames, h);
		try
		{
			for (int i = 0; i < frames; i++)
			{
				img[i] = sheet.getSubimage(i * w, 0, w, h);
			}
			return img;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Error loading graphics.");
			System.exit(0);
		}
		return null;
	}
}