package indigo.Weapon;

import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import indigo.Entity.Entity;
import indigo.Manager.Animation;
import indigo.Stage.Stage;

public abstract class Weapon
{
	protected Stage stage;
	protected Entity user;

	protected int damage;
	protected int attackTime;

	protected Animation animation; // Used to render weapons
	protected int currentAnimation; // Current animation frame for the weapon

	public Weapon(Entity user, int dmg)
	{
		stage = user.getStage();
		this.user = user;
		stage = user.getStage();
		damage = dmg;
		attackTime = -1;
		
		animation = new Animation();
	}

	// Method used to change animation
	protected void setAnimation(int count, BufferedImage[] images, int delay)
	{
		currentAnimation = count;
		animation.setFrames(images);
		animation.setDelay(delay);
	}

	public abstract void update();

	public abstract void render(Graphics g);

	public abstract void collide(Entity ent);

	public abstract Line2D.Double getHitbox();
}