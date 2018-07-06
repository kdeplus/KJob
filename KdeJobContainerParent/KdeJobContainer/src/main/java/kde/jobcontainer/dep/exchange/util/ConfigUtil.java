package kde.jobcontainer.dep.exchange.util;

import java.util.Properties;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.dataSync.util.DataConstants;
import kde.jobcontainer.dep.PlatformUtil;
import kde.jobcontainer.util.domain.DbConfig;

/**
 * @author xxxxxxxx
 * @date 2014-9-21, 下午8:09:44
 * 从默认的配置文件(默认是configs3.xml)中获取到相关的配置数据
 */
public class ConfigUtil {
	private ConfigUtil() throws Exception {
		init();
	}
	
	private static ConfigUtil instance;
	
	private Properties props;
	
	private DbConfig dbConfig;
	
	public static ConfigUtil getInstance() throws Exception {
		if(instance == null) {
			instance = new ConfigUtil();
		}
		return instance;
	}
	
	private void init() throws Exception {  //数据初始化
		this.props = PlatformUtil.getPropertites();
		JSONObject dbConfigJson = new JSONObject();
		dbConfigJson.put("driver", this.props.get(DataConstants.DEFAULT_DB_DRIVER_KEY));
		dbConfigJson.put("url", this.props.get(DataConstants.DEFAULT_DB_URL_KEY));
		String dbType = this.props.getProperty(DataConstants.DEFAULT_DB_TYPE_KEY);
		if(!DataConstants.DERBY_DB_TYPE_UPPER_SIGN.equals(dbType.toUpperCase())) {   //如果不是DERBY的话就要配置用户名和密码
			dbConfigJson.put("username", this.props.getProperty(DataConstants.DEFAULT_DB_USERNAME_KEY));
			dbConfigJson.put("userpwd", this.props.getProperty(DataConstants.DEFAULT_DB_PASSWORD_KEY));
		}
		this.dbConfig = new DbConfig(dbConfigJson);
	}
	
	public String getDataSyncName() {  //得到本同步程序的配置项
		return this.props.getProperty(DataConstants.SYNCNAME_KEY);
	}
	
	public int getSocketOpenPort() {  //获取到开放端口
		return Integer.parseInt(this.props.getProperty(DataConstants.SYNC_OPEN_PORT_KEY));
	}
 
	public String getJarFileBasePath() {   //得到配置的jar包根路径
		return this.props.getProperty(DataConstants.DEFAULT_JARFILE_PATH);
	}
	
	public String getMqUrl() {  //得到获取订阅信息的mq位置
		return this.props.getProperty(DataConstants.MQ_URL_KEY);
	}
	
	public String getTopicName() {
		String forHead = this.props.getProperty(DataConstants.MQ_TOPIC_FORHEAD_KEY);   //先得到前缀
		String syncName = this.getDataSyncName();
		return forHead+syncName;
	}
	
	public DbConfig getDbConfig() {
		return dbConfig;
	}
	public void setDbConfig(DbConfig dbConfig) {
		this.dbConfig = dbConfig;
	}
	
}

