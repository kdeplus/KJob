package kde.jobcontainer.dep.exchange.service;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.activemq.BlobMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.dataSync.domain.DEPNode;
import kde.jobcontainer.dataSync.domain.DepJobConfig;
import kde.jobcontainer.dataSync.policy.PolicyRequest;
import kde.jobcontainer.dataSync.policy.PolicyResponse;
import kde.jobcontainer.dataSync.util.DataConstants;
import kde.jobcontainer.dep.exchange.util.ClassFactory;
import kde.jobcontainer.dep.exchange.util.ConfigUtil;

/**
 * @author xxxxxxxx
 * @date 2014-9-24, 下午2:12:09
 */
public class MessageDealer {
	
	private Message message;
	
	public static Logger logger = LoggerFactory.getLogger(  MessageDealer.class );
	
	public MessageDealer(Message message) {
		this.message = message;
	}
	
	//处理message数据
	public PolicyResponse dataDeal() {
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
					/*long l1 = l + len;
					if(l1 >= fileLength) {
						long l2 = fileLength - l;
						fos.write(buf,0,(int)l2);
						l = fileLength;
						break;
					} else {*/
						fos.write(buf,0,len);
						l += len;
						len = dis.read(buf);
					//}
				}
				fos.close();
				logger.info("读取和写入结束...");
				
				String contentStr = blobMsg.getStringProperty("content");   //得到主体内容
				if(requestType == PolicyRequest.ADD_JOBCONFIG_SIGN) {    //新增job请求
					JSONObject jObj = JSONObject.parseObject(contentStr);
					DepJobConfig depJobConfig = (DepJobConfig) JSONObject.toJavaObject(jObj, DepJobConfig.class);
					depJobConfig.setJarPath(filePath);
					response = service.addConfigJob(depJobConfig);
				} else if(requestType == PolicyRequest.UPDATE_JOBCONFIG_SIGN) {   //修改job请求
					JSONObject jObj = JSONObject.parseObject(contentStr);
					DepJobConfig depJobConfig = (DepJobConfig) JSONObject.toJavaObject(jObj, DepJobConfig.class);
					depJobConfig.setJarPath(filePath);
					response = service.updateConfigJob(depJobConfig);
				} else {
					if(logger.isInfoEnabled()) {
						logger.info("错误的请求类型requestType="+requestType);
						response = new PolicyResponse(PolicyResponse.FAILURE_SIGN);
						response.addParameter(DataConstants.ERROR_MESSAGE_KEY, "不支持的请求类型");
					}
				}
				dis.close();
			} else if (this.message instanceof ObjectMessage) {
				PolicyRequest request = (PolicyRequest)((ObjectMessage) message).getObject();
				int requestType = request.getRequestType();
				if(requestType == PolicyRequest.UPDATE_JOBCONFIG_SIGN) {
					DepJobConfig depJobConfig = (DepJobConfig) request.getParamValue("content");
					depJobConfig.setJarPath(null);     //这种形式的请求不需要修改原jar包
					service.updateConfigJob(depJobConfig);
					
				} else if(requestType == PolicyRequest.DELETE_JOBCONFIG_SIGN) {
					DepJobConfig depJobConfig = (DepJobConfig) request.getParamValue("content");
					service.deleteConfigJob(depJobConfig);
				} else if(requestType == PolicyRequest.UPDATE_NODE_STATUS_SIGN) {
					DEPNode node = (DEPNode) request.getParamValue("content");
					service.updateNodeInfo(node, new Date());
				} else {
					if(logger.isInfoEnabled()) {
						logger.info("错误的请求类型requestType="+requestType);
						if(logger.isInfoEnabled()) {
							logger.info("错误的请求类型requestType="+requestType);
							response = new PolicyResponse(PolicyResponse.FAILURE_SIGN);
							response.addParameter(DataConstants.ERROR_MESSAGE_KEY, "不支持的请求类型");
						}
					}
				}
			}
			logger.info("数据操作成功!");
		} catch (Exception e) {
			response = new PolicyResponse(e);
			logger.error(e.getMessage(),e);
		}
		return response;
	}
}

