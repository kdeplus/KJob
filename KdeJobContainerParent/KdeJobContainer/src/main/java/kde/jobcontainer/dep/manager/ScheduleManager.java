package kde.jobcontainer.dep.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.dep.domain.JobSchedulerBean;
import kde.jobcontainer.dep.listeners.JobConfigObserver;
import kde.jobcontainer.util.domain.DEPJobConfig;
import kde.jobcontainer.util.utils.BaseConstants;

public class ScheduleManager {
	private static Logger logger = LoggerFactory.getLogger( ScheduleManager.class );
	private Map<String,JobSchedulerBean> jobScheduleMap;
	private SchedulerFactory sf;
	
	public ScheduleManager(List<DEPJobConfig> configs)throws Exception {
		logger.info("初始化！");
		jobScheduleMap = new HashMap<String,JobSchedulerBean>();
		sf = new StdSchedulerFactory();
		Scheduler sched;
		DEPJobConfig cfg = null;
		JobSchedulerBean jsb = null;
		JobConfigObserver configObserver = null;
		for (Iterator<DEPJobConfig> it = configs.iterator(); it.hasNext();) {
			cfg = it.next();
			sched = sf.getScheduler();
			jsb = new JobSchedulerBean(cfg,sched);
			configObserver = new JobConfigObserver( jsb );	//配置监听器
			cfg.addObserver( configObserver );				//指定配置的监听
			jobScheduleMap.put(cfg.getJobSystemName(),jsb);
		}
	}
	public void startNewJob(DEPJobConfig cfg) throws Exception{
		Scheduler sched = sf.getScheduler();
		JobSchedulerBean jsb = new JobSchedulerBean(cfg,sched);
		JobConfigObserver configObserver = new JobConfigObserver( jsb );	//配置监听器
		cfg.addObserver( configObserver );				//指定配置的监听
		jobScheduleMap.put(cfg.getJobSystemName(),jsb);
		//TODO resumeJob可否正常执行？或是该用start？
		sched.resumeJob( jsb.getJobDetail().getKey() );
	}
	
	
	public void startAll(){
		if(jobScheduleMap==null||jobScheduleMap.keySet().size()==0){
			logger.info( "任务序列为空" );
		}else{
			JobSchedulerBean jsb = null;
			for(Iterator<String> it = jobScheduleMap.keySet().iterator();it.hasNext();){
				jsb = jobScheduleMap.get( it.next() );
				if(jsb.getConfig().getStart()==BaseConstants.YES_INT){
					try{
						//TODO 20150811 lidong 所有的jsb中都是同一个 Scheduler,只要启动一个，则所有的都会启动
						jsb.startScheduleJob();
					}catch(Exception e){
						logger.info( jsb.getConfig().getName()+"启动不成功" );
						logger.error(e.getMessage(),e);
					}
				}else{
					logger.info( jsb.getConfig().getName()+"已设置为不启动" );
				}
			}
		}
	}
	public void startOneJob(String jobSystemName) throws Exception{
		JobSchedulerBean jsb =this.jobScheduleMap.get( jobSystemName ); 
		jsb.scheduleJob();

	}
	public void shutDownOneJob(String jobSystemName) throws Exception{
		JobSchedulerBean jsb =this.jobScheduleMap.get( jobSystemName ); 
		jsb.getScheduler().deleteJob( jsb.getJobDetail().getKey() );
		this.jobScheduleMap.remove(jobSystemName);
	}
	public void deleteOneJob(String jobSystemName) throws Exception{
		JobSchedulerBean jsb =this.jobScheduleMap.get( jobSystemName ); 
		jsb.getScheduler().deleteJob( jsb.getJobDetail().getKey() );
	}
	public void resumeJob(String jobSystemName) throws Exception {
		JobSchedulerBean jsb =this.jobScheduleMap.get( jobSystemName ); 
		Scheduler sched = sf.getScheduler();
		sched.resumeJob(jsb.getJobDetail().getKey());
	}
	public void pauseJob(String jobSystemName) throws Exception{
		JobSchedulerBean jsb =this.jobScheduleMap.get( jobSystemName ); 
		Scheduler sched = sf.getScheduler();
		sched.pauseJob(jsb.getJobDetail().getKey());
	}
	public void addNewJob(DEPJobConfig cfg) throws Exception{
		Scheduler sched = sf.getScheduler();
		JobSchedulerBean jsb = new JobSchedulerBean(cfg,sched);
		JobConfigObserver configObserver = new JobConfigObserver( jsb );	//配置监听器
		cfg.addObserver( configObserver );				//指定配置的监听
		jobScheduleMap.put(cfg.getJobSystemName(),jsb);
	}
	
}