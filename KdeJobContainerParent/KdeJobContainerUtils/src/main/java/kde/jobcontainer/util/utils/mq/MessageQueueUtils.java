package kde.jobcontainer.util.utils.mq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lidong 2014-8-14
 * MessageQueue 辅助工具类,后续需逐步完善
 */
public class MessageQueueUtils {
	
	private static final Logger logger = LoggerFactory.getLogger( MessageQueueUtils.class );
	
	
	public static void main(String[] args) throws Exception{
		ConnectionFactory connectionFactory;        
		Connection connection = null;        
		Session session = null;        
		Destination destination;        
		MessageProducer producer;        
		connectionFactory = new ActiveMQConnectionFactory( "tcp://10.1.226.146:9014" ); 
		
		try {            
			connection = connectionFactory.createConnection();            
			connection.start();
			//createSession参数1设置为true时,会忽略第二个参数?
			session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue( "testtt" );            
			producer = session.createProducer(destination);            
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);            
			Message message = session.createObjectMessage(new String("1231312"));
			producer.send(message);
			session.commit();        
		}catch(Exception e){
			logger.error( e.getMessage() , e);
			throw new JMSException(e.getMessage());
		}finally {            
			try {
				if(session!=null)
					session.close();
				if (null != connection)                    
					connection.close();            
			} catch (Throwable ignore) {            
			}        
		}
		/*ConnectionFactory connectionFactory;        
		Connection connection = null;        
		Session session = null;        
		Destination destination;        
		MessageProducer producer;        
		connectionFactory = new ActiveMQConnectionFactory( "tcp://10.22.2.151:61616" ); 
		
		try {       
			MessageQueueUtils mqu = new MessageQueueUtils();
			mqu.sendMessage("3123123",
					 "tcp://10.22.2.151:61616", "hydro");
		}catch(Exception e){
			logger.error(e);
			throw new JMSException(e.getMessage());
		}finally {            
			try {
				if(session!=null)
					session.close();
				if (null != connection)                    
					connection.close();            
			} catch (Throwable ignore) {            
			}        
		}*/
	}
	
	
	/**
	 * <pre>
	 * 发送消息到指定地址,指定的的队列中
	 * </pre>
	 * @author Lidong 2014-8-14
	 * @param msgObj	传输对象
	 * @param mqServerAddr	消息队列服务器地址
	 * @param queuePrefix	队列标识
	 * @throws JMSException	
	 */
	public void sendMessage(Serializable msgObj, String mqServerAddr,String queuePrefix) throws JMSException {
		
		ConnectionFactory connectionFactory;        
		Connection connection = null;        
		Session session = null;        
		Destination destination;        
		MessageProducer producer;        
		connectionFactory = new ActiveMQConnectionFactory( mqServerAddr ); 
		
		try {            
			connection = connectionFactory.createConnection();            
			connection.start();
			//createSession参数1设置为true时,会忽略第二个参数?
			session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue( queuePrefix+"."+msgObj.getClass().getName() );            
			producer = session.createProducer(destination);            
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);            
			Message message = session.createObjectMessage(msgObj);
			producer.send(message);
			session.commit();        
		}catch(Exception e){
			logger.error( e.getMessage() , e);
			throw new JMSException(e.getMessage());
		}finally {            
			try {
				if(session!=null)
					session.close();
				if (null != connection)                    
					connection.close();            
			} catch (Throwable ignore) {            
				logger.error( ignore.getMessage() );
			}        
		}
	}
	
	/**
	 * <pre>
	 * 从指定的消息队列中获取消息
	 * </pre>
	 * @author Lidong 2014-8-17
	 * @param mqServerAddr	消息队列服务器地址
	 * @param queueName	队列名称
	 * @param fetchSize 取出的数量
	 * @param timeout 超时时间
	 * @throws JMSException	
	 */
	public List<Message> getMessage(String mqServerAddr,String queueName,Integer fetchSize,long timeout){
		ConnectionFactory connectionFactory;        
		Connection connection = null;        
		Session session = null;        
		Destination destination;        
		connectionFactory = new ActiveMQConnectionFactory( mqServerAddr ); 
		MessageConsumer consumer = null;
		List<Message> mq = new ArrayList<Message>();
		try {            
			connection = connectionFactory.createConnection();            
			connection.start();            
			session = connection.createSession(Boolean.FALSE, Session.CLIENT_ACKNOWLEDGE);
			destination = session.createQueue(queueName);
			consumer = session.createConsumer(destination);
			//接收消息，参数：接收消息的超时时间，为0的话则不超时，receive返回下一个消息，但是超时了或者 
			int size=0;
			Message tempMsg = null;
			
			while(size<fetchSize){
				tempMsg = consumer.receive( timeout );
				if(tempMsg!=null){
					mq.add( tempMsg );
					tempMsg.acknowledge();
					size++;
				}else{
					break;
				}
			}
			return mq;
		}catch(Exception e){
			logger.error( e.getMessage() , e);
			//接收过程中出了异常,数据已经接收了,但异常了,可能无法正常return已经接收的数据
			return mq;
		}finally {            
			try {
				if(consumer!=null)
					consumer.close();
				if(session!=null)
					session.close();
				if (null != connection)                    
					connection.close();            
			} catch (Throwable ignore) {            
				logger.error( ignore.getMessage() );
			}

		}
	}
	
	
	
}
