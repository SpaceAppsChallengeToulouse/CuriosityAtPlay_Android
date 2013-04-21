package orders;

public class ActionOrder extends Order
{
	public enum Type
	{
		DIG,
		LASER,
		CAM
	}

	Type type;

	public ActionOrder(Type type)
	{
		super();
		this.type = type;
	}
	
	@Override
	public String toString()
	{
		return "a" + type.toString();
	}
	
	public Type getType()
	{
		return type;
	}
}
