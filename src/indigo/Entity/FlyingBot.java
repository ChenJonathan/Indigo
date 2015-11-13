package indigo.Entity;

import indigo.Manager.ContentManager;
import indigo.Projectile.ElectricBall;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class FlyingBot extends Entity
{
	private double dx;
	private double dy;
	private double angle; // Cannon angle

	private int timer; // When timer hits zero, move and reset timer

	private final int DEFAULT = 0;
	private final int DEATH = 1;

	private final int MOVE_SPEED = 30;
	private final int MAX_RANGE = 1000;
	private final double ANGLE_BOUND = Math.PI / 4; // Angle from vertical that the cannon cannot shoot from

	public static final double FLYING_BOT_WIDTH = 60;
	public static final double FLYING_BOT_HEIGHT = 60;
	public static final int BASE_HEALTH = 50;
	public static final int BASE_EXPERIENCE = 20;
	public static final int DEFAULT_TIMER = 30;

	public FlyingBot(Stage stage, double x, double y)
	{
		this(stage, x, y, BASE_HEALTH, BASE_EXPERIENCE);
	}
	
	public FlyingBot(Stage stage, double x, double y, int health, int experience)
	{
		super(stage, x, y, health, experience);

		width = FLYING_BOT_WIDTH;
		height = FLYING_BOT_HEIGHT;

		pushability = 5;
		solid = true;
		flying = true;
		frictionless = false;

		friendly = false;

		setAngle();

		timer = DEFAULT_TIMER;

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.FLYING_BOT_IDLE), 3);
	}

	public void update()
	{
		if(currentAnimation == DEATH)
		{
			super.update();
			if(animation.hasPlayedOnce())
			{
				// Mark entity as dead if death animation has finished playing
				dead = true;
			}
			return;
		}

		super.update();

		if(timer == 0)
		{
			timer = DEFAULT_TIMER;

			if(Math.random() < 0.5)
			{
				// 50% chance to move every time the timer counts down
				move();
			}
		}
		else
		{
			timer--;
		}

		setAngle();

		if(canAttack() && Math.random() < 0.02)
		{
			// 2% chance to attack every tick
			attack();
		}
	}

	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), null);

		// Rotation breaks if x is negative
		if(getX() > 0 && getX() < stage.getMapX() && currentAnimation != DEATH)
		{
			g.rotate(angle, getX(), getY());
			g.drawImage(ContentManager.getImage(ContentManager.FLYING_BOT_CANNON), (int)(getX() + 15), (int)(getY() - 5.5), null);
			g.rotate(-angle, getX(), getY());
		}
		
		super.render(g);
	}
	
	public String getName()
	{
		return "a flying bot";
	}

	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public void attack()
	{
		// Scale is the distance from entity to player
		double scale = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		if(scale < MAX_RANGE)
		{
			// Splits the direction of the projectile into x and y components
			double velX = ElectricBall.SPEED * dx / scale;
			double velY = ElectricBall.SPEED * dy / scale;

			stage.getProjectiles().add(
					new ElectricBall(this, getX() + velX / 2, getY() + velY / 2, velX, velY, ElectricBall.DAMAGE));
		}
	}

	public void move()
	{
		double moveX = 0;
		double moveY = 0;
		boolean outOfBounds = false;

		do
		{
			double rand = 0;
			if(getY() <= 0)
			{
				// If above the map, only downwards movement is allowed
				outOfBounds = true;
				rand = Math.random() / 2 + 0.5;
			}
			else
			{
				rand = Math.random();
			}

			if(rand >= 0.25 || rand > 0.75) // If facing left
			{
				setDirection(false);
			}
			else
			// Facing right
			{
				setDirection(true);
			}

			// Splits movement into x and y components
			moveX = Math.cos(2 * rand * Math.PI) * MOVE_SPEED;
			moveY = Math.sin(2 * rand * Math.PI) * MOVE_SPEED;
		}
		while(getX() + 30 * moveX > stage.getMapX() || getX() + 30 * moveX < 0 || getY() + 30 * moveY > stage.getMapY()
				|| (getY() + 30 * moveY < 0 && !outOfBounds));
		// Repeat the loop if this movement would carry the entity outside of map boundaries

		setVelX(getVelX() + moveX);
		setVelY(getVelY() + moveY);
	}

	public void setAngle()
	{
		dx = stage.getPlayer().getX() - getX();
		dy = stage.getPlayer().getY() - getY();

		// Calculate cannon angle
		if(dx > 0)
		{
			angle = Math.atan(dy / dx);
		}
		else if(dx < 0)
		{
			if(dy >= 0)
			{
				angle = Math.atan(dy / dx) + Math.PI;
			}
			else
			{
				angle = Math.atan(dy / dx) - Math.PI;
			}
		}
		else
		{
			angle = dy / Math.abs(dy) * Math.PI / 2;
		}

		// Restricting angle
		if(angle <= Math.PI / 2 && angle > ANGLE_BOUND)
		{
			dx = Math.sqrt(2) / 2;
			dy = Math.sqrt(2) / 2;
			angle = ANGLE_BOUND;
		}
		else if(angle > Math.PI / 2 && angle < Math.PI / 2 + ANGLE_BOUND)
		{
			dx = -Math.sqrt(2) / 2;
			dy = Math.sqrt(2) / 2;
			angle = Math.PI / 2 + ANGLE_BOUND;
		}
		else if(angle > -Math.PI / 2 && angle < -ANGLE_BOUND)
		{
			dx = Math.sqrt(2) / 2;
			dy = -Math.sqrt(2) / 2;
			angle = -ANGLE_BOUND;
		}
		else if(angle <= -Math.PI / 2 && angle > -Math.PI / 2 - ANGLE_BOUND)
		{
			dx = -Math.sqrt(2) / 2;
			dy = -Math.sqrt(2) / 2;
			angle = -Math.PI / 2 - ANGLE_BOUND;
		}
	}

	public boolean isActive()
	{
		return currentAnimation != DEATH;
	}

	public void die()
	{
		flying = false;
		setVelX(0);
		setAnimation(DEATH, ContentManager.getAnimation(ContentManager.FLYING_BOT_DEATH), 4);
	}
}