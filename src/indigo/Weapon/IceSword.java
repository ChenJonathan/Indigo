package indigo.Weapon;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import indigo.Entity.Entity;
import indigo.Entity.Player;
import indigo.Manager.ContentManager;

public class IceSword extends Weapon
{
	private ArrayList<Entity> entitiesHit = new ArrayList<Entity>();

	private final int length = 100;
	private final int radialOffset = 30;
	private final int verticalOffset = -10;

	private boolean attacking = false; // TODO Consider replacing with animation check
	private boolean whirlwind = false;

	private double swordAngle;
	private double renderAngle;
	private double angleOffset;
	private boolean slashMode = false;

	private final double SLASH_ANGLE = 120.0;

	private int beginSwordX = 0;
	private int beginSwordY = 0;
	private int endSwordX = 0;
	private int endSwordY = 0;

	// Animations
	private final int IDLE_LEFT = 0;
	private final int IDLE_RIGHT = 1;
	private final int DOWNSLASH_LEFT = 2;
	private final int DOWNSLASH_RIGHT = 3;
	private final int UPSLASH_LEFT = 4;
	private final int UPSLASH_RIGHT = 5;
	private final int STAB_LEFT = 6;
	private final int STAB_RIGHT = 7;

	public static final int DAMAGE = 25;
	public static final int SLASH_DURATION = 6;
	public static final int STAB_DAMAGE_MULTIPLIER = 2;
	public static final int WHIRLWIND_DAMAGE_MULTIPLIER = 4;

	public IceSword(Entity user, int dmg)
	{
		super(user, dmg);

		if(user.isFacingRight())
		{
			setAnimation(IDLE_RIGHT, ContentManager.getAnimation(ContentManager.ICE_SWORD_IDLE_RIGHT), -1);
		}
		else
		{
			setAnimation(IDLE_LEFT, ContentManager.getAnimation(ContentManager.ICE_SWORD_IDLE_LEFT), -1);
		}
	}

	public void update()
	{
		if(attacking)
		{
			attackTime++;
		}
		else
		{
			swordAngle = determineMouseAngle(stage.getMouseX(), stage.getMouseY());
		}

		animation.update();

		if(animation.hasPlayedOnce())
		{
			if(attacking)
			{
				attacking = false;
				attackTime = -1;
				entitiesHit.clear();
				swordAngle = determineMouseAngle(stage.getMouseX(), stage.getMouseY());
				if(user.canTurn())
				{
					user.setDirection(stage.getMouseX() > user.getX());
				}
			}

			if(user.isFacingRight())
			{
				setAnimation(IDLE_RIGHT, ContentManager.getAnimation(ContentManager.ICE_SWORD_IDLE_RIGHT), -1);
			}
			else
			{
				setAnimation(IDLE_LEFT, ContentManager.getAnimation(ContentManager.ICE_SWORD_IDLE_LEFT), -1);
			}
		}
		else if(user.isFacingRight() != (currentAnimation % 2 == 1))
		{
			if(user.isFacingRight())
			{
				int frame = animation.getFrame();
				switch(currentAnimation)
				{
					case IDLE_LEFT:
						setAnimation(IDLE_RIGHT, ContentManager.getAnimation(ContentManager.ICE_SWORD_IDLE_RIGHT), -1);
						break;
					case DOWNSLASH_LEFT:
						setAnimation(DOWNSLASH_RIGHT,
								ContentManager.getAnimation(ContentManager.ICE_SWORD_DOWNSLASH_RIGHT), 1);
						animation.setFrame(frame);
						break;
					case UPSLASH_LEFT:
						setAnimation(UPSLASH_RIGHT,
								ContentManager.getAnimation(ContentManager.ICE_SWORD_UPSLASH_RIGHT), 1);
						animation.setFrame(frame);
						break;
					case STAB_LEFT:
						setAnimation(STAB_RIGHT, ContentManager.getAnimation(ContentManager.ICE_SWORD_STAB_RIGHT), 2);
						animation.setFrame(frame);
						break;
					default:
						break;
				}
			}
			else
			{
				int frame = animation.getFrame();
				switch(currentAnimation)
				{
					case IDLE_RIGHT:
						setAnimation(IDLE_LEFT, ContentManager.getAnimation(ContentManager.ICE_SWORD_IDLE_LEFT), -1);
						break;
					case DOWNSLASH_RIGHT:
						setAnimation(DOWNSLASH_LEFT,
								ContentManager.getAnimation(ContentManager.ICE_SWORD_DOWNSLASH_LEFT), 1);
						animation.setFrame(frame);
						break;
					case UPSLASH_RIGHT:
						setAnimation(UPSLASH_LEFT, ContentManager.getAnimation(ContentManager.ICE_SWORD_UPSLASH_LEFT),
								1);
						animation.setFrame(frame);
						break;
					case STAB_RIGHT:
						setAnimation(STAB_LEFT, ContentManager.getAnimation(ContentManager.ICE_SWORD_STAB_LEFT), 2);
						animation.setFrame(frame);
						break;
					default:
						break;
				}
			}
		}

		/**
		 * When Slash Mode is activated, our sword angle will have an offset from the actual angle.
		 */
		if(slashMode)
		{
			if((swordAngle >= 0 && swordAngle < (Math.PI / 2))
					|| (swordAngle > Math.PI && swordAngle < (3 * Math.PI) / 2))
			{
				angleOffset = (SLASH_ANGLE / 2) - (SLASH_ANGLE * attackTime) / SLASH_DURATION;
			}
			else
			{
				angleOffset = -(SLASH_ANGLE / 2) + (SLASH_ANGLE * attackTime) / SLASH_DURATION;
			}
			angleOffset = Math.toRadians(angleOffset);
		}

		beginSwordX = (int)(user.getX() + (radialOffset * Math.cos(swordAngle + angleOffset)));
		beginSwordY = (int)(user.getY() + verticalOffset - (radialOffset * Math.sin(swordAngle + angleOffset)));
		endSwordX = (int)(beginSwordX + (length * Math.cos(swordAngle + angleOffset)));
		endSwordY = (int)(beginSwordY - (length * Math.sin(swordAngle + angleOffset)));

		// Calculating render angle - Placing this in render causes input delay issues
		if((swordAngle >= 0 && swordAngle < (Math.PI / 2)) || (swordAngle > Math.PI && swordAngle <= (3 * Math.PI) / 2))
		{
			renderAngle = -swordAngle % Math.PI;
		}
		else
		{
			renderAngle = -swordAngle % Math.PI + Math.PI;
		}

		// If Ice Chains is active and mouse is on other side of player
		if(!user.canTurn() && user.isFacingRight() != stage.getMouseX() >= user.getX())
		{
			renderAngle = -renderAngle;
		}
	}

	public void render(Graphics2D g)
	{
		double xOffset = ((Player)user).getWeaponXOffset();
		double yOffset = ((Player)user).getWeaponYOffset();

		if(user.isFacingRight())
		{
			g.rotate(renderAngle, user.getX() + xOffset + 20, user.getY() + yOffset + 75);
			g.drawImage(animation.getImage(), (int)(user.getX() + xOffset), (int)(user.getY() + yOffset), null);
			g.rotate(-renderAngle, user.getX() + xOffset + 20, user.getY() + yOffset + 75);
		}
		else
		{
			g.rotate(renderAngle, user.getX() + xOffset + 118, user.getY() + yOffset + 75);
			g.drawImage(animation.getImage(), (int)(user.getX() + xOffset), (int)(user.getY() + yOffset), null);
			g.rotate(-renderAngle, user.getX() + xOffset + 118, user.getY() + yOffset + 75);
		}
	}

	public void collide(Entity ent)
	{
		if(!entitiesHit.contains(ent))
		{
			ent.mark();
			if(whirlwind)
			{
				ent.setHealth(ent.getHealth() - damage * WHIRLWIND_DAMAGE_MULTIPLIER);
			}
			else if(slashMode)
			{
				ent.setHealth(ent.getHealth() - damage);
			}
			else
			{
				ent.setHealth(ent.getHealth() - damage * STAB_DAMAGE_MULTIPLIER);
			}
			entitiesHit.add(ent);
		}
	}

	public Line2D.Double getHitbox()
	{
		if(whirlwind)
		{
			return new Line2D.Double(user.getX() - user.getWidth() / 2, user.getY() - 40, user.getX() + user.getWidth()
					/ 2, user.getY() - 40);
		}
		else if(attacking)
		{
			return new Line2D.Double(beginSwordX, beginSwordY, endSwordX, endSwordY);
		}
		return null;
	}

	public void slash()
	{
		attacking = true;
		slashMode = true;

		swordAngle = determineMouseAngle(stage.getMouseX(), stage.getMouseY());

		user.setDirection(stage.getMouseX() > user.getX());
		if(user.isFacingRight())
		{
			if(swordAngle <= Math.PI)
			{
				setAnimation(DOWNSLASH_RIGHT, ContentManager.getAnimation(ContentManager.ICE_SWORD_DOWNSLASH_RIGHT), 1);
			}
			else
			{
				setAnimation(UPSLASH_RIGHT, ContentManager.getAnimation(ContentManager.ICE_SWORD_UPSLASH_RIGHT), 1);
			}
		}
		else
		{
			if(swordAngle <= Math.PI)
			{
				setAnimation(DOWNSLASH_LEFT, ContentManager.getAnimation(ContentManager.ICE_SWORD_DOWNSLASH_LEFT), 1);
			}
			else
			{
				setAnimation(UPSLASH_LEFT, ContentManager.getAnimation(ContentManager.ICE_SWORD_UPSLASH_LEFT), 1);
			}
		}
	}

	public void stab()
	{
		attacking = true;
		slashMode = false;
		angleOffset = 0;

		swordAngle = determineMouseAngle(stage.getMouseX(), stage.getMouseY());

		user.setDirection(stage.getMouseX() > user.getX());
		if(user.isFacingRight())
		{
			setAnimation(STAB_RIGHT, ContentManager.getAnimation(ContentManager.ICE_SWORD_STAB_RIGHT), 2);
		}
		else
		{
			setAnimation(STAB_LEFT, ContentManager.getAnimation(ContentManager.ICE_SWORD_STAB_LEFT), 2);
		}
	}

	/**
	 * Determines the current angle of the mouse to the player coordinate, and translates the angle to a unit circle.
	 * 
	 * @param xCoord The X Coordinate of the Mouse
	 * @param yCoord The Y Coordinate of the Mouse
	 * @return The angle (0 to 360) of the mouse coordinates to the player (Radians)
	 */
	public double determineMouseAngle(double xCoord, double yCoord)
	{
		double mouseAngle = 0.0;
		mouseAngle = -Math.atan2((yCoord - user.getY()), (xCoord - user.getX()));
		mouseAngle = (mouseAngle < 0? mouseAngle + 2 * Math.PI : mouseAngle);
		mouseAngle = mouseAngle % (2 * Math.PI);
		return mouseAngle;
	}

	public boolean isAttacking()
	{
		return attacking;
	}

	public void setWhirlwind(boolean whirlwind)
	{
		this.whirlwind = whirlwind;
	}
}