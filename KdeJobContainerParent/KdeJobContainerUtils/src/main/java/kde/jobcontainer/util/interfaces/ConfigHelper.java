package kde.jobcontainer.util.interfaces;

import java.util.List;
import java.util.Properties;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.util.domain.DEPJobConfig;

public interface ConfigHelper {
	
	public List<DEPJobConfig> getConfigs();
	
	public void init(Properties property) throws Exception;
	
	public List<DEPJobConfig> getRefreshedConfigs() throws Exception;
	
	public void updateConfigs(List<DEPJobConfig> l);
}
