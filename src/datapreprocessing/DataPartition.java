package datapreprocessing;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/**
 * This class is used to randomly pick certain number of records out from raw
 * dataset to generate 2 sub-datasets.
 * 
 * @author txsing
 *
 */
public class DataPartition {

	public static void main(String[] args) {
		filePartition("enrollment.csv");
	}

	
	/**
	 * randomly choose N numbers from [min, min+1, min+2, ... , max-1, max, n]
	 * @return The array which stores the resulting N numbers.
	 */
	public static Integer[] randomArray(int min, int max, int n) {
		int len = max - min + 1;
		if (max < min || n > len) {
			return null;
		}

		int[] source = new int[len];
		for (int i = min; i < min + len; i++) {
			source[i - min] = i;
		}

		Integer[] result = new Integer[n];
		Random rd = new Random();
		int index = 0;
		for (int i = 0; i < result.length; i++) {
			index = Math.abs(rd.nextInt() % len--);
			result[i] = source[index];
			source[index] = source[len];
		}
		return result;
	}

	public static void filePartition(String inputfilename) {
		File input = new File(inputfilename);
		File train = new File(inputfilename + "_train.csv");
		File test = new File(inputfilename + "_test.csv");

		HashSet<Integer> trainSet = null;

		Integer[] trainArray = new Integer[72325];
		trainArray = randomArray(0, 120541, 72325);
		trainSet = new HashSet<Integer>(Arrays.asList(trainArray));

		BufferedReader reader = null;
		BufferedWriter trainWr = null;
		BufferedWriter testWr = null;

		try {
			reader = new BufferedReader(new FileReader(input));
			trainWr = new BufferedWriter(new FileWriter(train));
			testWr = new BufferedWriter(new FileWriter(test));

			String curLine = null;
			int i = 0;

			while ((curLine = reader.readLine()) != null) {
				if (trainSet.contains(i)) {
					trainWr.write(curLine + "\n");
					trainWr.flush();
				} else {
					testWr.write(curLine + "\n");
					testWr.flush();
				}
				i++;
			}

			trainWr.close();
			testWr.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}
