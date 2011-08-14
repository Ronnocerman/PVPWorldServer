package pvpworldserver;

public class PhysicsVector 
{
	private int width;
	private int height;
	public PhysicsVector(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	public void setWidth(int width)
	{
		this.width = width;
	}
	public void setHeight(int height)
	{
		this.height = height;
	}
	public int getWidth()
	{
		return width;
	}
	public int getHeight()
	{
		return height;
	}
}
