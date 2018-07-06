package kde.jobcontainer.dep.manager.configHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.dep.PlatformUtil;
import kde.jobcontainer.util.domain.DEPJobConfig;
import kde.jobcontainer.util.interfaces.ConfigHelper;

public class PropertiesConfigHelperImpl implements ConfigHelper{
	private static Logger logger = LoggerFactory.getLogger( PropertiesConfigHelperImpl.class );
	private List<DEPJobConfig> cfgs;
	private Properties prop;
	public List<DEPJobConfig> getConfigs() {
		return cfgs;
	}
	/*
	 configHelperInfo={datas:[{
	 					id:'1',serverId:'server_uuid',tag:'nsj_rsvr',type:'file_parse',
	 					name:'\u519C\u6C34\u5C40\u6C34\u5E93\u62A5\u6C5B\u6570\u636E',
	 					schedule:'0 0/20 * * * ?',jarPath:'e:/RWDEJ_File_20140408.jar',
	 					jobClassName:'kde.jobcontainer..dep.client.FileJob',
						jobConfig:{f
							ileDictory:'e:/datas',fileType:'txt',
							fileNameFilter:'YYYYMMDDHH'}
						}]
					}
	*/
	public void init(Properties property) throws Exception {
		this.prop = property;
		this.cfgs = this.getConfigs( this.prop );
	}
	
	private List<DEPJobConfig> getConfigs(Properties property) throws Exception{
		logger.info( "根据Properties进行初始化!" );
		String configHelperInfo = property.getProperty("configHelperInfo");
		if(configHelperInfo==null||"".equals( configHelperInfo.trim() )){
			throw new Exception("系统配置中并无PropertiesConfigHelperImpl所需配置信息！");
		}
		JSONObject json = JSONObject.parseObject( configHelperInfo );
		List<DEPJobConfig> jcs = new ArrayList<DEPJobConfig>();
		JSONArray arr = ((JSONObject)json).getJSONArray( "datas" );
		DEPJobConfig cfg = null;
		JSONObject j = null;//临时
		if(arr!=null&&arr.size()>0){
			for(Iterator it=arr.iterator();it.hasNext();){
				j = (JSONObject)it.next();
				cfg = new DEPJobConfig( j );
				logger.info( "得到任务配置："+cfg.getName() );
				jcs.add( cfg );
			}
		}
		return jcs;
	}
	/**
	 * <pre>
	 * @author lidong
	 * 可能只有properties的配置需要重新加载properties文件
	 * </pre>
	 */
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
