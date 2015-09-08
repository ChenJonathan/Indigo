package indigo.GameState;

import indigo.Display.HUD;
import indigo.Entity.Entity;
import indigo.Entity.Player;
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

// Handles inputs (skill use) and sends the info to the HUD, Stage, and Phase objects
public class PlayState extends GameState 
{
	public Stage stage;
	public Player player;
	public HUD display;
	
	private ArrayList<Entity> entities;
	private ArrayList<Projectile> projectiles;
	private ArrayList<Platform> platforms;
	private ArrayList<Wall> walls;
	
	private int time;
	
	private Phase activePhase; // Current phase: Water or Ice
	private Phase[] phases; // Array containing the two Phase objects
	
	private int swapCooldown; // Cooldown for switching classes
	
	private int maxSwapCooldown = 0;
	
	// Values corresponding to each stage
	public static final int BEACH = 0;
	
	public PlayState(GameStateManager gsm)
	{
		super(gsm);
		data.resetLevelData();
		
		gsm.setCursor(Content.CURSOR);
		
		// Initialize stage
		switch(data.getStage())
		{
			case BEACH: stage = new Beach(this); break;
		}
		
		// Initialize stage objects
		entities = stage.getEntities();
		projectiles = stage.getProjectiles();
		platforms = stage.getPlatforms();
		walls = stage.getWalls();
		player = (Player)stage.getPlayer();
		
		// Initialize phases
		phases = new Phase[Data.NUM_PHASES];
		phases[Phase.WATER] = new Water(this);
		phases[Phase.ICE] = new Ice(this);
		activePhase = phases[Phase.WATER];
		player.setPhase(activePhase);
		
		// Initialize display
		display = new HUD(this);
		display.setPhase(activePhase);
		
		// Initialize timer
		time = -1;
	}
	
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
		phases[1 - activePhase.id()].lowerCooldowns(); // Only decrement cooldowns for non-active phase
		
		stage.update();
		display.update();
	}
	
	public void render(Graphics2D g)
	{
		stage.updateCam(g);
		stage.render(g);
		stage.resetCam(g);
		
		display.render(g);
	}

	// Relays skillcasting information to both stage and HUD
	// Also checks for pause, class switch, etc
	public void handleInput()
	{
		// Movement
		if(player.canMove())
		{
			if(input.keyDown(InputManager.W) && player.isGrounded())
			{
				player.jump();
			}
			if(input.keyDown(InputManager.S))
			{
				player.crouch(true);
			}
			else
			{
				player.crouch(false);
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
				y -= 5;
			}
			if(input.keyDown(InputManager.S))
			{
				y += 5;
			}
			if(input.keyDown(InputManager.A))
			{
				x -= 5;
			}
			if(input.keyDown(InputManager.D))
			{
				x += 5;
			}
			
			player.setMistDirection(x, y);
			player.shift();
		}
		
		// Combat
		if(input.mousePress())
		{
			if(activePhase.skillSelected())
			{
				// Cast skill // TODO Consider placing skill selection check in canCast
				if(activePhase.getSkillState(activePhase.selectedSkill()) == Phase.AIM 
						&& activePhase.canCast(activePhase.selectedSkill()))
				{
					activePhase.cast();
				}
			}
			else
			{
				// Manual attacking
				if(activePhase.canNormalAttack())
				{
					player.attack();
				}
			}
		}
		else if(input.mouseDown() && activePhase.canNormalAttack())
		{
			// Automatic attacking
			player.attack();
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
		
		// Menu
		if(input.keyPress(InputManager.ESCAPE))
		{
			// If a skill is selected, deselect it
			if(activePhase.skillSelected() && activePhase.getSkillState(activePhase.selectedSkill()) == Phase.AIM)
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
			if(((Water)activePhase).attackDelay == 1)
			{
				if(player.cheat)
				{
					((Water)activePhase).attackDelay = 8;
					player.cheat = false;
				}
				else
				{
					player.cheat = true;
				}
			}
			else
			{
				((Water)activePhase).attackDelay = 1;
			}
			
			// gsm.setTalents(true);
		}
	}
	
	public int getTime()
	{
		return time;
	}
	
	public Phase[] getPhases()
	{
		return phases;
	}
	
	// Switches professions
	// TODO Consider placing in Phase
	public void swapPhases()
	{
		activePhase.resetSkillStates(); // Deselects any selected skills
		activePhase = phases[1 - activePhase.id()]; // Swaps value representing current phase
		player.setPhase(activePhase); // Swaps value representing current phase in Player
		display.setPhase(activePhase); // Swaps value representing current phase in Display
		swapCooldown = maxSwapCooldown; // Trigger cooldown for profession swap
	}
	
	public Entity getPlayer()
	{
		return entities.get(0);
	}
	
	public ArrayList<Entity> getEntities()
	{
		return entities;
	}
	
	public ArrayList<Projectile> getProjectiles()
	{
		return projectiles;
	}
	
	public ArrayList<Platform> getPlatforms()
	{
		return platforms;
	}
	
	public ArrayList<Wall> getWalls()
	{
		return walls;
	}
	
	public double getMapX()
	{
		return stage.getMapX();
	}
	
	public double getMapY()
	{
		return stage.getMapY();
	}
	
	public double getMouseX()
	{
		return stage.getMouseX();
	}
	
	public double getMouseY()
	{
		return stage.getMouseY();
	}
	
	public void endGame()
	{
		data.setClearTime(time);
		gsm.setState(GameStateManager.CLEAR);
	}
	
	public Data getData()
	{
		return data;
	}
	
	public InputManager getInput()
	{
		return input;
	}
}