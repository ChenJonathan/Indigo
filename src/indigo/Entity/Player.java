package indigo.Entity;

import indigo.Landscape.Land;
import indigo.Manager.ContentManager;
import indigo.Manager.SoundManager;
import indigo.Phase.Phase;
import indigo.Projectile.FrostOrb;
import indigo.Projectile.WaterBolt;
import indigo.Stage.Stage;
import indigo.Weapon.IceSword;
import indigo.Weapon.Staff;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Player extends Entity
{
	private int mana, maxMana;
	private int stamina, maxStamina;
	private boolean crouching;

	private int jumpTime;

	// Phase related mechanics
	private Phase phase;
	private boolean doubleJumpReady;
	private boolean iceArmor;

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
	private final int BLOCK_LEFT = 8;
	private final int BLOCK_RIGHT = 9;
	private final int MIST = 10;
	private final int WHIRLWIND_LEFT = 11;
	private final int WHIRLWIND_RIGHT = 12;
	private final int DEATH_LEFT = 13;
	private final int DEATH_RIGHT = 14;

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

	// Amount regenerated
	public static final int HEALTH_REGEN = 0;
	public static final int MANA_REGEN = 1;
	public static final int STAMINA_REGEN = 1;

	// Time between each regeneration
	public static final int HEALTH_REGEN_DELAY = 15;
	public static final int MANA_REGEN_DELAY = 3;
	public static final int STAMINA_REGEN_DELAY = 1;

	// Time until next regeneration after corresponding value is lowered (through damage, skillcasting, or blocking)
	public static final int HEALTH_REGEN_LONG_DELAY = 150;
	public static final int MANA_REGEN_LONG_DELAY = 150;
	public static final int STAMINA_REGEN_LONG_DELAY = 10;

	public Player(Stage stage, double x, double y, int health, int mana, int stamina)
	{
		super(stage, x, y, health, 0);

		width = PLAYER_WIDTH;
		height = PLAYER_HEIGHT;

		maxMana = this.mana = mana;
		maxStamina = this.stamina = stamina;

		healthRegenTime = manaRegenTime = staminaRegenTime = -1;

		pushability = 5;
		solid = true;
		flying = false;
		frictionless = false;

		jumpTime = 0;
		doubleJumpReady = false;
		iceArmor = false;

		friendly = true;

		weapon = new Staff(this, Staff.DAMAGE);

		setAnimation(JUMP_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_JUMP_LEFT), -1);
	}

	public void update()
	{
		super.update();

		// Set direction
		if(canTurn() && !hasWeaponHitbox())
		{
			setDirection(stage.getMouseX() > this.getX());
		}

		// Update weapon
		if(hasWeapon())
		{
			weapon.update();
		}

		// Animation related activities
		if(currentAnimation == MIST)
		{
			if(animation.hasPlayed(1))
			{
				animation.setReverse(true);
				while(animation.getFrame() == 0)
				{
					animation.update();
				}
			}
			else if(animation.hasPlayed(3))
			{
				canAttack(true);
				canMove(true);
				canTurn(true);

				dodging = false;
				flying = false;
				frictionless = false;
				solid = true;
			}
		}
		else if(currentAnimation == WHIRLWIND_LEFT || currentAnimation == WHIRLWIND_RIGHT)
		{
			if(animation.hasPlayed(2))
			{
				canAttack(true);
				canMove(true);
				canTurn(true);

				dodging = false;

				((IceSword)weapon).setWhirlwind(false);
			}
		}
		else if(currentAnimation == DEATH_LEFT || currentAnimation == DEATH_RIGHT)
		{
			if(animation.hasPlayed(1))
			{
				if(stage.isSuddenDeath())
				{
					dead = true;
				}
				else
				{
					flying = true;
					solid = false;

					setX(stage.getStartingX());
					setY(stage.getStartingY());
					removeGround();

					setVelX(0);
					setVelY(0);

					animation.setReverse(true);
					while(animation.getFrame() == 0)
					{
						animation.update();
					}
				}
				return;
			}
			else if(animation.hasPlayed(3))
			{
				if(phase.id() == Phase.WATER)
				{
					weapon = new Staff(this, Staff.DAMAGE);
				}
				else if(phase.id() == Phase.ICE)
				{
					weapon = new IceSword(this, IceSword.DAMAGE);
				}

				canAttack(true);
				canMove(true);
				canTurn(true);

				setHealth(getMaxHealth());
				setMana(getMaxMana());
				setStamina(getMaxStamina());

				flying = false;
				solid = true;

				// Forces animation reset
				currentAnimation = -1;
			}
			else
			{
				return;
			}
		}

		// Variable jump height counter
		if(jumpTime > 0)
		{
			jumpTime--;
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
				if(isFacingRight() && currentAnimation != CROUCH_RIGHT && currentAnimation != BLOCK_RIGHT)
				{
					if(phase.id() == Phase.WATER)
					{
						setAnimation(CROUCH_RIGHT, ContentManager.getAnimation(ContentManager.PLAYER_CROUCH_RIGHT), -1);
					}
					else if(iceArmor)
					{
						setAnimation(BLOCK_RIGHT, ContentManager.getAnimation(ContentManager.PLAYER_BLOCK_RIGHT_ARMOR),
								-1);
					}
					else
					{
						setAnimation(BLOCK_RIGHT, ContentManager.getAnimation(ContentManager.PLAYER_BLOCK_RIGHT), -1);
					}
				}
				else if(!isFacingRight() && currentAnimation != CROUCH_LEFT && currentAnimation != BLOCK_LEFT)
				{
					if(phase.id() == Phase.WATER)
					{
						setAnimation(CROUCH_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_CROUCH_LEFT), -1);
					}
					else if(iceArmor)
					{
						setAnimation(BLOCK_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_BLOCK_LEFT_ARMOR),
								-1);
					}
					else
					{
						setAnimation(BLOCK_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_BLOCK_LEFT), -1);
					}
				}
			}
			else if(ground == null)
			{
				if(isFacingRight() && currentAnimation != JUMP_RIGHT)
				{
					if(iceArmor)
					{
						setAnimation(JUMP_RIGHT, ContentManager.getAnimation(ContentManager.PLAYER_JUMP_RIGHT_ARMOR),
								-1);
					}
					else
					{
						setAnimation(JUMP_RIGHT, ContentManager.getAnimation(ContentManager.PLAYER_JUMP_RIGHT), -1);
					}
				}
				else if(!isFacingRight() && currentAnimation != JUMP_LEFT)
				{
					if(iceArmor)
					{
						setAnimation(JUMP_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_JUMP_LEFT_ARMOR), -1);
					}
					else
					{
						setAnimation(JUMP_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_JUMP_LEFT), -1);
					}
				}
			}
			else if(getVelX() == 0)
			{
				if(isFacingRight() && currentAnimation != GROUND_RIGHT)
				{
					if(iceArmor)
					{
						setAnimation(GROUND_RIGHT, ContentManager.getAnimation(ContentManager.PLAYER_IDLE_RIGHT_ARMOR),
								3);
					}
					else
					{
						setAnimation(GROUND_RIGHT, ContentManager.getAnimation(ContentManager.PLAYER_IDLE_RIGHT), 3);
					}
				}
				else if(!isFacingRight() && currentAnimation != GROUND_LEFT)
				{
					if(iceArmor)
					{
						setAnimation(GROUND_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_IDLE_LEFT_ARMOR), 3);
					}
					else
					{
						setAnimation(GROUND_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_IDLE_LEFT), 3);
					}
				}
			}
			else if(isFacingRight())
			{
				if(currentAnimation != MOVE_RIGHT)
				{
					if(iceArmor)
					{
						setAnimation(MOVE_RIGHT, ContentManager.getAnimation(ContentManager.PLAYER_MOVE_RIGHT_ARMOR), 2);
					}
					else
					{
						setAnimation(MOVE_RIGHT, ContentManager.getAnimation(ContentManager.PLAYER_MOVE_RIGHT), 2);
					}
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
					if(iceArmor)
					{
						setAnimation(MOVE_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_MOVE_LEFT_ARMOR), 2);
					}
					else
					{
						setAnimation(MOVE_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_MOVE_LEFT), 2);
					}
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

	public void render(Graphics2D g)
	{
		if(currentAnimation == WHIRLWIND_LEFT || currentAnimation == WHIRLWIND_RIGHT)
		{
			g.drawImage(animation.getImage(), (int)(getX() - 100), (int)(getY() - 60), null);
		}
		else
		{
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), null);
		}

		if(hasWeapon() && currentAnimation != MIST && currentAnimation != WHIRLWIND_LEFT
				&& currentAnimation != WHIRLWIND_RIGHT)
		{
			weapon.render(g);
		}

		super.render(g);
	}

	public double getWeaponXOffset()
	{
		if(isFacingRight())
		{
			return phase.id() == Phase.WATER? -30 : -26;
		}
		else
		{
			return phase.id() == Phase.WATER? -70 : -112;
		}
	}

	public double getWeaponYOffset()
	{
		double yOffset = 0;
		if(isCrouching())
		{
			return phase.id() == Phase.WATER? -24 : -76;
		}
		else
		{
			yOffset -= 54;
			if(currentAnimation == GROUND_LEFT || currentAnimation == GROUND_RIGHT)
			{
				switch(animation.getFrame())
				{
					case 0:
						break;
					case 1:
						yOffset += 1;
						break;
					case 2:
						yOffset += 2;
						break;
					case 3:
						yOffset += 4;
						break;
					case 4:
						yOffset += 6;
						break;
					case 5:
						yOffset += 2;
						break;
					default:
						break;
				}
			}
			else if(currentAnimation == MOVE_LEFT || currentAnimation == MOVE_RIGHT)
			{
				switch(animation.getFrame())
				{
					case 0:
						break;
					case 1:
						yOffset += 2;
						break;
					case 2:
						break;
					case 3:
						yOffset -= 2;
						break;
					case 4:
						yOffset -= 1;
						break;
					case 5:
						yOffset += 2;
						break;
					case 6:
						break;
					case 7:
						yOffset -= 1;
						break;
					default:
						break;
				}
			}
		}
		return phase.id() == Phase.WATER? yOffset : yOffset - 48;
	}

	public String getName()
	{
		return "yourself";
	}

	public Shape getHitbox()
	{
		if(isCrouching())
		{
			return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2 + 25, getWidth(),
					getHeight() - 25);
		}
		else
		{
			return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
		}
	}

	public void attackMain()
	{
		if(phase.id() == Phase.WATER)
		{
			// Water phase attack
			double angle = Math.atan2(stage.getMouseY() - getY(), stage.getMouseX() - getX());
			angle += isFacingRight()? -Math.PI / 8 : Math.PI / 8;
			double staffX = getX() + Math.cos(angle) * 69;
			double staffY = getY() + Math.sin(angle) * 69;

			double scale = Math.sqrt(Math.pow(stage.getMouseY() - staffY, 2) + Math.pow(stage.getMouseX() - staffX, 2));
			double velX = WaterBolt.SPEED * (stage.getMouseX() - staffX) / scale;
			double velY = WaterBolt.SPEED * (stage.getMouseY() - staffY) / scale;

			stage.getProjectiles().add(new WaterBolt(this, staffX - velX, staffY - velY, velX, velY, WaterBolt.DAMAGE));

			((Staff)weapon).attack();

			phase.setAttackTimer(isCrouching()? 3 : 6);

			SoundManager.play(ContentManager.WATER_BOLT_EFFECT);
		}
		else
		{
			// Ice phase attack
			((IceSword)weapon).slash();

			phase.setAttackTimer(10);

			SoundManager.play(ContentManager.SLASH_EFFECT);
		}
	}

	public void attackAlt()
	{
		if(phase.id() == Phase.WATER)
		{
			// Water phase attack
			double angle = Math.atan2(stage.getMouseY() - getY(), stage.getMouseX() - getX());
			angle += isFacingRight()? -Math.PI / 8 : Math.PI / 8;
			double staffX = getX() + Math.cos(angle) * 69;
			double staffY = getY() + Math.sin(angle) * 69;

			double scale = Math.sqrt(Math.pow(stage.getMouseY() - staffY, 2) + Math.pow(stage.getMouseX() - staffX, 2));
			double velX = FrostOrb.SPEED * (stage.getMouseX() - staffX) / scale;
			double velY = FrostOrb.SPEED * (stage.getMouseY() - staffY) / scale;

			stage.getProjectiles().add(new FrostOrb(this, staffX - velX, staffY - velY, velX, velY, FrostOrb.DAMAGE));

			((Staff)weapon).attack();

			phase.setAttackTimer(isCrouching()? 20 : 40);

			SoundManager.play(ContentManager.FROST_ORB_EFFECT);
		}
		else
		{
			// Ice phase attack
			((IceSword)weapon).stab();

			phase.setAttackTimer(20);

			SoundManager.play(ContentManager.STAB_EFFECT);
		}
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
				// Slower acceleration and maximum move speed when moving backwards
				setVelX(Math.max(getVelX() - REDUCED_ACCELERATION, -REDUCED_MOVE_SPEED));
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
				// Slower acceleration and maximum move speed when moving backwards in air
				setVelX(Math.max(getVelX() - REDUCED_ACCELERATION, -REDUCED_MOVE_SPEED));
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
		return canMove() && isGrounded() && !blocking && currentAnimation != JUMP_LEFT
				&& currentAnimation != JUMP_RIGHT;
	}

	public void jump()
	{
		setVelY(-INITIAL_JUMP_SPEED);

		if(isCrouching())
		{
			uncrouch();
		}

		jumpTime = JUMP_TIME;

		SoundManager.play(ContentManager.JUMP_EFFECT);
	}

	public boolean canJumpMore()
	{
		return canMove() && jumpTime > 0;
	}

	public void jumpMore()
	{
		setVelY(getVelY() - JUMP_INCREMENT);
	}

	public boolean canCrouch()
	{
		return canMove() && !isCrouching() && stamina >= CROUCH_STAMINA_REQUIREMENT;
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

	public void shift()
	{
		if(phase.id() == Phase.WATER)
		{
			canAttack(false);
			canMove(false);
			canTurn(false);

			setVelX(0);
			setVelY(0);

			removeGround();

			dodging = true;
			flying = true;
			frictionless = true;
			solid = false;

			setAnimation(MIST, ContentManager.getAnimation(ContentManager.PLAYER_MIST), 1);
		}
		else if(phase.id() == Phase.ICE)
		{
			canAttack(false);
			canMove(false);
			canTurn(false);

			setVelX(0);
			setVelY(0);

			((IceSword)weapon).setWhirlwind(true);

			dodging = true;

			jump();
			jumpMore();

			if(isFacingRight())
			{
				if(iceArmor)
				{
					setAnimation(WHIRLWIND_RIGHT,
							ContentManager.getAnimation(ContentManager.PLAYER_WHIRLWIND_RIGHT_ARMOR), 1);
				}
				else
				{
					setAnimation(WHIRLWIND_RIGHT, ContentManager.getAnimation(ContentManager.PLAYER_WHIRLWIND_RIGHT), 1);
				}
			}
			else
			{
				if(iceArmor)
				{
					setAnimation(WHIRLWIND_LEFT,
							ContentManager.getAnimation(ContentManager.PLAYER_WHIRLWIND_LEFT_ARMOR), 1);
				}
				else
				{
					setAnimation(WHIRLWIND_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_WHIRLWIND_LEFT), 1);
				}
			}
		}
	}

	public boolean shifted()
	{
		if(phase.id() == Phase.WATER)
		{
			return currentAnimation == MIST && animation.hasPlayed(2);
		}
		else
		{
			return (currentAnimation == WHIRLWIND_LEFT || currentAnimation == WHIRLWIND_RIGHT)
					&& animation.hasPlayedOnce() && animation.getFrame() == 7;
		}
	}

	public boolean isActive()
	{
		return currentAnimation != DEATH_LEFT && currentAnimation != DEATH_RIGHT;
	}

	public void die()
	{
		uncrouch();
		removeWeapon();

		canAttack(false);
		canMove(false);
		canTurn(false);

		flying = true;
		if(currentAnimation == MIST)
		{
			dodging = false;
			frictionless = false;
			solid = true;
		}

		setVelX(0);
		setVelY(0);

		if(phase.skillSelected())
		{
			phase.deselectSkill();
		}

		if(isFacingRight())
		{
			if(iceArmor)
			{
				setAnimation(DEATH_RIGHT, ContentManager.getAnimation(ContentManager.PLAYER_DEATH_RIGHT_ARMOR), 2);
			}
			else
			{
				setAnimation(DEATH_RIGHT, ContentManager.getAnimation(ContentManager.PLAYER_DEATH_RIGHT), 2);
			}
		}
		else
		{
			if(iceArmor)
			{
				setAnimation(DEATH_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_DEATH_LEFT_ARMOR), 2);
			}
			else
			{
				setAnimation(DEATH_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_DEATH_LEFT), 2);
			}
		}

		setHealth(0);

		SoundManager.play(ContentManager.DEATH_EFFECT);
	}

	public void setHealth(int health)
	{
		// Deducts mana instead of health if IceArmor skill is active
		if(iceArmor && getMana() > 0 && health < getHealth())
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

	public void setMaxMana(int mana)
	{
		maxMana = mana;
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
			manaRegenTime = Math.max(manaRegenTime, stage.getTime() + MANA_REGEN_DELAY);

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

	public int getMaxStamina()
	{
		return maxStamina;
	}

	public int getStamina()
	{
		return stamina;
	}

	public void setStamina(int stamina)
	{
		// Reset delay for next stamina regeneration
		if(stamina < getMaxStamina())
		{
			staminaRegenTime = Math.max(staminaRegenTime, stage.getTime() + STAMINA_REGEN_DELAY);

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

		if(phase.id() == Phase.WATER)
		{
			weapon = new Staff(this, Staff.DAMAGE);
			iceArmor = false;

			if(isCrouching())
			{
				if(isFacingRight())
				{
					setAnimation(CROUCH_RIGHT, ContentManager.getAnimation(ContentManager.PLAYER_CROUCH_RIGHT), -1);
				}
				else
				{
					setAnimation(CROUCH_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_CROUCH_LEFT), -1);
				}
			}
		}
		else
		{
			weapon = new IceSword(this, IceSword.DAMAGE);

			if(isCrouching())
			{
				if(isFacingRight())
				{
					setAnimation(BLOCK_RIGHT, ContentManager.getAnimation(ContentManager.PLAYER_BLOCK_RIGHT), -1);
				}
				else
				{
					setAnimation(BLOCK_LEFT, ContentManager.getAnimation(ContentManager.PLAYER_BLOCK_LEFT), -1);
				}
			}
		}
	}

	public void setGround(Land ground)
	{
		super.setGround(ground);
		doubleJumpReady = true;
	}

	public void canMove(boolean canMove)
	{
		super.canMove(canMove);
		if(!canMove)
		{
			uncrouch();
		}
	}

	public boolean isCrouching()
	{
		return crouching;
	}

	public boolean canDoubleJump()
	{
		return canMove() && !blocking && phase.id() == Phase.ICE && doubleJumpReady;
	}

	public void canDoubleJump(boolean canDoubleJump)
	{
		doubleJumpReady = canDoubleJump;
	}

	public void setIceArmor(boolean active)
	{
		iceArmor = active;
		if(isActive())
		{
			currentAnimation = -1; // Forces animation reset
		}
	}
}