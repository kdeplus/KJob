package kde.jobcontainer.job.synchydro;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.job.synchydro.service.PptnSumDrpSyncService;
import kde.jobcontainer.util.abstracts.KJob;

/**
 * <pre>
 *    用来进行降雨量累计统计和对比同步的
 * </pre>
 * @author lidong
 * @date 2016年11月8日 上午10:17:29
 */
public class PptnSumDrpSyncJob extends KJob {
	
	private static Logger logger = LoggerFactory.getLogger( PptnSumDrpSyncJob.class );
	
	private PptnSumDrpSyncService psdsService;
	
	public PptnSumDrpSyncJob(){
		logger.debug( "实例化PptnSumDrpSyncJob" );
	}
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		this.executeInit(HYITSTriRecSyncJob.class, arg0);
		logger.info( "HYITSTriRecSendJob 执行工作!" +this.config.getName());
		//执行任务
		this.getPsdsService().doJob(this.config);
		logger.info( "HYITSTriRecSendJob 结束工作!" +this.config.getName() );
		
	}
	public PptnSumDrpSyncService getPsdsService() {
		if(this.psdsService==null)
			this.psdsService = PptnSumDrpSyncService.getInstance();
		return psdsService;
	}
	public void setPsdsService(PptnSumDrpSyncService psdsService) {
		this.psdsService = psdsService;
	}

}
