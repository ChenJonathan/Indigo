package indigo.GameState;

import indigo.Display.HUD;
import indigo.Entity.Entity;
import indigo.Entity.Player;
import indigo.Item.Item;
import indigo.Landscape.Platform;
import indigo.Landscape.Wall;
import indigo.Manager.Content;
import indigo.Manager.Data;
import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;
import indigo.Phase.Ice;
import indigo.Phase.Phase;
import indigo.Phase.Water;
import indigo.Projectile.Projectile;
import indigo.Stage.Beach;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * The state where the player is actively playing the game. Handles inputs (skill use) and sends the info to the HUD,
 * Stage, and Phase objects.
 */
public class PlayState extends GameState
{
	public Stage stage;
	public Player player;
	public HUD display;

	private ArrayList<Entity> entities;
	private ArrayList<Item> items;
	private ArrayList<Projectile> projectiles;
	private ArrayList<Platform> platforms;
	private ArrayList<Wall> walls;

	private int time;

	private Phase activePhase; // Current phase: Water or Ice
	private Phase inactivePhase;

	private int swapCooldown; // Cooldown for switching classes

	private int maxSwapCooldown = 0; // TODO Set

	// Values corresponding to each stage
	public static final int BEACH = 0;

	/**
	 * Sets up the play state and initializes stage, stage objects, phases, display, and timer.
	 * 
	 * @param gsm The game state manager.
	 */
	public PlayState(GameStateManager gsm)
	{
		super(gsm);
		data.resetLevelData();

		gsm.setCursor(Content.CURSOR);

		// Initialize stage
		switch(data.getStage())
		{
			case BEACH:
				stage = new Beach(this);
				break;
		}

		// Initialize stage objects
		entities = stage.getEntities();
		items = stage.getItems();
		projectiles = stage.getProjectiles();
		platforms = stage.getPlatforms();
		walls = stage.getWalls();
		player = (Player)stage.getPlayer();

		// Initialize phases
		activePhase = new Water(this);
		inactivePhase = new Ice(this);
		player.setPhase(activePhase);

		// Initialize display
		display = new HUD(this);
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
	}

	@Override
	public void render(Graphics2D g)
	{
		stage.updateCam(g);
		stage.render(g);
		stage.resetCam(g);

		display.render(g);
	}

	/**
	 * Handles all of the player input during play. Relays skillcasting information to both stage and HUD. Also checks
	 * for pause, class switch, etc.
	 */
	public void handleInput()
	{
		if(player.isActive())
		{
			// Movement
			if(player.canMove())
			{
				if(input.keyDown(InputManager.W) && player.canJump())
				{
					player.jump();
				}
				else if(input.keyPress(InputManager.W) && player.canDoubleJump())
				{
					player.canDoubleJump(false);
					player.jump();
				}
				else if(input.keyDown(InputManager.W) && player.canJumpMore())
				{
					player.jumpMore();
				}
				if(input.keyDown(InputManager.S) && player.canCrouch())
				{
					player.crouch();
				}
				else if(input.keyRelease(InputManager.S))
				{
					player.uncrouch();
				}
				if(input.keyDown(InputManager.A) && !player.isCrouching())
				{
					player.left();
				}
				if(input.keyDown(InputManager.D) && !player.isCrouching())
				{
					player.right();
				}
			}
			if(input.keyPress(InputManager.SPACE) && activePhase.canShift())
			{
				int x = 0;
				int y = 0;

				if(input.keyDown(InputManager.W))
				{
					y -= 1;
				}
				if(input.keyDown(InputManager.S))
				{
					y += 1;
				}
				if(input.keyDown(InputManager.A))
				{
					x -= 1;
				}
				if(input.keyDown(InputManager.D))
				{
					x += 1;
				}

				// Parameters represent player direction
				player.shift(x, y);
			}

			// Combat
			if(input.mousePress())
			{
				if(activePhase.skillSelected())
				{
					// Cast skill
					if(activePhase.canCast(activePhase.selectedSkill()))
					{
						activePhase.cast();
					}
				}
				else if(activePhase.canNormalAttack())
				{
					// Manual attacking
					if(input.mouseLeftPress())
					{
						player.attackMain();
					}
					else
					{
						player.attackAlt();
					}
				}
			}
			else if(input.mouseDown() && activePhase.canNormalAttack())
			{
				// Automatic attacking
				if(input.mouseLeftDown())
				{
					player.attackMain();
				}
				else
				{
					player.attackAlt();
				}
			}
			if(input.keyPress(InputManager.Q) && activePhase.canSwap() && swapCooldown == 0)
			{
				// Phase swapping
				swapPhases();
			}
			else
			{
				// Skill selection - If a skill is already selected, it is automatically deselected
				if(input.keyPress(InputManager.K1) && activePhase.canSelect(0))
				{
					activePhase.selectSkill(0);
				}
				else if(input.keyPress(InputManager.K2) && activePhase.canSelect(1))
				{
					activePhase.selectSkill(1);
				}
				else if(input.keyPress(InputManager.K3) && activePhase.canSelect(2))
				{
					activePhase.selectSkill(2);
				}
				else if(input.keyPress(InputManager.K4) && activePhase.canSelect(3))
				{
					activePhase.selectSkill(3);
				}
			}
		}

		// Menu
		if(input.keyPress(InputManager.ESCAPE))
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
		else if(input.keyPress(InputManager.E))
		{
			if(activePhase.id() == Phase.WATER)
			{
				((Water)activePhase).attackDelay = 6 / ((Water)activePhase).attackDelay;
			}

			// gsm.setTalents(true);
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
	 * Adds experience to the player's total.
	 * 
	 * @param experience The amount of experience to be added.
	 */
	public void addExperience(int experience)
	{
		data.setExperience(data.getExperience() + experience);
		if(data.getExperience() > data.getMaxExperience())
		{
			data.setLevel(data.getLevel() + 1);
			data.setExperience(data.getExperience() - data.getMaxExperience());
			data.setMaxExperience((int)Math.pow(2, data.getLevel() - 1) * 100);
			levelUp();
		}
	}

	/**
	 * Levels up the player.
	 */
	public void levelUp()
	{
		if(data.getLevel() % 5 == 0)
		{
			activePhase.unlockSkill();
			inactivePhase.unlockSkill();
		}
		// TODO Finish
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
	 * @return A list of the entities in play.
	 */
	public ArrayList<Entity> getEntities()
	{
		return entities;
	}
	
	/**
	 * @return A list of the items in play.
	 */
	public ArrayList<Item> getItems()
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
	 * @return A list of the platforms in play.
	 */
	public ArrayList<Platform> getPlatforms()
	{
		return platforms;
	}

	/**
	 * @return A list of the walls in play.
	 */
	public ArrayList<Wall> getWalls()
	{
		return walls;
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
	 */
	public void endGame()
	{
		data.setClearTime(time);
		gsm.setState(GameStateManager.CLEAR);
	}

	/**
	 * @return The current game data.
	 */
	public Data getData()
	{
		return data;
	}

	/**
	 * @return The current input data.
	 */
	public InputManager getInput()
	{
		return input;
	}
}