package kde.jobcontainer.dep.listeners;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.dep.domain.JobSchedulerBean;
import kde.jobcontainer.util.abstracts.JobConfigObserverAbstract;
import kde.jobcontainer.util.domain.DEPJobConfig;
import kde.jobcontainer.util.utils.BeanUtil;

public class JobConfigObserver extends JobConfigObserverAbstract{
	private static Logger logger = LoggerFactory.getLogger( JobConfigObserver.class );
	
	private DEPJobConfig oldCfg;	//持有原来的配置，用于对原来的任务进行处理
	private JobSchedulerBean jsb;	//持有任务相关资源
	
	public JobConfigObserver(JobSchedulerBean jsb){
		this.oldCfg = new DEPJobConfig();
		this.jsb = jsb;
		//对原配置进行备份，会耗费内存，但是当配置变更时可以获知变更内容
		BeanUtil.copyNotNullProperties(jsb.getConfig(), oldCfg);
	}

	/**
	 * <pre>
	 * 监视内容更新后的操作
	 * @param Observable o 为监控到发生变动的对象
	 * @param Object arg 为notifyObservers传入的参数
	 * 需在外部进行检查，单独的一个任务，定时触发config的查询，获取可用config,如果发现新的config和任务中正在执行的config不同，则调用原有config的notifyObservers方法。
	 * </pre>
	 */
	@Override
	public void update(Observable o, Object arg) {
		logger.info( "配置发生改变,进行重置" );
		
		try{
			//根据新的config进行任务处理
			DEPJobConfig j = (DEPJobConfig)o;
			//停止原来的任务
			jsb.deleteScheduleJob();
			//重新初始化jsb
			jsb.init(jsb.getScheduler(),j);
			//开始新任务
			jsb.startScheduleJob();
			//保存新配置
			BeanUtil.copyNotNullProperties(jsb.getConfig(), oldCfg);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
}
