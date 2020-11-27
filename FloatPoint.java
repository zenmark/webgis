package cancernet.census.map;
public class FloatPoint
{

	public float x;
	public float y;

	public FloatPoint(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public FloatPoint()
	{
		this(0.0F, 0.0F);
	}

	public void setLocation(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	public static FloatPoint screenToMap(int x, int y, Projection projection){
		return new FloatPoint((float)x / projection.zoom - projection.shift.x, -((float)y / projection.zoom - projection.shift.y));
	}
}
