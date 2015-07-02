package br.ufmg.dcc.paa.tp2.util;

import java.io.IOException;
import java.util.ArrayList;
import java.io.*;

/**
 * Faz a leitura do arquivo de entrada para o problema.
 * 
 * @author Wilson de Carvalho
 */
public class GraphReader {
	/**
	 * Faz a leitura das linhas do arquivo de entrada e verifica se os dados sao
	 * validos.
	 * 
	 * @return Lista de vertices sybil.
	 */
	public int[] readSybil(String fileName) {
		int[] ret = null;
		BufferedReader br = null;
		BufferedReader tmpBR = null;
		try {
			try {
				br = new BufferedReader(new FileReader(fileName));
				tmpBR = new BufferedReader(new FileReader(fileName));
				int linesCount = 0;
				while (tmpBR.readLine() != null)
					linesCount++;
				tmpBR.close();
				ret = new int[linesCount];
				String line;
				int i = 0;
				while ((line = br.readLine()) != null) {
					if (!line.trim().isEmpty()) {
						Integer vLine;
						vLine = this.validateLineSybil(line);
						if (vLine == null) {
							System.out.println("Linha invalida: " + line);
							return null;
						} else {
							ret[i] = vLine;
							i++;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * Faz a leitura das linhas do arquivo de entrada e verifica se os dados sao
	 * validos.
	 * 
	 * @return Lista de adjacencias do grafo lido.
	 */
	public ArrayList<ArrayList<Integer>> readGraph(String fileName) {
		ArrayList<ArrayList<Integer>> ret = new ArrayList<ArrayList<Integer>>();
		BufferedReader bf = null;
		try {
			try {
				bf = new BufferedReader(new FileReader(fileName));
				String line;
				while ((line = bf.readLine()) != null) {
					if (!line.trim().isEmpty()) {
						ArrayList<Integer> vLine;
						vLine = this.validateLineGraph(line);
						if (vLine == null) {
							System.out.println("Linha invalida: " + line);
							return null;
						} else {
							ret.add(vLine);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (bf != null)
					bf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * Verifica se uma dada linha do arquivo e' valida.
	 * 
	 * @param line
	 *            Linha do arquivo em forma de string.
	 * @return Array de inteiros correspondendo a lista de adjacencia da linha.
	 */
	private ArrayList<Integer> validateLineGraph(String line) {
		String[] s = line.split(" ");
		ArrayList<Integer> ret = null;
		try {
			String[] values = line.split(" ");
			if (s.length > 0) {
				ret = new ArrayList<Integer>(values.length);
				// Varre a linha a partir do segundo elemento, pois o primeiro
				// e' o indicador do vertice.
				for (int i = 1; i < values.length; i++) {
					ret.add(Integer.parseInt(values[i]));
				}
			}
		} catch (Exception ex) {
		}
		return ret;
	}

	/**
	 * Verifica se uma dada linha do arquivo e' valida.
	 * 
	 * @param line
	 *            Linha do arquivo em forma de string.
	 * @return Array de inteiros correspondendo a lista de adjacencia da linha.
	 */
	private Integer validateLineSybil(String line) {
		Integer ret = null;
		try {
			ret = Integer.parseInt(line);
		} catch (Exception ex) {
		}
		return ret;
	}
}