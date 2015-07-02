package br.ufmg.dcc.paa.tp2;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

import br.ufmg.dcc.paa.tp2.util.Math;

class Edge {
	private Integer u;
	private Integer v;

	public Edge(Integer u, Integer v) {
		this.u = u;
		this.v = v;
	}

	public Integer u() {
		return u;
	}

	public Integer v() {
		return v;
	}

	@Override
	public boolean equals(Object obj) {
		return obj.getClass() == this.getClass() && (((Edge) obj).u == this.u)
				&& (((Edge) obj).v == this.v);
	}
}

class NodeColor {
	public static final int WHITE = 1;
	public static final int GRAY = 2;
	public static final int BLACK = 3;

	public Integer id = -1;
	public int color = WHITE;
	public Integer f = 0;
	public Integer d = 0;
	public Integer p = -1; // predecessor

	public NodeColor(Integer nodeId) {
		id = nodeId;
	}
}

/**
 * Classe com implementacao de grafo por lista de adjacencias e algumas
 * funcionalidades basicas de navegacao.
 * 
 * @author Wilson de Carvalho
 */
public class Graph {
	private HashMap<Integer, HashSet<Integer>> adjList = new HashMap<Integer, HashSet<Integer>>();

	/**
	 * Cria uma instancia do grafo. Custo de criacao: O(1)
	 * 
	 * @param input
	 *            Lista de adjacencias do grafo a ser criado.
	 */
	public Graph(HashMap<Integer, HashSet<Integer>> input) {
		this.adjList = input;
	}

	/**
	 * Cria uma instancia do grafo. Custo de criacao: Theta(V+E)
	 * 
	 * @param input
	 *            Lista de adjacencias do grafo a ser criado.
	 */
	public Graph(ArrayList<ArrayList<Integer>> input) {
		this.adjList = new HashMap<Integer, HashSet<Integer>>();
		for (int u = 0; u < input.size(); u++) {  // Theta(V)
			HashSet<Integer> adjU = new HashSet<Integer>();
			adjList.put(u, adjU);
			for (int v = 0; v < input.get(u).size(); v++) {  // Theta(Adj(u))
				adjU.add(v);
			}
		}
	}

	public int getVertexCount() {
		return adjList.size();
	}

	/**
	 * Conta o numero de arestas.
	 * 
	 * Custo: Theta(V+E)
	 * 
	 * @return
	 */
	public int getEdgeCount() {
		if (adjList == null)
			return 0;
		int edgeCount = 0;
		HashMap<Integer, HashSet<Integer>> counted = new HashMap<Integer, HashSet<Integer>>();
		Iterator<Integer> itr = adjList.keySet().iterator();
		while (itr.hasNext()) { // Theta(V)
			Integer u = itr.next();
			Iterator<Integer> adjU = adjList.get(u).iterator();
			while (adjU.hasNext()) { // Theta(Adj(u))
				Integer v = adjU.next();
				boolean countedHere = false;
				if (counted.containsKey(u)) {
					if (!counted.get(u).contains(v)) {
						counted.get(u).add(v);
						edgeCount++;
						countedHere = true;
					}
				} else {
					HashSet<Integer> set = new HashSet<Integer>();
					set.add(v);
					counted.put(u, set);
					edgeCount++;
					countedHere = true;
				}
				if (counted.containsKey(v)) {
					if (!counted.get(v).contains(u)) {
						counted.get(v).add(u);
						if (!countedHere)
							edgeCount++;
					}
				} else {
					HashSet<Integer> set = new HashSet<Integer>();
					set.add(u);
					counted.put(v, set);
					if (!countedHere)
						edgeCount++;
				}
			}
		}
		return edgeCount;
	}

	public Set<Integer> getVertices() {
		return this.adjList.keySet();
	}

	public ArrayList<Edge> getEdges() {
		ArrayList<Edge> ret = new ArrayList<Edge>();
		for (int i = 0; i < this.adjList.size(); i++) {
			if (adjList.containsKey(i) && this.adjList.get(i) != null) {
				for (int j = 0; j < this.adjList.get(i).size(); j++) {
					ret.add(new Edge(i, j));
				}
			}
		}
		return ret;
	}

	/**
	 * Retorna as arestas adjacentes de um dado vertice.
	 * 
	 * @param u
	 *            Vertice.
	 * @return Arestas adjacentes.
	 */
	public Set<Edge> getAdjEdges(Integer u) {
		if (!this.adjList.containsKey(u))
			return null;
		HashSet<Edge> edges = new HashSet<Edge>();
		Iterator<Integer> adjU = adjList.get(u).iterator();
		while (adjU.hasNext()) {
			Integer v = adjU.next();
			edges.add(new Edge(u, v));
		}
		return edges;
	}

	/**
	 * Adiciona uma nova aresta no grafo.
	 * 
	 * @param u
	 *            Vertice fonte.
	 * @param v
	 *            Vertice destino.
	 */
	public void addEdge(Integer u, Integer v, boolean undirected) {
		if (adjList == null)
			return;
		if (adjList.get(u) == null)
			adjList.put(u, new HashSet<Integer>());
		if (!adjList.get(u).contains(v)) {
			adjList.get(u).add(v);
		}
		if (undirected) {
			this.addEdge(v, u, false);
		}
	}

	public void addEdge(Edge e, boolean undirected) {
		this.addEdge(e.u(), e.v(), undirected);
	}

	public void addEdges(Set<Edge> edges, boolean undirected) {
		Iterator<Edge> itr = edges.iterator();
		while (itr.hasNext()) {
			Edge e = itr.next();
			this.addEdge(e.u(), e.v(), undirected);
		}
	}

	/**
	 * Adiciona um vertice ao grafo.
	 * 
	 * @param v
	 *            Vertice que sera adicionado.
	 */
	public void addVertex(Integer v) {
		if (adjList == null || adjList.containsKey(v))
			return;
		adjList.put(v, new HashSet<Integer>());
	}

	/**
	 * Remove uma dada aresta do grafo.
	 * 
	 * @param s
	 *            Vertice fonte.
	 * @param t
	 *            Vertice destino.
	 */
	private void removeEdge(Integer u, Integer v) {
		if (!adjList.containsKey(u))
			return;
		if (adjList.get(u) != null && adjList.get(u).contains(v)) {
			adjList.get(u).remove(v);
			if (adjList.get(u).isEmpty())
				adjList.remove(u);
		}
	}

	public void removeEdge(Edge e, boolean undirected) {
		this.removeEdge(e.u(), e.v());
		if (undirected)
			this.removeEdge(e.v(), e.u());
	}

	/**
	 * Remove um dado vertice do grafo.
	 * 
	 * @param u
	 *            Vertice a ser removido.
	 */
	public void removeVertex(Integer u) {
		if (adjList == null || !adjList.containsKey(u))
			return;
		HashSet<Integer> adjU = adjList.get(u);
		if (adjU != null) {
			// Como o grafo e' nao direcionado, remove as referencias u->v e
			// v->u
			Iterator<Integer> itr = adjU.iterator();
			while (itr.hasNext()) {
				Integer v = itr.next();
				adjList.get(v).remove(u);
			}
			adjList.remove(u);
		}
	}

	/**
	 * Verifica se a aresta informada existe no grafo.
	 * 
	 * @param e
	 *            Aresta a ser avaliada.
	 * @return Verdadeiro se existir.
	 */
	public boolean contains(Edge e) {
		Set<Integer> adjU = adjList.get(e.u());
		if (adjU == null)
			return false;
		else
			return adjU.contains(e.v());
	}

	/**
	 * Verifica se o vertice informado existe no grafo.
	 * 
	 * @param u
	 *            Vertice a ser avaliado.
	 * @return Verdadeiro se existir.
	 */
	public boolean contains(Integer u) {
		return this.adjList.containsKey(u);
	}

	/**
	 * Obtem arestas adjacentes de um dado vertice.
	 * 
	 * @param s
	 *            Vertice origem.
	 * @return Lista com os elementos que compoem arestas a partir do vertice s.
	 */
	public Set<Integer> getEdges(Integer s) {
		return adjList.get(s);
	}

	/**
	 * Salva o grafo em um arquivo, ordenando-o por vertices e posteriormente
	 * arestas.
	 * 
	 * @param fileName
	 *            Nome do arquivo.
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public void saveToFile(String fileName) {
		PrintWriter writer = null;
		try {
			try {
				writer = new PrintWriter(fileName, "UTF-8");
				ArrayList<Integer> keys = new ArrayList<Integer>(
						this.adjList.keySet());
				Collections.sort(keys);
				Iterator<Integer> itrU = keys.iterator();
				while (itrU.hasNext()) {
					Integer u = itrU.next();
					writer.println(u);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	/**
	 * Imprime este grafo (util para depuracao).
	 */
	public void print() {
		if (this.adjList == null)
			return;
		ArrayList<Integer> keys = new ArrayList<Integer>(this.adjList.keySet());
		Collections.sort(keys);
		Iterator<Integer> itrU = keys.iterator();
		while (itrU.hasNext()) {
			Integer u = itrU.next();
			System.out.print("\n" + u.toString());
			Iterator<Integer> itrAdjU = this.adjList.get(u).iterator();
			while (itrAdjU.hasNext()) {
				System.out.print(" " + itrAdjU.next().toString());
			}
		}
	}

	/**
	 * Calcula o grau medio dos vertices do grafo.
	 * 
	 * Custo: Theta(V+E)
	 * 
	 * @return Grau medio do grafo.
	 */
	public float avgDegree() {
		if (adjList == null)
			return 0;
		float sumDegree = 0;
		Iterator<Integer> itr = adjList.keySet().iterator();
		while (itr.hasNext()) { // Theta(V)
			Integer u = itr.next();
			Iterator<Integer> adjU = adjList.get(u).iterator();
			while (adjU.hasNext()) { // Theta(Adj(u))
				Integer v = adjU.next();
				if (v == u)
					sumDegree += 2;
				else
					sumDegree += 1;
			}
		}
		return sumDegree / this.getVertexCount();
	}

	/**
	 * Calcula o coeficiente de agrupamento do grafo. O coeficiente de
	 * agrupamento e dado pela razao entre o numero de arestas existestes entre
	 * os vertices adjacentes de um dado vertice V e o numero de arestas
	 * possiveis interligando tais vertices adjacentes.
	 * 
	 * Neste metodo, sera' calculado a media do coeficiente de agrupamento local
	 * de todos os vertices.
	 * 
	 * Custo: Theta(V+3E) (usar analise amortizada)
	 * 
	 * @return Coeficiente de agrupamento medio do grafo.
	 */
	public float clustCoeficient() {
		if (adjList == null || adjList.isEmpty())
			return 0;
		HashMap<Integer, Float> coefByVertex = new HashMap<Integer, Float>();
		Iterator<Integer> itr = adjList.keySet().iterator();
		while (itr.hasNext()) { // Theta(V)
			Integer u = itr.next();
			int n = adjList.get(u).size();
			// Calcula numero de combinacoes possiveis entre os vertices
			// adjacentes de U
			int c = Math.simplifyFactorialDivision(n, n - 2) / 2;
			int count = 0;
			Iterator<Integer> adjU = adjList.get(u).iterator();
			while (adjU.hasNext()) { // Theta(Adj(u))
				Integer v = adjU.next();
				Iterator<Integer> adjV = adjList.get(v).iterator();
				while (adjV.hasNext()) { // Theta(Adj(v))
					Integer t = adjV.next();
					if (t != u && adjList.get(u).contains(t)) {
						count++;
					}
				}
			}
			if (c == 0)
				coefByVertex.put(u, (float) 0);
			else
				coefByVertex.put(u, (float) count / 2 / c);
		}
		// Soma os coeficientes de agrupamento de todos os vertices
		Iterator<Float> itrF = coefByVertex.values().iterator();
		float sum = 0;
		while (itrF.hasNext()) {
			sum += itrF.next();
		}
		// Calcula a media
		return sum / (float) coefByVertex.size();
	}

	/**
	 * Efetua a busca em largura no grafo (breadth-first search).
	 * 
	 * @param s
	 *            No de inicio da busca.
	 * @return Dados dos nos visitados.
	 */
	public NodeColor[] bfs(int s) {
		// Cria um vetor para o controle das cores dos nos. O(n).
		NodeColor[] nodes = this.initNodes();
		ArrayDeque<Integer> q = new ArrayDeque<Integer>();
		q.push(s);
		while (!q.isEmpty()) {
			Integer u = q.pop();
			Iterator<Integer> adjU = adjList.get(u).iterator(); // nos
																// adjacentes de
																// U
			while (adjU.hasNext()) {
				Integer v = adjU.next();
				if (nodes[v].color == NodeColor.WHITE) {
					nodes[v].color = NodeColor.GRAY;
					nodes[v].d = nodes[u].d + 1;
					nodes[v].p = u;
					q.push(v);
				}
			}
			nodes[u].color = NodeColor.BLACK;
		}
		return nodes;
	}

	/**
	 * Efetua a busca em profundidade no grafo (depth-first search).
	 * 
	 * @return Dados dos nos visitados.
	 */
	public NodeColor[] dfs() {
		// Cria um vetor para o controle das cores dos nos. O(n).
		NodeColor[] nodes = this.initNodes();
		Integer time = 0;
		for (int u = 0; u < nodes.length; u++) {
			if (nodes[u].color == NodeColor.WHITE)
				DFS_Visit(u, nodes, time);
		}
		return nodes;
	}

	/**
	 * Efetua a busca em profundidade no grafo (depth-first search).
	 * 
	 * @param s
	 *            No de inicio da busca.
	 * @return Dados dos nos visitados.
	 */
	public NodeColor[] dfs(int s) {
		// Cria um vetor para o controle das cores dos nos. O(n).
		NodeColor[] nodes = this.initNodes();
		Integer time = 0;
		DFS_Visit(s, nodes, time);
		return nodes;
	}

	private void DFS_Visit(int u, NodeColor[] nodes, Integer time) {
		time += 1;
		nodes[u].color = NodeColor.GRAY;
		nodes[u].d = time;
		Iterator<Integer> adjU = adjList.get(u).iterator(); // nos adjacentes de
															// U
		while (adjU.hasNext()) {
			Integer v = adjU.next();
			if (nodes[v].color == NodeColor.WHITE)
				DFS_Visit(v, nodes, time);
		}
		time += 1;
		nodes[u].color = NodeColor.BLACK;
		nodes[u].f = time;
	}

	/**
	 * Inicializa os nos para busca BFS ou DFS.
	 * 
	 * @return
	 */
	private NodeColor[] initNodes() {
		NodeColor[] nodes = new NodeColor[adjList.size()];
		for (int i = 0; i < nodes.length; i++)
			nodes[i] = new NodeColor(i);
		return nodes;
	}
}