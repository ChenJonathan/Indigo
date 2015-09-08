package indigo.Entity;

import indigo.Landscape.Wall;
import indigo.Manager.Content;
import indigo.Melee.IceSword;
import indigo.Phase.Phase;
import indigo.Projectile.Mortar;
import indigo.Projectile.WaterProjectile;
import indigo.Stage.Stage;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Player extends Entity
{
	public boolean cheat; // TODO Revert all
	
	private int mana, maxMana;
	private int stamina;
	private boolean crouching;
	
	private int healthRegenTime; // Last time when health was regenerated
	private int manaRegenTime; // Last time when mana was regenerated
	private int staminaRegenTime; // Last time when stamina was regenerated
	
	private Phase phase;
	private int mistX;
	private int mistY;
	
	// Values corresponding to each animation
	private final int GROUND_LEFT = 0;
	private final int GROUND_RIGHT = 1;
	private final int MOVE_LEFT = 2;
	private final int MOVE_RIGHT = 3;
	private final int JUMP_LEFT = 4;
	private final int JUMP_RIGHT = 5;
	private final int CROUCH_LEFT = 6;
	private final int CROUCH_RIGHT = 7;
	private final int MIST = 8;
	
	private final double ACCELERATION = 5;
	private final double MOVE_SPEED = 20;
	private final double JUMP_SPEED = 40;
	
	public static final int PLAYER_WIDTH = 60;
	public static final int PLAYER_HEIGHT = 120;
	
	public static final int BASE_HEALTH = 200;
	public static final int BASE_MANA = 150;
	public static final int BASE_STAMINA = 100;
	
	// Stamina costs - Crouch stamina is the minimum stamina required to start crouching
	public static final int CROUCH_STAMINA_COST = 1;
	public static final int CROUCH_STAMINA_REQUIREMENT = 25;
	public static final int SHIFT_STAMINA_COST = 10;
	
	// Amount regenerated
	public static final int HEALTH_REGEN = 1;
	public static final int MANA_REGEN = 3;
	public static final int STAMINA_REGEN = 1;
	
	// Time between each regeneration
	public static final int HEALTH_REGEN_DELAY = 30;
	public static final int MANA_REGEN_DELAY = 30;
	public static final int STAMINA_REGEN_DELAY = 1;
	
	// Time until next regeneration after corresponding value is lowered (through damage, skillcasting, or blocking)
	public static final int HEALTH_REGEN_LONG_DELAY = 150;
	public static final int MANA_REGEN_LONG_DELAY = 150;
	public static final int STAMINA_REGEN_LONG_DELAY = 30;
	
	// X is center, Y is at feet level
	// FIX - Use roll animation checks, not dodge checks (switch statement for animation?)
	public Player(Stage stage, double x, double y, int health, int mana)
	{
		super(stage, x, y, health);
		name = "yourself";
		
		width = PLAYER_WIDTH;
		height = PLAYER_HEIGHT;
		
		maxMana = this.mana = mana;
		stamina = BASE_STAMINA;
		
		healthRegenTime = manaRegenTime = staminaRegenTime = -50; // Prevents initial regeneration check

		movability = 5;
		solid = true;
		friendly = true;
		
		setAnimation(GROUND_RIGHT, Content.PLAYER_IDLE_RIGHT, 15);
	}
	
	public void update()
	{
		// Animation related activities
		if(currentAnimation == MIST)
		{
			// TODO Check death and end method if player is dying
			if(animation.hasPlayedOnce())
			{
				dodging = false;
				flying = false;
				
				canAttack(true);
				canMove(true);
				
				mistX = 0;
				mistY = 0;
				
				phase.resetAttackTimer();
			}
			else
			{
				// Mist functionality goes here
				for(int count = 0; count < 30; count++) //
				{
					setX(getX() + mistX);
					setY(getY() + mistY);
					
					for(Wall wall: stage.getWalls())
					{
						/* TODO
						if(wall.killsEntities())
						{
							if(intersects(wall.getLine()))
							{
								data.setKiller(wall.getName());
								die();
							}
						}
						*/
						if(!wall.isHorizontal())
						{
							// Leftward collision into wall
							if(isRightOfLine(wall.getLine()))
							{
								while(intersects(wall.getLine()))
								{
									setX(getX() + Stage.PUSH_AMOUNT);
									setVelX(Math.max(getVelX(), 0));
								}
							}
							// Rightward collision into wall
							else
							{
								while(intersects(wall.getLine()))
								{
									setX(getX() - Stage.PUSH_AMOUNT);
									setVelX(Math.min(getVelX(), 0));
								}
							}
						}
						else
						{
							// Downward collision into wall
							if(isAboveLine(wall.getLine()))
							{
								while(intersects(wall.getLine()))
								{
									setY(getY() - Stage.PUSH_AMOUNT);
									setVelY(Math.min(getVelY(), 0));
								}
							}
							// Upward collision into wall
							else
							{
								while(intersects(wall.getLine()))
								{
									setY(getY() + Stage.PUSH_AMOUNT);
									setVelY(Math.max(getVelY(), 0));
								}
							}
						}
					}
				}
			}
		}
		
		super.update();
		
		// Update weapon
		if(hasWeapon())
		{
			weapon.update();
		}
		
		// Change direction
		if(currentAnimation != MIST && !hasWeapon()) // TODO Add death animation
		{
			setDirection(stage.getMouseX() > this.getX());
		}
		
		// Default animations
		if(!dodging && !crouching && !blocking && !flying)
		{
			if(ground == null)
			{
				if(isFacingRight() && currentAnimation != JUMP_RIGHT)
				{
					setAnimation(JUMP_RIGHT, Content.PLAYER_JUMP_RIGHT, -1);
				}
				else if(!isFacingRight() && currentAnimation != JUMP_LEFT)
				{
					setAnimation(JUMP_LEFT, Content.PLAYER_JUMP_LEFT, -1);
				}
			}
			else if(getVelX() == 0)
			{
					if(isFacingRight() && currentAnimation != GROUND_RIGHT)
					{
						setAnimation(GROUND_RIGHT, Content.PLAYER_IDLE_RIGHT, 15);
					}
					else if(!isFacingRight() && currentAnimation != GROUND_LEFT)
					{
						setAnimation(GROUND_LEFT, Content.PLAYER_IDLE_LEFT, 15);
					}
			}
			else if(isFacingRight())
			{
				if(currentAnimation != MOVE_RIGHT)
				{
					setAnimation(MOVE_RIGHT, Content.PLAYER_MOVE_RIGHT, 3);
				}
				
				if(getVelX() < 0)
				{
					animation.setReverse(true);
				}
				else
				{
					animation.setReverse(false);
				}
			}
			else if(!isFacingRight())
			{
				if(currentAnimation != MOVE_LEFT)
				{
					setAnimation(MOVE_LEFT, Content.PLAYER_MOVE_LEFT, 3);
				}
				
				if(getVelX() > 0)
				{
					animation.setReverse(true);
				}
				else
				{
					animation.setReverse(false);
				}
			}
		}
		
		// Regeneration
		if(stage.getTime() == healthRegenTime)
		{
			setHealth(getHealth() + HEALTH_REGEN);
		}
		if(stage.getTime() == manaRegenTime)
		{
			setMana(getMana() + MANA_REGEN);
		}
		if(stage.getTime() == staminaRegenTime)
		{
			setStamina(getStamina() + STAMINA_REGEN);
		}
	}
	
	public void render(Graphics g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), (int)getWidth(), (int)getHeight(), null);
		
		if(hasWeapon())
		{
			weapon.render(g);
		}
	}
	
	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}
	
	public void attack()
	{
		// Water phase attack
		if(phase.id() == Phase.WATER)
		{
			if(cheat)
			{
				double scale = Math.sqrt(Math.pow(stage.getMouseY() - getY(), 2) + Math.pow(stage.getMouseX() - getX(), 2));
				double velX = Mortar.SPEED * (stage.getMouseX() - getX()) / scale;
				double velY = Mortar.SPEED * (stage.getMouseY() - getY()) / scale;
				
				stage.getProjectiles().add(new Mortar(this, getX() + velX * 0.25, getY() + velY * 0.4, velX, velY, Mortar.DAMAGE));
			}
			else
			{
				double scale = Math.sqrt(Math.pow(stage.getMouseY() - getY(), 2) + Math.pow(stage.getMouseX() - getX(), 2));
				double velX = WaterProjectile.SPEED * (stage.getMouseX() - getX()) / scale;
				double velY = WaterProjectile.SPEED * (stage.getMouseY() - getY()) / scale;
				
				stage.getProjectiles().add(new WaterProjectile(this, getX() + velX * 0.25, getY() + velY * 0.4, velX, velY, WaterProjectile.DAMAGE));
			}
			
			
		}
		// Ice phase attack
		else
		{
			weapon = new IceSword(this, IceSword.DAMAGE);
			// TODO Melee attack
		}
		
		phase.resetAttackTimer();
	}
	
	public void left()
	{
		if(isGrounded())
		{
			// Y velocity changed if on slope
			setVelX(getVelX() - ACCELERATION);
		}
		else
		{
			// Slower ACCELERATION in air
			setVelX(getVelX() - ACCELERATION * 0.75);
		}
		if(getVelX() < -MOVE_SPEED)
		{
			setVelX(-MOVE_SPEED);
		}
	}
	
	public void right()
	{
		if(isGrounded())
		{
			// Y velocity changed if on slope
			setVelX(getVelX() + ACCELERATION);
		}
		else
		{
			// Slower acceleration in air
			setVelX(getVelX() + ACCELERATION * 0.75);
		}
		if(getVelX() > MOVE_SPEED)
		{
			setVelX(MOVE_SPEED);
		}
	}
	
	public void jump()
	{
		if(isGrounded()) // TODO Move to PlayState when audio is implemented
		{
			setVelY(-JUMP_SPEED);
			removeGround();
		}
	}
	
	// Called every update
	public void crouch(boolean crouching)
	{
		if(crouching && canMove() && isGrounded() && stamina >= CROUCH_STAMINA_COST)
		{
			// When the player initially crouches
			if(!isCrouching() && stamina >= CROUCH_STAMINA_REQUIREMENT)
			{
				this.crouching = true;
				blocking = (phase.id() == Phase.ICE); // If in Ice phase, set blocking to true
				
				// TODO Change weapon animation
				if(!isFacingRight())
				{
					setAnimation(CROUCH_LEFT, Content.PLAYER_CROUCH_LEFT, -1);
				}
				else
				{
					setAnimation(CROUCH_RIGHT, Content.PLAYER_CROUCH_RIGHT, -1);
				}
			}

			if(isCrouching())
			{
				setStamina(stamina - CROUCH_STAMINA_COST);
			}
			else
			{
				this.crouching = false;
				this.blocking = false;
			}
		}
		else
		{
			this.crouching = false;
			this.blocking = false;
		}
	}
	
	public void shift()
	{
		if(phase.id() == Phase.WATER)
		{
			setAnimation(MIST, Content.PLAYER_MIST, 1);
			
			setVelX(0);
			setVelY(0);
			
			flying = true;
			dodging = true;
			
			canAttack(false);
			canMove(false);
			
			setStamina(stamina - SHIFT_STAMINA_COST);
		}
		else
		{
			// TODO Ice shift
		}
	}
	
	public boolean isActive()
	{
		return currentAnimation != 999; // TODO Create dying animation
	}
	
	public void die()
	{
		// Set animation
		dead = true; // Temporary
	}
	
	public void setHealth(int health)
	{
		// Reset delay for next health regeneration
		if(health < getMaxHealth())
		{
			healthRegenTime = stage.getTime() + HEALTH_REGEN_DELAY;
			
			// If damaged, the initial delay is 5 times as long
			if(health < getHealth())
			{
				healthRegenTime = stage.getTime() + HEALTH_REGEN_LONG_DELAY;
			}
		}
		
		super.setHealth(health);
	}
	
	public int getMaxMana()
	{
		return maxMana;
	}
	
	public int getMana()
	{
		return mana;
	}
	
	public void setMana(int mana)
	{
		if(mana < getMaxMana())
		{
			// Reset delay for next mana regeneration
			manaRegenTime = stage.getTime() + MANA_REGEN_DELAY;
			
			// If damaged, the initial delay is 5 times as long
			if(mana < getMana())
			{
				manaRegenTime = stage.getTime() + MANA_REGEN_LONG_DELAY;
			}
		}
		
		this.mana = mana;
		if(this.mana > maxMana)
		{
			this.mana = maxMana;
		}
	}
	
	public int getStamina()
	{
		return stamina;
	}
	
	public void setStamina(int stamina)
	{
		// Reset delay for next stamina regeneration
		if(stamina < BASE_STAMINA)
		{
			staminaRegenTime = stage.getTime() + STAMINA_REGEN_DELAY;
			
			// If damaged, the initial delay is 5 times as long
			if(stamina < getStamina())
			{
				staminaRegenTime = stage.getTime() + STAMINA_REGEN_LONG_DELAY;
			}
		}
		
		this.stamina = stamina;
		if(this.stamina > BASE_STAMINA)
		{
			this.stamina = BASE_STAMINA;
		}
	}
	
	public void setPhase(Phase phase)
	{
		this.phase = phase;
	}
	
	public void setMistDirection(int x, int y)
	{
		mistX = x;
		mistY = y;
	}
	
	public boolean isCrouching()
	{
		return crouching;
	}
}