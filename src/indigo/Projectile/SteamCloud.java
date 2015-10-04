package indigo.Projectile;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;

public class SteamCloud extends Projectile {
	
	private final int DEFAULT = 0;
	private final int DEATH = 1;

	public static final int DAMAGE = 0;
	public static final int WIDTH = 50;
	public static final int HEIGHT = 50;
	public static final double SPEED = 60;
	
	public SteamCloud(Entity entity, double x, double y, double velX, double velY, int dmg) {
		super(entity, x, y, velX, velY, dmg);
		
		width = WIDTH;
		height = HEIGHT;
		solid = true;
		flying = false;
	}
	public void render(Graphics2D g) {
		g.drawImage(animation.getImage(), (int)getX() - WIDTH / 2, (int)getY() - HEIGHT / 2, WIDTH, HEIGHT, null);
	}

	public Shape getHitbox() {
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public void collide(Entity ent) {
		//don't die
		ent.setVelY(ent.getVelY() - ent.getPushability() / 3);
		ent.setVelX(ent.getVelX() - ent.getPushability() / 3);
	}

	public void collide(Wall wall) {
		die();
	}

	public boolean isActive() {
		return true;
	}
}
