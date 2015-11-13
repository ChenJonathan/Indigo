package indigo.Manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.net.URLDecoder;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * This class is the content manager for Indigo. Call {@link ContentManager#getImage(ImageData)},
 * {@link ContentManager#getAnimation(AnimationData)}, or {@link ContentManager#getSound(SoundData)} with the respective
 * static image or animation data to get and cache the content, if it hasn't already. <br>
 * <br>
 * Call {@link #dispose()} whenever the stage is changed. To prevent RAM hogging.<br>
 * <br>
 * Conventions should dictate that {@link #dispose()} can be called at any given time, so images and animations should
 * be reattained from the cache every frame.
 * 
 * @author Jan Risse (a.k.a. <a href="http://strongjoshua.com">StrongJoshua</a>)
 * @author Jered Tupik (a.k.a HighTide)
 */
public class ContentManager
{
	// Cursor
	public static ImageData CURSOR = new ImageData("/images/cursor/cursor.png", 32, 32);

	// Elements
	public static ImageData FORCE_FIELD = new ImageData("/images/elements/force_field.png", 100, 30);
	public static ImageData PLATFORM = new ImageData("/images/elements/platform.png", 290, 70);
	public static ImageData SPIKE_WALL = new ImageData("/images/elements/spike_wall.png", 100, 30);
	public static ImageData STONE_TILE_LEFT = new ImageData("/images/elements/stone_tile_left.png", 100, 30);
	public static ImageData STONE_TILE_CENTER = new ImageData("/images/elements/stone_tile_center.png", 100, 30);
	public static ImageData STONE_TILE_RIGHT = new ImageData("/images/elements/stone_tile_right.png", 100, 30);

	// HUD
	public static ImageData HUD_ICE = new ImageData("/images/hud/hud_ice.png", 1920, 135);
	public static ImageData HEALTH_BAR = new ImageData("/images/hud/health_bar.png", 110, 20);
	public static ImageData HUD_WATER = new ImageData("/images/hud/hud_water.png", 1920, 135);
	public static ImageData OVERLAY_CAST = new ImageData("/images/hud/overlay_cast.png", 125, 125);
	public static ImageData OVERLAY_SELECT = new ImageData("/images/hud/overlay_select.png", 125, 125);
	public static ImageData SKILL_GEYSER = new ImageData("/images/hud/skill_geyser.png", 125, 125);
	public static ImageData SKILL_ICE_ARMOR = new ImageData("/images/hud/skill_mist.png", 125, 125);
	public static ImageData SKILL_ICE_CHAINS = new ImageData("/images/hud/skill_mist.png", 125, 125);
	public static ImageData SKILL_LOCKED = new ImageData("/images/hud/skill_locked.png", 125, 125);
	public static ImageData SKILL_MIST = new ImageData("/images/hud/skill_mist.png", 125, 125);
	public static ImageData SKILL_PULSE = new ImageData("/images/hud/skill_pulse.png", 125, 125);
	public static ImageData SKILL_WHIRLWIND = new ImageData("/images/hud/skill_whirlwind.png", 125, 125);
	public static ImageData SPRING = new ImageData("/images/hud/spring.png", 101, 89);
	public static ImageData SPRING_TOP_ICE = new ImageData("/images/hud/spring_top_ice.png", 101, 10);
	public static ImageData SPRING_TOP_WATER = new ImageData("/images/hud/spring_top_water.png", 101, 10);

	// Interactives
	public static AnimationData BRANCH_LEFT = new AnimationData("/images/interactives/branch/left.png", 240, 75, 1);
	public static AnimationData BRANCH_RIGHT = new AnimationData("/images/interactives/branch/right.png", 240, 75, 1);
	public static AnimationData HEALTH_PICKUP_DEATH = new AnimationData("/images/interactives/health_pickup/death.png",
			110, 110, 5);
	public static AnimationData HEALTH_PICKUP_IDLE = new AnimationData("/images/interactives/health_pickup/idle.png",
			110, 110, 4);
	public static AnimationData HEALTH_PICKUP_SPAWN = new AnimationData("/images/interactives/health_pickup/spawn.png",
			110, 110, 5);
	public static AnimationData STEAM_VENT = new AnimationData("/images/interactives/steam_vent/default.png", 100, 15, 1);

	// Menus
	public static ImageData ARROW_BIG_LEFT = new ImageData("/images/menus/arrow_big_left.png", 200, 300);
	public static ImageData ARROW_BIG_RIGHT = new ImageData("/images/menus/arrow_big_right.png", 200, 300);
	public static ImageData ARROW_LEFT = new ImageData("/images/menus/arrow_left.png", 40, 60);
	public static ImageData ARROW_RIGHT = new ImageData("/images/menus/arrow_right.png", 40, 60);
	public static ImageData ARROW_LEFT_ACTIVE = new ImageData("/images/menus/arrow_left_active.png", 60, 75);
	public static ImageData ARROW_LEFT_INACTIVE = new ImageData("/images/menus/arrow_left_inactive.png", 60, 75);
	public static ImageData ARROW_RIGHT_ACTIVE = new ImageData("/images/menus/arrow_right_active.png", 60, 75);
	public static ImageData ARROW_RIGHT_INACTIVE = new ImageData("/images/menus/arrow_right_inactive.png", 60, 75);
	public static ImageData BOOK = new ImageData("/images/menus/book.png", 900, 600);
	public static ImageData BUTTON_BACK = new ImageData("/images/menus/button_back.png", 358, 106);
	public static ImageData BUTTON_BLANK = new ImageData("/images/menus/button_blank.png", 400, 106);
	public static ImageData BUTTON_LEVEL = new ImageData("/images/menus/button_level.png", 106, 106);
	public static ImageData BUTTON_CLEAR = new ImageData("/images/menus/button_clear.png", 106, 106);
	public static ImageData BUTTON_CREDITS = new ImageData("/images/menus/button_credits.png", 358, 106);
	public static ImageData BUTTON_EXIT = new ImageData("/images/menus/button_exit.png", 106, 106);
	public static ImageData BUTTON_INSTRUCTIONS = new ImageData("/images/menus/button_instructions.png", 358, 106);
	public static ImageData BUTTON_LEVEL_EDITOR = new ImageData("/images/menus/button_level_editor.png", 358, 106);
	public static ImageData BUTTON_LOAD = new ImageData("/images/menus/button_load.png", 358, 106);
	public static ImageData BUTTON_NEXT = new ImageData("/images/menus/button_next.png", 358, 106);
	public static ImageData BUTTON_OPTIONS = new ImageData("/images/menus/button_options.png", 358, 106);
	public static ImageData BUTTON_PLAY = new ImageData("/images/menus/button_play.png", 358, 106);
	public static ImageData BUTTON_PREVIOUS = new ImageData("/images/menus/button_previous.png", 358, 106);
	public static ImageData BUTTON_QUIT = new ImageData("/images/menus/button_quit.png", 358, 106);
	public static ImageData BUTTON_RESUME = new ImageData("/images/menus/button_resume.png", 358, 106);
	public static ImageData BUTTON_SAVE = new ImageData("/images/menus/button_save.png", 358, 106);
	public static ImageData BUTTON_SAVE_LOAD = new ImageData("/images/menus/button_save_load.png", 358, 106);
	public static ImageData CONFIRM_BUTTON = new ImageData("/images/menus/confirm_button.png", 175, 50);
	public static ImageData CREDITS = new ImageData("/images/menus/credits.png", 1920, 1080);
	public static ImageData DESCRIPTION_BOX = new ImageData("/images/menus/description_box.png", 300, 440);
	public static ImageData GLOW_CIRCLE_CLICK = new ImageData("/images/menus/glow_circle_click.png", 166, 166);
	public static ImageData GLOW_CIRCLE_HOVER = new ImageData("/images/menus/glow_circle_hover.png", 166, 166);
	public static ImageData GLOW_RECTANGLE_CLICK = new ImageData("/images/menus/glow_rectangle_click.png", 418, 166);
	public static ImageData GLOW_RECTANGLE_HOVER = new ImageData("/images/menus/glow_rectangle_hover.png", 418, 166);
	public static ImageData INSTRUCTIONS_CONTROLS = new ImageData("/images/menus/instructions_controls.png", 1720, 880);
	public static ImageData INSTRUCTIONS_OBJECTIVES = new ImageData("/images/menus/instructions_objectives.png", 1720, 880);
	public static ImageData INSTRUCTIONS_SKILLS = new ImageData("/images/menus/instructions_skills.png", 1720, 880);
	public static ImageData MENU_BACKGROUND = new ImageData("/images/menus/menu_background.png", 1920, 1080);
	public static ImageData OPTION_BAR = new ImageData("/images/menus/option_bar.png", 1720, 158);
	public static ImageData PAUSE = new ImageData("/images/menus/pause.png", 570, 200);
	public static ImageData SAVE_LOAD_BAR = new ImageData("/images/menus/save_load_bar.png", 1720, 158);
	public static ImageData SELECTION_BOX = new ImageData("/images/menus/selection_box.png", 300, 75);
	public static ImageData TITLE_BACKGROUND = new ImageData("/images/menus/title_background.png", 1920, 1080);
	public static ImageData TOOLBAR = new ImageData("/images/menus/toolbar.png", 302, 402);

	// Projectiles
	public static AnimationData BULLET = new AnimationData("/images/projectiles/bullet/default.png", 30, 10, 1);
	public static AnimationData ELECTRIC_BALL = new AnimationData("/images/projectiles/electric_ball.png", 100, 100, 1);
	public static AnimationData ELECTRIC_SPARK = new AnimationData("/images/projectiles/electric_spark.png", 100, 100,
			1);
	public static AnimationData FROST_ORB = new AnimationData("/images/projectiles/frost_orb/default.png", 110, 110, 8);
	public static AnimationData GEYSER_BASE = new AnimationData("/images/projectiles/geyser_base.png", 100, 100, 1);
	public static AnimationData GEYSER = new AnimationData("/images/projectiles/geyser_particle.png", 80, 50, 1);
	public static AnimationData ICICLE = new AnimationData("/images/projectiles/icicle/default.png", 160, 73, 1);
	public static AnimationData MORTAR_DEATH = new AnimationData("/images/projectiles/mortar_death.png", 100, 100, 9);
	public static AnimationData MORTAR = new AnimationData("/images/projectiles/mortar.png", 50, 50, 1);
	public static AnimationData PULSE_WAVE = new AnimationData("/images/projectiles/pulse_wave/default.png", 100, 100,
			1);
	public static AnimationData STEAM_CLOUD = new AnimationData("/images/projectiles/steam_cloud/default.png", 100, 50,
			1);
	public static AnimationData STEEL_BEAM = new AnimationData("/images/projectiles/steel_beam/default.png", 50, 100, 1);
	public static AnimationData STEEL_BEAM_DEATH = new AnimationData("/images/projectiles/steel_beam/death.png", 50,
			100, 10);
	public static AnimationData WATER_BOLT_DEATH = new AnimationData("/images/projectiles/water_bolt/death.png", 80,
			73, 2);
	public static AnimationData WATER_BOLT_DEATH_WALL = new AnimationData(
			"/images/projectiles/water_bolt/death_wall.png", 80, 73, 2);
	public static AnimationData WATER_BOLT = new AnimationData("/images/projectiles/water_bolt/default.png", 80, 73, 2);

	// Sprites
	public static AnimationData BLOCKADE_IDLE = new AnimationData("/images/sprites/blockade/idle.png", 200, 200, 4);
	public static AnimationData CORE_IDLE = new AnimationData("/images/sprites/core/idle.png", 124, 162, 1);
	public static ImageData FLYING_BOT_CANNON = new ImageData("/images/sprites/flying_bot/cannon.png", 18, 11);
	public static AnimationData FLYING_BOT_DEATH = new AnimationData("/images/sprites/flying_bot/death.png", 60, 60, 6);
	public static AnimationData FLYING_BOT_IDLE = new AnimationData("/images/sprites/flying_bot/idle.png", 60, 60, 4);
	public static AnimationData INCENDIARY_TURRET_BASE_DEATH = new AnimationData(
			"/images/sprites/incendiary_turret/base_death.png", 130, 110, 7);
	public static AnimationData INCENDIARY_TURRET_BASE_DEFAULT = new AnimationData(
			"/images/sprites/incendiary_turret/base_default.png", 130, 110, 1);
	public static AnimationData INCENDIARY_TURRET_BARREL_DEATH = new AnimationData(
			"/images/sprites/incendiary_turret/barrel_death.png", 130, 130, 7);
	public static AnimationData INCENDIARY_TURRET_BARREL_DEFAULT = new AnimationData(
			"/images/sprites/incendiary_turret/barrel_default.png", 130, 130, 1);
	public static AnimationData PLAYER_BLOCK_LEFT = new AnimationData("/images/sprites/player/block_left.png", 68, 111,
			1);
	public static AnimationData PLAYER_BLOCK_RIGHT = new AnimationData("/images/sprites/player/block_right.png", 68,
			111, 1);
	public static AnimationData PLAYER_BLOCK_LEFT_ARMOR = new AnimationData(
			"/images/sprites/player/block_left_armor.png", 68, 111, 1);
	public static AnimationData PLAYER_BLOCK_RIGHT_ARMOR = new AnimationData(
			"/images/sprites/player/block_right_armor.png", 68, 111, 1);
	public static AnimationData PLAYER_CROUCH_LEFT = new AnimationData("/images/sprites/player/crouch_left.png", 68,
			111, 1);
	public static AnimationData PLAYER_CROUCH_RIGHT = new AnimationData("/images/sprites/player/crouch_right.png", 68,
			111, 1);
	public static AnimationData PLAYER_DEATH_LEFT = new AnimationData("/images/sprites/player/death_left.png", 68, 111,
			15);
	public static AnimationData PLAYER_DEATH_LEFT_ARMOR = new AnimationData(
			"/images/sprites/player/death_left_armor.png", 68, 111, 15);
	public static AnimationData PLAYER_DEATH_RIGHT = new AnimationData("/images/sprites/player/death_right.png", 68,
			111, 15);
	public static AnimationData PLAYER_DEATH_RIGHT_ARMOR = new AnimationData(
			"/images/sprites/player/death_right_armor.png", 68, 111, 15);
	public static AnimationData PLAYER_IDLE_LEFT = new AnimationData("/images/sprites/player/idle_left.png", 68, 111, 6);
	public static AnimationData PLAYER_IDLE_LEFT_ARMOR = new AnimationData(
			"/images/sprites/player/idle_left_armor.png", 68, 111, 6);
	public static AnimationData PLAYER_IDLE_RIGHT = new AnimationData("/images/sprites/player/idle_right.png", 68, 111,
			6);
	public static AnimationData PLAYER_IDLE_RIGHT_ARMOR = new AnimationData(
			"/images/sprites/player/idle_right_armor.png", 68, 111, 6);
	public static AnimationData PLAYER_JUMP_LEFT = new AnimationData("/images/sprites/player/jump_left.png", 68, 111, 1);
	public static AnimationData PLAYER_JUMP_LEFT_ARMOR = new AnimationData(
			"/images/sprites/player/jump_left_armor.png", 68, 111, 1);
	public static AnimationData PLAYER_JUMP_RIGHT = new AnimationData("/images/sprites/player/jump_right.png", 68, 111,
			1);
	public static AnimationData PLAYER_JUMP_RIGHT_ARMOR = new AnimationData(
			"/images/sprites/player/jump_right_armor.png", 68, 111, 1);
	public static AnimationData PLAYER_MIST = new AnimationData("/images/sprites/player/mist.png", 68, 111, 4);
	public static AnimationData PLAYER_MOVE_LEFT = new AnimationData("/images/sprites/player/move_left.png", 68, 111, 8);
	public static AnimationData PLAYER_MOVE_LEFT_ARMOR = new AnimationData(
			"/images/sprites/player/move_left_armor.png", 68, 111, 8);
	public static AnimationData PLAYER_MOVE_RIGHT = new AnimationData("/images/sprites/player/move_right.png", 68, 111,
			8);
	public static AnimationData PLAYER_MOVE_RIGHT_ARMOR = new AnimationData(
			"/images/sprites/player/move_right_armor.png", 68, 111, 8);
	public static AnimationData PLAYER_WHIRLWIND_LEFT = new AnimationData("/images/sprites/player/whirlwind_left.png",
			200, 120, 8);
	public static AnimationData PLAYER_WHIRLWIND_LEFT_ARMOR = new AnimationData(
			"/images/sprites/player/whirlwind_left_armor.png", 200, 120, 8);
	public static AnimationData PLAYER_WHIRLWIND_RIGHT = new AnimationData(
			"/images/sprites/player/whirlwind_right.png", 200, 120, 8);
	public static AnimationData PLAYER_WHIRLWIND_RIGHT_ARMOR = new AnimationData(
			"/images/sprites/player/whirlwind_right_armor.png", 200, 120, 8);
	public static AnimationData TREE_DEFAULT = new AnimationData("/images/sprites/tree/default.png", 660, 960, 1);
	public static AnimationData TURRET_BASE_DEATH = new AnimationData("/images/sprites/turret/base_death.png", 130,
			110, 7);
	public static AnimationData TURRET_BASE_DEFAULT = new AnimationData("/images/sprites/turret/base_default.png", 130,
			110, 1);
	public static AnimationData TURRET_CANNON_DEATH = new AnimationData("/images/sprites/turret/cannon_death.png", 130,
			130, 7);
	public static AnimationData TURRET_CANNON_DEFAULT = new AnimationData("/images/sprites/turret/cannon_default.png",
			130, 130, 1);

	// Stages
	public static ImageData STAGE_BEACH = new ImageData("/images/stages/beach.png", 6400, 1200);
	public static ImageData BACKGROUND = new ImageData("/images/stages/background.png", 640, 270);

	// Weapons
	public static AnimationData ICE_SWORD_IDLE_LEFT = new AnimationData("/images/weapon/ice_sword/idle_left.png", 138,
			146, 1);
	public static AnimationData ICE_SWORD_IDLE_RIGHT = new AnimationData("/images/weapon/ice_sword/idle_right.png",
			138, 146, 1);
	public static AnimationData ICE_SWORD_DOWNSLASH_LEFT = new AnimationData(
			"/images/weapon/ice_sword/downslash_left.png", 138, 146, 6);
	public static AnimationData ICE_SWORD_DOWNSLASH_RIGHT = new AnimationData(
			"/images/weapon/ice_sword/downslash_right.png", 138, 146, 6);
	public static AnimationData ICE_SWORD_STAB_LEFT = new AnimationData("/images/weapon/ice_sword/stab_left.png", 138,
			146, 4);
	public static AnimationData ICE_SWORD_STAB_RIGHT = new AnimationData("/images/weapon/ice_sword/stab_right.png",
			138, 146, 4);
	public static AnimationData ICE_SWORD_UPSLASH_LEFT = new AnimationData("/images/weapon/ice_sword/upslash_left.png",
			138, 146, 6);
	public static AnimationData ICE_SWORD_UPSLASH_RIGHT = new AnimationData(
			"/images/weapon/ice_sword/upslash_right.png", 138, 146, 6);
	public static AnimationData STAFF_ATTACK_LEFT = new AnimationData("/images/weapon/staff/attack_left.png", 100, 90,
			1);
	public static AnimationData STAFF_ATTACK_RIGHT = new AnimationData("/images/weapon/staff/attack_right.png", 100,
			90, 1);
	public static AnimationData STAFF_CAST_LEFT = new AnimationData("/images/weapon/staff/cast_left.png", 100, 90, 6);
	public static AnimationData STAFF_CAST_RIGHT = new AnimationData("/images/weapon/staff/cast_right.png", 100, 90, 6);
	public static AnimationData STAFF_IDLE_LEFT = new AnimationData("/images/weapon/staff/idle_left.png", 100, 90, 1);
	public static AnimationData STAFF_IDLE_RIGHT = new AnimationData("/images/weapon/staff/idle_right.png", 100, 90, 1);

	// Sounds
	public static SoundData DEATH_EFFECT = new SoundData("/sounds/effects/death.wav", false);
	public static SoundData FROST_ORB_EFFECT = new SoundData("/sounds/effects/frost_orb.wav", false);
	public static SoundData GEYSER_MID_EFFECT = new SoundData("/sounds/effects/geyser_mid.wav", true);
	public static SoundData GEYSER_START_EFFECT = new SoundData("/sounds/effects/geyser_start.wav", false);
	public static SoundData HEALTH_PICKUP_EFFECT = new SoundData("/sounds/effects/health_pickup.wav", false);
	public static SoundData ICE_ARMOR_OFF_EFFECT = new SoundData("/sounds/effects/ice_armor_off.wav", false);
	public static SoundData ICE_ARMOR_ON_EFFECT = new SoundData("/sounds/effects/ice_armor_on.wav", false);
	public static SoundData ICE_CHAINS_END_EFFECT = new SoundData("/sounds/effects/ice_chains_end.wav", false);
	public static SoundData ICE_CHAINS_MID_EFFECT = new SoundData("/sounds/effects/ice_chains_mid.wav", true);
	public static SoundData ICE_CHAINS_START_EFFECT = new SoundData("/sounds/effects/ice_chains_start.wav", false);
	public static SoundData JUMP_EFFECT = new SoundData("/sounds/effects/jump.wav", false);
	public static SoundData PULSE_EFFECT = new SoundData("/sounds/effects/pulse.wav", false);
	public static SoundData SLASH_EFFECT = new SoundData("/sounds/effects/slash.wav", false);
	public static SoundData STAB_EFFECT = new SoundData("/sounds/effects/stab.wav", false);
	public static SoundData TELEPORT_EFFECT = new SoundData("/sounds/effects/teleport.wav", false);
	public static SoundData WATER_BOLT_EFFECT = new SoundData("/sounds/effects/water_bolt.wav", false);

	// Music
	public static SoundData BACKGROUND_THEME = new SoundData("/sounds/music/background.wav", true);
	public static SoundData TITLE_THEME = new SoundData("/sounds/music/skylights.wav", true);

	// Storage
	private static HashMap<ImageData, BufferedImage> imageMap;
	private static HashMap<AnimationData, BufferedImage[]> animationMap;
	private static HashMap<SoundData, byte[]> soundMap;

	private static JSONParser parser;

	static
	{
		imageMap = new HashMap<>();
		animationMap = new HashMap<>();
		soundMap = new HashMap<>();

		parser = new JSONParser();
	}

	/**
	 * @param ad The ImageData to retrieve the image from. Should be attained from {@link ContentManager}.
	 * @return A BufferedImage composing the requested image.
	 */
	public static BufferedImage getImage(ImageData id)
	{
		BufferedImage img = imageMap.get(id);
		if(img != null)
		{
			return img;
		}
		img = load(id);
		imageMap.put(id, img);
		return img;
	}

	/**
	 * @param ad The AnimationData to retrieve the animation from. Should be attained from {@link ContentManager}.
	 * @return A BufferedImage[] composing the requested animation.
	 */
	public static BufferedImage[] getAnimation(AnimationData ad)
	{
		BufferedImage[] ani = animationMap.get(ad);
		if(ani != null)
		{
			return ani;
		}
		ani = load(ad);
		animationMap.put(ad, ani);
		return ani;
	}

	/**
	 * @param sd The SoundData to retrieve the sound from. Should be attained from {@link ContentManager}
	 * @return A byte array containing the requested sound data.
	 */
	public static byte[] getSound(SoundData sd)
	{
		byte[] snd = soundMap.get(sd);
		if(snd != null)
		{
			return snd;
		}
		snd = load(sd);
		soundMap.put(sd, snd);
		return snd;
	}

	private static class ImageData
	{
		private String path;
		private int width, height;

		private ImageData(String path, int width, int height)
		{
			this.path = path;
			this.width = width;
			this.height = height;
		}
	}

	private static class AnimationData
	{
		private String path;
		private int width, height, frames;

		private AnimationData(String path, int width, int height, int frames)
		{
			this.path = path;
			this.width = width;
			this.height = height;
			this.frames = frames;
		}
	}

	static class SoundData
	{
		private String path;
		private boolean doesLoop;

		private SoundData(String path, boolean doesLoop)
		{
			this.path = path;
			this.doesLoop = doesLoop;
		}

		public String path()
		{
			return path;
		}

		public boolean doesLoop()
		{
			return doesLoop;
		}
	}

	private static BufferedImage load(ImageData id)
	{
		return load(id.path, id.width, id.height);
	}

	private static BufferedImage load(String path, int width, int height)
	{
		BufferedImage img;
		try
		{
			img = ImageIO.read(ContentManager.class.getResourceAsStream(path));
			img = img.getSubimage(0, 0, width, height);
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

	private static BufferedImage[] load(AnimationData ad)
	{
		BufferedImage[] img = new BufferedImage[ad.frames];
		BufferedImage sheet = load(ad.path, ad.width * ad.frames, ad.height);
		try
		{
			for(int i = 0; i < ad.frames; i++)
			{
				img[i] = sheet.getSubimage(i * ad.width, 0, ad.width, ad.height);
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

	private static byte[] load(SoundData sd)
	{
		byte[] snd = new byte[0];
		try
		{
			String FilePath = URLDecoder.decode(ContentManager.class.getResource(sd.path).getPath(), "UTF-8");
			File AudioFile = new File(FilePath);
			AudioInputStream ais = AudioSystem.getAudioInputStream(AudioFile);
			AudioFormat Format = ais.getFormat();
			snd = new byte[(int)(ais.getFrameLength() * Format.getFrameSize())];
			ais.read(snd, 0, snd.length);
			ais.close();
			return snd;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Error loading sounds.");
			System.exit(0);
		}
		return null;
	}

	public static JSONObject load(String path)
	{
		try
		{
			path = new File("").getAbsolutePath().concat("/resources/data" + path);
			return (JSONObject)parser.parse(new FileReader(path));
		}
		catch(Exception e)
		{
			System.out.println("File not found.");
		}
		return null;
	}

	/**
	 * Clears the image, animation, and sound cache. To be called between stages, but should conventionally be able to
	 * be called at any time.
	 */
	public static void dispose()
	{
		imageMap.clear();
		animationMap.clear();
		soundMap.clear();
	}
}