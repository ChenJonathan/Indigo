package indigo.Weapon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import indigo.Entity.Entity;

public class IceSword extends Weapon
{
	private ArrayList<Entity> entitiesHit = new ArrayList<Entity>();

	private final int length = 100;
	private final int radialOffset = 20;
	private final int yOffset = -20;

	public static final int DAMAGE = 50;
	public static final int ATTACK_DURATION = 8;
	
	private boolean attacking = false; // TODO Replace with animation check
	
	private double swordAngle;
	private double angleOffset = 0.0;
	private boolean slashMode = false;
	private final double slashAngle = 45.0;

	private int beginSwordX = 0;
	private int beginSwordY = 0;
	private int endSwordX = 0;
	private int endSwordY = 0;

	public IceSword(Entity user, int dmg)
	{
		super(user, dmg);
	}

	public void update()
	{
		if(attacking)
		{
			attackTime++;
		}

		if(attackTime == 0)
		{
			swordAngle = determineMouseAngle(stage.getMouseX(), stage.getMouseY());
			angleOffset = 0.0;
		}
		else if(attackTime == ATTACK_DURATION) // TODO Animation.hasPlayedOnce()
		{
			attacking = false;
			attackTime = -1;
			entitiesHit.clear();
		}

		/**
		 * When Slash Mode is activated, our sword angle will have an offset from the actual angle.
		 */
		if(slashMode)
		{
			if((swordAngle > 0 && swordAngle < (Math.PI / 2)) || (swordAngle > Math.PI && swordAngle < (3 * Math.PI) / 2))
			{
				angleOffset = (attackTime == 0? slashAngle / 2 : (slashAngle / 2) + -(slashAngle * attackTime) / ATTACK_DURATION);
			}
			else
			{
				angleOffset = (attackTime == 0? -slashAngle / 2 : (slashAngle / 2) + (slashAngle * attackTime) / ATTACK_DURATION);
			}
			angleOffset = Math.toRadians(angleOffset);
		}

		beginSwordX = (int)(user.getX() + (radialOffset * Math.cos(swordAngle + angleOffset)));
		beginSwordY = (int)(user.getY() + yOffset - (radialOffset * Math.sin(swordAngle + angleOffset)));
		endSwordX = (int)(beginSwordX + (length * Math.cos(swordAngle + angleOffset)));
		endSwordY = (int)(beginSwordY - (length * Math.sin(swordAngle + angleOffset)));
	}

	public void render(Graphics g)
	{
		// Draws a simple line representing the sword // TODO Temporary
		g.setColor(Color.BLUE);
		g.drawLine(beginSwordX, beginSwordY, endSwordX, endSwordY);
	}

	public void collide(Entity ent)
	{
		if(!entitiesHit.contains(ent))
		{
			ent.mark();
			ent.setHealth(ent.getHealth() - damage);
			entitiesHit.add(ent);
		}
	}

	public Line2D.Double getHitbox()
	{
		if(attacking)
		{
			return new Line2D.Double(beginSwordX, beginSwordY, endSwordX, endSwordY);
		}
		return null;
	}
	
	public void slash()
	{
		attacking = true;
		slashMode = true;
	}
	
	public void stab()
	{
		attacking = true;
		slashMode = false;
	}

	/**
	 * Determines the current angle of the mouse to the player coordinate, and translates the angle to a unit circle.
	 * 
	 * @param xCoord The X Coordinate of the Mouse
	 * @param yCoord The Y Coordinate of the Mouse
	 * @return The angle (0, 360) of the mouse coordinates to the player radians
	 */
	public double determineMouseAngle(double xCoord, double yCoord)
	{
		double mouseAngle = 0.0;
		mouseAngle = -Math.atan2((yCoord - user.getY()), (xCoord - user.getX()));
		mouseAngle = (mouseAngle < 0 ? mouseAngle + 2 * Math.PI : mouseAngle);
		mouseAngle = mouseAngle % (2 * Math.PI);
		System.out.println(Math.toDegrees(mouseAngle));
		return mouseAngle;
	}
	
	public boolean isAttacking()
	{
		return attacking;
	}
}