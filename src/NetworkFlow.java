import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class NetworkFlow {
	
	private class Node 
	{
		String id; 
		boolean visited = false;
		ArrayList<Edge> edgelist;
		//HashMap<String, Edge> edges; 
		ArrayList<Edge> bedgelist;
		//HashMap<String, Edge> bedges; 
		public Node(String ID){id = ID;}
	}
	
	private class Edge
	{
		int capacity; 
		//int flow; 
		int backflow; 
		Node u; 
		Node v;
		
		public Edge(String ustr, String vstr, int w)
		{
			capacity = w; 
			addNode(ustr); // add node if it doesn't already exist
			addNode(vstr);
			u = nodes.get(ustr);
			v = nodes.get(vstr);
			u.edgelist.add(this);
			//u.edges.put(vstr, this); // add this edge to node
			v.bedgelist.add(this);
			//v.bedges.put(ustr, this);
		}
	}
	
	// NetworkFlow Fields //
	private ArrayList<Node> nodelist;
	private ArrayList<Node> cut; 
	HashMap<String, Node> nodes;
	 
	public NetworkFlow()
	{
		this.nodelist = new ArrayList<Node>();
		this.nodes = new HashMap<String, Node>();
	}
	
	public void addNode(String u)
	{
		if(null == nodes.get(u))
		{
			nodes.put(u, new Node(u)); 
			nodelist.add(nodes.get(u));
			nodes.get(u).bedgelist = new ArrayList<Edge>();
			nodes.get(u).edgelist = new ArrayList<Edge>();
			//nodes.get(u).bedges = new HashMap<String, Edge>();
			//nodes.get(u).edges = new HashMap<String, Edge>();
		}
	}
	
	public void readGraph(String fname) throws FileNotFoundException
	{
		File file = new File(fname);
		Scanner input = new Scanner(file);
		
		while (input.hasNext()) {
			String estr[] = input.nextLine().split(" ");
			int c = Integer.parseInt(estr[2]);
			new Edge(estr[0], estr[1], c); // add edges (and nodes to graph) 
		}
		input.close();
	}
	
	public int findPath(int prevMax, Node u)
	{
		if(u == nodelist.get(1)) return prevMax;
		int max = prevMax;

		for(Edge e: u.edgelist)
		{
			if(e.v.visited == false){ 
				if(e.capacity > 0)
				{
					e.v.visited = true;
					max = prevMax > e.capacity ? 
							e.capacity : prevMax; 
					// find max capacity down path 
					max = findPath(max, e.v);
					if(max == -1) continue; // jump to next iteration
					e.backflow += max;
					e.capacity -= max;
					return max;
				}
			}
		}
		
		for(Edge e: u.bedgelist)
		{
			if(e.u.visited == false)
			{
				if(e.backflow > 0)
				{
					e.u.visited = true; 
					max = prevMax > e.backflow ?
							e.backflow : prevMax; 
					max = findPath(max, e.u);
					if(max == -1) continue; 
					e.backflow -= max;
					e.capacity += max;
					return max;
				}
			}
		}
		return -1;
	}
	
	/* MUST readGraph() on valid input graph */
	public void augment()
	{
		int c = 0;
		for(Edge e: nodelist.get(0).edgelist){
			c += e.capacity; 
		}
		while(findPath(c, nodelist.get(0)) != -1){;
			for(Node u: nodelist)
				u.visited = false;
		}
		for(Node u: nodelist)
			u.visited = false;
	}
	
	/* MUST call augment() just before */
	public int maxFlow()
	{
		int c = 0;
		for(Edge e: nodelist.get(0).edgelist){
			c += e.backflow; 
		}
		return c;
	}
	
	/* MUST call augment() just before */
	public void findCut(Node u)
	{
		u.visited = true;
		for(Edge e: u.edgelist){
			if(e.capacity > 0)
			{
				findCut(e.v);
			}
		}
	}
	
	/* MUST call findCut() just before */
	public void printCut()
	{
		for(Node n : nodelist)
		{
			if(n.visited == true)
				System.out.print(n.id + " ");
		}
		System.out.println();
	}
	
	public static void main(String[] args)
	{
		NetworkFlow network = new NetworkFlow();
		// add source and sink 
		network.addNode(args[0]);
		network.addNode(args[1]);
		// um ... read input.txt 
		try {
			network.readGraph(args[2]); 
		} catch (FileNotFoundException e) {
			System.out.println("did you mean: java NetworkFlow s t input.txt");
			System.out.println("please do not use \"<\" sign");
			e.printStackTrace();
		}
		// prolly hella inefficient 
		network.augment();
		// print value of max flow
		System.out.println(network.maxFlow());
		// print out nodes in A cut
		network.findCut(network.nodelist.get(0));
		network.printCut();
    }
}
