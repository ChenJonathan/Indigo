package indigo.Interactive;

import indigo.Entity.Player;
import indigo.Manager.Animation;
import indigo.Stage.Respawnable;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

// Interacts with the player only
public abstract class Interactive implements Respawnable
{
	protected Stage stage;
	protected Player player;

	private double x, y;

	protected double width, height;

	protected Animation animation;
	protected int currentAnimation;

	protected boolean facingRight;
	protected boolean dead;

	// Subclasses - Initialize solid and flying
	public Interactive(Stage stage, double x, double y)
	{
		this.stage = stage;
		player = (Player)stage.getEntities().get(0);

		this.x = x;
		this.y = y;

		animation = new Animation();
	}

	protected void setAnimation(int count, BufferedImage[] images, int delay)
	{
		currentAnimation = count;
		animation.setFrames(images);
		animation.setDelay(delay);
	}

	public void update()
	{
		animation.update();
	}

	public abstract void render(Graphics2D g);

	public abstract void activate(Player player); // Does whatever the item is supposed to do upon collision

	public abstract Shape getHitbox();

	public abstract boolean isActive(); // Able to activate

	public abstract void die();

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double getWidth()
	{
		return width;
	}

	public double getHeight()
	{
		return height;
	}

	public boolean isFacingRight()
	{
		return facingRight;
	}

	public boolean isDead()
	{
		return dead;
	}

	// Do not use
	public void setDead()
	{
		dead = true;
	}

	public Stage getStage()
	{
		return stage;
	}
}