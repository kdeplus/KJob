package kde.jobcontainer.dep.communication.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.dataSync.policy.PolicyRequest;
import kde.jobcontainer.dataSync.policy.PolicyResponse;
import kde.jobcontainer.dataSync.util.DataConstants;
import kde.jobcontainer.dep.communication.action.AddConfigJobAction;
import kde.jobcontainer.dep.communication.action.base.BaseAction;
import kde.jobcontainer.dep.communication.interfaces.InputStreamInterGatter;

/**
 * @author xxxxxxxx
 * @date 2014-9-21, 下午12:31:41
 * 在获取到socket后进行的
 */
public class ServerThread extends Thread {
	
	public static Logger logger = LoggerFactory.getLogger(  ServerThread.class );
	
	//socket对象，用于获取数据
	private Socket socket;
	
	private InputStream is;   //socket输入流
	
	private OutputStream os;    //socket输出流
	
	private ObjectInputStream ois;   //对象输入流
	
	private ObjectOutputStream oos;   //对象输出流

	public ServerThread(Socket socket) {
		this.socket = socket;
		try {   //声明要有顺序，先输入流就先声明输入
			this.is = socket.getInputStream();
			this.os = socket.getOutputStream();
			this.ois = new ObjectInputStream(is);
			this.oos = new ObjectOutputStream(os);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public void run() {
		PolicyResponse response = null;
		PolicyRequest request = null;
		try {
			request = (PolicyRequest) ois.readObject();    //不管是何协议，都必须先传一个request对象提供解析，其他的数据放在request对象后
			if(logger.isInfoEnabled()) {
				logger.info("收到请求，请求类型参数="+request.getRequestType());
			}
			BaseAction action = null;
			switch (request.getRequestType()) {    //根据请求分别处理
			   case PolicyRequest.TEST_CONNECTION_SIGN:   //测试连接返回请求（初始化）
				   response = new PolicyResponse();
				   response.createASimpleSuccess();    //返回一个成功消息
				   break;
			   case PolicyRequest.ADD_JOBCONFIG_SIGN:
				   action = new AddConfigJobAction();
				   break;
			   default:
				   response = new PolicyResponse();
				   response.setState(PolicyResponse.FAILURE_SIGN);
				   response.addParameter(DataConstants.ERROR_MESSAGE_KEY, "不识别该请求类型，请检查...");
			}
			if(response == null) {
				if(action instanceof InputStreamInterGatter) {   //如果是InputSStreamInterGatter实现类，就把inputStream传给它进行使用
					((InputStreamInterGatter)action).setInputStream(is);
				}
				response = action.execute(request);
			}
			oos.writeObject(response);
			ois.close();
			oos.close();
			socket.close();
		} catch(Exception e) {
			response = new PolicyResponse(e);
			if(logger.isErrorEnabled()) {
				logger.info("线程处理失败，请查看!");
			}
			try {
				oos.writeObject(response);
				oos.close();
			} catch (IOException e1) {
				logger.error(e.getMessage(),e);
			}
			logger.error(e.getMessage(),e);
		}
	}
	
	//Gatters and Setters
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
}

