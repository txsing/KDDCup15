package datapreprocessing;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class WeightComputaion {
		
	public static void main(String[] args) {
		File output = new File("weight.csv");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));
			
			Connection conn = DBConnector.connectPosgres();
			Statement stmt = conn.createStatement();
			
			String sql = "select L.etype,T.result, L.etime,count(T.result) "
					+ "FROM enrollment_train E, log L, truth T "
					+ "WHERE E.eid = L.eid AND E.eid = T.eid "
					+ "GROUP BY L.etime, T.result, L.etype "
					+ "ORDER BY L.etime, L.etype, T.result";
			
			System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			String result = null;
			while(rs.next()){
				result = rs.getString(1)+","+rs.getString(2)+","+rs.getString(3)+","+rs.getString(4)+"\n";
				writer.write(result);
				writer.flush();
			}
			writer.close();
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}
}
