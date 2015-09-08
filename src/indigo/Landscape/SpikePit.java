package indigo.Landscape;

public class SpikePit extends Wall
{

	public SpikePit(double x1, double y1, double x2, double y2)
	{
		super(x1, y1, x2, y2);
		
		name = "a spike pit";
		
		killsSolidEntities = true;
	}
}