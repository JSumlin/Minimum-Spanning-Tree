/* Joseph Sumlin
 * 002-42-4529
 * Algorithms HW5 Q2
 */

import java.util.*;
public class main {

	public static void main(String[] args) {
		Integer[][] coord = new Integer[5][2];
		List<Edge> chosenEdges = new ArrayList<Edge>();
		Random rand = new Random();
		EdgeMinHeap minHeap = new EdgeMinHeap();
		
		//generating random coordinates for each point
		for(int i=0; i<coord.length; i++) {
			coord[i][0] = rand.nextInt(500);
			coord[i][1] = rand.nextInt(500);
		}
		
		//stores the edges between every point
		for(int i=0; i<coord.length; i++) {
			for(int j=i+1; j<coord.length; j++) {
				minHeap.add(new Edge(Math.abs(coord[i][0]-coord[j][0]) + Math.abs(coord[i][1]-coord[j][1]), i, j));
			}
		}
		
		// finds |V|-1 edges which don't form a cycle.
		while(chosenEdges.size()!=coord.length-1) {
			Edge minEdge = minHeap.pop();
			chosenEdges.add(minEdge);
			if(isCyclic(coord, chosenEdges, minEdge.getPointA())) chosenEdges.remove(chosenEdges.size()-1);
		}
		
		// tallies minimum cost
		int minCost=0;
		for(int i=0; i<chosenEdges.size(); i++) {
			minCost+=chosenEdges.get(i).getDist();
		}
		
		// output
		System.out.println("Minimum cost: "+minCost);
		System.out.println("Edges (Distance, Point A, Point B): "+chosenEdges);
	}
	
	//Builds an adjacency matrix with the chosenEdges. This is used for isCyclic.
	private static boolean[][] buildGraph(Integer[][] coord, List<Edge> chosenEdges) {
		boolean[][] graph = new boolean[coord.length][coord.length];
		for(int i=0; i<chosenEdges.size(); i++) {
			graph[chosenEdges.get(i).getPointA()][chosenEdges.get(i).getPointB()] = true;
			graph[chosenEdges.get(i).getPointB()][chosenEdges.get(i).getPointA()] = true;
		}
		return graph;
	}
	
	// Depth first search with an adjacency matrix. It determines if the graph contains a cycle.
	// Most of this is code I wrote for HW3 Q4.
	private static boolean isCyclic(Integer[][] coord, List<Edge> chosenEdges, int start) {
		boolean[][] graph = buildGraph(coord, chosenEdges);
		boolean[] visited=new boolean[graph[0].length]; //tracks what nodes we've visited.
		visited[start] = true;
		Stack<Integer> stack=new Stack<Integer>(); //stack to retrace our steps.
		stack.push(start);
		
		//depth first search
		int previous = -1;
		for(int i=0;i<graph[0].length; i++) {
			if(stack.isEmpty()) break; //when the stack is empty, it has run out of paths. the loop ends.
			if(i==previous) continue; //skip over the node we came from
			if(graph[stack.peek()][i]==true && visited[i]==true) return true; //return true if we retrace our steps
			//if it finds a path in the adjacency matrix to a node that hasn't been visited, it goes down the path
			if(graph[stack.peek()][i]==true && visited[i]==false) {
				previous = stack.peek();
				stack.push(i);
				visited[i]=true;
				i=-1;
				continue;
			}
			if(i==graph[0].length-1) {
				i=stack.pop(); // returns to previous node when finished searching current node for paths.
				if(!stack.isEmpty()) previous = stack.peek();
			}
		}
		return false; // returns false if we never retrace our steps.
	}
	
	
}

class Edge {
	private Integer dist;
	private Integer[] adjPair = new Integer[2];
	
	public Edge(Integer dist, int pointA, int pointB) {
		this.dist = dist;
		adjPair[0] = pointA;
		adjPair[1] = pointB;
	}
	
	public String toString() {
		return "("+dist+", "+adjPair[0]+", "+adjPair[1]+")";
	}
	
	public Integer getDist() {
		return dist;
	}
	
	public Integer[] getAdjPair() {
		return adjPair;
	}
	
	public Integer getPointA() {
		return adjPair[0];
	}
	
	public Integer getPointB() {
		return adjPair[1];
	}
	
	public void setDist(Integer dist) {
		this.dist = dist;
	}
	
	public void setAdjPair(Integer pointA, Integer pointB) {
		adjPair[0] = pointA;
		adjPair[1] = pointB;
	}
}

// acts as a MinHeap for edges.
class EdgeMinHeap {
	PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>();
	Map<Integer, List<Edge>> dict = new HashMap();
	
	public String toString() {
		return ""+dict.values();
	}
	
	/* stores the weight of the edge in the minHeap, and then stores the edge inside an ArrayList that's inside a 
	 * HashMap where the key is its weight. The reason it's a map of ArrayLists and not Edges is in case multiple edges
	 * have the same weight. That way both edges are stored.
	 */
	public void add(Edge edge) {
		minHeap.add(edge.getDist());
		if(!dict.containsKey(edge.getDist())) dict.put(edge.getDist(), new ArrayList<Edge>());
		dict.get(edge.getDist()).add(edge);
	}
	
	/* Gets the smallest available distance from the MinHeap, and uses it to find the corresponding edge. If more than
	 * one edge has that distance, it takes the first one in the ArrayList. It also removes the distance from the MinHeap,
	 * removes the edge from the ArrayList, and if the ArrayList is empty, removes the ArrayList from the map. It then
	 * returns the appropriate edge.
	 */
	public Edge pop() {
		Integer key = minHeap.poll();
		Edge edge = dict.get(key).get(0);
		dict.get(key).remove(0);
		if(dict.get(key).isEmpty()) dict.remove(key);
		return edge;
	}
}
