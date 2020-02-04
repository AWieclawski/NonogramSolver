import java.util.Arrays;

public class TestFactory {
	
	public static void main(String[] args) {
	int count = 0;
	for (int[][][] c : Samples.cluesList()) {
		System.out.println("\nProcess result: ");
//		printMatrix(new Nonogram(c).solve());
		printPattern(new Nonogram(c).solve());
		System.out.println("Correct answer: ");
//		printMatrix(answers[count]);
		printPattern(Samples.answersList()[count]);
		System.out.println("result=answer? " + Arrays.deepEquals(new Nonogram(c).solve(), Samples.answersList()[count]));
		count++;
	}
}

static void printMatrix(int[][] bitMatrix) {
	for (int[] m : bitMatrix) {
		String matrix = Arrays.toString(m);
		System.out.println(matrix);
	}
}

static void printPattern(int[][] bitMatrix) {
	for (int[] m : bitMatrix) {
		String matrix = Arrays.toString(m);
		System.out.println(matrix.replaceAll("0", ".").replaceAll("1", "#").replaceAll("[\\[\\],]", ""));
	}
}

}
