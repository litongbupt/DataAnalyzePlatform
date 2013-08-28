package com.bupt.core.base.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * JDBC工具
 * @author litong
 *
 */
public final class JdbcUtils {

	private JdbcUtils() {
		throw new AssertionError();
	}

	public static Connection getConnection(String driver,String url,String user,String password) throws SQLException {
		 //Class.forName(driver);虚拟机已经加载过改驱动，无需再次加载
		 return DriverManager.getConnection(url, user, password);
	}

	public static void free(ResultSet rs, Statement st, Connection conn) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (conn != null)
					try {
						conn.close();
						// myDataSource.free(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}
	}
}
