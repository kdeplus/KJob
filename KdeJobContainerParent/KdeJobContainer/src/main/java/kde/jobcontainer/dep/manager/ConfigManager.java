package kde.jobcontainer.dep.manager;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.util.domain.DEPJobConfig;
import kde.jobcontainer.util.interfaces.ConfigHelper;

/**
 * @author lidong
 * 获取配置信息
 * 应该兼容配置文件\数据库等多种方式
 */
public class ConfigManager{
	
	private static Logger logger = LoggerFactory.getLogger( ConfigManager.class );
	private ConfigManager(){};
	private static ConfigManager cfgMgr;
	private static ConfigHelper cfgHelper;
	public static ConfigManager getInstance(Properties p) throws Exception{
		if(p==null){
			logger.error( "Properties为空,无法执行!" );
			System.exit(0);
		}
		logger.info( "初始化" );
		if(cfgMgr==null){
			cfgMgr = new ConfigManager();
			String configHelper = p.getProperty( "configHelper" );
			cfgHelper =  (ConfigHelper)Class.forName( "kde.jobcontainer.dep.manager.configHelper."+configHelper ).newInstance();
			cfgHelper.init( p );
		}
		return cfgMgr;
	}

	/**
	 * 如果配置保存在数据库中，应每隔一段时间监控配置情况，如配置发生变更进行任务上的处理通过ScheduleManager?
	 * @return
	 */
	public List<DEPJobConfig> getConfigs() {
		return this.cfgHelper.getConfigs();
	}
	
	public List<DEPJobConfig> getRefreshedConfigs() throws Exception{
		return this.cfgHelper.getRefreshedConfigs();
	}
	
	public void updateConfigs(List<DEPJobConfig> l){
		this.cfgHelper.updateConfigs( l );
	}
}
