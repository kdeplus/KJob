package kde.jobcontainer.dep;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.dep.job.cfgmonitor.MonitorJobCfgs;
import kde.jobcontainer.dep.job.ipmonitor.MonitorIpJob;
import kde.jobcontainer.dep.manager.ConfigManager;
import kde.jobcontainer.dep.manager.ScheduleManager;
import kde.jobcontainer.dep.utils.DaoFactory;
import kde.jobcontainer.util.domain.DEPJobConfig;

import sun.misc.BASE64Encoder;

public class Main {
	public static Logger logger = LoggerFactory.getLogger(  Main.class );
	protected static ScheduleManager sm = null;
	private static ScheduledExecutorService scheduledService=Executors.newScheduledThreadPool(5);//**这个更牛
	private static MonitorJobCfgs jobService = DaoFactory.getInstance("kde.jobcontainer.dep.job.cfgmonitor.MonitorJobCfgs");
	private static MonitorIpJob ipService = DaoFactory.getInstance("kde.jobcontainer.dep.job.ipmonitor.MonitorIpJob");
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stubshzh_hydrological
		// 首先获取配置
		try{
		
			Properties p = PlatformUtil.getPropertites();
			ConfigManager cm = ConfigManager.getInstance(p);
			List<DEPJobConfig> jobCfgList = cm.getConfigs();
			logger.info( "获取配置数量"+(jobCfgList==null?"null":jobCfgList.size() ));
			sm = new ScheduleManager(jobCfgList);
			sm.startAll();

			
//			String j = "";
//			JSONArray ar = JSONArray.fromObject( j );
			
			/*
			scheduledService.scheduleAtFixedRate(new Runnable() {
				public void run() {
					try {
						System.out.println("自动检查数据库");
						jobService.execute(null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 30,30,TimeUnit.SECONDS );
			scheduledService.scheduleAtFixedRate(new Runnable() {
				public void run() {
					try {
						System.out.println("检查网络连接");
						ipService.execute(null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 3,3,TimeUnit.MINUTES);
			*/
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			System.exit(0);
		}
	}
	public void addAndUpdateMsgModelList(String s) throws IOException{
		BufferedReader br=null;
		StringBuffer sb=new StringBuffer();
		String pageEncoding="UTF-8";
		try{
			StringBuilder nums = new StringBuilder("18901164125");
			String content = s;
			
			String gatewayUrl="http://d/rwsms/sendMessage"; //url短信网关
			URL postUrl = new URL(gatewayUrl);
			String username = URLEncoder.encode(URLEncoder.encode("山洪",pageEncoding));
			//String password = URLEncoder.encode(URLEncoder.encode("123456",pageEncoding));
			
			String password =  new BASE64Encoder().encodeBuffer("123456".getBytes());
			content = URLEncoder.encode(URLEncoder.encode(content,pageEncoding));
			 // 打开连接
	        HttpURLConnection connection = (HttpURLConnection) postUrl
	                .openConnection();
	        connection.setDoOutput(true);
	        connection.setDoInput(true);
	        connection.setRequestMethod("POST");
	        connection.setUseCaches(false);
	        connection.setInstanceFollowRedirects(true);
	        connection.setRequestProperty("Content-Type",
	                "application/x-www-form-urlencoded");
	        connection.connect();
	        DataOutputStream out = new DataOutputStream(connection
	                .getOutputStream());
	        String urlText = "username="+username+"&password="+password+"&content="+content+"&mobile=" + nums.toString();
	        out.writeBytes(urlText); 
	        out.flush();
	        out.close(); 
	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	                connection.getInputStream()));
	        String line;
	        while ((line = reader.readLine()) != null){
	            sb.append(line);
	        }
	        reader.close();
	        connection.disconnect();
			//成功失败判断
			String responseText=sb.toString();
			System.out.println(responseText);  
			
			
		}finally{
			if(br!=null)
				br.close();
		}
	}
}
