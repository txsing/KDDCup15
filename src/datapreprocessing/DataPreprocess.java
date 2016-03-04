package datapreprocessing;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataPreprocess {
	public static void main(String[] args) {
		constructFullVector();
		encodeDate();
		encodeEvent("log.csv");
	}
	
	/**
	 *	This  
	 */
	public static void constructFullVector() {
		File input = new File("WT_Test_Vector.csv");
		File output = new File("WOT_Test_Vector_2.csv");

		try {
			BufferedReader br = new BufferedReader(new FileReader(input));
			BufferedWriter bw = new BufferedWriter(new FileWriter(output));
			String curLine = "491,3,1,3,0";
			String tmp = "";
			String[] curLineArray = null;
			String[] tmpArray = null;
			
			while (curLine != null) {
				String result = "";
				
				for (int i = 1; i < 4; i++) {
					tmp = "" + curLine;
					tmpArray = tmp.split(",");
					curLine = br.readLine();
					if (curLine == null) {
						break;
					}
					
					curLineArray = curLine.split(",");

					if ((curLineArray[0].equals(tmpArray[0]) && curLineArray[1].equals(tmpArray[1]))) {
						// [12] or [23]
						if (Integer.parseInt(curLineArray[2]) == i) {
							result = result + "," + curLineArray[3];

							// [13]
						} else if (Integer.parseInt(curLineArray[2]) - i == 1) {
							i++;
							result = result + ",0," + curLineArray[3];
						}
					} else {
						// [3][1]
						if (Integer.parseInt(curLineArray[2]) - i == 0 && i == 1) {
							result = "," + curLineArray[3];

							// [2][3]
						} else if (Integer.parseInt(curLineArray[2]) - i == 0 && i == 3) {
							result = result + ",0";
							bw.write(result + "\n");
							bw.flush();
							result = ",0,0," + curLineArray[3];

							// [1][2]
						} else if (Integer.parseInt(curLineArray[2]) - i == 0 && i == 2) {
							result = result + ",0,0";
							bw.write(result + "\n");
							bw.flush();
							result = ",0," + curLineArray[3];

							// [1][3]
						} else if (Integer.parseInt(curLineArray[2]) - i == 1 && i == 2) {
							i++;
							result = result + ",0,0";
							bw.write(result + "\n");
							bw.flush();
							result = ",0,0," + curLineArray[3];

							// [3][2]
						} else if (Integer.parseInt(curLineArray[2]) - i == 1 && i == 1) {
							i++;
							result = ",0," + curLineArray[3];

							// [3][3]
						} else if (Integer.parseInt(curLineArray[2]) - i == 2) {
							i = i + 2;
							result = result + ",0,0," + curLineArray[3];

							// [1][1]
						} else if (Integer.parseInt(curLineArray[2]) - i == -1 && i == 2) {
							i = i - 1;
							result = result + ",0,0";
							bw.write(result + "\n");
							bw.flush();
							result = "," + curLineArray[3];

							// [2][2]
						} else if (Integer.parseInt(curLineArray[2]) - i == -1 && i == 3) {
							i = i - 1;
							result = result + ",0";
							bw.write(result + "\n");
							bw.flush();
							result = ",0," + curLineArray[3];

							// [2][1]
						} else if (Integer.parseInt(curLineArray[2]) - i == -2) {
							i = i - 2;
							result = result + ",0";
							bw.write(result + "\n");
							bw.flush();
							result = "," + curLineArray[3];
						}
					}

				}
				bw.write(result + "\n");
				bw.flush();
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}

	public static void encodeDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			File output = new File("log_timeprocess.csv");
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));

			Connection conn = DBConnector.connectPosgres();
			Statement stmt = conn.createStatement();
			String sql1 = "SELECT C.fromdate, L.eid, L.etime, L.esource, L.etype,  L.oid "
					+ "FROM log L, enrollment E, coursedate C " + "WHERE L.eid = E.eid AND E.cid = C.cid";

			ResultSet rs = stmt.executeQuery(sql1);
			Date date1 = null;
			Date date2 = null;
			long day = 1;
			String result = null;
			while (rs.next()) {
				date1 = format.parse(rs.getString(1));
				date2 = format.parse(rs.getString(3));
				day = (date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000);

				if (day > 30) {
					day = 4;
				} else if (day > 20) {
					day = 3;
				} else if (day > 10) {
					day = 2;
				} else {
					day = 1;
				}

				result = rs.getString(2) + "," + day + "," + rs.getString(4) + "," + rs.getString(5) + ","
						+ rs.getString(6) + "\n";
				writer.write(result);
				writer.flush();

			}
			writer.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void encodeEvent(String searchResultFile) {
		File input = new File(searchResultFile);
		File output = new File("log_train_aftpro.csv");
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(new FileReader(input));
			writer = new BufferedWriter(new FileWriter(output));
			String curLine = null;
			while ((curLine = reader.readLine()) != null) {
				String[] log = curLine.split(",");

				// timestamp
				log[1] = log[1].substring(0, 10);

				// source
				if (log[2].equals("server")) {
					log[2] = "0";
				}
				if (log[2].equals("browser")) {
					log[2] = "1";
				}

				// event
				if (log[3].equals("navigate")) {
					log[3] = "0";
				}
				if (log[3].equals("access")) {
					log[3] = "1";
				}
				if (log[3].equals("problem")) {
					log[3] = "2";
				}
				if (log[3].equals("page_close")) {
					log[3] = "3";
				}
				if (log[3].equals("wiki")) {
					log[3] = "4";
				}
				if (log[3].equals("video")) {
					log[3] = "5";
				}
				if (log[3].equals("discussion")) {
					log[3] = "6";
				}

				writer.write(log[0] + "," + log[1] + "," + log[2] + "," + log[3] + "," + log[4] + "\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			System.err.println("error");
		}
	}

}
