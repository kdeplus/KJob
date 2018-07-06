package kde.jobcontainer.dep.exchange.thread;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.activemq.BlobMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.dataSync.domain.DepJobConfig;
import kde.jobcontainer.dataSync.policy.PolicyRequest;
import kde.jobcontainer.dataSync.policy.PolicyResponse;
import kde.jobcontainer.dep.exchange.service.DataDealService;
import kde.jobcontainer.dep.exchange.util.ClassFactory;
import kde.jobcontainer.dep.exchange.util.ConfigUtil;

/**
 * @author xxxxxxxx
 * @date 2014-9-22, 下午8:36:25
 * mq获取消息处理线程
 */
public class MessageDealThread extends Thread {
	public static Logger logger = LoggerFactory.getLogger(  MessageDealThread.class );
	
	private Message message;
	
	public MessageDealThread(Message message) {
		this.message = message;
	}
	
	public void run() {
		PolicyResponse response = null;
		try {
			DataDealService service = ClassFactory.getDefaultDealService();
			if (this.message instanceof BlobMessage) {
				BlobMessage blobMsg = (BlobMessage) message;
				String fileName = blobMsg.getStringProperty("fileName");
				long fileLength = blobMsg.getLongProperty("fileLength");
				int requestType = blobMsg.getIntProperty("requestType");  //请求类型
				ConfigUtil configUtil = ConfigUtil.getInstance();
				String fileBasePath = configUtil.getJarFileBasePath();
				String filePath = fileBasePath + File.separator + fileName;
				File file = new File(filePath);   
				
				FileOutputStream fos = new FileOutputStream(file);
				DataInputStream dis = new DataInputStream(new BufferedInputStream(blobMsg.getInputStream()));
				
				logger.info("开始读取流和写入文件...");
				byte[] buf = new byte[256];
				int len = 0;
				long l = 0l;
				len = dis.read(buf);
				while(len > 0) {      //精确拷贝，由于可能会有多余的流字节，所以使用强行判断，只输入规定内字节流大小
					long l1 = l + len;
					if(l1 >= fileLength) {
						long l2 = fileLength - l;
						fos.write(buf,0,(int)l2);
						l = fileLength;
						break;
					} else {
						fos.write(buf);
						l += len;
						len = dis.read(buf);
					}
				}
				fos.close();
				logger.info("读取和写入结束...");
				
				String contentStr = blobMsg.getStringProperty("content");   //得到主体内容
				if(requestType == PolicyRequest.ADD_JOBCONFIG_SIGN) {    //新增job请求
					JSONObject jObj = JSONObject.parseObject(contentStr);
					DepJobConfig depJobConfig = (DepJobConfig) JSONObject.toJavaObject(jObj, DepJobConfig.class);
					depJobConfig.setJarPath(filePath);
					service.addConfigJob(depJobConfig);
				} else if(requestType == PolicyRequest.UPDATE_JOBCONFIG_SIGN) {   //修改job请求
					JSONObject jObj = JSONObject.parseObject(contentStr);
					DepJobConfig depJobConfig = (DepJobConfig) JSONObject.toJavaObject(jObj, DepJobConfig.class);
					depJobConfig.setJarPath(filePath);
					service.updateConfigJob(depJobConfig);
				} else {
					if(logger.isInfoEnabled()) {
						logger.info("错误的请求类型requestType="+requestType);
					}
				}
				dis.close();
			} else if (this.message instanceof ObjectMessage) {
				PolicyRequest request = (PolicyRequest)((ObjectMessage) message).getObject();
				DepJobConfig depJobConfig = (DepJobConfig) request.getParamValue("content");
				int requestType = request.getRequestType();
				if(requestType == PolicyRequest.UPDATE_JOBCONFIG_SIGN) {
					//depJobConfig.setJarPath(null);   
					service.updateConfigJob(depJobConfig);
				} else if(requestType == PolicyRequest.DELETE_JOBCONFIG_SIGN) {
					service.deleteConfigJob(depJobConfig);
				} else {
					if(logger.isInfoEnabled()) {
						logger.info("错误的请求类型requestType="+requestType);
					}
				}
			}
			logger.info("数据操作成功!");
		} catch (Exception e) {
			response = new PolicyResponse(e);
			logger.error(e.getMessage(),e);
		}
	}
}

