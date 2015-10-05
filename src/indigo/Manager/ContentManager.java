
package indigo.Manager;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class ContentManager
{
	private HashMap<ImageData, BufferedImage> imageMap;
	private HashMap<AnimationData, BufferedImage[]> animationMap;

	public ContentManager()
	{
		imageMap = new HashMap<>();
		animationMap = new HashMap<>();
	}

	public BufferedImage getImage(ImageData id)
	{
		BufferedImage img = imageMap.get(id);
		if(img != null)
			return img;
		img = load(id);
		imageMap.put(id, img);
		return img;
	}

	public void getAnimation(AnimationData ad)
	{

	}

	private class ImageData
	{
		private String path;
		private int width, height;

		private ImageData(String path, int width, int height)
		{
			this.path = path;
			this.width = width;
			this.height = height;
		}
	}

	private class AnimationData
	{
		private String path;
		private int width, height, frames;

		private AnimationData(String path, int width, int height, int frames)
		{
			this.path = path;
			this.width = width;
			this.height = height;
			this.frames = frames;
		}
	}

	private BufferedImage load(ImageData id)
	{
		return load(id.path, id.width, id.height);
	}

	private BufferedImage load(String path, int width, int height)
	{
		BufferedImage img;
		try
		{
			img = ImageIO.read(Content.class.getResourceAsStream(path));
			img = img.getSubimage(0, 0, width, height);
			return img;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Error loading graphics.");
			System.exit(0);
		}
		return null;
	}

	private BufferedImage[] load(AnimationData ad)
	{
		BufferedImage[] img = new BufferedImage[ad.frames];
		BufferedImage sheet = load(ad.path, ad.width * ad.frames, ad.height);
		try
		{
			for(int i = 0; i < ad.frames; i++)
			{
				img[i] = sheet.getSubimage(i * ad.width, 0, ad.width,
						ad.height);
			}
			return img;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Error loading graphics.");
			System.exit(0);
		}
		return null;
	}
}
