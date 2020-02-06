import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

public class Nonogram {

	private static int[][][] x;

	public static int[][][] getX() {
		return x;
	}

	public Nonogram(int[][][] clues) {
		x = clues;
	}

	int[][] solve() {

		int[][] horrClues = Nonogram.getX()[0];
		int[][] vertClues = Nonogram.getX()[1];

		List<List<BitSet>> possibleRowsNumbersLists, possibleColsNumbersLists;
		possibleRowsNumbersLists = samplesListFactory(horrClues, vertClues.length);
		possibleColsNumbersLists = samplesListFactory(vertClues, horrClues.length);

		int sampleNumberToReduction;
		do {
			sampleNumberToReduction = reduceMutual(possibleRowsNumbersLists, possibleColsNumbersLists);
			if (sampleNumberToReduction == -1) {
				System.out.println("No solution");
				return null; 
			}
		} while (sampleNumberToReduction > 0);

		int[][] bitMatrix = bitMatrixGen(possibleColsNumbersLists, possibleRowsNumbersLists.size());
		return bitMatrix;
	}

	private int[][] bitMatrixGen(List<List<BitSet>> colsSamples, int rowsSamplesSize) {

		int[][] bitMatrix = new int[colsSamples.size()][rowsSamplesSize];
		int countRow = 0;
		for (List<BitSet> row : colsSamples) {
			for (int i = 0; i < rowsSamplesSize; i++) {
				bitMatrix[countRow][i] = row.get(0).get(i) ? 1 : 0;
			}
			countRow++;
		}
		return bitMatrix;
	}

	private List<List<BitSet>> samplesListFactory(int[][] twoDimIntInputArr, int len) {
		List<List<BitSet>> result = new ArrayList<>();
		for (int[] intArray : twoDimIntInputArr) {
			List<BitSet> lst = new LinkedList<>();
			int numbersSum = Arrays.stream(intArray).sum();
			List<String> fullCellsList = Arrays.stream(intArray).boxed().map(x -> markReproduction(x, "1"))
					.collect(toList());

			for (String r : permutationsGenerator(fullCellsList, len - numbersSum + 1)) {
				char[] bits = r.substring(1).toCharArray();
				BitSet bitset = new BitSet(bits.length);
				for (int i = 0; i < bits.length; i++)
					bitset.set(i, bits[i] == '1');
				lst.add(bitset);
			}
			result.add(lst);
		}
		return result;
	}

	// I don't know why, but this generator of permutations works ;)
	private List<String> permutationsGenerator(List<String> onesList, int zerosNumber) {
		if (onesList.isEmpty())
			return asList(markReproduction(zerosNumber, "0"));

		List<String> result = new ArrayList<>();
		for (int x = 1; x < zerosNumber - onesList.size() + 2; x++) {
			List<String> skipOne = onesList.stream().skip(1).collect(toList());
			for (String tail : permutationsGenerator(skipOne, zerosNumber - x))
				result.add(markReproduction(x, "0") + onesList.get(0) + tail);
		}
		return result;
	}

	private String markReproduction(int n, String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++)
			sb.append(s);
		return sb.toString();
	}

	/*
	 * If all row samples have a common value for a specific cell, then the only
	 * possible result is specified. All samples in the appropriate column must also
	 * have this value for this cell. Those that do not comply with this are
	 * removed. The same applies to all columns. The algorithm methodically checks
	 * the list back and forth until further samples cannot be deleted or the list
	 * is empty (then the failure is declared).
	 */

	private int reduceMutual(List<List<BitSet>> possibleRows, List<List<BitSet>> possibleCols) {
		int removedCountColsRows = mutualReduction(possibleRows, possibleCols);
		if (removedCountColsRows == -1)
			return -1;

		int removedCountRowsCols = mutualReduction(possibleCols, possibleRows);
		if (removedCountRowsCols == -1)
			return -1;

		return removedCountColsRows + removedCountRowsCols;
	}

	private int mutualReduction(List<List<BitSet>> a, List<List<BitSet>> b) {
		int removedCount = 0;

		for (int i = 0; i < a.size(); i++) {

			BitSet commonOn = new BitSet();
			commonOn.set(0, b.size());
			BitSet commonOff = new BitSet();

			// defines which values in all samples of a[i] must be common
			for (BitSet bitSetSample : a.get(i)) {
				commonOn.and(bitSetSample);
				commonOff.or(bitSetSample);
			}

			// remove from b[j] all samples that don't share the defined values
			for (int j = 0; j < b.size(); j++) {
				final int fi = i, fj = j;

				if (b.get(j).removeIf(cnd -> (commonOn.get(fj) && !cnd.get(fi)) || (!commonOff.get(fj) && cnd.get(fi))))
					removedCount++;

				if (b.get(j).isEmpty())
					return -1;
			}
		}
		return removedCount;
	}

}
