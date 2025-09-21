import java.util.Scanner;
public class FibonacciSequence {
	public static void main(String[] args) {
		Scanner in =  new Scanner(System.in);
		int n = in.nextInt();
		in.close();
		System.out.println((int)((1/Math.sqrt(5) * Math.pow((1 + Math.sqrt(5))/2, n)) - (1/Math.sqrt(5) * Math.pow((1 - Math.sqrt(5))/2, n))));
	}
}
