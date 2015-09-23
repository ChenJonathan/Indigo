package indigo.Projectile;

import indigo.Entity.Entity;
import indigo.Entity.Player;
import indigo.Landscape.Wall;
import indigo.Manager.Content;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class ManaPickup extends Projectile
{
	private final int DEFAULT = 0;

	protected Player player;

	public final static int DAMAGE = 10;
	public final static int WIDTH = 20;
	public final static int HEIGHT = 20;
	public final static double SPEED = 1;

	private int timer = 0;

	public ManaPickup(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = false;
		flying = true;

		setAnimation(DEFAULT, Content.MANA_PICKUP, 0);
	}

	public void update()
	{
		super.update();
	}

	public void render(Graphics2D g)
	{
		if (timer < 20) {
			g.drawImage(animation.getImage(), (int) getX(), (int) getY() + 1, WIDTH, HEIGHT, null);
			timer++;
		} else if (timer < 40) {
			g.drawImage(animation.getImage(), (int) getX(), (int) getY() - 1, WIDTH, HEIGHT, null);
			timer++;
		} else {
			timer = 0;
		}
	}

	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public void collide(Entity ent)
	{
		((Player)ent).setMana(((Player)ent).getMana() + 50); // TODO Reconsider this
		die();

	}

	// Not used
	public void collide(Wall wall) { }

	public boolean isActive()
	{
		return true;
	}

	public void die()
	{

		dead = true;
		setVelX(0);
		setVelY(0);

	}
}