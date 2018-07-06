package kde.jobcontainer.dep.exchange.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.dep.exchange.service.MqTopicReceiver;
import kde.jobcontainer.dep.exchange.service.SysConfigService;
import kde.jobcontainer.dep.exchange.util.ClassFactory;
import kde.jobcontainer.dep.exchange.util.ConfigUtil;

/**
 * @author xxxxxxxx
 * @date 2014-9-23, 上午9:36:08
 * mq指定topic订阅者监听指定的topic
 */
public class MqTopicListenerThread extends Thread {
	public Logger logger = LoggerFactory.getLogger(MqTopicListenerThread.class);
	
	public void run() {
		try {
			MqTopicReceiver receiver = new MqTopicReceiver();
			ConfigUtil configUtil = ConfigUtil.getInstance();
			String mqUrl = configUtil.getMqUrl();
			String sysName = configUtil.getDataSyncName();
			String topicName = configUtil.getTopicName();  //得到topicName
			
			SysConfigService service = ClassFactory.getConfigService();
			service.checkAndInsert(sysName);
			
			if(logger.isInfoEnabled()) {
				logger.info("开始监听mqURl"+mqUrl+"上的"+topicName+" queue...");
			}
			receiver.startTopicListen(mqUrl, topicName); 
		} catch (Exception e) {
			if(logger.isErrorEnabled()) {
				logger.error(e.getMessage(),e);
			}
		}
		
	}
	
}

