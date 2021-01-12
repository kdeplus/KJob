package kde.jobcontainer.job.synchydro;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.job.synchydro.service.HYITSTriggerRecordSyncToV2Service;
import kde.jobcontainer.util.abstracts.KJob;
import kde.jobcontainer.util.domain.DEPJobConfig;


/**
 * @author lidong 20210108
 * <pre>
 *   代码调整适配一下，在原来基础上，增加对管家库到融合库版本的渗压、渗流的特殊处理，主要是数据字段有变化
	 配置上没有什么差异，主要在tblAndPks需要写目标表的表表名以及主键字段名
 * </pre>
 */
public class HYITSTriRecSyncToV2Job extends KJob {

	private static Logger logger = LoggerFactory.getLogger( HYITSTriRecSyncToV2Job.class );
	private HYITSTriggerRecordSyncToV2Service htrsService;	//服务
	
	public HYITSTriRecSyncToV2Job(){
		logger.debug( "实例化HYITSTriRecSyncToV2Job" );
	}
	
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 * 任务执行方法
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		this.executeInit(HYITSTriRecSyncToV2Job.class, arg0);
		logger.info( "HYITSTriRecSyncToV2Job 执行工作!" +this.config.getName());
		//执行任务
		this.getHtrsService().doJob(this.config);
		logger.info( "HYITSTriRecSyncToV2Job 结束工作!" +this.config.getName() );	
	}

	public static void main(String[] args) throws Exception{
		DEPJobConfig dp = new DEPJobConfig();
		PropertyConfigurator.configure("/mnt/Work/Tepia/04_Code/KJob/KdeJobContainerParent/Job_SyncHydro/log4j.properties");
		File file = new File("/mnt/Work/Tepia/04_Code/KJob/KdeJobContainerParent/Job_SyncHydro/src/main/java/config_hyitstriggerrec_sync_toV2.xml");
	    InputStream in = new FileInputStream(file);
	  
		Properties p = new Properties();
		p.loadFromXML( in );
		String configHelperInfo = p.getProperty("configHelperInfo");
		configHelperInfo = configHelperInfo.trim();
		System.out.println( configHelperInfo );
		JSONObject json = JSONObject.parseObject( configHelperInfo );
		JSONArray arr = ((JSONObject)json).getJSONArray( "datas" );
		DEPJobConfig cfg = new DEPJobConfig( arr.getJSONObject(0) );
		HYITSTriRecSyncToV2Job d = new HYITSTriRecSyncToV2Job();
		d.getHtrsService().doJob(  cfg );
	}
	
	
	

	public HYITSTriggerRecordSyncToV2Service getHtrsService() {
		//注意，这里用子类做的初始化，应该获取到的是子类中的实例
		if(this.htrsService==null)
			this.htrsService = HYITSTriggerRecordSyncToV2Service.getInstance();
		return htrsService;
	}

	public void setHtrsService(HYITSTriggerRecordSyncToV2Service htrsService) {
		this.htrsService = htrsService;
	}
	
}
