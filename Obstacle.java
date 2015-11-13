import java.util.ArrayList;

public class Obstacle {
	private ArrayList<Point> vertices = new ArrayList<Point>();
	
	public void addVertex(Point vertex)
	{
		vertices.add(vertex);
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (Point p : vertices)
			builder.append("\t" + p + "\n");

        return builder.toString(); 
    }
}
