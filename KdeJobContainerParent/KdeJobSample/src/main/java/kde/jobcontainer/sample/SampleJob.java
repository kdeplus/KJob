package kde.jobcontainer.sample;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.util.abstracts.KJob;
import kde.jobcontainer.util.domain.DEPJobConfig;

/**
 * <pre>
 *    样例任务程序,每新建一个调度任务,都集成KJob,由外部进行统一配置管理,由KdeJobContainer进行初始化和调度管理,
 *    任务尽量通用,通过外部的配置来可同时执行多个job的实例对象,如雨水情数据库的同步,可从使用一个KJob的实现类,赋予多个配置,实例化后有多个对象,按时执行,完成不同的任务
 *    单个任务的实现类中只关注业务方法
 *    编写时注意资源的争抢
 * </pre>
 * @author lidong
 * @date 2018年7月6日 下午4:20:53
 */
public class SampleJob extends KJob  {
	static int instanceTime = 0;
	private static Logger logger = LoggerFactory.getLogger( SampleJob.class );
	
	public  SampleJob(){
		synchronized(SampleJob.class){
			if(logger.isDebugEnabled()){
				logger.debug( "实例化SampleJob,次数"+(++instanceTime) );	//多线程?
				if(instanceTime>=Integer.MAX_VALUE-5){
					logger.debug( "实例化SampleJob,次数过多,重置" );
					instanceTime=0;
				}
			}
		}
	}
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 * 任务实际执行的方法,由框架程序自动调用
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//按照框架要求，进行一定的初始化操作,会通过初始化本job实例中持有的配置信息
		this.executeInit(SampleJob.class, context);
		logger.info( "SampleJob 执行工作!" + (this.config==null?"无上下文配置":this.config.getName() ));
		//执行任务
		this.doJob(this.config);
		logger.info( "HYITSTriRecSendJob 结束工作!" + (this.config==null?"无上下文配置":this.config.getName()) );	
		
	}
	
	/**
	  * <pre>
	  *    具体的业务实现类,一般会写在其他的类里
	  * </pre>
	  * @Title: doJob
	  * @param jobConfig
	  * @author lidong
	  * @date 2018年7月6日 下午4:46:13
	  */
	private void doJob(DEPJobConfig jobConfig){
		//创建一个httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();

		//创建一个uri对象
		try{
			logger.info( jobConfig==null?"无上下文配置":jobConfig.getJobSystemName() );
			URIBuilder uriBuilder = new URIBuilder("http://60.172.244.3:8800/GK/ZXGK/GetChartData.aspx");
			uriBuilder.addParameter("qyid","W1248");
			uriBuilder.addParameter("yzid","B01");
			HttpGet get = new HttpGet(uriBuilder.build());
	
			//执行请求
			CloseableHttpResponse response =httpClient.execute(get);
	
			//取响应的结果
			int statusCode =response.getStatusLine().getStatusCode();
	
			System.out.println(statusCode);
	
			HttpEntity entity =response.getEntity();
	
			String string = EntityUtils.toString(entity,"utf-8");
			//TODO 进行后续处理，如解析数据，入库等
			
			System.out.println(string);
	
			//关闭httpclient
			response.close();
	
			httpClient.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SampleJob sj = new SampleJob();
		try {
			sj.execute(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
