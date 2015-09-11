package indigo.Melee;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import indigo.Entity.Entity;

public class IceSword extends Melee
{
	private ArrayList<Entity> entitiesHit = new ArrayList<Entity>();
	
	private final int length = 100; // 80
	private final int radialOffset = 20;
	private final int yOffset = -20;
	
	public static final int DAMAGE = 50;
	
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
		
		if(user.isFacingRight()){
			beginSwordX = (int)(user.getX() + radialOffset);
		}else{
			beginSwordX = (int)(user.getX() - radialOffset);
		}
		beginSwordY = (int)(user.getY() + yOffset);
		
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
		
		if(attackTime == 15) // TODO Animation.hasPlayedOnce()
		{
			user.removeWeapon();
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
	private double determineSwordAngle(double mouseX, double mouseY)
	{
		double Angle = 0.0;
		Angle = Math.toDegrees(Math.atan2(mouseY - user.getY(), mouseX - user.getX()));
		// System.out.println(Angle);
		return Angle;
	}
	
	// TODO Comment this code better. I hate trig.
	private double determineSlashAngle()
	{
		double swordSlashAngle = swordAngle;
		double angleOffset = 0.0;
		if(user.isFacingRight())
		{
			angleOffset = Math.toRadians(-((initialSwordAngle * 2.0) / 14.0));
			swordSlashAngle += angleOffset;
		}
		else
		{
			if(initialSwordAngle < -90)
			{
				angleOffset = Math.toRadians(-(((90 + (initialSwordAngle % 90)) * 2.0) / 14.0));
				swordSlashAngle += angleOffset;
			}
			else
			{
				angleOffset = Math.toRadians(-(((90 - (initialSwordAngle % 90)) * 2.0) / 14.0));
				swordSlashAngle -= angleOffset;
			}
		}
		return swordSlashAngle;
	}
	
	public void setSlashMode(boolean slash)
	{
		slashMode = slash;
	}
}
