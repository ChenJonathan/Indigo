package indigo.Entity;

import indigo.Interactive.Interactive;
import indigo.Landscape.Land;
import indigo.Landscape.Wall;
import indigo.Manager.Animation;
import indigo.Projectile.Projectile;
import indigo.Stage.Named;
import indigo.Stage.Respawnable;
import indigo.Stage.Stage;
import indigo.Weapon.Weapon;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class Entity implements Respawnable, Named
{
	protected Stage stage;

	private double x, y;
	private double velX, velY;

	private double prevX, prevY;
	private Line2D.Double travel; // Line formed by previous and current positions; used for collision checking

	protected double width, height;
	protected double pushability; // How much an entity moves when pushed

	private int health, maxHealth;
	private int experience;

	protected boolean facingRight;

	protected boolean dodging; // Prevents all collision
	protected boolean blocking; // Prevents (or halves) damage

	protected Land ground; // Ground that the entity is standing on (null if none or flying)
	protected Weapon weapon; // Melee weapon (null if none)

	private boolean canAttack;
	private boolean canMove;
	private boolean canTurn;

	protected boolean solid;
	protected boolean flying;
	protected boolean frictionless;

	protected boolean friendly;
	protected boolean marked;
	protected boolean dead;

	protected Animation animation; // Used to render entities
	protected int currentAnimation; // Current animation frame for the entity

	// Subclasses - Initialize name, width, height, solid, flying, frictionless, pushability, move speed, jump speed
	public Entity(Stage stage, double x, double y, int health, int experience)
	{
		this.stage = stage;

		velX = velY = 0;

		this.x = prevX = x;
		this.y = prevY = y;
		travel = new Line2D.Double(prevX, prevY, x, y); // Line formed by previous and current position

		maxHealth = this.health = health; // TODO Modify based on player level
		this.experience = experience;

		canAttack = canMove = canTurn = true;

		marked = false;

		animation = new Animation();
	}

	// Method used to change animation
	protected void setAnimation(int count, BufferedImage[] images, int delay)
	{
		currentAnimation = count;
		animation.setFrames(images);
		animation.setDelay(delay);
	}

	// Standard position updating stuff: gravity, friction, etc
	public void update()
	{
		// Prevents x or y velocity from being too extreme (glitching through walls)
		if(velX < -Stage.TERMINAL_VELOCITY)
		{
			velX = -Stage.TERMINAL_VELOCITY;
		}
		else if(velX > Stage.TERMINAL_VELOCITY)
		{
			velX = Stage.TERMINAL_VELOCITY;
		}
		if(velY < -Stage.TERMINAL_VELOCITY)
		{
			velY = -Stage.TERMINAL_VELOCITY;
		}
		else if(velY > Stage.TERMINAL_VELOCITY)
		{
			velY = Stage.TERMINAL_VELOCITY;
		}

		// If the entity is grounded, take the y position corresponding to the ground
		if(ground != null)
		{
			y = ground.getSurface(x, true) - getHeight() / 2;
			velY = 0;
		}

		prevX = x;
		prevY = y;

		x += velX;
		y += velY;

		// Friction and gravity
		if(flying)
		{
			if(!frictionless)
			{
				double vel = Math.sqrt(Math.pow(velX, 2) + Math.pow(velY, 2));

				// Flying entities gradually slow down
				if(vel > Stage.FRICTION)
				{
					if(velX < 0)
					{
						velX = velX + Stage.FRICTION * -velX / vel;
					}
					else if(velX > 0)
					{
						velX = velX - Stage.FRICTION * velX / vel;
					}
					if(velY < 0)
					{
						velY = velY + Stage.FRICTION * -velY / vel;
					}
					else if(velY > 0)
					{
						velY = velY - Stage.FRICTION * velY / vel;
					}
				}
				else
				{
					velX = 0;
					velY = 0;
				}
			}
		}
		else
		{
			if(!frictionless)
			{
				// Ground entities slow down horizontally
				if(velX < 0)
				{
					velX = Math.min(velX + Stage.FRICTION, 0);
				}
				else if(velX > 0)
				{
					velX = Math.max(velX - Stage.FRICTION, 0);
				}
			}

			// Applying gravity to ground entities
			if(ground == null)
			{
				velY += Stage.GRAVITY;
			}
		}

		updateTravelLine();

		animation.update();
	}

	public abstract void render(Graphics2D g); // Draws the entity

	public abstract Shape getHitbox(); // Returns hitbox

	public abstract boolean isActive(); // Returns whether the entity is dying or not

	public abstract void die(); // Triggers death animation

	// Used for entity-item collision
	public boolean intersects(Interactive item)
	{
		Area entArea = new Area(getHitbox());
		entArea.intersect(new Area(item.getHitbox()));
		return !entArea.isEmpty();
	}

	// Used for entity-entity collision
	public boolean intersects(Entity ent)
	{
		Area entArea = new Area(getHitbox());
		entArea.intersect(new Area(ent.getHitbox()));
		return !entArea.isEmpty();
	}

	// Used for entity-melee collision - Checks hitbox and travel line intersection
	public boolean intersects(Weapon weapon)
	{
		if(!(this instanceof Blockade))
		{
			Line2D.Double link = new Line2D.Double(getX(), getY(), weapon.getUser().getX(), weapon.getUser().getY());
			for(Wall wall : stage.getWalls())
			{
				if(link.intersectsLine(wall.getLine()))
				{
					return false;
				}
			}
		}

		boolean intersects = false;

		if(getHitbox() instanceof Rectangle2D.Double)
		{
			intersects = weapon.getHitbox().intersects((Rectangle2D.Double)getHitbox());
		}
		else if(getHitbox() instanceof Ellipse2D.Double)
		{
			intersects = weapon.getHitbox().ptSegDist(getX(), getY()) < getHeight() / 2;
		}

		return intersects || weapon.getHitbox().intersectsLine(travel);
	}

	// Used for entity-wall collision - Checks hitbox intersection
	public boolean intersects(Wall wall)
	{
		Area entArea = new Area(getHitbox());
		entArea.intersect(new Area(wall.getHitbox()));
		return !entArea.isEmpty() || wall.getLine().intersectsLine(travel);
	}

	// Used for entity-platform collision - Utilizes previous entity feet position
	public boolean feetAboveLand(Land land)
	{
		double deltaX = land.getLine().getP2().getX() - land.getLine().getP1().getX();
		// Formula to calculate if a point is located above the platform.getLine()
		double value = (land.getLine().getP2().getY() - land.getLine().getP1().getY())
				* (getPrevX() - land.getLine().getP1().getX())
				- (getPrevY() + getHeight() / 2 - land.getLine().getP1().getY())
				* (land.getLine().getP2().getX() - land.getLine().getP1().getX());
		return value * deltaX > 0;
	}

	// Used for entity-projectile collision - TODO Not foolproof (Consider adding travel-line check)
	public boolean intersects(Projectile proj)
	{
		Area entArea = new Area(getHitbox());
		entArea.intersect(new Area(proj.getHitbox()));
		if(!entArea.isEmpty())
		{
			Line2D.Double link = new Line2D.Double(getX(), getY(), proj.getX(), proj.getY());
			for(Wall wall : stage.getWalls())
			{
				if(link.intersectsLine(wall.getLine()))
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public abstract String getName();

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
		updateTravelLine();
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
		updateTravelLine();
	}

	public double getVelX()
	{
		return velX;
	}

	public void setVelX(double velX)
	{
		this.velX = velX;
		if(velX < -Stage.TERMINAL_VELOCITY)
		{
			this.velX = -Stage.TERMINAL_VELOCITY;
		}
		else if(velX > Stage.TERMINAL_VELOCITY)
		{
			this.velX = Stage.TERMINAL_VELOCITY;
		}
	}

	public double getVelY()
	{
		return velY;
	}

	public void setVelY(double velY)
	{
		this.velY = velY;
		if(velY < -Stage.TERMINAL_VELOCITY)
		{
			this.velY = -Stage.TERMINAL_VELOCITY;
		}
		else if(velY > Stage.TERMINAL_VELOCITY)
		{
			this.velY = Stage.TERMINAL_VELOCITY;
		}

		if(velY < 0)
		{
			removeGround();
		}
	}

	public double getPrevX()
	{
		return prevX;
	}

	public double getPrevY()
	{
		return prevY;
	}

	public Line2D.Double getTravelLine()
	{
		return travel;
	}

	public void updateTravelLine()
	{
		travel = new Line2D.Double(prevX, prevY, x, y);
	}

	public double getWidth()
	{
		return width;
	}

	public double getHeight()
	{
		return height;
	}

	public boolean isPushable()
	{
		return pushability > 0;
	}

	public double getPushability()
	{
		return pushability;
	}

	public int getMaxHealth()
	{
		return maxHealth;
	}

	public void setMaxHealth(int health)
	{
		maxHealth = health;
	}

	public int getHealth()
	{
		return health;
	}

	public void setHealth(int health)
	{
		this.health = health;
		if(this.health > maxHealth)
		{
			this.health = maxHealth;
		}
		else if(health <= 0)
		{
			this.health = 0;
			if(isActive())
			{
				die();
			}
		}
	}

	public int getExperience()
	{
		return experience;
	}

	public boolean isFacingRight()
	{
		return facingRight;
	}

	public void setDirection(boolean right)
	{
		facingRight = right;
	}

	public boolean isGrounded()
	{
		return ground != null;
	}

	public Land getGround()
	{
		return ground;
	}

	public void setGround(Land ground)
	{
		velY = 0;
		this.ground = ground;
	}

	public void removeGround()
	{
		ground = null;
	}

	public double getSlope()
	{
		if(ground != null)
		{
			return ground.getSlope();
		}
		return 0;
	}

	public boolean hasWeapon()
	{
		return weapon != null;
	}

	public boolean hasWeaponHitbox()
	{
		return hasWeapon() && weapon.getHitbox() != null;
	}

	public Weapon getWeapon()
	{
		return weapon;
	}

	public void removeWeapon()
	{
		weapon = null;
	}

	public boolean canAttack()
	{
		return canAttack;
	}

	public void canAttack(boolean canAttack)
	{
		this.canAttack = canAttack;
	}

	public boolean canMove()
	{
		return canMove;
	}

	public void canMove(boolean canMove)
	{
		this.canMove = canMove;
	}

	public boolean canTurn()
	{
		return canTurn;
	}

	public void canTurn(boolean canTurn)
	{
		this.canTurn = canTurn;
	}

	public boolean isSolid()
	{
		return solid;
	}

	public boolean isFlying()
	{
		return flying;
	}

	public boolean isFriendly()
	{
		return friendly;
	}

	public boolean isBlocking(boolean projDirection)
	{
		return blocking && facingRight != projDirection;
	}

	public boolean isDodging()
	{
		return dodging;
	}

	public boolean isMarked()
	{
		return marked;
	}

	public void mark()
	{
		marked = true;
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