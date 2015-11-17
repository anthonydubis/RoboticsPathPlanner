import java.util.*;

public class Vertex implements Comparable<Vertex>{
	private Point pt;
	private ArrayList<Vertex> adjList;
	private double minDist;
	private Vertex updatedBy;

	public Vertex(Point pt){
		this.pt = pt;
		adjList = new ArrayList<Vertex>();
		minDist = Double.POSITIVE_INFINITY;
		updatedBy = null;
	}

	public Point getPt(){
		return pt;
	}

	public ArrayList<Vertex> getAdjList(){
		return adjList;
	}

	public double getMinDist(){
		return minDist;
	}

	public Vertex getUpdatedBy(){
		return updatedBy;
	}

	public void setMinDist(double minDist){
		this.minDist = minDist;
	}

	public void setUpdatedBy(Vertex updatedBy){
		this.updatedBy = updatedBy;
	}

	public void addNeighbor(Vertex v){
		adjList.add(v);
	}

	@Override
	public int compareTo(Vertex v2) {
		return Double.compare(minDist, v2.minDist);
	}
}