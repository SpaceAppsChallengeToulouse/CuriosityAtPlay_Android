package orders;

public class RotateOrder extends Order
{
	public float angle;

	public RotateOrder(float angle)
	{
		this.angle = angle;
	}
	
	@Override
	public String toString()
	{
		return "r" + angle;
	}

}