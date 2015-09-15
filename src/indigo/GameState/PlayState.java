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
	private Phase inactivePhase;
	
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
		activePhase = new Water(this);
		inactivePhase = new Ice(this);
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
		inactivePhase.lowerCooldowns(); // Only decrement cooldowns for non-active phase
		
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
			else if(input.keyPress(InputManager.W) && player.canDoubleJump())
			{
				player.canDoubleJump(false);
				player.jump();
			}
			if(input.keyDown(InputManager.S) && player.canCrouch())
			{
				player.crouch();
			}
			else if(input.keyRelease(InputManager.S))
			{
				player.crouch();
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
		if(input.mouseLeftPress() || input.mouseRightPress())
		{
			if(activePhase.skillSelected())
			{
				// Cast skill
				if(activePhase.canCast(activePhase.selectedSkill()))
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
					if(activePhase.id() == Phase.ICE) // TODO Remove check when permanent weapons are implemented
					{
						if(input.mouseLeftDown())
						{
							player.setSlashMode(false);
						}
						else
						{
							player.setSlashMode(true);
						}
					}
				}
			}
		}
		else if((input.mouseLeftDown() || input.mouseRightDown()) && activePhase.canNormalAttack())
		{
			// Automatic attacking
			player.attack();
			if(activePhase.id() == Phase.ICE) // TODO Remove check when permanent weapons are implemented
			{
				if(input.mouseLeftDown())
				{
					player.setSlashMode(false);
				}
				else
				{
					player.setSlashMode(true);
				}
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
	
	public int getTime()
	{
		return time;
	}
	
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
	
	public void levelUp()
	{
		if(data.getLevel() % 5 == 0)
		{
			activePhase.unlockSkill();
			inactivePhase.unlockSkill();
		}
		// TODO Finish
	}
	
	// Switches professions
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