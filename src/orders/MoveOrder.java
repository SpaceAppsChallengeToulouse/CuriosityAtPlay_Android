package orders;
public class MoveOrder extends Order
{
	public float distance;

	public MoveOrder(float distance)
	{
		this.distance = distance;
	}
	
	@Override
	public String toString()
	{
		return "m" + distance;
	}
}