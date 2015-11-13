import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PathPlanner {
	private Point start;
	private Point goal;
	private Obstacle world;
	private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	
	private void getWorldAndObstacles(String file)
	{
		
	}
	
	/*
	 * Sets the start and goal instance variables using the data in file.
	 * Assumes file is a txt file with two lines:
	 * First line is the x,y coordinates for start
	 * Second line is the x,y coordinates for goal
	 */
	private void getStartAndGoal(String file) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		for (int i = 0; i < 2; i++) {
			String line = br.readLine();
			System.out.println(line);
			String[] parts = line.split(" ");
			Point p = new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
			if (i == 0)
				start = p;
			else
				goal = p;
		}
		
		br.close();
	}
	
	/*
	 * Assumes the following two arguments (file names) are passed in:
	 * Arg0: The file containing the world and obstacle vertices
	 * Arg1: The file containing the start and goal vertices
	 */
	public static void main(String[] args) throws IOException
	{
		PathPlanner planner = new PathPlanner();
		planner.getWorldAndObstacles(args[0]);
		planner.getStartAndGoal(args[1]);
	}
}
