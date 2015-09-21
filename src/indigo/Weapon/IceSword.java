/*package indigo.Weapon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import indigo.Entity.Entity;

public class IceSword extends Weapon
{
	private ArrayList<Entity> entitiesHit = new ArrayList<Entity>();
	
	private final int length = 100; // 80
	private final int radialOffset = 20;
	private final int yOffset = -20;
	
	public static final int DAMAGE = 50;
	public static final int ATTACK_DURATION = 8;
	
	private double swordAngle;
	private double initialSwordAngle;
	private boolean slashMode = false;
	
	private int beginSwordX = 0;
	private int beginSwordY = 0;
	private int endSwordX = 0;
	private int endSwordY = 0;
	
	public IceSword(Entity user, int dmg)
	{
		super(user, dmg);
		swordAngle = 0.0;
	}
	
	public void update()
	{
		super.update();
		
		if(attackTime == 0)
		{
			swordAngle = Math.toRadians(determineSwordAngle(stage.getMouseX(), stage.getMouseY()));
			initialSwordAngle = Math.toDegrees(swordAngle);
		}
		else if(attackTime == ATTACK_DURATION) // TODO Animation.hasPlayedOnce()
		{
			user.removeWeapon();
		}
		
		beginSwordX = (int)(user.getX() + (radialOffset * Math.cos(swordAngle)));
		beginSwordY = (int)(user.getY() + yOffset + (radialOffset * Math.sin(swordAngle)));
		
		if(slashMode)
		{
			endSwordX = (int)(beginSwordX + (length * Math.cos(swordAngle)));
			endSwordY = (int)(beginSwordY + (length * Math.sin(swordAngle)));
			swordAngle = determineSlashAngle();
		}
		else
		{
			endSwordX = (int)(beginSwordX + (length * Math.cos(swordAngle)));
			endSwordY = (int)(beginSwordY + (length * Math.sin(swordAngle)));
		}
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
		return new Line2D.Double(beginSwordX, beginSwordY, endSwordX, endSwordY);
	}
	
	/**
 * @param mouseX - The X coordinate of the mouse relative to the player
 * @param mouseY - The Y coordinate of the mouse relative to the player
 * @return The angle to draw the sword at (in degrees)
 */
/*private double determineSwordAngle(double mouseX, double mouseY)
 {
 double Angle = 0.0;
 Angle = Math.toDegrees(Math.atan2(mouseY - user.getY(), mouseX - user.getX()));
 return Angle;
 }

 // TODO Comment this code better. I hate trig.
 private double determineSlashAngle()
 {
 double swordSlashAngle = swordAngle;
 double angleOffset = 0.0;
 if(user.isFacingRight())
 {
 angleOffset = Math.toRadians(-((initialSwordAngle * 2.0) / (ATTACK_DURATION - 1)));
 swordSlashAngle += angleOffset;
 }
 else
 {
 if(initialSwordAngle < -90)
 {
 angleOffset = Math.toRadians(-(((90 + (initialSwordAngle % 90)) * 2.0) / (ATTACK_DURATION - 1)));
 swordSlashAngle += angleOffset;
 }
 else
 {
 angleOffset = Math.toRadians(-(((90 - (initialSwordAngle % 90)) * 2.0) / (ATTACK_DURATION - 1)));
 swordSlashAngle -= angleOffset;
 }
 }
 return swordSlashAngle;
 }

 public void setSlashMode(boolean slash)
 {
 slashMode = slash;
 }
 }*/

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

	private double swordAngle;
	private double angleOffset = 0.0;
	private boolean slashMode = false;
	private final double slashAngle = 90.0;

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
		super.update();

		if(attackTime == 0)
		{
			swordAngle = determineMouseAngle(stage.getMouseX(), stage.getMouseY());
			angleOffset = 0.0;
		}
		else if(attackTime == ATTACK_DURATION) // TODO Animation.hasPlayedOnce()
		{
			user.removeWeapon();
		}

		/**
		 * When Slash Mode is activated, our sword angle will have an offset from the actual angle.
		 */
		if(slashMode)
		{
			if((swordAngle > 0 && swordAngle < (Math.PI / 2))
					|| (swordAngle > Math.PI && swordAngle < (3 * Math.PI) / 2))
			{
				angleOffset = (attackTime == 0? slashAngle / 2 : -(slashAngle * attackTime) / (ATTACK_DURATION - 1));
			}
			else
			{
				angleOffset = (attackTime == 0? -slashAngle / 2 : (slashAngle * attackTime) / (ATTACK_DURATION - 1));
			}
			angleOffset = Math.toRadians(angleOffset);
		}

		beginSwordX = (int)(user.getX() + (radialOffset * Math.cos(swordAngle + angleOffset)));
		beginSwordY = (int)(user.getY() + yOffset + (radialOffset * Math.sin(swordAngle + angleOffset)));
		endSwordX = (int)(beginSwordX + (length * Math.cos(swordAngle + angleOffset)));
		endSwordY = (int)(beginSwordY + (length * Math.sin(swordAngle + angleOffset)));
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
		return new Line2D.Double(beginSwordX, beginSwordY, endSwordX, endSwordY);
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
		mouseAngle = Math.atan2((user.getY() - yCoord), (user.getX() - xCoord)) + Math.PI;
		System.out.println(mouseAngle);
		mouseAngle = mouseAngle % 360.0;
		return mouseAngle;
	}

	public void setSlashMode(boolean sM)
	{
		slashMode = sM;
	}
}