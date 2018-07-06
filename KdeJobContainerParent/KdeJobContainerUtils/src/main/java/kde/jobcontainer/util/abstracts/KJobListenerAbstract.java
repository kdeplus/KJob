package kde.jobcontainer.util.abstracts;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public abstract class KJobListenerAbstract implements JobListener {

	public abstract String getName();

	public void jobExecutionVetoed(JobExecutionContext arg0) {
		// TODO Auto-generated method stub执行前的操作，
		// 根据任务管理器的信息，检查之前的运行状况，进行记录
	}

	public void jobToBeExecuted(JobExecutionContext arg0) {
		// TODO Auto-generated method stub
		// 执行时记录相关信息
	}

	public void jobWasExecuted(JobExecutionContext arg0,
			JobExecutionException arg1) {
		// TODO Auto-generated method stub
		// 当任务结束时，告知任务管理器
		// 检查服务配置状态，如果要求结束则不再执行后续操作
	}
}
