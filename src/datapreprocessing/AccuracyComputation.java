package datapreprocessing;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * This class is used to compute the accuracy of our prediction.
 * @author txsing
 *
 */
public class AccuracyComputation {
	
	public static void main(String[] args) {
		try {
			Connection conn = DBConnector.connectPosgres();
			Statement stmt = conn.createStatement();
			
			String sql = "SELECT count(T.eid) FROM truth T, result R WHERE T.eid = R.eid AND T.result = R.result";
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				System.out.println(rs.getInt(1)/48217.0);
			}			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
