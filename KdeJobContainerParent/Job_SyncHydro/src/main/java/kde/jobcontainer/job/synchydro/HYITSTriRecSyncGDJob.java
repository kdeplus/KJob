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

import kde.jobcontainer.job.synchydro.service.HYITSTriggerRecordSyncToGDService;
import kde.jobcontainer.util.abstracts.KJob;
import kde.jobcontainer.util.domain.DEPJobConfig;


/**
 * @author lidong 20191116
 * <pre>
 * 按广东的格式拼装实时水雨情、图像、渗流数据的内容
 * </pre>
 */
public class HYITSTriRecSyncGDJob extends KJob {

	private static Logger logger = LoggerFactory.getLogger( HYITSTriRecSyncGDJob.class );
	private HYITSTriggerRecordSyncToGDService htrsService;	//服务
	
	public HYITSTriRecSyncGDJob(){
		logger.debug( "实例化HYITSTriRecSyncJob" );
	}
	
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 * 任务执行方法
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		this.executeInit(HYITSTriRecSyncGDJob.class, arg0);
		logger.info( "HYITSTriRecSendJob 执行工作!" +this.config.getName());
		//执行任务
		this.getHtrsService().doJob(this.config);
		logger.info( "HYITSTriRecSendJob 结束工作!" +this.config.getName() );	
	}

	public static void main(String[] args) throws Exception{
		DEPJobConfig dp = new DEPJobConfig();
		PropertyConfigurator.configure("/mnt/605AC4275AC3F834/Tepia/04_Code/KJob/KdeJobContainerParent/Job_SyncHydro/log4j.properties");
		File file = new File("/mnt/605AC4275AC3F834/Tepia/04_Code/KJob/KdeJobContainerParent/Job_SyncHydro/src/main/java/config_hyitstriggerrec_gd_sync.xml");
	    InputStream in = new FileInputStream(file);
	  
		Properties p = new Properties();
		p.loadFromXML( in );
		String configHelperInfo = p.getProperty("configHelperInfo");
		configHelperInfo = configHelperInfo.trim();
		System.out.println( configHelperInfo );
		JSONObject json = JSONObject.parseObject( configHelperInfo );
		JSONArray arr = ((JSONObject)json).getJSONArray( "datas" );
		DEPJobConfig cfg = new DEPJobConfig( arr.getJSONObject(0) );
		HYITSTriRecSyncGDJob d = new HYITSTriRecSyncGDJob();
		d.getHtrsService().doJob(  cfg );
	}
	
	
	

	public HYITSTriggerRecordSyncToGDService getHtrsService() {
		//注意，这里用子类做的初始化，应该获取到的是子类中的实例
		if(this.htrsService==null)
			this.htrsService = HYITSTriggerRecordSyncToGDService.getInstance();
		return htrsService;
	}

	public void setHtrsService(HYITSTriggerRecordSyncToGDService htrsService) {
		this.htrsService = htrsService;
	}
	
}
