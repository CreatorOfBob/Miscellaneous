import java.util.Scanner;
public class GaussCircleProblem {
	public static void main(String[] args) {
		Scanner in =  new Scanner(System.in);
		int r = in.nextInt();
		in.close();
		int sum=0;
		for (int n=0; n<=Integer.MAX_VALUE; n++) {
			sum+=Math.floor((r*r/(4*n+1)))-Math.floor((r*r/(4*n+3)));
			if (Math.floor((r*r/(4*n+1)))==0){
				System.out.println(1+4*sum);
				break;
      }
    }
  }
}
