package indigo.Entity;

import indigo.Landscape.Land;
import indigo.Manager.Content;
import indigo.Phase.Phase;
import indigo.Projectile.WaterProjectile;
import indigo.Stage.Stage;
import indigo.Weapon.IceSword;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Player extends Entity
{
	private int mana, maxMana;
	private int stamina;
	private boolean crouching;

	private int jumpTime;

	// Phase related mechanics
	private Phase phase;
	private boolean canDoubleJump;
	private boolean iceArmor;
	private boolean iceChains;

	private int healthRegenTime; // Last time when health was regenerated
	private int manaRegenTime; // Last time when mana was regenerated
	private int staminaRegenTime; // Last time when stamina was regenerated

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
	private final int DEATH_LEFT = 9;
	private final int DEATH_RIGHT = 10;

	// Movement constants
	private final double ACCELERATION = 4;
	private final double REDUCED_ACCELERATION = 3; // Lower acceleration in air or when moving backwards
	private final double MOVE_SPEED = 20;
	private final double REDUCED_MOVE_SPEED = 16; // Lower maximum movement speed when moving backwards
	private final double INITIAL_JUMP_SPEED = 24;
	private final double JUMP_INCREMENT = 5;
	private final int JUMP_TIME = 4;

	public static final int PLAYER_WIDTH = 68;
	public static final int PLAYER_HEIGHT = 111;

	public static final int BASE_HEALTH = 200;
	public static final int BASE_MANA = 150;
	public static final int BASE_STAMINA = 100;

	// Stamina costs - Crouch stamina is the minimum stamina required to start crouching
	public static final int CROUCH_STAMINA_COST = 1;
	public static final int CROUCH_STAMINA_REQUIREMENT = 25;
	public static final int SHIFT_STAMINA_COST = 25;

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

	public Player(Stage stage, double x, double y, int health, int mana)
	{
		super(stage, x, y, health);
		name = "yourself";

		width = PLAYER_WIDTH;
		height = PLAYER_HEIGHT;

		maxMana = this.mana = mana;
		stamina = BASE_STAMINA;

		healthRegenTime = manaRegenTime = staminaRegenTime = -1;

		pushability = 5;
		flying = false;
		frictionless = false;

		jumpTime = 0;
		canDoubleJump = false;
		iceArmor = false;
		
		friendly = true;

		setAnimation(GROUND_RIGHT, Content.PLAYER_IDLE_RIGHT, 15);
	}

	public void update()
	{
		// Animation related activities
		if(currentAnimation == MIST)
		{
			if(animation.hasPlayedOnce())
			{
				flying = false;
				frictionless = false;
				dodging = false;

				canAttack(true);
				canMove(true);

				setVelX(0);
				setVelY(0);

				phase.resetAttackTimer();
			}
		}
		else if(currentAnimation == DEATH_LEFT || currentAnimation == DEATH_RIGHT)
		{
			animation.update();
			if(animation.hasPlayedOnce())
			{
				dead = true;
			}
			return;
		}

		// Set direction
		if(!hasWeapon() && currentAnimation != DEATH_LEFT && currentAnimation != DEATH_RIGHT) // TODO Change hasWeapon() call to weapon animation check
		{
			setDirection(stage.getMouseX() > this.getX());
		}

		super.update();
		
		// Variable jump height counter
		if(jumpTime > 0)
		{
			jumpTime--;
		}

		// Update weapon
		if(hasWeapon())
		{
			weapon.update();
		}

		// Crouching stamina drain
		if(isCrouching())
		{
			if(stamina > CROUCH_STAMINA_COST)
			{
				setStamina(stamina - CROUCH_STAMINA_COST);
			}
			else
			{
				uncrouch();
			}
		}

		// Default animations
		if(!dodging)
		{
			if(isCrouching())
			{
				if(isFacingRight() && currentAnimation != CROUCH_RIGHT)
				{
					setAnimation(CROUCH_RIGHT, Content.PLAYER_CROUCH_RIGHT, -1);
				}
				else if(!isFacingRight() && currentAnimation != CROUCH_LEFT)
				{
					setAnimation(CROUCH_LEFT, Content.PLAYER_CROUCH_LEFT, -1);
				}
			}
			else if(ground == null)
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
					setAnimation(GROUND_RIGHT, Content.PLAYER_IDLE_RIGHT, 3);
				}
				else if(!isFacingRight() && currentAnimation != GROUND_LEFT)
				{
					setAnimation(GROUND_LEFT, Content.PLAYER_IDLE_LEFT, 3);
				}
			}
			else if(isFacingRight())
			{
				if(currentAnimation != MOVE_RIGHT)
				{
					setAnimation(MOVE_RIGHT, Content.PLAYER_MOVE_RIGHT, 2);
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
					setAnimation(MOVE_LEFT, Content.PLAYER_MOVE_LEFT, 2);
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
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2),
				(int)(getY() - getHeight() / 2), (int)getWidth(),
				(int)getHeight(), null);

		if(hasWeapon())
		{
			weapon.render(g);
		}
	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY()
				- getHeight() / 2, getWidth(), getHeight());
	}

	public void attack()
	{
		// Water phase attack
		if(phase.id() == Phase.WATER)
		{
			double scale = Math.sqrt(Math.pow(stage.getMouseY() - getY(), 2)
					+ Math.pow(stage.getMouseX() - getX(), 2));
			double velX = WaterProjectile.SPEED * (stage.getMouseX() - getX())
					/ scale;
			double velY = WaterProjectile.SPEED * (stage.getMouseY() - getY())
					/ scale;

			stage.getProjectiles().add(
					new WaterProjectile(this, getX() + velX * 0.25, getY()
							+ velY * 0.4, velX, velY, WaterProjectile.DAMAGE));
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
			if(!isFacingRight() && getVelX() > -MOVE_SPEED)
			{
				// Moving forwards
				setVelX(Math.max(getVelX() - ACCELERATION, -MOVE_SPEED));
			}
			else if(isFacingRight() && getVelX() > -REDUCED_MOVE_SPEED)
			{
				// Slower acceleration and maximum move speed when moving
				// backwards
				setVelX(Math.max(getVelX() - REDUCED_ACCELERATION,
						-REDUCED_MOVE_SPEED));
			}
		}
		else
		{
			if(!isFacingRight() && getVelX() > -MOVE_SPEED)
			{
				// Slower acceleration when in air
				setVelX(Math.max(getVelX() - REDUCED_ACCELERATION, -MOVE_SPEED));
			}
			else if(isFacingRight() && getVelX() > -REDUCED_MOVE_SPEED)
			{
				// Slower acceleration and maximum move speed when moving
				// backwards in air
				setVelX(Math.max(getVelX() - REDUCED_ACCELERATION,
						-REDUCED_MOVE_SPEED));
			}
		}
	}

	public void right()
	{
		if(isGrounded())
		{
			if(isFacingRight() && getVelX() < MOVE_SPEED)
			{
				// Moving forwards
				setVelX(Math.min(getVelX() + ACCELERATION, MOVE_SPEED));
			}
			else if(!isFacingRight() && getVelX() < REDUCED_MOVE_SPEED)
			{
				// Slower acceleration and maximum move speed when moving backwards
				setVelX(Math.min(getVelX() + REDUCED_ACCELERATION, REDUCED_MOVE_SPEED));
			}
		}
		else
		{
			if(isFacingRight() && getVelX() < MOVE_SPEED)
			{
				// Slower acceleration when in air
				setVelX(Math.min(getVelX() + REDUCED_ACCELERATION, MOVE_SPEED));
			}
			else if(!isFacingRight() && getVelX() < REDUCED_MOVE_SPEED)
			{
				// Slower acceleration and maximum move speed when moving backwards in air
				setVelX(Math.min(getVelX() + REDUCED_ACCELERATION, REDUCED_MOVE_SPEED));
			}
		}
	}

	public boolean canJump()
	{
		return isGrounded();
	}

	public void jump()
	{
		setVelY(-INITIAL_JUMP_SPEED);
		removeGround();
		jumpTime = JUMP_TIME;
	}

	public boolean canJumpMore()
	{
		return jumpTime > 0;
	}

	public void jumpMore()
	{
		setVelY(getVelY() - JUMP_INCREMENT);
	}

	public boolean canCrouch()
	{
		return !isCrouching() && isGrounded() && canMove() && stamina >= CROUCH_STAMINA_REQUIREMENT;
	}

	public void crouch()
	{
		crouching = true;
		blocking = (phase.id() == Phase.ICE); // If in Ice phase, set blocking to true
	}

	public void uncrouch()
	{
		this.crouching = false;
		this.blocking = false;
	}

	public void shift(int x, int y) // Parameters represent player direction
	{
		if(phase.id() == Phase.WATER)
		{
			setAnimation(MIST, Content.PLAYER_MIST, 1);

			setVelX(x * 65);
			setVelY(y * 65);

			flying = true;
			frictionless = true;
			dodging = true;

			canAttack(false);
			canMove(false);
			
			uncrouch();

			setStamina(stamina - SHIFT_STAMINA_COST);
		}
		else
		{
			// TODO Ice charge
		}
	}

	public boolean isActive()
	{
		return currentAnimation != DEATH_LEFT && currentAnimation != DEATH_RIGHT;
	}

	public void die()
	{
		if(isFacingRight())
		{
			setAnimation(DEATH_RIGHT, Content.PLAYER_DEATH_RIGHT, 2);
		}
		else
		{
			setAnimation(DEATH_LEFT, Content.PLAYER_DEATH_LEFT, 2);
		}
	}

	public void setHealth(int health)
	{
		// Deducts mana instead of health if IceArmor skill is active
		if(iceArmor && getMana() > 0)
		{
			if(getMana() > (getHealth() - health))
			{
				setMana(getMana() - (getHealth() - health));
			}
			else
			{
				int damageBlocked = getMana();
				setMana(0);
				setHealth(health + damageBlocked);
			}
		}
		else
		{
			// Reset delay for next health regeneration
			if(health < getMaxHealth())
			{
				healthRegenTime = stage.getTime() + HEALTH_REGEN_DELAY;

				// If damaged, the initial delay is longer
				if(health < getHealth())
				{
					healthRegenTime = stage.getTime() + HEALTH_REGEN_LONG_DELAY;
				}
			}

			super.setHealth(health);
		}
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
			manaRegenTime = Math.max(manaRegenTime, stage.getTime()
					+ MANA_REGEN_DELAY);

			// If damaged, the initial delay is longer
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
			staminaRegenTime = Math.max(staminaRegenTime, stage.getTime()
					+ STAMINA_REGEN_DELAY);

			// If damaged, the initial delay is longer
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

	public void setGround(Land ground)
	{
		super.setGround(ground);
		canDoubleJump = true;
	}

	public boolean isCrouching()
	{
		return crouching;
	}

	public void setSlashMode(boolean slash)
	{
		((IceSword)weapon).setSlashMode(slash);
	}

	public boolean canDoubleJump()
	{
		return phase.id() == Phase.ICE && canDoubleJump;
	}

	public void canDoubleJump(boolean canDoubleJump)
	{
		this.canDoubleJump = canDoubleJump;
	}

	public boolean getIceArmor()
	{
		return iceArmor;
	}

	public void setIceArmor(boolean active)
	{
		iceArmor = active;
	}
	
	public boolean getIceChains()
	{
		return iceChains;
	}
	
	public void setIceChains(boolean active)
	{
		iceChains = active;
	}
}