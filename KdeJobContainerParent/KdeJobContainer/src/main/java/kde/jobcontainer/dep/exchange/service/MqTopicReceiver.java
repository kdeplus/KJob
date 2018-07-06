package kde.jobcontainer.dep.exchange.service;

import java.util.Date;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xxxxxxxx
 * @date 2014-9-22, 下午6:15:08
 * 订阅消息获取
 */
public class MqTopicReceiver {
	
	public Logger logger = LoggerFactory.getLogger(MqTopicReceiver.class);
	
	/**
	 * 开始监听指定的订阅消息topic
	 * @param mqUrl         activemq的url
	 * @param topicName     订阅name
	 */
	public void startTopicListen(String mqUrl,String topicName) throws Exception {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(mqUrl);
		ActiveMQConnection connection = (ActiveMQConnection) factory.createConnection();
		connection.start();
		
		ActiveMQSession session = (ActiveMQSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//Topic topic = session.createTopic(topicName);
		Destination destination = session.createQueue(topicName);
		MessageConsumer consumer = session.createConsumer(destination);
		
		//要监听在监听器开启之前的遗留消息
		logger.info("收取监听之前的消息队列...");
		while(true) {   //先收取之前的消息，然后再开启监听，否则监听之前的消息就会丢失(使用线程可能会导致处理时间不同而处理过程错误)
			//设置接收者接收消息的时间，为了便于测试，这里谁定为100s
            Message message = consumer.receive(2000);
            if (null != message) {
            	if(logger.isInfoEnabled()) {
            		logger.info("接收到信息，类型="+message.getClass().getName());
            	}
            	MessageDealer dealer = new MessageDealer(message);
            	dealer.dataDeal();
            } else {
            	logger.info("没有收到相关消息，停止接收...");
                break;
            }
		}
		
		logger.info("离线消息接收完毕，开启监听...");
		//创立consumer,处于监听状态
		consumer.setMessageListener(new MessageListener() {

			public void onMessage(Message arg0) {
				logger.info("收到订阅消息,启用处理功能...");
				MessageDealer dealer = new MessageDealer(arg0);
            	dealer.dataDeal();
			}
			
		});
	}
	
	public static void main(String[] args) {
		Date date = new Date(1380002005260l);
		System.out.println(date);
	}
}

