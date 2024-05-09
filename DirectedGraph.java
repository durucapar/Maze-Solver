
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

/**
 * A class that implements the ADT directed graph.
 * 
 * @author Frank M. Carrano
 * @author Timothy M. Henry
 * @version 5.1
 */
public class DirectedGraph<T> implements GraphInterface<T> {
	private DictionaryInterface<T, VertexInterface<T>> vertices;
	private int edgeCount;
	private int[][] adjacency;

	public DirectedGraph() {
		vertices = new UnsortedLinkedDictionary<>();
		edgeCount = 0;
	} // end default constructor

	public String getOrigin(String file) throws FileNotFoundException {
		String origin = "";
		boolean flag = false;

		File f1 = new File(file);
		int row = 0;

		try (Scanner textFileReader = new Scanner(f1)) {

			while (textFileReader.hasNextLine()) {
				String line = textFileReader.nextLine();
				if (line.contains(" ")) {

					String[] splitted_line = line.split("");

					for (int i = 0; i < splitted_line.length; i++) {

						if (splitted_line[i].equals(" ")) {
							origin = row + "-" + i;
							flag = true;
							break;

						}
					}
					if (flag) {
						break;
					}
					row++;

				}

			}
			textFileReader.close();

		}
		return origin;

	}

	public String getEnd(String file) throws FileNotFoundException {
		String end = "";

		File f1 = new File(file);
		int row = 0;

		try (Scanner textFileReader = new Scanner(f1)) {

			while (textFileReader.hasNextLine()) {
				String line = textFileReader.nextLine();
				if (line.contains(" ")) {

					String[] splitted_line = line.split("");

					for (int i = 0; i < splitted_line.length; i++) {

						if (splitted_line[i].equals(" ")) {
							end = row + "-" + i;

						}
					}
					row++;

				}

			}
			textFileReader.close();

		}
		return end;

	}

	public int[][] getAdjacency(String file, int[][] emptyArr) throws FileNotFoundException {

		adjacency = emptyArr;

		File f1 = new File(file);
		int row = 0;

		try (Scanner textFileReader = new Scanner(f1)) {

			while (textFileReader.hasNextLine()) {
				String line = textFileReader.nextLine();
				String[] splitted_line = line.split("");

				for (int i = 0; i < emptyArr[row].length; i++) {

					if (splitted_line[i].equals(" ")) {
						adjacency[row][i] = 1;

					} else {
						adjacency[row][i] = 0;
					}

				}
				row++;
			}
			textFileReader.close();
			return adjacency;

		}
	}

	public boolean addVertex(T vertexLabel) {
		VertexInterface<T> addOutcome = vertices.add(vertexLabel, new Vertex<>(vertexLabel));
		return addOutcome == null; // Was addition to dictionary successful?
	} // end addVertex

	public boolean addEdge(T begin, T end, double edgeWeight) {
		boolean result = false;
		VertexInterface<T> beginVertex = vertices.getValue(begin);
		VertexInterface<T> endVertex = vertices.getValue(end);
		if ((beginVertex != null) && (endVertex != null))
			result = beginVertex.connect(endVertex, edgeWeight);
		if (result)
			edgeCount++;
		return result;
	} // end addEdge

	public boolean addEdge(T begin, T end) {
		return addEdge(begin, end, 0);
	} // end addEdge

	public boolean hasEdge(T begin, T end) {
		boolean found = false;
		VertexInterface<T> beginVertex = vertices.getValue(begin);
		VertexInterface<T> endVertex = vertices.getValue(end);
		if ((beginVertex != null) && (endVertex != null)) {
			Iterator<VertexInterface<T>> neighbors = beginVertex.getNeighborIterator();
			while (!found && neighbors.hasNext()) {
				VertexInterface<T> nextNeighbor = neighbors.next();
				if (endVertex.equals(nextNeighbor))
					found = true;
			} // end while
		} // end if

		return found;
	} // end hasEdge

	public boolean isEmpty() {
		return vertices.isEmpty();
	} // end isEmpty

	public void clear() {
		vertices.clear();
		edgeCount = 0;
	} // end clear

	public int getNumberOfVertices() {
		return vertices.getSize();
	} // end getNumberOfVertices

	public int getNumberOfEdges() {
		return edgeCount;
	} // end getNumberOfEdges

	protected void resetVertices() {
		Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();
		while (vertexIterator.hasNext()) {
			VertexInterface<T> nextVertex = vertexIterator.next();
			nextVertex.unvisit();
			nextVertex.setCost(0);
			nextVertex.setPredecessor(null);
		} // end while
	} // end resetVertices

	public StackInterface<T> getTopologicalOrder() {
		resetVertices();

		StackInterface<T> vertexStack = new LinkedStack<>();
		int numberOfVertices = getNumberOfVertices();
		for (int counter = 1; counter <= numberOfVertices; counter++) {
			VertexInterface<T> nextVertex = findTerminal();
			nextVertex.visit();
			vertexStack.push(nextVertex.getLabel());
		} // end for

		return vertexStack;
	} // end getTopologicalOrder

	public QueueInterface<T> getBreadthFirstSearch(T origin, T end) {
		resetVertices();
		QueueInterface<T> traversalOrder = new LinkedQueue<>();
		QueueInterface<VertexInterface<T>> vertexQueue = new LinkedQueue<>();
		VertexInterface<T> originVertex = vertices.getValue(origin);
		originVertex.visit();
		traversalOrder.enqueue(origin); // Enqueue vertex label
		vertexQueue.enqueue(originVertex); // Enqueue vertex
		while (!vertexQueue.isEmpty()) {
			VertexInterface<T> frontVertex = vertexQueue.dequeue();
			Iterator<VertexInterface<T>> neighbors = frontVertex.getNeighborIterator();
			while (neighbors.hasNext()) {
				VertexInterface<T> nextNeighbor = neighbors.next();
				if (!nextNeighbor.isVisited()) {
					nextNeighbor.visit();
					traversalOrder.enqueue(nextNeighbor.getLabel());
					vertexQueue.enqueue(nextNeighbor);
				} // end if
			} // end while
		} // end while
		return traversalOrder;
	}

	public QueueInterface<T> getDepthFirstSearch(T origin, T end) {
		resetVertices();
		QueueInterface<T> traversalOrder = new LinkedQueue<>();
		StackInterface<VertexInterface<T>> vertexStack = new LinkedStack<>();
		VertexInterface<T> originVertex = vertices.getValue(origin);
		originVertex.visit();
		traversalOrder.enqueue(origin); // Enqueue vertex label
		vertexStack.push(originVertex); // Enqueue vertex
		while (!vertexStack.isEmpty()) {
			VertexInterface<T> topVertex = vertexStack.peek();
			VertexInterface<T> neighbors= topVertex.getUnvisitedNeighbor();

			if (neighbors != null) {
				Iterator<VertexInterface<T>> n = topVertex.getNeighborIterator();
				VertexInterface<T> nextNeighbor = n.next();
				nextNeighbor.visit();
				traversalOrder.enqueue(nextNeighbor.getLabel());
				vertexStack.push(nextNeighbor);
			} // end if
			else {
				vertexStack.pop();
			}
			// end while
		} // end while
		return traversalOrder;
	}

	public int getShortestPath(T begin, T end, StackInterface<T> path) {
		resetVertices();
		boolean done = false;
		QueueInterface<VertexInterface<T>> vertexQueue = new LinkedQueue<>();
		VertexInterface<T> originVertex = vertices.getValue(begin);
		VertexInterface<T> endVertex = vertices.getValue(end);
		originVertex.visit();
		// Assertion: resetVertices() has executed setCost(0)
		// and setPredecessor(null) for originVertex
		vertexQueue.enqueue(originVertex);
		while (!done && !vertexQueue.isEmpty()) {
			VertexInterface<T> frontVertex = vertexQueue.dequeue();
			Iterator<VertexInterface<T>> neighbors = frontVertex.getNeighborIterator();
			while (!done && neighbors.hasNext()) {
				VertexInterface<T> nextNeighbor = neighbors.next();
				if (!nextNeighbor.isVisited()) {
					nextNeighbor.visit();
					nextNeighbor.setCost(1 + frontVertex.getCost());
					nextNeighbor.setPredecessor(frontVertex);
					vertexQueue.enqueue(nextNeighbor);
				} // end if
				if (nextNeighbor.equals(endVertex))
					done = true;
			} // end while
		} // end while
			// Traversal ends; construct shortest path
		int pathLength = (int) endVertex.getCost();
		path.push(endVertex.getLabel());
		VertexInterface<T> vertex = endVertex;
		while (vertex.hasPredecessor()) {
			vertex = vertex.getPredecessor();
			path.push(vertex.getLabel());
		} // end while
		return pathLength;
	}

	@SuppressWarnings("unchecked")
	public double getCheapestPath(T begin, T end, StackInterface<T> path) {
		resetVertices();
		boolean done = false;
		Random rand = new Random();
		@SuppressWarnings("rawtypes")
		PriorityQueueInterface<EntryPQ> priorityQueue = new HeapPriorityQueue();
		VertexInterface<T> originVertex = vertices.getValue(begin);
		VertexInterface<T> endVertex = vertices.getValue(end);
		priorityQueue.add(new EntryPQ(originVertex, 0, null));
		double nextCost = 0.0;

		while (!done && !priorityQueue.isEmpty()) {
			EntryPQ frontEntry = priorityQueue.remove();
			VertexInterface<T> frontVertex = frontEntry.getVertex();

			Iterator<VertexInterface<T>> neighbors = frontVertex.getNeighborIterator();

			if (!frontVertex.isVisited()) {
				frontVertex.visit();
				frontVertex.setCost(1 + frontEntry.getCost());
				frontVertex.setPredecessor(frontEntry.getPredecessor());

				if (frontVertex.equals(endVertex)) {
					done = true;

				} else {
					while (neighbors.hasNext()) {
						VertexInterface<T> nextNeighbors = neighbors.next();

						if (!nextNeighbors.isVisited()) {
							double weight = rand.nextDouble(1, 5);
							nextCost = weight + frontVertex.getCost();
							priorityQueue.add(new EntryPQ(nextNeighbors, nextCost, frontVertex));
						}
					}
				}
			} // end if

		} // end while
			// Traversal ends; construct shortest path
		int pathCost = (int) endVertex.getCost();
		path.push(endVertex.getLabel());
		VertexInterface<T> vertex = endVertex;
		while (vertex.hasPredecessor()) {
			vertex = vertex.getPredecessor();
			path.push(vertex.getLabel());
		} // end while
		return pathCost;
	}

	protected VertexInterface<T> findTerminal() {
		boolean found = false;
		VertexInterface<T> result = null;

		Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();

		while (!found && vertexIterator.hasNext()) {
			VertexInterface<T> nextVertex = vertexIterator.next();

			// If nextVertex is unvisited AND has only visited neighbors)
			if (!nextVertex.isVisited()) {
				if (nextVertex.getUnvisitedNeighbor() == null) {
					found = true;
					result = nextVertex;
				} // end if
			} // end if
		} // end while

		return result;
	} // end findTerminal

	public static String[][] getMazeArr(String textFile, int i, int j) throws FileNotFoundException {

		String[][] temp = new String[i][j];

		File f1 = new File(textFile);
		int row = 0;

		try (Scanner textFileReader = new Scanner(f1)) {

			while (textFileReader.hasNextLine()) {
				String line = textFileReader.nextLine();
				String[] splitted_line = line.split("");
				for (int a = 0; a < splitted_line.length; a++) {
					temp[row][a] = splitted_line[a];
				}

				row++;
			}
			textFileReader.close();
		}

		return temp;
	}

	public void mazeWithPattern_withQ(QueueInterface<T> q, String fileName) throws FileNotFoundException {
		int count = 0;
		String[][] arr = getMazeArr(fileName, adjacency.length, adjacency[0].length);

		while (!q.isEmpty()) {

			String[] v = q.getFront().toString().split("-");
			int row = Integer.valueOf(v[0]);
			int column = Integer.valueOf(v[1]);
			arr[row][column] = ".";
			q.dequeue();
			count++;

		}
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				System.out.print(arr[i][j]);
			}
			System.out.print("\n");
		}

		System.out.println("The number of visited vertices: " + count);

	}

	public void mazeWithPattern_withStack(StackInterface<T> s, String fileName) throws FileNotFoundException {
		int count = 0;
		String[][] arr = getMazeArr(fileName, adjacency.length, adjacency[0].length);

		while (!s.isEmpty()) {

			String[] v = s.peek().toString().split("-");
			int row = Integer.valueOf(v[0]);
			int column = Integer.valueOf(v[1]);
			arr[row][column] = ".";
			s.pop();
			count++;

		}
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				System.out.print(arr[i][j]);
			}
			System.out.print("\n");
		}

		System.out.println("The number of visited vertices: " + count);

	}

	// Used for testing
	public void displayEdges() {
		System.out.println("\nEdges exist from the first vertex in each line to the other vertices in the line.");
		System.out.println("(Edge weights are given; weights are zero for unweighted graphs):\n");
		Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();
		while (vertexIterator.hasNext()) {
			((Vertex<T>) (vertexIterator.next())).display();
		} // end while
	} // end displayEdges

	private class EntryPQ implements Comparable<EntryPQ> {
		private VertexInterface<T> vertex;
		private VertexInterface<T> previousVertex;
		private double cost; // cost to nextVertex

		private EntryPQ(VertexInterface<T> vertex, double cost, VertexInterface<T> previousVertex) {
			this.vertex = vertex;
			this.previousVertex = previousVertex;
			this.cost = cost;
		} // end constructor

		public VertexInterface<T> getVertex() {
			return vertex;
		} // end getVertex

		public VertexInterface<T> getPredecessor() {
			return previousVertex;
		} // end getPredecessor

		public double getCost() {
			return cost;
		} // end getCost

		public int compareTo(EntryPQ otherEntry) {
			// Using opposite of reality since our priority queue uses a maxHeap;
			// could revise using a minheap
			return (int) Math.signum(otherEntry.cost - cost);
		} // end compareTo

		public String toString() {
			return vertex.toString() + " " + cost;
		} // end toString
	} // end EntryPQ

} // end DirectedGraph
