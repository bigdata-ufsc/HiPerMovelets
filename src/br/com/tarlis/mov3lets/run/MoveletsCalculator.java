package br.com.tarlis.mov3lets.run;

/**
 * The Class MoveletsCalculator.
 */
public class MoveletsCalculator {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		long n = 20;
		long l = 6;
		
		long candidates   = (n*(n+1))/2;
		long combinations = (long) (Math.pow(2.0, l * 1.0) - 1.0);
		
		long log = (long) (Math.ceil(Math.log(n))+1);
		

		long candidatesLog   = 0;
		for (long size = 1; size <= log; size++)
			for (long start = 1; start <= n-size+1; start++)
				candidatesLog++;
					
//		long candidatesLog   = (n*(n+1))/2;
		
		System.out.println("Number of Subtrajectories : " + candidates);
		System.out.println("Number of Combinations    : " + combinations);
		System.out.println("Number of Candidates      : " + (candidates * combinations));
		System.out.println("With LOG -----------------: " + log);
		System.out.println("Number of Subtrajectories : " + candidatesLog);
		System.out.println("Number of Candidates      : " + (candidatesLog * combinations));
	}

}
