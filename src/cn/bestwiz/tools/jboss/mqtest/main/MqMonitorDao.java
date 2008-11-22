package cn.bestwiz.tools.jboss.mqtest.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MqMonitorDao {
	
//	private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
//	private static final String URL = "jdbc:oracle:thin:@10.15.2.33:1521:jhf";
//	private static final String USERNAME = "main";
//	private static final String PASSWORD = "bestwiz";
	
	private static final String DRIVER = MqMonitor.CONF.getProperty("DRIVER");
	private static final String URL = MqMonitor.CONF.getProperty("URL");
	private static final String USERNAME = MqMonitor.CONF.getProperty("USERNAME");
	private static final String PASSWORD = MqMonitor.CONF.getProperty("PASSWORD");
	
	static {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> getMqList() throws SQLException{
		List<String> lst = new ArrayList<String>();
		String sql = " Select URL From JHF_JMS_VENDOR WHERE STATUS = 1";
		Connection con = DriverManager.getConnection(URL,USERNAME,PASSWORD);
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while (rs.next()) {
			lst.add(rs.getString("URL"));
		}
		rs.close();
		st.close();
		con.close();
		return lst;
	}
	
	public static void updateMqDown(String ip) throws SQLException{
		String sql = " Update JHF_JMS_VENDOR SET STATUS = 2,UPDATE_DATE = ? WHERE URL = ?";
		Connection con = DriverManager.getConnection(URL,USERNAME,PASSWORD);
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		ps.setString(2, ip);
		int count = ps.executeUpdate();
		if(count == 0){
			throw new SQLException("Update failed");
		}
		con.close();
	}
}
