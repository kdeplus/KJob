package kde.jobcontainer.dep.communication.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.dataSync.util.DataConstants;
import kde.jobcontainer.dep.communication.thread.ServerThread;

/**
 * 用于与中心节点通信的使用，如果启用将开启一个serverSocket，用于与调用方通信使用
 * @author xxxxxxxx
 * @date 2014-9-21, 上午11:42:28
 */
public class StartSocketService {
	private boolean stop = false;   //是否停止，初始化为false，如果要停止，将此值设置为true，即停止接收
	
	public static Logger logger = LoggerFactory.getLogger(  StartSocketService.class );
	
	//创立socket，参数：开放端口号
	public void startSocket(int port) throws IOException {
		logger.info("开始启动soket...");
		ServerSocket serverSocket = new ServerSocket(port);   //创建serverSocket
		if(logger.isInfoEnabled()) {
			logger.info("启动结束，等待在端口"+port+"上...");
		}
		while(!stop) {
			Socket socket = serverSocket.accept();
			if(logger.isInfoEnabled()) {
				logger.info("收到消息，来自ip:"+socket.getInetAddress());
				ServerThread thread = new ServerThread(socket);
				thread.start();
			}
		}
	}
	
	//停止服务
	public void stopSocketService() {
		this.stop = false;
	}
	
	/*  Gatters and Setters  */
	public boolean isStop() {
		return stop;
	}
	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	public static void main(String[] args) throws Exception {
		int port = DataConstants.DEFAULT_OPEN_PORT;
		StartSocketService service = new StartSocketService();
		service.startSocket(port);
	}
}

