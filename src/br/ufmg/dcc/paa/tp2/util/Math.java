package br.ufmg.dcc.paa.tp2.util;

public class Math {
	public static int factorial(int n) {
		int fact = 1;
		for (int i = 1; i <= n; i++) {
			fact *= i;
		}
		return fact;
	}

	/**
	 * Simplifica dois numeros cuja divisao fatorial sera' dividida, evitando
	 * assim o calculo desnecessario de fatoriais muito grandes.
	 * 
	 * @param n1
	 *            Numero maior
	 * @param n2
	 *            Numero menor
	 * @return Resultado da divisao entre os dois numeros. Caso n1 seja menor
	 *         que n2, retorna -1.
	 */
	public static int simplifyFactorialDivision(int n1, int n2) {
		if (n2 > n1)
			return -1;
		int ret = 1;
		for (int i = n2 + 1; i <= n1; i++) {
			ret *= i;
		}
		return ret;
	}
}
