package kde.jobcontainer.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.util.domain.DataOpLog;
import kde.jobcontainer.util.domain.DbConfig;

/**
 * 功能类, 通过参数文件获取数据,并获得连接,
 * 返回连接
 * @author teacher
 *
 */
public class ConnectionUtils {
	public static final Logger logger = LoggerFactory.getLogger( ConnectionUtils.class );
  private static String url;
  private static String dbUser;
  private static String dbPassword;
  
  /**
   * 读入filename文件中的键值对信息,给三个全局变量赋值
   * 
   * @param filename
   */
  public static void getParam(JSONObject json) {
      url = json.get("url")+";databaseName="+json.get("databasename");
      dbUser = (String)json.get("username");
      dbPassword = (String)json.get("password");
  }
  
  public static void getParam(DataOpLog d,JSONObject json) {
      url = json.get("url")+";databaseName="+d.getDatabasename();
      dbUser = (String)json.get("username");
      dbPassword = (String)json.get("password");
  }
  private static void getParam(Properties p) {
		url = p.getProperty("url");
		dbUser = p.getProperty("dbusername");
		dbPassword = p.getProperty("dbuserpwd");
		
  }
  /**
   * 通过三个参数构造连接并返回
   * @return 连接对象
   */
  public static Connection getConnection(JSONObject json){
    //先把三个必须的参数url/dbUser/dbPassword赋值
    getParam(json);

    Connection conn = null;
    try {
		Class.forName((String)json.get("driverclass"));
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    try{
      conn = DriverManager
        .getConnection(url, dbUser, dbPassword);
    }catch(Exception e){
      e.printStackTrace();
    }
    
    return conn;
  }
  
  public static Connection getConnection(DataOpLog d,JSONObject json){
	    //先把三个必须的参数url/dbUser/dbPassword赋值
	    getParam(d,json);

	    Connection conn = null;
	    try {
			Class.forName((String)json.get("driverclass"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		}
	    try{
	      conn = DriverManager
	        .getConnection(url, dbUser, dbPassword);
	    }catch(Exception e){
	    	logger.error(e.getMessage(),e);
	    }
	    
	    return conn;
	  }
  /**
   * 关闭ResultSet对象
   * @param rs
   */
  public static void close(ResultSet rs){
    try{
      if (rs != null) {
        rs.close();
      }
    }catch(Exception e){
    	logger.error(e.getMessage(),e);
    }
  }
  /**
   * 关闭Statement对象
   * @param stmt
   */
  public static void close(Statement stmt){
    try{
      if (stmt != null) {
        stmt.close();
      }
    }catch(Exception e){
    	logger.error(e.getMessage(),e);
    }
  }
  public static void close(Connection conn){
    try{
      if (conn != null){
        conn.close();
      }
    }catch(Exception e){
    	logger.error(e.getMessage(),e);
    }
  }

public static Connection getConnection(DbConfig config) {
	Connection conn = null;
	try {
		Class.forName( config.getDriver() );
		conn = DriverManager.getConnection( config.getUrl(), config.getUsername(), config.getUserpwd());
	} catch (ClassNotFoundException e) {
		logger.error(e.getMessage(),e);
	} catch (SQLException e) {
		logger.error(e.getMessage(),e);
	}
	return conn;
}

public static Connection getConnection(Properties p) {
	getParam(p);
	Connection conn = null;
    try {
		Class.forName(p.getProperty("driver"));
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		logger.error(e.getMessage(),e);
	}
    try{
      conn = DriverManager
        .getConnection(url, dbUser, dbPassword);
    }catch(Exception e){
    	logger.error(e.getMessage(),e);
    }
    return conn;
}


  
  
  
  
  
  
  
  
  
  
  
  
}
