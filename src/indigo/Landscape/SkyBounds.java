package indigo.Landscape;

public class SkyBounds extends Wall
{
	public SkyBounds(double x1, double y1, double x2, double y2)
	{
		super(x1, y1, x2, y2);

		name = "";

		blocksEntities = true;
		blocksNonsolidProjectiles = false;
		blocksSolidProjectiles = false;

		killsEntities = false;
		killsNonsolidProjectiles = true;
		killsSolidProjectiles = false;
	}

}