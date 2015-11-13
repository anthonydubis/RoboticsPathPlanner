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
	
	/* Assumes world/obstacle file is in the format specified by the assignment. */
	private void getWorldAndObstacles(String file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line;
		int n = Integer.parseInt(br.readLine());
		Obstacle curr = null;
		
		assert(n > 0);
		
		while ((line = br.readLine()) != null) {
			String[] parts = line.split(" ");
			if (parts.length == 1) {
				if (curr != null)
					obstacles.add(curr);
				curr = new Obstacle();
			} else {
				curr.addVertex(Point.pointFromStringArray(parts));
			}
		}
		obstacles.add(curr);
		br.close();
		
		/* The world is the first obstacle in the text file. */
		world = obstacles.remove(0);
	}
	
	/* Assumes goal/start file is in the format specified by the assignment. */
	private void getStartAndGoal(String file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		for (int i = 0; i < 2; i++) {
			String line = br.readLine();
			String[] parts = line.split(" ");
			Point p = Point.pointFromStringArray(parts);
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
	public static void main(String[] args) throws IOException {
		PathPlanner planner = new PathPlanner();
		planner.getWorldAndObstacles(args[0]);
		planner.getStartAndGoal(args[1]);
		
		/* Print out parsed data to ensure correctness. */
		System.out.println("Start:\n\t" + planner.start);
		System.out.println("Goal:\n\t" + planner.goal);
		System.out.println("World:\n" + planner.world);
		for (Obstacle o : planner.obstacles)
			System.out.println("Obstacle:\n" + o);
	}
}
