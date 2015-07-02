package br.ufmg.dcc.paa.tp2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import br.ufmg.dcc.paa.tp2.util.Metrics;
import br.ufmg.dcc.paa.tp2.util.Tuple2;

/**
 * Implementacao de algoritmo de deteccao de comunidades (ADC).
 * 
 * @author Wilson de Carvalho
 */
public class ADC {
	// Quantidade de elementos que deverao ser gerados para a semente.
	private final int seedSize = 20;

	/**
	 * Executa o algoritmo de deteccao de comunidades (ADC).
	 * 
	 * @param adjListGraph
	 * @param sybil
	 *            Vertices sybil conhecidos para comparacao.
	 */
	public void execute(ArrayList<ArrayList<Integer>> adjListGraph,
			int[] sybil, String graphName) {
		// Monta os grafos A e B, onde B = V - A
		System.out.println("\tIniciando criacao dos grafos A e B...");
		long start = System.currentTimeMillis();
		Tuple2<Graph, Graph> tuple = this.createGraphsAB(adjListGraph);
		this.endProcessing(start);
		Graph graphA = tuple._1;
		Graph graphB = tuple._2;
		// Separa as regioes do grafo
		System.out.println("\tProcessando regioes honesta e Sybil...");
		start = System.currentTimeMillis();
		this.processRegions(graphA, graphB, adjListGraph);
		this.endProcessing(start);
		System.out.println("\tCalculando metricas...");
		start = System.currentTimeMillis();
		Metrics met = this
				.calculateMetrics(adjListGraph, graphA, graphB, sybil);
		this.endProcessing(start);
		met.saveToFile("metricsG" + graphName + ".txt");
		graphA.saveToFile("regionHonestG" + graphName + ".txt");
		graphB.saveToFile("regionSybilG" + graphName + ".txt");
	}

	private void endProcessing(long startMillis) {
		System.out.println("\t\tFinalizado.");
		long current = System.currentTimeMillis();
		double total = current - startMillis;
		if (total > 1000) {
			double totalSec = (total / 1000);
			System.out.println("\t\t\tTempo total de execucao: " + totalSec
					+ " s.");
		} else {
			System.out.println("\t\t\tTempo total de execucao: " + total
					+ " ms.");
		}
	}

	/**
	 * Calcula as metricas do grafo.
	 * 
	 * @param graph
	 */
	private Metrics calculateMetrics(
			ArrayList<ArrayList<Integer>> adjListGraph, Graph graphHonest,
			Graph graphSybil, int[] origSybil) {
		Metrics met = new Metrics();
		met.setAvgDegre(new Graph(adjListGraph).avgDegree());
		met.setCondutHonest(this.conductance(graphHonest, graphSybil,
				adjListGraph));
		met.setCondutSybil(this.conductance(graphSybil, graphHonest,
				adjListGraph));
		met.setCorrectnessSybil(this.sybilCorrectness(graphSybil, origSybil));
		met.setCorrectnessHonest(this.honestCorrectness(graphHonest, origSybil,
				adjListGraph.size()));
		met.setClustCoefHonest(graphHonest.clustCoeficient());
		met.setClustCoefSybil(graphSybil.clustCoeficient());
		met.setFalsePositive(1 - met.getCorrectnessHonest());
		met.setFalseNegative(1 - met.getCorrectnessSybil());
		met.setModularity(this
				.modularity(graphHonest, graphSybil, adjListGraph));
		return met;
	}

	/**
	 * Calcula o percentual de acertos da regiao sybil.
	 * 
	 * Custo = O(n), onde n e' o tamanho da regiao sybil.
	 * 
	 * @param graphSybil
	 *            Grafo da regiao Sybil encontrada.
	 * @param sybil
	 *            Regiao sybil conhecida.
	 * @return Valor entre 0 e 1 (em P.U.) de acertos.
	 */
	private float sybilCorrectness(Graph graphSybil, int[] sybil) {
		int correctCount = 0;
		for (int i = 0; i < sybil.length; i++) {
			if (graphSybil.contains(sybil[i]))
				correctCount++;
		}
		return (float) correctCount / (float) sybil.length;
	}

	/**
	 * Calcula o percentual de acertos na regiao honesta.
	 * 
	 * @param graphHonest
	 *            Grafo da regiao honesta encontrada.
	 * @param sybil
	 *            Regiao sybil conhecida.
	 * @param origGraphVertexCount
	 *            Numero de vertices do grafo original.
	 * 
	 * @return Valor entre 0 e 1 (em P.U.) de acertos.
	 */
	private float honestCorrectness(Graph graphHonest, int[] sybil,
			int origGraphVertexCount) {
		float correctCount = graphHonest.getVertexCount();
		for (int i = 0; i < sybil.length; i++) {
			if (graphHonest.contains(sybil[i]))
				correctCount--;
		}
		return correctCount / (float) (origGraphVertexCount - sybil.length);
	}

	/**
	 * Executa o processamento necessario para calcular a condutancia
	 * normalizada (CN).
	 * 
	 * O metodo faz a separacao entre as regioes honesta e sybil nos proprios
	 * grafos fornecidos, sendo que o grafo A ficara com a regiao honesta e o
	 * grafo B com a regiao sybil.
	 * 
	 * Custo: O(V^2 + VE + E)
	 * 
	 * @param graphA
	 *            Grafo A
	 * @param graphB
	 *            Grafo B
	 * @param adjListGraph
	 *            Lista de adjacencias com o grafo original
	 * @return Condutancia normalizada
	 */
	private double processRegions(Graph graphA, Graph graphB,
			ArrayList<ArrayList<Integer>> adjListGraph) {
		// Cria o grafo com as arestas entre A e B
		Graph abEdges = this.getABEdges(adjListGraph, graphA, graphB);
		double CN = this.calculateCN(graphA, graphB, abEdges);
		Set<Integer> verticesInB = new HashSet<Integer>(graphB.getVertices());
		Iterator<Integer> itr = verticesInB.iterator();
		// Guarda as arestas removidas de B e que nao foram inseridas em A
		HashMap<Integer, HashSet<Integer>> remainingEdges = new HashMap<Integer, HashSet<Integer>>();
		// Para cada vertice em B, faz sua insercao em A e verifica se a
		// condutancia normalizada aumentou. Em caso positivo, prossegue
		while (itr.hasNext()) { // Theta(V)
			Integer u = itr.next();
			graphA.addVertex(u);
			// Tenta adicionar as arestas remanescentes ao grafo
			this.addRemainingEdges(u, remainingEdges, graphA);
			Set<Edge> adjU = graphB.getAdjEdges(u);
			if (adjU != null) {
				Iterator<Edge> itrE = adjU.iterator();
				while (itrE.hasNext()) { // Theta(E)
					Edge e = itrE.next();
					// Se existir o outro o vertice da aresta, insere em A
					if (graphA.contains(e.v())) {
						graphA.addEdge(e, true);
					} else {
						// Caso contrario, coloca na lista de remanescente
						addRemainingEdge(e.u(), e.v(), remainingEdges);
						addRemainingEdge(e.v(), e.u(), remainingEdges);
					}
				}
				graphB.removeVertex(u);
			}
			double newCN = this.calculateCN(graphA, graphB, abEdges); // Theta(V+E)
			if (newCN > CN) {
				CN = newCN;
			} else {
				graphA.removeVertex(u);
				graphB.addVertex(u);
				if (adjU != null)
					graphB.addEdges(adjU, true);
			}
			abEdges = this.getABEdges(adjListGraph, graphA, graphB); // Theta(V+E)
		}
		return CN;
	}

	private void addRemainingEdge(Integer u, Integer v,
			HashMap<Integer, HashSet<Integer>> remainingEdges) {
		if (remainingEdges.containsKey(u))
			remainingEdges.get(u).add(v);
		else {
			HashSet<Integer> set = new HashSet<Integer>();
			set.add(v);
			remainingEdges.put(u, set);
		}
	}

	private void addRemainingEdges(Integer u,
			HashMap<Integer, HashSet<Integer>> remainingEdges, Graph graph) {
		if (remainingEdges.containsKey(u)) {
			Iterator<Integer> itr = remainingEdges.get(u).iterator();
			while (itr.hasNext()) {
				Integer v = itr.next();
				if (graph.contains(v)) {
					Edge e = new Edge(u, v);
					graph.addEdge(e, true);
					itr.remove();
				}
			}
		}
	}

	/**
	 * Cria um novo grafo com as sementes para iniciar o ADC.
	 * 
	 * O(V+E)
	 * 
	 * @param adjListGraph
	 *            Lista de adjacencias do grafo original
	 * @return Grafo apenas com as arestas originarias nos vertices semente.
	 */
	private Tuple2<Graph, Graph> createGraphsAB(
			ArrayList<ArrayList<Integer>> input) {
		HashMap<Integer, HashSet<Integer>> tmpA = new HashMap<Integer, HashSet<Integer>>(
				input.size());
		HashMap<Integer, HashSet<Integer>> tmpB = new HashMap<Integer, HashSet<Integer>>(
				input.size());
		HashSet<Integer> seeds = this.getSeeds();
		// Somente adiciona a nova lista de arestas dos vertices semente
		for (int i = 0; i < input.size(); i++) {
			if (seeds.contains(i)) {
				HashSet<Integer> adjUSet = new HashSet<Integer>();
				tmpA.put(i, adjUSet);
				ArrayList<Integer> adjU = input.get(i);
				for (int j = 0; j < adjU.size(); j++) {
					if (seeds.contains(adjU.get(j))) {
						adjUSet.add(adjU.get(j));
					}
				}
			} else {
				HashSet<Integer> adjUSet = new HashSet<Integer>();
				tmpB.put(i, adjUSet);
				ArrayList<Integer> adjU = input.get(i);
				for (int j = 0; j < adjU.size(); j++) {
					if (!seeds.contains(adjU.get(j))) {
						adjUSet.add(adjU.get(j));
					}
				}
			}
		}
		return new Tuple2<Graph, Graph>(new Graph(tmpA), new Graph(tmpB));
	}

	/**
	 * Cria um grafo com as arestas que nao foram incluidas no grafo A nem no
	 * grafo B.
	 * 
	 * O(V+E)
	 * 
	 * @param adjListGraph
	 *            Lista de adjacencias do grafo original
	 * @param graphA
	 *            Grafo A
	 * @param graphB
	 *            Grafo B
	 * 
	 * @return Grafo apenas com as arestas entre grafos A e B.
	 */
	private Graph getABEdges(ArrayList<ArrayList<Integer>> input, Graph graphA,
			Graph graphB) {
		HashMap<Integer, HashSet<Integer>> tmp = new HashMap<Integer, HashSet<Integer>>();
		for (int i = 0; i < input.size(); i++) {
			Integer u = new Integer(i);
			Iterator<Integer> itr = input.get(u).iterator();
			while (itr.hasNext()) {
				Integer v = itr.next();
				if ((graphA.contains(u) && graphB.contains(v))
						|| (graphA.contains(v) && graphB.contains(u))) {
					if (!tmp.containsKey(u))
						tmp.put(u, new HashSet<Integer>());
					tmp.get(u).add(v);
				}
			}
		}
		return new Graph(tmp);
	}

	/**
	 * Gera uma lista aleatoria de vertices, inicialmente 20, dentro os 100
	 * primeiros vertices do grafo para compor a semente que sera' utilizada no
	 * ADC
	 * 
	 * @return Conjunto de vertices para compor a semente.
	 */
	private HashSet<Integer> getSeeds() {
		HashSet<Integer> values = new HashSet<Integer>();
		/* values.add(2); values.add(5); values.add(7); return values; */
		Random r = new Random();
		while (values.size() != seedSize) {
			int seed = r.nextInt(101);
			if (!values.contains(seed))
				values.add(seed);
		}
		return values;
	}

	/**
	 * Calcula a condutancia do grafo A.
	 * 
	 * Custo: O(V+E)
	 * 
	 * @param graphA
	 *            Grafo A
	 * @param graphB
	 *            Grafo B (onde B = V - A)
	 * @param graphOrg
	 *            Grafo original
	 * @return Condutancia.
	 */
	private float conductance(Graph graphA, Graph graphB,
			ArrayList<ArrayList<Integer>> graphOrg) {
		Graph graphAB = this.getABEdges(graphOrg, graphA, graphB); // O(V+E)
		float eAA = graphA.getEdgeCount(); // O(V+E)
		float eAB = graphAB.getEdgeCount(); // O(V+E)
		return eAB / eAA;
	}

	/**
	 * Calcula a modularidade do grafo original a partir das particoes honesta e
	 * Sybil.
	 * 
	 * Custo: O(V+E)
	 * 
	 * @param graphHonest
	 *            Regiao honesta
	 * @param graphSybil
	 *            Regiao Sybil
	 * @param graphOrg
	 *            Grafo original
	 * @return Modularidade.
	 */
	private float modularity(Graph graphHonest, Graph graphSybil,
			ArrayList<ArrayList<Integer>> graphOrg) {
		// Numero de arestas entre as regioes honesta e Sybil
		float eHS = this.getABEdges(graphOrg, graphHonest, graphSybil)
				.getEdgeCount();
		// Numero de arestas na regiao honesta
		float eH = (float) graphHonest.getEdgeCount();
		// Numero de arestas na regiao Sybil
		float eS = (float) graphSybil.getEdgeCount();

		// Total de arestas do grafo original
		float E = new Graph(graphOrg).getEdgeCount();

		// % de arestas na regiao honesta
		float eiiH = eH / E;
		// % de arestas na regiao Sybil
		float eiiS = eS / E;
		// % de arestas com pelo menos um vertice na regiao honesta
		float aiH = (eH + eHS) / E;
		// % de arestas com pelo menos um vertice na regiao Sybil
		float aiS = (eS + eHS) / E;

		// Modularidade => Q = \sum_{i=1}^{k}(e_{ii}-a_{i}^2)
		// Referencia:
		// https://www.cs.umd.edu/class/fall2009/cmsc858l/lecs/Lec10-modularity.pdf
		float Q = (eiiH - (aiH * aiH)) + (eiiS - (aiS * aiS));

		return Q;
	}

	/**
	 * Calcula a condutancia normalizada (Cn) do grafo. Custo: Theta(V+E)
	 * 
	 * @param graphA
	 * @param graphB
	 * @param abEdges
	 * @return Condutancia normalizada.
	 */
	private double calculateCN(Graph graphA, Graph graphB, Graph abEdges) {
		double eAA = graphA.getEdgeCount();
		double eBB = graphB.getEdgeCount();
		double eAB = abEdges.getEdgeCount();
		double K = eAA / (eAA + eAB);
		double eA = eAA + eAB;
		double eB = eBB + eAB;
		return K - ((eA * eB) / ((eA * eA) + (eA * eB)));
	}
}