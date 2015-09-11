package indigo.Melee;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import indigo.Entity.Entity;

public class IceSword extends Melee
{
	private ArrayList<Entity> entitiesHit = new ArrayList<Entity>();
	
	public final int length = 80;
	public final int radialOffset = 10;
	public final int yOffset = -20;
	
	public static final int DAMAGE = 50;
	
	protected double swordAngle;
	private double initialSwordAngle;
	protected boolean beginningAttack = true;
	private boolean slashMode = false;
	
	protected int beginSwordX = 0;
	protected int beginSwordY = 0;
	protected int endSwordX = 0;
	protected int endSwordY = 0;
	
	public IceSword(Entity user, int dmg)
	{
		super(user, dmg);
		swordAngle = 0.0;
		slashMode = stage.rightClickPressed();
	}
	
	public void update()
	{
		super.update();
		
		if(beginningAttack == true){
			swordAngle = Math.toRadians(determineSwordAngle(stage.getMouseX(), stage.getMouseY()));
			initialSwordAngle = Math.toDegrees(swordAngle);
			beginningAttack = false;
		}
			
		beginSwordX = (int)(user.getX() + radialOffset);
		beginSwordY = (int)(user.getY() + yOffset);
		
		if(slashMode){
			endSwordX = (int)(beginSwordX + (length * Math.cos(swordAngle)));
			endSwordY = (int)(beginSwordY + (length * Math.sin(swordAngle)));
			swordAngle = determineSlashAngle();
		}else{
			endSwordX = (int)(beginSwordX + (length * Math.cos(swordAngle)));
			endSwordY = (int)(beginSwordY + (length * Math.sin(swordAngle)));
		}
		
		
		if(attackTime == 15) // TODO Animation.hasPlayedOnce()
		{
			user.removeWeapon();
			beginningAttack = true;
		}
	}
	
	public void render(Graphics g)
	{
		// Draws a simple line representing the sword // TODO Temporary
		g.setColor(Color.BLUE);
		g.drawLine(beginSwordX, beginSwordY, endSwordX, endSwordY);
		/*g.setColor(Color.RED);
		if(user.isFacingRight())
		{
			g.drawLine((int)user.getX() + 10, (int)user.getY() - 20, (int)user.getX() + 100, (int)user.getY() - 20);
		}
		else
		{
			g.drawLine((int)user.getX() - 10, (int)user.getY() - 20, (int)user.getX() - 100, (int)user.getY() - 20);
		}*/
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
		/*if(user.isFacingRight())
		{
			return new Line2D.Double(user.getX() + 10, user.getY() - 20, user.getX() + 100, user.getY() - 20);
		}
		else
		{
			return new Line2D.Double(user.getX() - 10, user.getY() - 20, user.getX() - 100, user.getY() - 20);
		}*/
	}
	
	/**
	 * 
	 * @param mouseX - The X coordinate of the mouse relative to the player
	 * @param mouseY - The Y coordinate of the mouse relative to the player
	 * @return The angle to draw the sword at (in degrees)
	 */
	private double determineSwordAngle(double mouseX, double mouseY){
		double Angle = 0.0;
		Angle = Math.toDegrees(Math.atan2(mouseY - user.getY(), mouseX - user.getX()));
		System.out.println(Angle);
		return Angle;
	}
	
	//TODO Comment this code better. I hate trig.
	private double determineSlashAngle(){
		double swordSlashAngle = swordAngle;
		double angleOffset = 0.0;
		if(user.isFacingRight()){
			angleOffset = Math.toRadians(-((initialSwordAngle * 2.0) / 14.0));
			swordSlashAngle += angleOffset;
		}else{
			if(initialSwordAngle < -90){
				angleOffset = Math.toRadians(-(((90 + (initialSwordAngle % 90)) * 2.0) / 14.0));
				swordSlashAngle += angleOffset;
			}else{
				angleOffset = Math.toRadians(-(((90 - (initialSwordAngle % 90)) * 2.0) / 14.0));
				swordSlashAngle -= angleOffset;
			}
		}
		return swordSlashAngle;
	}
}
