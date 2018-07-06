package kde.jobcontainer.util.utils.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.util.domain.DbConfig;
import kde.jobcontainer.util.utils.BaseConstants;
import kde.jobcontainer.util.utils.BeanUtil;
import kde.jobcontainer.util.utils.DateUtil;

public class DbUtil {
	public static Logger logger = LoggerFactory.getLogger( DbUtil.class );
	/**
	 * <pre>
	 * 在数据表中检查该表是否存在,语句中查询语句中绑定了derby数据库
	 * </pre>
	 * @author Lidong 2014-6-23
	 * @param name
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static synchronized boolean checkTable(String name,Connection conn) throws Exception{
		String driver =conn.getMetaData().getDriverName();
		if(name==null|| BaseConstants.EMPTY_STR.equals( name.trim() ) )
			throw new Exception("DbUtil.checkTable方法中,查询表名不能为空!");
		boolean result = false;
		ResultSet rs = null;
		if( driver.indexOf( "derby" )!=-1 ){
			rs = conn.createStatement().executeQuery("select * from SYS.SYSTABLES t where t.tablename like '%"+name.toUpperCase()+"%'");
		}else if(driver.indexOf("jtds")!=-1 || driver.indexOf( "microsoft.sqlserver" )!=-1){
			//TODO 检查SQLServer数据库中表是否存在
			rs = conn.createStatement().executeQuery( "SELECT  * FROM dbo.SysObjects WHERE ID = object_id(N'["+name+"]') AND OBJECTPROPERTY(ID, 'IsTable') = 1" );
		}else if(driver.indexOf( "oracle.jdbc" )!=-1){
			//TODO 检查Oracle数据库中表是否存在
			String user = conn.getMetaData().getUserName();	//用连接的username与表owner比较的话不是很严谨
			//select *  from all_tables where TABLE_NAME = 'EMP' and OWNER='SCOTT'; 
			//rs = conn.createStatement().executeQuery( "select * from all_tables where TABLE_NAME=upper('"+name+"') and OWNER=upper('"+user+"')" );
			rs = conn.createStatement().executeQuery( "select TABLE_NAME from all_tables where TABLE_NAME=upper('"+name+"') " );
		}else if(driver.indexOf( "mysql.jdbc" )!=-1){
			//TODO 检查mysql数据库中表是否存在
			//select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_NAME='res_mos_cpu_statistics'
			rs = conn.createStatement().executeQuery( "select * from INFORMATION_SCHEMA.TABLES where TABLE_NAME='"+name+"'" );
		}		
		if(rs!=null){
			result = rs.next();
			rs.close();
		}
		return result;
	}

	/**
	 * <pre>
	 * 删除数据表
	 * </pre>
	 * @author Lidong 2014-6-23
	 * @param name	删除表的名称
	 * @param conn	数据库连接
	 * @return	执行是否成功
	 * @throws Exception
	 */
	public static synchronized boolean dropTable(String name,Connection conn) throws Exception{
		return conn.createStatement().execute(" drop table "+name.toUpperCase()+"");
	}
	/**
	 * <pre>
	 * 执行sql语句
	 * </pre>
	 * @author Lidong 2014-6-23
	 * @param sql	sql语句
	 * @param conn	数据库连接
	 * @return	执行是否成功
	 * @throws Exception
	 */
	public static synchronized boolean execute(String sql,Connection conn) throws Exception{
		return conn.createStatement().execute( sql );
	}
	/**
	 * <pre>
	 * 获取数据库连接
	 * </pre>
	 * @author Lidong 2014-6-23
	 * @param cfg	封装数据库连接信息的对象
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection(DbConfig cfg) throws Exception{
		Connection conn = null;
		try {
			Class.forName( cfg.getDriver() );
			conn = DriverManager.getConnection( cfg.getUrl(), cfg.getUsername(), cfg.getUserpwd());
		} catch (ClassNotFoundException e) {
			logger.error( e.getMessage(),e );
		} catch (SQLException e) {
			logger.error( e.getMessage(),e );
		}
		return conn;
	}
	/**
	 * <pre>
	 * 获取数据库连接
	 * </pre>
	 * @author Lidong 2014-6-23
	 * @param driver	驱动
	 * @param url		地址
	 * @param username	用户名
	 * @param password	密码
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection(String driver,String url,String username,String password) throws Exception {
		Connection conn = null;
		try {
			Class.forName( driver );
			conn = DriverManager.getConnection( url, username, password);
		} catch (ClassNotFoundException e) {
			logger.error( e.getMessage() , e);
		} catch (SQLException e) {
			logger.error( e.getMessage() , e);
		}
		return conn;
	}

	/**
	 * <pre>
	 * 关闭数据库连接
	 * </pre>
	 * @author Lidong 2014-6-23
	 * @param conn
	 */
	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error( e.getMessage() , e);
			}
		}
	}

	public static Integer getInteger(ResultSet rs,String name) throws Exception{
		return rs.getInt( name );
	}
	public static String getString(ResultSet rs,String name) throws Exception{
		return rs.getString( name );
	}
	public static Double getDouble(ResultSet rs,String name) throws Exception{
		return rs.getDouble( name );
	}
	public static Timestamp getTimestamp(ResultSet rs,String name) throws Exception{
		return rs.getTimestamp( name );
	}
	/**
	 * <pre>
	 * 将json中的某一字段值转换为sql语句中对应的字符串形式的值
	 * </pre>
	 * @author Lidong 2014-6-23
	 * @param obj			持有数据的
	 * @param jsonFieldName	数据字段在json中的字段名称
	 * @param fieldType		数据类型,应为java中类的名称
	 * @param dbTypeHelper	数据库类型的名称,用来获取对应的helper将数据处理为sql格式语句
	 * @return
	 */
	public static String getJsonSQLValue(JSONObject obj,String jsonFieldName,String fieldType,String dbTypeHelper){
		if(!obj.containsKey( jsonFieldName )){
			return "null";
		}
		if("Integer".equals( fieldType )||"Double".equals( fieldType )||"Long".equals( fieldType )){
			//TODO 应该做校验
			return obj.getString( jsonFieldName );	
		}else if("String".equals( fieldType ) ){
			return '\''+obj.getString( jsonFieldName )+'\'';
		}else if("Date".equals( fieldType )||"Timestamp".equals( fieldType )){
			//TODO 应该由 dbTypeHelper来进行
			return '\''+ obj.getString( jsonFieldName ) +'\'';//"yyyy-MM-dd HH:mm:ss"
		}
		
		return null;
	}
	/**
	 * <pre>
	 * 将对象中的属性转换为拼写对应sql语句所需要的字符串值,根据其在对象中的类型进行自动判定和转换
	 * </pre>
	 * @author Lidong 2014-6-23
	 * @param obj			持有值的对象
	 * @param fieldName		字段名称
	 * @param dbTypeHelper	数据库类型的名称,用来获取对应的helper将数据处理为sql格式语句
	 * @return
	 */
	public static String getObjSQLValue(Object obj,String fieldName,String dbTypeHelper){
		Object v = BeanUtil.GetValueByPropertyName(fieldName, obj);
		if(v==null)
			return "null";
		if(v instanceof java.lang.String){
			return '\''+v.toString()+'\'';
		}else if(v instanceof java.lang.Integer || v instanceof java.lang.Double || v instanceof java.lang.Long){
			return  v.toString();
		}else if(v instanceof java.util.Date||v instanceof java.sql.Timestamp){
			//TODO 应该要按数据库类型区分
			return '\''+DateUtil.DateTimeToString( (Date)v , 9)+'\'';//"yyyy-MM-dd HH:mm:ss"
		}else{
			return v.toString();
		}
		
	}
}
