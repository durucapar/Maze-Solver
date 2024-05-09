
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Test {

	public static void main(String[] args) throws FileNotFoundException {

		String fileName = "maze1.txt";

		DirectedGraph<String> adjMatrix = new DirectedGraph<String>();
		StackInterface<String> path1 = new LinkedStack<>();
		StackInterface<String> path2 = new LinkedStack<>();

		int[][] adjArr = adjMatrix.getAdjacency(fileName, getEmptyArray(fileName));

		addVerticesAndEdges(adjMatrix, adjArr);

		String origin = adjMatrix.getOrigin(fileName);
		String end = adjMatrix.getEnd(fileName);

		System.out.println("• Adjacency Lists of Each Vertex of the Graph After Maze to Graph Operation");
		adjMatrix.displayEdges();
		System.out.println("\n• Adjacency Matrix of the Graph After Maze to Graph Operation:");
		print(adjArr);
		System.out.println("\n• The number of edges found: " + adjMatrix.getNumberOfEdges() + "\n");
		System.out.println("• BFS output between the starting and the end points of the maze:");
		adjMatrix.mazeWithPattern_withQ(adjMatrix.getBreadthFirstSearch(origin, end), fileName);
		System.out.println("\n• Shortest path between the starting and the end points of the maze:");
		adjMatrix.getShortestPath(origin, end, path1);
		adjMatrix.mazeWithPattern_withStack(path1, fileName);
		System.out.println("\n• The cheapest path for the Weighted Graph:");
		double cost = adjMatrix.getCheapestPath(origin, end, path2);
		adjMatrix.mazeWithPattern_withStack(path2, fileName);
		System.out.println("\nThe cost of the cheapest path: " + cost + "\n");

		System.out.println("\n• DFS output between the starting and the end points of the maze:");
		adjMatrix.mazeWithPattern_withQ(adjMatrix.getDepthFirstSearch(origin, end), fileName);

	}

	public static int[][] getEmptyArray(String textFile) throws FileNotFoundException {

		File f1 = new File(textFile);
		int row = 0;
		int column = 0;
		try (Scanner textFileReader = new Scanner(f1)) {

			while (textFileReader.hasNextLine()) {
				String line = textFileReader.nextLine();
				column = line.length();
				row++;
			}
			textFileReader.close();
		}

		int[][] tempArray = new int[row][column];

		return tempArray;
	}

	public static void print(int[][] arr) {

		System.out.print("\t");
		for (int j = 0; j < arr[0].length; j++) {
			System.out.print("(0-" + j + ")\t");

		}
		System.out.println();

		for (int i = 0; i < arr.length; i++) {
			System.out.print("(" + i + "-0)\t");
			for (int j = 0; j < arr[i].length; j++) {
				System.out.print(" " + arr[i][j] + "\t");

			}
			System.out.println();

		}

	}

	private static void addVerticesAndEdges(DirectedGraph<String> directedGraph, int[][] adjacencyMatrix) {

		for (int i = 0; i < adjacencyMatrix.length; i++) {
			for (int j = 0; j < adjacencyMatrix[i].length; j++) {
				if (adjacencyMatrix[i][j] == 1) {
					directedGraph.addVertex(i + "-" + j);

					if (i != 0 && adjacencyMatrix[i - 1][j] == 1) {
						directedGraph.addEdge((i - 1) + "-" + j, i + "-" + j);
						directedGraph.addEdge(i + "-" + j, (i - 1) + "-" + j);

					}
					if (i != adjacencyMatrix.length - 1 && adjacencyMatrix[i + 1][j] == 1) {
						directedGraph.addEdge(i + "-" + j, (i + 1) + "-" + j);
						directedGraph.addEdge((i + 1) + "-" + j, i + "-" + j);

					}
					if (j != 0 && adjacencyMatrix[i][j - 1] == 1) {
						directedGraph.addEdge(i + "-" + (j - 1), i + "-" + j);
						directedGraph.addEdge(i + "-" + j, i + "-" + (j - 1));

					}
					if (j != adjacencyMatrix[i].length - 1 && adjacencyMatrix[i][j + 1] == 1) {
						directedGraph.addEdge(i + "-" + j, i + "-" + (j + 1));
						directedGraph.addEdge(i + "-" + (j + 1), i + "-" + j);

					}

				}
			}
		}
		System.out.println("\n");
	}
}
