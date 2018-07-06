package kde.jobcontainer.util.abstracts;

import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.util.domain.DEPJobConfig;
import kde.jobcontainer.util.utils.BaseConstants;

public abstract class KJob implements Job  {
	public DEPJobConfig config;
	public Logger logger;	//20140817 lidong logge不能用静态的,否则子类会每次执行会对同个logger不断赋值
	
	private static Map<String , URLClassLoader> loaders = new HashMap<String,URLClassLoader>();
	/**
	 * <pre>
	 * 初始化子类job的相关配置和日志工具
	 * </pre>
	 * @author Lidong 2014-8-17
	 * @param c
	 * @param jec
	 */
	public void executeInit(Class c,JobExecutionContext jec){
		//由于job不是单例模式，不能持有参数、状态等信息， 需要使用jobdetail进行传参
		if(jec!=null)
			this.config = (DEPJobConfig)jec.getJobDetail().getJobDataMap().get( BaseConstants.CONFIG_IN_JOBDATAMAP_NAME );
		logger = LoggerFactory.getLogger( c );
	}

	public Object getProperty(String name){
		if(config==null||config.getJobConfig()==null)
			return null;
		return config.getJobConfigJson().get( name );
	}
	public String getStringProperty(String name){
		if(config==null||config.getJobConfig()==null)
			return null;
		return config.getJobConfigJson().getString( name );
	}
	public Integer getIntegerProperty(String name){
		if(config==null||config.getJobConfig()==null)
			return null;
		return config.getJobConfigJson().getInteger( name );
	}
	public JSONObject getJSONObject(String name){
		if(config==null||config.getJobConfig()==null){
			logger.info("****************************** config is null");
			return null;
		}
		return config.getJobConfigJson().getJSONObject( name );
	}
	
	/**
	 * <pre>
	 * 持有类加载器
	 * </pre>
	 * @author Lidong 2014-8-31
	 * @param loader
	 */
	public void setURLClassLoader(String serverId,URLClassLoader loader){
		loaders.put(serverId, loader);
	}
	
	public URLClassLoader getURLClassLoader(){
		return loaders.get( this.config.getServerId() );
	}
}
