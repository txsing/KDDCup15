package datapreprocessing;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnector {
	public static final String url = "jdbc:postgresql://localhost/kddcup";
	public static final String postgredriver = "org.postgresql.Driver";
	public static final String user = "postgres";
	public static final String password = "root";
	
	public static Connection connectPosgres(){
		try {
			Class.forName(postgredriver).newInstance();
			Connection conn = DriverManager.getConnection(url, user, password);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
