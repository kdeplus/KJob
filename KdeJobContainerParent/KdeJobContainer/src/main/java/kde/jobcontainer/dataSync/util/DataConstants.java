package kde.jobcontainer.dataSync.util;

/**
 * @author xxxxxxxx
 * @date 2014-9-21, 下午5:20:20
 * socket服务基本参数配置
 */
public class DataConstants {
	//出现错误时，返回数据的错误展示Key值
	public static final String ERROR_MESSAGE_KEY = "Error_Message";  
	
	//成功时的基本消息key
	public static final String BASE_SUCCESS_KEY = "Success_Message";
	
	//开启的默认端口是1555
	public static final int DEFAULT_OPEN_PORT = 1555;   
	
	public static final String DEFAULT_DB_URL_KEY = "dburl";
	
	public static final String DEFAULT_DB_DRIVER_KEY = "dbdriver";
	
	public static final String DEFAULT_DB_TYPE_KEY = "dbtype";
	
	public static final String DERBY_DB_TYPE_UPPER_SIGN = "DERBY";
	
	public static final String DEFAULT_DB_USERNAME_KEY = "dbusername";
	
	public static final String DEFAULT_DB_PASSWORD_KEY = "dbuserpwd";
	
	public static final String SYNCNAME_KEY = "syncName";   //本同步程序名
	
	public static final String SYNC_OPEN_PORT_KEY = "socketPort";    //开放端口
	
	public static final String DEFAULT_JARFILE_PATH = "jarFilePath";   //配置中的jar包放置位置
	
	public static final String MQ_URL_KEY = "mqUrl";  
	
	public static final String MQ_TOPIC_FORHEAD_KEY = "mqForHead";
	
	public static final int DEFAULT_SYS_STATUS = 1;   //初始化时默认开启服务
	
}

