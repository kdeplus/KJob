package kde.jobcontainer.dep.job.cfgmonitor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.dep.PlatformUtil;
import kde.jobcontainer.dep.manager.ConfigManager;
import kde.jobcontainer.dep.manager.ScheduleManager;
import kde.jobcontainer.util.abstracts.KJob;
import kde.jobcontainer.util.domain.DEPJobConfig;
import kde.jobcontainer.util.utils.BeanUtil;
import kde.jobcontainer.util.utils.JobConfigUtil;

public class MonitorJobCfgs extends KJob {
	private static Logger logger = LoggerFactory.getLogger( MonitorJobCfgs.class );
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		//获取配置管理器，系统启动时应该已经进行过初始化，传递null参数
		try{
			ScheduleManager sm = PlatformUtil.getScheduleManager();
			ConfigManager cm = ConfigManager.getInstance( null );
			List<DEPJobConfig> oldcfgs = cm.getConfigs();
			List<DEPJobConfig> newcfgs = cm.getRefreshedConfigs();
			//根据本地ID对比差异，如果都存在本地ID，对比是否变化，如果新的存在，则添加任务，如果新的少了，停止原来老的运行，操作完成后，如有变动，更新cm持有的配置
			Map<String,DEPJobConfig> oldCfgMap = JobConfigUtil.getConfigMap(oldcfgs);
			Map<String,DEPJobConfig> newCfgMap = JobConfigUtil.getConfigMap(newcfgs);
			String i = null;
			boolean ifChanged = false;
			for(Iterator<String> it=newCfgMap.keySet().iterator();it.hasNext();){
				i = it.next();
				int j=0;
				if(oldCfgMap.containsKey( i )){
					//原来有这个配置，进行对比，如不同则将任务重置
					if(!newCfgMap.get(i).equals( oldCfgMap.get(i) )){
						logger.info("配置不同，开始更新");
						if(newCfgMap.get(i).getStart()!=oldCfgMap.get(i).getStart()){//如果状态不相同，说明肯定进行开启或关闭操作
							if(newCfgMap.get(i).getStart()==1){//新属性为1，说明要开启任务
								j=1;
							}else if(newCfgMap.get(i).getStart()==0){//为0说明要关闭任务
								j=2;
							}
						}
						BeanUtil.copyNotNullProperties( newCfgMap.get(i) , oldCfgMap.get(i));
						//启动配置监听
						oldCfgMap.get(i).changed();
						//使用原来的配置
						ifChanged = true;
						if(j==1){
							sm.resumeJob(oldCfgMap.get(i).getJobSystemName());
						}else if(j==2){
							sm.pauseJob(oldCfgMap.get(i).getJobSystemName());
						}
						logger.info("更新成功");
					}
				}else{
					//原来没有这个配置，开启新任务, startNewJob会自动添加监听器
					logger.info("开启新任务");
					if(newCfgMap.get(i).getStart()==0){
						sm.addNewJob( newCfgMap.get(i) );
					}else{
						sm.startNewJob( newCfgMap.get(i) );
					}
					
					ifChanged = true;
				}
			}
			for(Iterator<String> it=oldCfgMap.keySet().iterator();it.hasNext();){
				i = it.next();
				if(!newCfgMap.containsKey(i)){
					System.out.println("删除任务");
					sm.shutDownOneJob(oldCfgMap.get(i).getJobSystemName());
					ifChanged = true;
				}
			}
			cm.updateConfigs(newcfgs);//完了需要更新原有的任务list，不然会一直显示不相同
			if(ifChanged){
				//配置有变化，更新配置信息
				//TODO 需注意Oberver信息
				logger.info("配置发生变更！");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);;
			throw new JobExecutionException(e);
		}
	}

}
