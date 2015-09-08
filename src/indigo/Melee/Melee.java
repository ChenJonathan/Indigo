package indigo.Melee;

import java.awt.Graphics;
import java.awt.geom.Line2D;

import indigo.Entity.Entity;

public abstract class Melee
{
	// TODO Don't hit enemies multiple times
	protected Entity user;
	
	protected int damage;
	protected int attackTime;
	
	public Melee(Entity user, int dmg)
	{
		this.user = user;
		damage = dmg;
		attackTime = -1;
	}
	
	public void update()
	{
		attackTime++;
	}
	
	public abstract void render(Graphics g);
	
	public abstract void collide(Entity ent);
	
	public abstract Line2D.Double getHitbox();
}