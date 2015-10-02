package indigo.Weapon;

import java.awt.Graphics;
import java.awt.geom.Line2D;

import indigo.Entity.Entity;
import indigo.Entity.Player;
import indigo.Manager.Content;

public class Staff extends Weapon
{
	// Animations
	private final int IDLE_LEFT = 0;
	private final int IDLE_RIGHT = 1;
	private final int ATTACK_LEFT = 2;
	private final int ATTACK_RIGHT = 3;
	private final int CAST_LEFT = 4;
	private final int CAST_RIGHT = 5;

	public static final int DAMAGE = 0;

	public Staff(Entity user, int dmg)
	{
		super(user, dmg);

		if(user.isFacingRight())
		{
			setAnimation(IDLE_RIGHT, Content.STAFF_IDLE_RIGHT, -1);
		}
		else
		{
			setAnimation(IDLE_LEFT, Content.STAFF_IDLE_LEFT, -1);
		}
	}

	public void update()
	{
		animation.update();
		
		// Animation checking
		if(animation.hasPlayedOnce())
		{
			if(user.isFacingRight() && currentAnimation != IDLE_RIGHT)
			{
				setAnimation(IDLE_RIGHT, Content.STAFF_IDLE_RIGHT, -1);
			}
			else if(!user.isFacingRight() && currentAnimation != IDLE_LEFT)
			{
				setAnimation(IDLE_LEFT, Content.STAFF_IDLE_LEFT, -1);
			}
		}
		else if(user.isFacingRight())
		{
			int frame = animation.getFrame();
			switch(currentAnimation)
			{
				case ATTACK_LEFT:
					setAnimation(ATTACK_RIGHT, Content.STAFF_ATTACK_RIGHT, 8);
					animation.setFrame(frame);
					break;
				case IDLE_LEFT:
					setAnimation(IDLE_RIGHT, Content.STAFF_IDLE_RIGHT, -1);
					animation.setFrame(frame);
					break;
				case CAST_LEFT:
					setAnimation(CAST_RIGHT, Content.STAFF_CAST_RIGHT, 2);
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
				case ATTACK_RIGHT:
					setAnimation(ATTACK_LEFT, Content.STAFF_ATTACK_LEFT, 8);
					animation.setFrame(frame);
					break;
				case IDLE_RIGHT:
					setAnimation(IDLE_LEFT, Content.STAFF_IDLE_LEFT, -1);
					animation.setFrame(frame);
					break;
				case CAST_RIGHT:
					setAnimation(CAST_LEFT, Content.STAFF_CAST_LEFT, 2);
					animation.setFrame(frame);
					break;
				default:
					break;
			}
		}
	}

	public void render(Graphics g)
	{
		double xOffset = ((Player)user).getWeaponXOffset();
		double yOffset = ((Player)user).getWeaponYOffset();
		
		if(((Player)user).isCrouching())
		{
			g.drawImage(animation.getImage(), (int)(user.getX() + xOffset), (int)(user.getY() + yOffset), 100, 90, null);
		}
		else
		{
			g.drawImage(animation.getImage(), (int)(user.getX() + xOffset), (int)(user.getY() + yOffset), 100, 90, null);
		}
	}

	// Not used
	public void collide(Entity ent)
	{

	}

	public Line2D.Double getHitbox()
	{
		return null;
	}

	public void attack()
	{
		if(user.isFacingRight())
		{
			setAnimation(ATTACK_RIGHT, Content.STAFF_ATTACK_RIGHT, 8);
		}
		else
		{
			setAnimation(ATTACK_LEFT, Content.STAFF_ATTACK_LEFT, 8);
		}
	}

	public void cast()
	{
		if(user.isFacingRight())
		{
			setAnimation(CAST_RIGHT, Content.STAFF_CAST_RIGHT, 2);
		}
		else
		{
			setAnimation(CAST_LEFT, Content.STAFF_CAST_LEFT, 2);
		}
	}
	
	public void holdCast()
	{
		if((currentAnimation == CAST_LEFT || currentAnimation == CAST_RIGHT) && animation.getFrame() == 3)
		{
			animation.setFrame(3);
			animation.setCount(0);
		}
	}
}