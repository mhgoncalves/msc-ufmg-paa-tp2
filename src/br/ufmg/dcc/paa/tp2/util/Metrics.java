package br.ufmg.dcc.paa.tp2.util;

import java.io.PrintWriter;

/**
 * Classe criada para armazenar as metricas propostas para o trabalho.
 * 
 * @author Wilson de Carvalho
 */
public class Metrics {
	private float avgDegre;
	private float modularity;
	private float condutHonest;
	private float condutSybil;
	private float clustCoefHonest;
	private float clustCoefSybil;
	private float correctnessHonest;
	private float correctnessSybil;
	private float falsePositive;
	private float falseNegative;

	public float getAvgDegre() {
		return avgDegre;
	}

	public void setAvgDegre(float avgDegre) {
		this.avgDegre = avgDegre;
	}

	public float getModularity() {
		return modularity;
	}

	public void setModularity(float modularity) {
		this.modularity = modularity;
	}

	public float getCondutHonest() {
		return condutHonest;
	}

	public void setCondutHonest(float condutHonest) {
		this.condutHonest = condutHonest;
	}

	public float getCondutSybil() {
		return condutSybil;
	}

	public void setCondutSybil(float condutSybil) {
		this.condutSybil = condutSybil;
	}

	public float getClustCoefHonest() {
		return clustCoefHonest;
	}

	public void setClustCoefHonest(float clustCoefHonest) {
		this.clustCoefHonest = clustCoefHonest;
	}

	public float getClustCoefSybil() {
		return clustCoefSybil;
	}

	public void setClustCoefSybil(float clustCoefSybil) {
		this.clustCoefSybil = clustCoefSybil;
	}

	public float getCorrectnessHonest() {
		return correctnessHonest;
	}

	public void setCorrectnessHonest(float correctnessHonest) {
		this.correctnessHonest = correctnessHonest;
	}

	public float getCorrectnessSybil() {
		return correctnessSybil;
	}

	public void setCorrectnessSybil(float correctnessSybil) {
		this.correctnessSybil = correctnessSybil;
	}

	public float getFalsePositive() {
		return falsePositive;
	}

	public void setFalsePositive(float falsePositive) {
		this.falsePositive = falsePositive;
	}

	public float getFalseNegative() {
		return falseNegative;
	}

	public void setFalseNegative(float falseNegative) {
		this.falseNegative = falseNegative;
	}

	@Override
	public String toString() {
		return String.format("%.2f", this.getAvgDegre()) + "\n"
				+ String.format("%.2f", this.getModularity()) + "\n"
				+ String.format("%.2f", this.getCondutSybil()) + "\n"
				+ String.format("%.2f", this.getCondutHonest()) + "\n"
				+ String.format("%.2f", this.getClustCoefSybil()) + "\n"
				+ String.format("%.2f", this.getClustCoefHonest()) + "\n"
				+ String.format("%.2f", this.getCorrectnessSybil()) + "\n"
				+ String.format("%.2f", this.getCorrectnessHonest()) + "\n"
				+ String.format("%.2f", this.getFalsePositive()) + "\n"
				+ String.format("%.2f", this.getFalseNegative());
	}

	/**
	 * Salva o objeto num arquivo.
	 * 
	 * @param fileName
	 *            Nome do arquivo.
	 */
	public void saveToFile(String fileName) {
		PrintWriter writer = null;
		try {
			try {
				writer = new PrintWriter(fileName, "UTF-8");
				writer.println(String.format("%.2f", this.getAvgDegre()));
				writer.println(String.format("%.2f", this.getModularity()));
				writer.println(String.format("%.2f", this.getCondutSybil()));
				writer.println(String.format("%.2f", this.getCondutHonest()));
				writer.println(String.format("%.2f", this.getClustCoefSybil()));
				writer.println(String.format("%.2f", this.getClustCoefHonest()));
				writer.println(String.format("%.2f", this.getCorrectnessSybil()));
				writer.println(String.format("%.2f", this.getCorrectnessHonest()));
				writer.println(String.format("%.2f", this.getFalsePositive()));
				writer.println(String.format("%.2f", this.getFalseNegative()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
