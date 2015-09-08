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
	
	public IceSword(Entity user, int dmg)
	{
		super(user, dmg);
	}
	
	public void update()
	{
		super.update();
		
		if(attackTime == 15) // TODO Animation.hasPlayedOnce()
		{
			user.removeWeapon();
		}
	}
	
	public void render(Graphics g)
	{
		// Draws a simple line representing the sword // TODO Temporary
		g.setColor(Color.RED);
		if(user.isFacingRight())
		{
			g.drawLine((int)user.getX() + 10, (int)user.getY() - 20, (int)user.getX() + 100, (int)user.getY() - 20);
		}
		else
		{
			g.drawLine((int)user.getX() - 10, (int)user.getY() - 20, (int)user.getX() - 100, (int)user.getY() - 20);
		}
	}
	
	public void collide(Entity ent)
	{
		if(!entitiesHit.contains(ent))
		{
			ent.setHealth(ent.getHealth() - damage);
			entitiesHit.add(ent);
		}
	}
	
	public Line2D.Double getHitbox()
	{
		if(user.isFacingRight())
		{
			return new Line2D.Double(user.getX() + 10, user.getY() - 20, user.getX() + 100, user.getY() - 20);
		}
		else
		{
			return new Line2D.Double(user.getX() - 10, user.getY() - 20, user.getX() - 100, user.getY() - 20);
		}
	}
}
