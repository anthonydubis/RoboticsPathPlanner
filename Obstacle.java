import java.util.ArrayList;
import java.awt.Point;

public class Obstacle {
	private ArrayList<Point> vertices = new ArrayList<Point>();
	
	public void addVertex(Point vertex)
	{
		vertices.add(vertex);
	}
}
