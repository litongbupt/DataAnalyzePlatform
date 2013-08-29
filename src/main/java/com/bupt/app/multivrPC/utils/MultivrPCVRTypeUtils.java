package com.bupt.app.multivrPC.utils;

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



public class MultivrPCVRTypeUtils {
	
	private static Map<String,String> map = null;
	
	private MultivrPCVRTypeUtils(){
		
	}
	
	public static Map<String,String> getVRType(){
		Properties prop = new Properties();
		InputStream is = JdbcUtils.class.getClassLoader()
				.getResourceAsStream("com/bupt/config/jdbc.properties");
		try {
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String driver = prop.getProperty("pcVrTypeDriver");
		String url = prop.getProperty("pcVrTypeUrl");
		String user = prop.getProperty("pcVrTypeUser");
		String password = prop.getProperty("pcVrTypePassword");
		
		if(map==null){
			map = new HashMap<String,String>();
			map.put("100601", "100601");
			map.put("100602", "100602");
			map.put("100603", "100603");
			map.put("209602", "209602");
			map.put("90000010_1", "翻译长句");
			map.put("90000010_2", "翻译词典");
			map.put("20009604", "酷讯机票国内城市到城市带flash");
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
				//rs = st.executeQuery("SELECT vr_id,res_name,distinct(vr_type) FROM vr_resource");
				//测试 TODO
				rs = st.executeQuery("SELECT distinct(type) FROM tb_detail_20130808");
				
				// 5.处理结果
				while (rs.next()) {
					//测试 TODO 
					map.put(rs.getString(1),rs.getString(1));
					//map.put(rs.getString(1),rs.getString(2));
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
