package kde.jobcontainer.dep.manager.configHelper;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.dep.PlatformUtil;
import kde.jobcontainer.dep.dao.JobDbUtil;
import kde.jobcontainer.util.domain.DEPJobConfig;
import kde.jobcontainer.util.domain.DbConfig;
import kde.jobcontainer.util.interfaces.ConfigHelper;

public class DbConfigHelperImpl implements ConfigHelper {
	private static Logger logger = LoggerFactory.getLogger( PropertiesConfigHelperImpl.class );
	private List<DEPJobConfig> cfgs;
	private Properties prop;
	public List<DEPJobConfig> getConfigs() {
		return cfgs;
	}

	public void init(Properties property) throws Exception{
		this.prop = property;
		this.cfgs = this.getConfigs( this.prop );
	}
	
	private List<DEPJobConfig> getConfigs(Properties property) throws Exception{
		logger.info( "根据Properties进行初始化!" );
		String dburl = property.getProperty("dburl");
		String dbdriver = property.getProperty("dbdriver");
		if(dburl==null||"".equals( dburl.trim() )||dbdriver==null||"".equals( dbdriver.trim() )){
			throw new Exception("系统配置中并无PropertiesConfigHelperImpl所需配置信息！");
		}
		JSONObject json = new JSONObject();
		json.put("url", dburl);
		json.put("driver", dbdriver);
		JobDbUtil dbUtil = new JobDbUtil( new DbConfig(json));
		dbUtil.checkAndCreateTable();
		List<DEPJobConfig> jcs = dbUtil.getConfigs();
		System.out.println(jcs.size());
		return jcs;
	}
	public List<DEPJobConfig> getRefreshedConfigs() throws Exception{
		logger.info( "重新加载properties，并且重新获取配置!" );
		//由于参数保存在properti中，需要重新加载properties文件
		this.prop = PlatformUtil.getPropertites();
		return this.getConfigs( this.prop );
	}
	
	public void updateConfigs(List<DEPJobConfig> l){
		this.cfgs = l;
	}
}
