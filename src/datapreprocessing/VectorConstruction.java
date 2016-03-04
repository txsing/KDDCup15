package datapreprocessing;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;

public class VectorConstruction {

	public static double[] weight = new double[7];

	public static void main(String[] args) {
		File output = new File("WOT_Test_Vector.csv");
		BufferedWriter writer = null;
		try {
			Connection conn = DBConnector.connectPosgres();
			Statement stmt = conn.createStatement();
			
			// String sql = "SELECT L.eid, etype, etime, count(etype)"
			// + "FROM coursedate C, enrollment_test E, log L "
			// + "WHERE L.eid = E.eid AND "
			// + "E.cid = C.cid GROUP BY " + "L.etime, L.eid, etype;";

			String sql2 = "SELECT L.eid, etype, count(etype), T.result " + "FROM enrollment_test E, log L, Truth T "
					+ "WHERE L.eid = E.eid AND " + "E.eid = T.eid GROUP BY " + "L.eid, etype, T.result;";

			// String sql3 = "SELECT L.eid, etype, L.etime,count(etype),
			// T.result "
			// + "FROM enrollment_train E, log L, truth T "
			// + "WHERE L.eid = E.eid AND "
			// + "E.eid = T.eid GROUP BY " + "L.eid, etype, L.etime, T.result;";

			// String sql4 = "select V11.count1 + V21.count, "
			// + "V12.count2 + V22.count, "
			// + "V13.count2 + V23.count "
			// + "FROM vector_train V11, vectorwtt_train V21,"
			// + "vector_train V12, vectorwtt_train V22, "
			// + "vector_train V13, vectorwtt_train V23 "
			// + "WHERE V11.eid = V21.eid AND V21.timeperiod=1 AND "
			// + "V12.eid = V22.eid AND V22.timeperiod=2 AND "
			// + "V13.eid = V23.eid AND V23.timeperiod=3";
			
			System.out.println(sql2);
			ResultSet rs = stmt.executeQuery(sql2);
			System.out.println("good");
			writer = new BufferedWriter(new FileWriter(output));
			String result = null;
			while (rs.next()) {
				result = rs.getString(1) + ',' + rs.getString(2) + ',' + rs.getString(4) + ',' + rs.getString(3) + "\n";
				writer.write(result);
				writer.flush();
			}
			conn.close();
			writer.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}
