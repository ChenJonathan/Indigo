package indigo.Entity;

import indigo.Landscape.Land;
import indigo.Manager.ContentManager;
import indigo.Projectile.Mortar;
import indigo.Stage.Stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Turret extends Entity
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;

	private double angle;
	private double groundAngle;
	boolean ccw;

	public static final double TURRET_WIDTH = 100;
	public static final double TURRET_HEIGHT = 130;
	public static final int BASE_HEALTH = 250;
	public final double TURRETANGLE = Math.PI / 4;

	public Turret(Stage stage, double x, double y)
	{
		this(stage, x, y, BASE_HEALTH);
	}

	public Turret(Stage stage, double x, double y, int health)
	{
		super(stage, x, y, health);
		name = "a turret";

		width = TURRET_WIDTH;
		height = TURRET_HEIGHT;

		pushability = 0;
		flying = false;
		frictionless = false;

		friendly = false;

		angle = Math.PI / 2;
		groundAngle = Math.PI * 3 / 2;
		ccw = false;

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.TURRET_IDLE), -1);
	}

	public void update()
	{
		if(currentAnimation == DEATH)
		{
			super.update();
			if(animation.hasPlayedOnce())
			{
				dead = true;
			}
			return;
		}

		super.update();

		if(canAttack() && inRange())
		{
			double optimalAngle = getOptimalAngle();
			double deltaAngle = optimalAngle - angle;

			if(Math.abs(deltaAngle) <= Math.PI / 18)
			{
				// angle = optimalAngle; breaks my code :(
				attack();
			}
			else
			{
				if(deltaAngle < 0)
				{
					deltaAngle += 2 * Math.PI;
				}
				if(angle >= 2 * Math.PI)
				{
					angle -= 2 * Math.PI;
				}
				else if(angle < 0)
				{
					angle += 2 * Math.PI;
				}
				if(checkArc(angle, optimalAngle)) {
					ccw = true;
				}
				else {
					ccw = false;
				}
				//System.out.println(reverse);
				if(ccw && isLegal(angle + Math.PI / 90))
				{
					angle += Math.PI / 90;
				}
				else if(!ccw && isLegal(angle - Math.PI / 90)) 
				{
					angle -= Math.PI / 90;
				}
			}
		}
	}

	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
				(int)(getWidth()), (int)(getHeight()), null);

		// Draws a simple line representing the turret arm // TODO Temporary
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(3));
		g.drawLine((int)getX(), (int)getY(), (int)(getX() + 50 * Math.cos(angle)), (int)(getY() - 50 * Math.sin(angle)));
	}

	public void attack()
	{
		if(stage.getTime() % 50 == 0) // TODO Change to be more similar to
			// player firing
		{
			double velX = Mortar.SPEED * Math.cos(angle);
			double velY = Mortar.SPEED * -Math.sin(angle);
			stage.getProjectiles().add(new Mortar(this, getX(), getY(), velX, velY, Mortar.DAMAGE));
		}
	}

	// Checks if player is in range
	public boolean inRange()
	{
		double deltaX = stage.getPlayer().getX() - getX();
		double deltaY = -(stage.getPlayer().getY() - getY());

		if(deltaX == 0)
		{
			// True if maximum vertical height of projectile fired straight up is greater than or equal to deltaY
			return Math.pow(Mortar.SPEED, 2) / (2 * Stage.GRAVITY) >= deltaY;
		}
		else
		{
			return Math.pow(Mortar.SPEED, 4) - Stage.GRAVITY
					* (Stage.GRAVITY * deltaX * deltaX + 2 * deltaY * Math.pow(Mortar.SPEED, 2)) >= 0;
		}
	}

	// Calculates the firing angle necessary to hit the player
	public double getOptimalAngle()
	{
		double optimalAngle;
		double deltaX = stage.getPlayer().getX() - getX();
		double deltaY = -(stage.getPlayer().getY() - getY());

		if(deltaX == 0)
		{
			if(deltaY > 0)
			{
				optimalAngle = Math.PI / 2;
			}
			else
			{
				optimalAngle = 3 * Math.PI / 2;
			}
		}
		else
		{
			double a1 = Math.atan((Math.pow(Mortar.SPEED, 2) + Math.sqrt(Math.pow(Mortar.SPEED, 4) - Stage.GRAVITY
					* (Stage.GRAVITY * deltaX * deltaX + 2 * deltaY * Math.pow(Mortar.SPEED, 2))))
					/ (Stage.GRAVITY * deltaX));
			double a2 = Math.atan((Math.pow(Mortar.SPEED, 2) - Math.sqrt(Math.pow(Mortar.SPEED, 4) - Stage.GRAVITY
					* (Stage.GRAVITY * deltaX * deltaX + 2 * deltaY * Math.pow(Mortar.SPEED, 2))))
					/ (Stage.GRAVITY * deltaX));

			if(deltaX < 0)
			{
				a1 += Math.PI;
				a2 += Math.PI;
			}
			a1 %= 2 * Math.PI;
			a2 %= 2 * Math.PI;
			if(a1 < 0)
			{
				a1 += 2 * Math.PI;
			}
			if(a2 < 0)
			{
				a2 += 2 * Math.PI;
			}
			if(Math.abs(Math.sin(a1)) < Math.abs(Math.sin(a2)))
			{
				optimalAngle = a1;
			}
			else
			{
				optimalAngle = a2;
			}
		}
		return optimalAngle;
	}

	// hopefully to be replaced
	public boolean isLegal(double testAngle) {
		boolean legal = false;

		double leftBound = groundAngle - TURRETANGLE;
		if(leftBound < 0) {
			leftBound += Math.PI * 2;
		}
		double rightBound = (groundAngle + TURRETANGLE) % (Math.PI * 2);

		// System.out.println(leftBound + ", " + testAngle + "," + rightBound); debugging tool
		if(rightBound > leftBound && (testAngle > rightBound || testAngle < leftBound)) {
			legal = true;
		}
		else if(rightBound < leftBound && testAngle > rightBound && testAngle < leftBound){
			legal = true;
		}
		return legal;
	} 

	public boolean checkArc(double startAngle, double endAngle) {
		boolean canReachCCW = true;
		startAngle = (startAngle > Math.PI)? -1 * (Math.PI * 2 - startAngle) : startAngle;
		endAngle = (endAngle > Math.PI)? -1 * (Math.PI * 2 - endAngle) : endAngle;

		double leftBound = groundAngle - TURRETANGLE;
		double rightBound = (groundAngle + TURRETANGLE) % (Math.PI * 2);
		leftBound = (leftBound > Math.PI) ? -1 * (Math.PI * 2 - leftBound) : leftBound;
		rightBound = (rightBound > Math.PI) ? -1 * (Math.PI * 2 - rightBound) : rightBound;
		
		//System.out.println("startAngle: " + startAngle + " endAngle: " + endAngle);
		if(startAngle > endAngle) {
			canReachCCW = (endAngle < leftBound)? true : false;
			//System.out.println(canReachCCW);
		}
		else {
			canReachCCW = (startAngle < leftBound && endAngle > rightBound)? false : true;
		}
		if(endAngle > leftBound && endAngle < rightBound) {
			canReachCCW = (endAngle < (leftBound + rightBound) / 2)? true : false;
		}
		return canReachCCW;
	}

	public double getAngle()
	{
		return angle;
	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public boolean isActive()
	{
		return currentAnimation != DEATH;
	}

	public void setGround(Land ground)
	{
		super.setGround(ground);
		canAttack(true);
	}

	public void removeGround()
	{
		super.removeGround();
		canAttack(false);
	}

	public void die()
	{
		if(currentAnimation != DEATH)
		{
			setAnimation(DEATH, ContentManager.getAnimation(ContentManager.TURRET_DEATH), 2);
		}
	}
}