package indigo.Weapon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import indigo.Entity.Entity;
import indigo.Entity.Harvester;

public class Saw extends Weapon
{
	private double beginX, beginY, endX, endY;

	public static final int DAMAGE = 2;
	private static final int DURATION = 10;

	public Saw(Entity user, int dmg)
	{
		super(user, dmg);
	}

	public void attack()
	{
		attackTime = -1;
	}

	public void update()
	{
		attackTime++;
		
		if(user.isFacingRight())
		{
			beginX = user.getX() + user.getWidth() / 2;
			beginY = user.getY();
			endX = beginX + Harvester.RANGE;
			endY = beginY;
		}
		else
		{
			beginX = user.getX() - user.getWidth() / 2;
			beginY = user.getY();
			endX = beginX - Harvester.RANGE;
			endY = beginY;
		}
	}

	public void render(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(10));
		g.drawLine((int)beginX, (int)beginY, (int)endX, (int)endY);
	}

	public void collide(Entity ent)
	{
		ent.mark();
		ent.setHealth(ent.getHealth() - DAMAGE);
	}

	public Line2D.Double getHitbox()
	{
		if(attackTime <= DURATION)
		{
			return new Line2D.Double(beginX, beginY, endX, endY);
		}
		return null;
	}
}