package kde.jobcontainer.dep.domain;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.util.abstracts.KJob;
import kde.jobcontainer.util.domain.DEPJobConfig;

public class JobSchedulerBean {
	private static Logger logger = LoggerFactory.getLogger( JobSchedulerBean.class );
	private KJob job;
	private Scheduler scheduler;
	private DEPJobConfig config;
	private JobDetail jobDetail;
	private CronTrigger trigger;
	private Integer status;		//0 待启动 1运行中 -1异常
	
	
	private void init(KJob job,Scheduler scheduler,DEPJobConfig cfg){
		this.job = job;
		this.scheduler = scheduler;
		this.config = cfg;
		try{
			jobDetail = JobBuilder.newJob(job.getClass())
						.withIdentity(config.getServerId(), config.getTag()+config.getType())
						.build();
			jobDetail.getJobDataMap().put( "jobConfig" , this.config );
			trigger = TriggerBuilder.newTrigger()
		            .withIdentity("trigger"+config.getServerId(),  config.getTag()+config.getType())
		            .withSchedule(CronScheduleBuilder.cronSchedule( config.getSchedule() ))
		            .build();
			this.scheduleJob();
	        
		}catch(Exception e){
			logger.error( "构造JobSchedulerBean时出现异常!"+e.getMessage(),e );
		}
	}
	public void init(Scheduler scheduler,DEPJobConfig cfg) throws Exception{
		this.job = this.getJobObject( cfg );
		this.init(this.job,scheduler, cfg);
	}
	public void scheduleJob() throws Exception{
		 Date d = this.scheduler.scheduleJob(jobDetail, trigger);
		 logger.info( " 计划在该时刻运行: " + d
	                + " 运行 : "+this.config.getName()+"("+jobDetail.getKey() +")");
	}
	
	public JobSchedulerBean(KJob job,Scheduler scheduler,DEPJobConfig cfg){
		this.init( job, scheduler, cfg);
	}
	
	public JobSchedulerBean(DEPJobConfig cfg,Scheduler scheduler) throws Exception{
		this.job = this.getJobObject( cfg );
		this.init(job, scheduler, cfg);
	}
	
	private String getURLClassLoaderUrl(String jarPath){
		//TODO windows? linux?
		return "file:///"+jarPath;
	}
	private KJob getJobObject( DEPJobConfig cfg ) throws Exception{
		URLClassLoader loader = new URLClassLoader(new URL[] {
				new URL( this.getURLClassLoaderUrl(cfg.getJarPath()) )});
		Class c = loader.loadClass( cfg.getJobClassName() );
		KJob o = (KJob)c.newInstance();
		//本处进行初始化没有意义 20140410 lidong
		return o;
	}
	//TODO 20150811 lidong 所有的jsb中都是同一个 Scheduler,只要启动一个，则所有的都会启动,可以在JOB的方法中处理
	public void startScheduleJob() throws Exception{
		this.scheduler.start();
	}
	
	public void deleteScheduleJob() throws Exception{
		this.scheduler.deleteJob( this.jobDetail.getKey() );
	}
	
	public KJob getJob() {
		return job;
	}
	public void setJob(KJob job) {
		this.job = job;
	}
	public Scheduler getScheduler() {
		return scheduler;
	}
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	public DEPJobConfig getConfig() {
		return config;
	}
	public void setConfig(DEPJobConfig config) {
		this.config = config;
	}
	public JobDetail getJobDetail() {
		return jobDetail;
	}
	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}
	public CronTrigger getTrigger() {
		return trigger;
	}
	public void setTrigger(CronTrigger trigger) {
		this.trigger = trigger;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	
}
