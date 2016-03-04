package datapreprocessing;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FullVectorHelper {
	static File input1 = new File("WOT_Test_Vector.csv");
	static File input2 = new File("WOT_Test_Vector_2.csv");
	static File output = new File("Test_FullVector.csv");

	public static void main(String[] args) {
		try {
			BufferedReader br1 = new BufferedReader(new FileReader(input1));
			BufferedReader br2 = new BufferedReader(new FileReader(input2));
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(output));
			String cur1 = "";
			String cur2 = "";
			while (cur1 != null && cur2 != null) {
				cur1 = br1.readLine();
				cur2 = br2.readLine();

				bWriter.write(cur1 + cur2 + "\n");
				bWriter.flush();
			}
			br1.close();
			br2.close();
			bWriter.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}

	/**
	 * Compute the total number of events happened in 3 time periods, and verify
	 * its correctness with raw data.
	 */
	public static void findError() {
		try {
			BufferedReader br1 = new BufferedReader(new FileReader(input1));
			BufferedReader br2 = new BufferedReader(new FileReader(input2));
			String cur1 = "";
			String cur2 = "";
			String[] array1 = null;
			String[] array2 = null;

			int i = 0;

			while (cur1 != null && cur2 != null) {
				i++;
				cur1 = br1.readLine();
				cur2 = br2.readLine();
				array1 = cur1.split(",");
				array2 = cur2.split(",");

				if (Integer.parseInt(array1[1]) + Integer.parseInt(array1[2]) + Integer.parseInt(array1[3]) != Integer
						.parseInt(array2[2])) {

					System.out.println(i);
					break;
				}
			}
			br1.close();
			br2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}