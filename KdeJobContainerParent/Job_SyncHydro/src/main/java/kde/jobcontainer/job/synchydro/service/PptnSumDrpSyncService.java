package kde.jobcontainer.job.synchydro.service;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.job.synchydro.dao.HYITSTriggerRecordSyncDao;
import kde.jobcontainer.job.synchydro.domain.PptnSumDrpSyncConfig;
import kde.jobcontainer.util.domain.DEPJobConfig;
import kde.jobcontainer.util.utils.DateUtil;
import kde.jobcontainer.util.utils.mq.MessageQueueUtils;

public class PptnSumDrpSyncService {
	private Logger logger = LoggerFactory
			.getLogger(PptnSumDrpSyncService.class);
	/**
	 *  由于是单例模式,所以本身不能持有状态及其他配置项信息,不然无法同时运行两个
	 **/
	private static PptnSumDrpSyncService _instance;
	
	private HYITSTriggerRecordSyncDao gwtpDao;
	
	public static PptnSumDrpSyncService getInstance(){	//单例方法
		if(_instance==null)
			_instance = new PptnSumDrpSyncService();
		return _instance; 
	}
	
	/**
	  * <pre>
	  *    对比两边数据,核对内容
	  * </pre>
	  * @Title: doJob
	  * @param depConfig
	  * @author lidong
	  * @date 2016年11月8日 上午11:28:37
	  */
	public void doJob(DEPJobConfig depConfig){
		String msg = null;
		PptnSumDrpSyncConfig cfg = null;
		StringBuilder sb = null;
		try{
			sb = new StringBuilder();
			sb.append( this.getLocalIP() ).append("\r\n");
			sb.append( depConfig.getJobClassName() ).append("\t\t");
			sb.append( depConfig.getName() ).append("\r\nSourceDb");
			cfg = (PptnSumDrpSyncConfig)
					JSONObject.toJavaObject( depConfig.getJobConfigJson(), PptnSumDrpSyncConfig.class);
			sb.append( cfg.getSourceDbConfig().getString( "url" ) ).append("\r\nTargetDb");
			sb.append( cfg.getTargetDbConfig().getString("url") ).append("\r\n");
			Date end = new Date();
			end = DateUtil.add( end , Calendar.HOUR, 1 );
			Date bg = DateUtil.add( end , Calendar.HOUR, -cfg.getHourAgo());
			
			String bgStr = DateUtil.DateTimeToString(bg,DateUtil.HH)+":00";
			String endStr = DateUtil.DateTimeToString(end,DateUtil.HH)+":00";
			//获取短历时的,来源数据,已经计算成小时的累计数了
			JSONObject source = this.getGwtpDao().getSourceData(bgStr,endStr,cfg,sb );
			logger.debug( "查询到源数据条数:"+source.size() );
			//获取目标库的数据,原本就应该是小时的累计数
			JSONObject target = this.getGwtpDao().getTargetData(bgStr,endStr,cfg,sb );
			logger.debug( "查询到目标数据条数:"+target.size() );
			//比较差异,得到要更新的和要新增的
			JSONObject obj = this.getDiff(source, target, sb);
			//更新处理目标库
			this.getGwtpDao().doUpdateTarget(obj, cfg, sb);
			
			msg = "\r\n操作完成";
			logger.info( msg );
			sb.append( msg );
		}catch(Exception e){
			logger.error( e.getMessage(),e );
		}finally{
			if(cfg!=null)
				this.sendMsgToMq(depConfig,cfg.getMqServerAddr(),sb==null?"":sb.toString() );
			else{
				msg = "PptnSumDrpSyncConfig cfg  为空,不能正常转换";
				logger.error(msg);
			}
		}
	}
	
	/**
	  * <pre>
	  *    
	  * </pre>
	  * @Title: getDiff
	  * @param source
	  * @param target
	  * @return
	  * @author lidong
	  * @date 2016年11月8日 上午11:46:50
	  */
	public JSONObject getDiff(JSONObject source,JSONObject target,StringBuilder sb){
		JSONObject obj = new JSONObject();
		JSONObject update = new JSONObject();
		JSONObject insert = new JSONObject();
		JSONObject delete = new JSONObject();//要保证数据确实是一个范围以内的,不要把其他水文的数据删掉
		String key = null;
		Double sValue = null;
		Double tValue = null;
		for(Iterator<String> it=source.keySet().iterator();it.hasNext();){
			key = it.next();
			sValue = source.getDouble(key);
			
			if(target.containsKey( key )){
				//有数据,要更新或者不操作
				tValue = target.getDouble( key );
				if(!String.valueOf( tValue ).equals( String.valueOf(sValue) )){
					//两者不等,这样处理null等情况,不过按说不应该有null
					update.put( key , sValue);
					logger.debug( "update\t{}\t{}",key,sValue );
				}
			}else{
				logger.debug( "to insert "+key );
				//没有,要新增
				insert.put( key , sValue);
				//logger.debug( "insert\t{}\t{}",key,sValue );
			}
			//处理完后从target里去掉这个key
			target.remove( key );
		}
		//循环剩下的,全部删除
		for(Iterator<String> it=target.keySet().iterator();it.hasNext();){
			key = it.next();
			logger.debug( "to delete"+key );
			delete.put( key , target.getDouble(key));
			logger.debug( "delete\t{}\t{}",key,null );
		}
		
		obj.put("update", update);
		obj.put("insert", insert);
		obj.put("delete", delete);
		return obj;
	}
	
	
	
	/**
	  * <pre>
	  *    发送运行消息
	  * </pre>
	  * @Title: sendMsgToMq
	  * @param address
	  * @param msg
	  * @author lidong
	  * @date 2016年11月8日 上午11:32:40
	  */
	private void sendMsgToMq(DEPJobConfig depConfig,String address,String msg){
		//发送执行的消息到平台
		try{
			if( address!=null
					&&!"".equals(address.trim())
					&&address.trim().indexOf( "tcp:" )!=-1){
				MessageQueueUtils mqu = new MessageQueueUtils();
				mqu.sendMessage( msg , address, "syncinfo");
				logger.info("完成任务运行相关信息的发送:"+depConfig.getName()+"\t"+depConfig.getServerId());
			}else{
				logger.info("未配置信息发送的目标,跳过:"+depConfig.getName()+"\t"+depConfig.getServerId());
			}
		}catch(Exception e){
			logger.error( e.getMessage(),e );
		}
	}
	
	
	private String getLocalIP(){
		try{
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostAddress().toString();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			return "无法获取本机ip";
		}
	}

	public HYITSTriggerRecordSyncDao getGwtpDao() {
		if(this.gwtpDao==null)
			this.gwtpDao = HYITSTriggerRecordSyncDao.getInstance();
		
		return this.gwtpDao;
	}

	public void setGwtpDao(HYITSTriggerRecordSyncDao gwtpDao) {
		this.gwtpDao = gwtpDao;
	}
}
