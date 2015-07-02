package br.ufmg.dcc.paa.tp2;

import java.util.ArrayList;

import br.ufmg.dcc.paa.tp2.util.GraphReader;

/**
 * Classe com metodo main para execucacao do TP2.
 * 
 * @author Wilson de Carvalho
 */
public class Main {

	public static void main(String[] args) {
		if (args.length != 4) {
			System.out
					.println("Sao necessarios 4 arquivos de entrada na seguinte ordem:");
			System.out.println("\tArquivo 1 - Grafo A");
			System.out.println("\tArquivo 2 - Grafo B");
			System.out.println("\tArquivo 3 - Vertices sybil para grafo A");
			System.out.println("\tArquivo 4 - Vertices sybil para grafo B");
			return;
		}
		ArrayList<ArrayList<Integer>> graphA = readGraph(args[0], "A");
		ArrayList<ArrayList<Integer>> graphB = readGraph(args[1], "B");
		int[] sybilA = readSybil(args[2], "sybil A");
		int[] sybilB = readSybil(args[3], "sybil B");
		if (graphA == null || graphB == null || sybilA == null
				|| sybilB == null) {
			return;
		} else {
			long start = System.currentTimeMillis();
			System.out.println("Iniciando processamento do grafo A");
			new ADC().execute(graphA, sybilA, "A");
			endProcessing(start, "A");
			start = System.currentTimeMillis();
			System.out.println("Iniciando processamento do grafo B");
			new ADC().execute(graphB, sybilB, "B");
			endProcessing(start, "B");
		}
	}

	private static void endProcessing(long startMillis, String graphName) {
		System.out
				.println("O resultado do processamento encontra-se nos arquivos 'metricsG"
						+ graphName
						+ ".txt', 'regionHonestG"
						+ graphName
						+ ".txt' e 'regionSybilG" + graphName + ".txt'.");
		long current = System.currentTimeMillis();
		double total = current - startMillis;
		if (total > 1000) {
			double totalSec = (total / 1000);
			System.out
					.println("\tTempo total de execucao: " + totalSec + " s.");
		} else {
			System.out.println("\tTempo total de execucao: " + total + " ms.");
		}
	}

	private static ArrayList<ArrayList<Integer>> readGraph(String fileName,
			String graphName) {
		System.out.println("Fazendo leitura do grafo " + graphName + "...");
		GraphReader gr = new GraphReader();
		ArrayList<ArrayList<Integer>> ret = gr.readGraph(fileName);
		if (ret == null)
			System.out.println("Erro ao fazer a leitura do grafo.");
		return ret;
	}

	private static int[] readSybil(String fileName, String sybilName) {
		System.out.println("Fazendo leitura dos vertices sybil " + sybilName
				+ "...");
		GraphReader gr = new GraphReader();
		int[] ret = gr.readSybil(fileName);
		if (ret == null)
			System.out.println("Erro ao fazer a leitura dos vertices sybil.");
		return ret;
	}
}