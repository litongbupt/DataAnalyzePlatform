package com.bupt.app.multivrWAP.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.bupt.core.base.util.JdbcUtils;

public class MultivrWAPVRTypeUtils {

	private static Map<String,String> map;

	public static Map<String, String> getVRType() {
		Properties prop = new Properties();
		InputStream is = JdbcUtils.class.getClassLoader()
				.getResourceAsStream("com/bupt/config/jdbc.properties");
		try {
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String driver = prop.getProperty("wapVrTypeDriver");
		String url = prop.getProperty("wapVrTypeUrl");
		String user = prop.getProperty("wapVrTypeUser");
		String password = prop.getProperty("wapVrTypePassword");
		
		if(map==null){
			map = new HashMap<String,String>();
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			try {
				// 2.建立连接
				conn = JdbcUtils.getConnection(driver,url,user,password);
				// conn = JdbcUtilsSing.getInstance().getConnection();
				// 3.创建语句
				st = conn.createStatement();

				// 4.执行语句
				rs = st.executeQuery("select VRTYPE, VRID, VRNAME from ubs_web_vrinfo order by VRID");
				
				// 5.处理结果
				while (rs.next()) {
					map.put(rs.getString(2),rs.getString(3));
					// 参数中的1,2,3是指sql中的列索引
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtils.free(rs, st, conn);
			}
		}
		return map;
	}

}
