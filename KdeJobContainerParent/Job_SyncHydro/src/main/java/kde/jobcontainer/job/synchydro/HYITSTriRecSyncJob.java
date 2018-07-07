package kde.jobcontainer.job.synchydro;


import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.job.synchydro.service.HYITSTriggerRecordSyncService;
import kde.jobcontainer.util.abstracts.KJob;


/**
 * @author lidong 2015�?�?9�?
 * <pre>
 * 
 * </pre>
 */
public class HYITSTriRecSyncJob extends KJob {

	private static Logger logger = LoggerFactory.getLogger( HYITSTriRecSyncJob.class );
	private HYITSTriggerRecordSyncService htrsService;	//服务
	
	public HYITSTriRecSyncJob(){
		logger.debug( "实例化HYITSTriRecSyncJob" );
	}
	
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 * 任务执行方法
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		this.executeInit(HYITSTriRecSyncJob.class, arg0);
		logger.info( "HYITSTriRecSendJob 执行工作!" +this.config.getName());
		//执行任务
		this.getHtrsService().doJob(this.config);
		logger.info( "HYITSTriRecSendJob 结束工作!" +this.config.getName() );	
	}


	public HYITSTriggerRecordSyncService getHtrsService() {
		if(this.htrsService==null)
			this.htrsService = HYITSTriggerRecordSyncService.getInstance();
		return htrsService;
	}

	public void setHtrsService(HYITSTriggerRecordSyncService htrsService) {
		this.htrsService = htrsService;
	}
	
}
