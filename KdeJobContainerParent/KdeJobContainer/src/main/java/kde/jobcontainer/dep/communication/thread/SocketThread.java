package kde.jobcontainer.dep.communication.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.dep.communication.service.StartSocketService;
import kde.jobcontainer.dep.exchange.util.ConfigUtil;

/**
 * @author xxxxxxxx
 * 端口启动程序
 */
public class SocketThread extends Thread {
	public static Logger logger = LoggerFactory.getLogger(  SocketThread.class );
	
	public void run() {
		try {
			StartSocketService service = new StartSocketService();
			ConfigUtil configUtil = ConfigUtil.getInstance();
			int openPort = configUtil.getSocketOpenPort();
			service.startSocket(openPort);
		} catch (Exception e) {
			if(logger.isErrorEnabled()) {
				logger.error("启动端口线程出错，请查看....", e);
			}
		}
	}
	
}