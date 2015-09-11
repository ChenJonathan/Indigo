package indigo.Melee;

import java.awt.Graphics;
import java.awt.geom.Line2D;

import indigo.Entity.Entity;
import indigo.Stage.Stage;

public abstract class Melee
{
	protected Stage stage;
	protected Entity user;
	
	protected int damage;
	protected int attackTime;
	
	public Melee(Entity user, int dmg)
	{
		stage = user.getStage();
		this.user = user;
		stage = user.getStage();
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