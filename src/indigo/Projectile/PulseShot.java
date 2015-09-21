package indigo.Projectile;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.Content;

public class PulseShot extends Projectile
{
	private int timer;

	private final int DEFAULT = 0;
	private final int SPARK = 1; // TODO: Remove, only for electric ball test image

	public static final int DAMAGE = 0;
	public final static int WIDTH = 50;
	public final static int HEIGHT = 50;
	public final static double SPEED = 25; // TODO: Change WIDTH, HEIGHT, and SPEED to suit the Pulse Shot
	public final static int DURATION = 100;

	public PulseShot(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = false;
		flying = true;

		timer = DURATION;

		setAnimation(DEFAULT, Content.ELECTRIC_BALL, -1); // TODO: Change to be that of the pulse shot
	}

	public void update()
	{
		super.update();
		timer--;

		if(timer == 0)
		{
			dead = true;
		}
	}

	public void render(Graphics2D g)
	{
		// TODO: If necessary, change to be that of the pulse shot
		g.drawImage(animation.getImage(), (int)getX() - WIDTH / 2, (int)getY() - HEIGHT / 2, WIDTH, HEIGHT, null);
	}

	public Shape getHitbox()
	{
		// TODO: If necessary, change to be that of the pulse shot
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public void collide(Entity ent)
	{
		setAnimation(SPARK, Content.ELECTRIC_SPARK, 1); // TODO: Change to pulse animations

		if(!ent.isDodging())
		{
			// Adds 1.2 times projectile's velocity to entity velocity, does no damage
			ent.setVelX(ent.getVelX() + this.getVelX() * 1.2);
			ent.setVelY(ent.getVelY() + this.getVelY() * 1.2);

			// pulse shooting someone into a pit can give you exp
			ent.mark();

			// Do I have to worry about ground?
			ent.removeGround();
		}
	}

	// Not used
	public void collide(Wall wall)
	{
	}

	// Not used
	public boolean isActive()
	{
		return true;
	}

	// Not used
	public void die()
	{
	}
}
