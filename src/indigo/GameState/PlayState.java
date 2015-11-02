package indigo.GameState;

import indigo.Display.HUD;
import indigo.Entity.Entity;
import indigo.Entity.Player;
import indigo.Interactive.Interactive;
import indigo.Landscape.Platform;
import indigo.Landscape.Wall;
import indigo.Manager.ContentManager;
import indigo.Manager.Data;
import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;
import indigo.Manager.Manager;
import indigo.Manager.SoundManager;
import indigo.Phase.Ice;
import indigo.Phase.Phase;
import indigo.Phase.Water;
import indigo.Projectile.Projectile;
import indigo.Stage.BattleStage;
import indigo.Stage.DefendStage;
import indigo.Stage.Stage;
import indigo.Stage.SurvivalStage;
import indigo.Stage.TravelStage;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * The state where the player is actively playing the game. Handles Manager.inputs (skill use) and sends the info to the
 * HUD, Stage, and Phase objects.
 */
public class PlayState extends GameState
{
	public Stage stage;
	public String type;

	public Player player;
	public HUD display;

	private ArrayList<Entity> entities;
	private ArrayList<Interactive> items;
	private ArrayList<Projectile> projectiles;
	protected ArrayList<Wall> walls;
	protected ArrayList<Platform> platforms;

	private int time;

	private Phase activePhase; // Current phase: Water or Ice
	private Phase inactivePhase;

	private int swapCooldown; // Cooldown for switching classes

	private int maxSwapCooldown = 150; // TODO Set

	/**
	 * Sets up the play state and initializes stage, stage objects, phases, display, and timer.
	 * 
	 * @param gsm The game state manager.
	 */
	public PlayState(GameStateManager gsm)
	{
		super(gsm);

		gsm.setCursor(ContentManager.getImage(ContentManager.CURSOR));

		// Initialize stage
		switch(data.getStage().get("type") + "")
		{
			case "Battle":
				stage = new BattleStage(this, data.getStage());
				break;
			case "Defend":
				stage = new DefendStage(this, data.getStage());
				break;
			case "Survival":
				stage = new SurvivalStage(this, data.getStage());
				break;
			case "Travel":
				stage = new TravelStage(this, data.getStage());
				break;
		}
		data.resetLevelData();

		// Initialize stage objects
		entities = stage.getEntities();
		items = stage.getInteractives();
		projectiles = stage.getProjectiles();
		walls = stage.getWalls();
		platforms = stage.getPlatforms();
		player = (Player)getPlayer();

		// Initialize phases
		activePhase = new Water(this);
		inactivePhase = new Ice(this);
		player.setPhase(activePhase);

		// Initialize display
		display = new HUD(this, stage);
		display.setPhase(activePhase);

		// Initialize timer
		time = -1;
	}

	@Override
	public void update()
	{
		handleInput();

		time++;

		// Decrememnt cooldowns every tick
		if(swapCooldown > 0)
		{
			swapCooldown--;
		}
		activePhase.update(); // Update current phase (includes cooldown decrement)
		inactivePhase.lowerCooldowns(); // Only decrement cooldowns for non-active phase

		stage.update();
		display.update();

		if(data.getExperience() > data.getMaxExperience())
		{
			levelUp();
		}
	}

	@Override
	public void render(Graphics2D g)
	{
		stage.updateCam(g);
		stage.render(g);

		display.render(g);
	}

	/**
	 * Handles all of the player Manager.input during play. Relays skillcasting information to both stage and HUD. Also
	 * checks for pause, class switch, etc.
	 */
	public void handleInput()
	{
		if(player.isActive())
		{
			// Movement
			if(Manager.input.keyDown(InputManager.W) && player.canJump())
			{
				player.jump();
			}
			else if(Manager.input.keyPress(InputManager.W) && player.canDoubleJump())
			{
				player.canDoubleJump(false);
				player.jump();
			}
			else if(Manager.input.keyDown(InputManager.W) && player.canJumpMore())
			{
				player.jumpMore();
			}
			if(Manager.input.keyDown(InputManager.S) && player.canCrouch())
			{
				player.crouch();
			}
			else if(Manager.input.keyRelease(InputManager.S))
			{
				player.uncrouch();
			}
			if(Manager.input.keyDown(InputManager.A) && player.canMove() && !player.isCrouching())
			{
				player.left();
			}
			if(Manager.input.keyDown(InputManager.D) && player.canMove() && !player.isCrouching())
			{
				player.right();
			}

			// Combat
			if(Manager.input.mousePress() && activePhase.skillSelected())
			{
				// Cast skill
				if(activePhase.canCast(activePhase.selectedSkill()))
				{
					activePhase.cast();
				}
			}
			else if(activePhase.canNormalAttack())
			{
				// Normal attacking
				if(Manager.input.mouseLeftDown())
				{
					player.attackMain();
				}
				else if(Manager.input.mouseRightDown())
				{
					player.attackAlt();
				}
			}
			if(Manager.input.keyPress(InputManager.CONTROL) && activePhase.canSwap() && swapCooldown == 0)
			{
				// Phase swapping
				swapPhases();
			}
			else
			{
				// Skill selection - If a skill is already selected, it is automatically deselected
				if(Manager.input.keyPress(InputManager.K1) && activePhase.canSelect(0))
				{
					activePhase.selectSkill(0);
				}
				else if(Manager.input.keyPress(InputManager.K2) && activePhase.canSelect(1))
				{
					activePhase.selectSkill(1);
				}
				else if(Manager.input.keyPress(InputManager.K3) && activePhase.canSelect(2))
				{
					activePhase.selectSkill(2);
				}
			}
		}

		// Menu
		if(Manager.input.keyPress(InputManager.ESCAPE))
		{
			// If a skill is selected, deselect it
			if(activePhase.skillSelected())
			{
				activePhase.deselectSkill();
			}
			else
			{
				gsm.setPaused(true);
			}
		}

		// Miscellaneous
		if(Manager.input.keyPress(InputManager.SPACE))
		{
			stage.toggleCam();
		}
	}

	/**
	 * @return The current game time.
	 */
	public int getTime()
	{
		return time;
	}

	/**
	 * Levels up the player.
	 */
	public void levelUp()
	{
		data.setLevel(data.getLevel() + 1);
		data.setExperience(data.getExperience() - data.getMaxExperience());
		data.setMaxExperience(data.getLevel() * 100);

		double healthRatio = (double)player.getHealth() / player.getMaxHealth();
		double manaRatio = (double)player.getMana() / player.getMaxMana();
		player.setMaxHealth(player.getMaxHealth() + 10);
		player.setMaxMana(player.getMaxMana() + 10);
		player.setHealth((int)(player.getMaxHealth() * healthRatio));
		player.setMana((int)(player.getMaxMana() * manaRatio));

		if(data.getLevel() % 5 == 0)
		{
			activePhase.unlockSkill();
			inactivePhase.unlockSkill();
		}

		// TODO Add animation
	}

	/**
	 * @return The current cooldown for swapping phases.
	 */
	public int getSwapCooldown()
	{
		return swapCooldown;
	}

	/**
	 * @return The maximum cooldown for swapping phases.
	 */
	public int getMaxSwapCooldown()
	{
		return maxSwapCooldown;
	}

	/**
	 * Swaps out the player's active phase with the player's inactive phase.
	 */
	public void swapPhases()
	{
		activePhase.resetSkillStates(); // Deselects any selected skills

		// Swaps current phase
		Phase temp = activePhase;
		activePhase = inactivePhase;
		inactivePhase = temp;

		player.setPhase(activePhase); // Swaps value representing current phase in Player
		display.setPhase(activePhase); // Swaps value representing current phase in Display
		swapCooldown = maxSwapCooldown; // Trigger cooldown for profession swap

		if(activePhase.id() == Phase.ICE)
		{
			player.canDoubleJump(true);
		}
	}

	/**
	 * @return The player entity.
	 */
	public Entity getPlayer()
	{
		return entities.get(0);
	}

	/**
	 * @return The stage containing objects in play.
	 */
	public Stage getStage()
	{
		return stage;
	}

	/**
	 * @return A list of the entities in play.
	 */
	public ArrayList<Entity> getEntities()
	{
		return entities;
	}

	/**
	 * @return A list of the items in play.
	 */
	public ArrayList<Interactive> getItems()
	{
		return items;
	}

	/**
	 * @return A list of the projectiles in play.
	 */
	public ArrayList<Projectile> getProjectiles()
	{
		return projectiles;
	}

	/**
	 * @return A list of the walls in play.
	 */
	public ArrayList<Wall> getWalls()
	{
		return walls;
	}

	/**
	 * @return A list of the platforms in play.
	 */
	public ArrayList<Platform> getPlatforms()
	{
		return platforms;
	}

	/**
	 * @return The greatest x-value of the stage displayed.
	 */
	public double getMapX()
	{
		return stage.getMapX();
	}

	/**
	 * @return The greatest y-value of the stage displayed.
	 */
	public double getMapY()
	{
		return stage.getMapY();
	}

	/**
	 * @return The current x-value of the mouse.
	 */
	public double getMouseX()
	{
		return stage.getMouseX();
	}

	/**
	 * @return The current y-value of the mouse.
	 */
	public double getMouseY()
	{
		return stage.getMouseY();
	}

	/**
	 * Ends the current game and transitions to ClearStateState.
	 * 
	 * @param victory Whether the game ended as a victory or not.
	 */
	public void endGame(boolean victory)
	{
		SoundManager.stopAll(); // TODO Temporary
		data.setClearTime(time);
		data.setVictory(victory);
		gsm.setState(GameStateManager.CLEAR);
	}

	/**
	 * @return The current game data.
	 */
	public Data getData()
	{
		return data;
	}
}