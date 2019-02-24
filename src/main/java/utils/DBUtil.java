package utils;

import com.alibaba.druid.pool.DruidDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {

	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	private static int maxline;
	private static DruidDataSource basicDataSource;
	public static ThreadLocal<Connection> ctLocal= new  ThreadLocal<Connection>();;
	
	static{
	
		String path ="config/db.properties";
		try {
			InputStream inputStream = DBUtil.class.getClassLoader().getResourceAsStream(path);
			Properties properties = new Properties();
			properties.load(inputStream);
			driver = properties.getProperty("driver");
			url = properties.getProperty("url");
			username = properties.getProperty("username");
			password = properties.getProperty("password");
			maxline = Integer.parseInt(properties.getProperty("maxline"));
			
			basicDataSource = new DruidDataSource();
			basicDataSource.setDriverClassName(driver);
			basicDataSource.setUrl(url);
			basicDataSource.setUsername(username);
			basicDataSource.setPassword(password);
			basicDataSource.setMaxActive(maxline);
			
			System.out.println(driver+","+url+","+username
					+","+password);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		
		Connection connection = ctLocal.get();
		try {
			if(connection==null){
				connection = basicDataSource.getConnection();
				ctLocal.set(connection);
			}
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static void begin() {
		
		Connection connection = ctLocal.get();
		try {
			if(connection!=null){
				connection.setAutoCommit(false);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void commit() {
		
		Connection connection = ctLocal.get();
		try {
			if(connection!=null){
				connection.commit();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void rollback(){
		
		Connection connection = ctLocal.get();
		try {
			if(connection!=null){
				connection.rollback();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void closeConnection(){
		
		Connection connection = ctLocal.get();
		try {
			if(connection!=null){
				connection.close();
				ctLocal.remove();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// 测试数据库能否连接
		//Connection connection = getConnection();
		//System.out.println(connection);
	}
}
