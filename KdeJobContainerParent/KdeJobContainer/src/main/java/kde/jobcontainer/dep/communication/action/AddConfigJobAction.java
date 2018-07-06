package kde.jobcontainer.dep.communication.action;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.dataSync.domain.DepJobConfig;
import kde.jobcontainer.dataSync.policy.PolicyRequest;
import kde.jobcontainer.dataSync.policy.PolicyResponse;
import kde.jobcontainer.dep.communication.action.base.BaseAction;
import kde.jobcontainer.dep.communication.interfaces.InputStreamInterGatter;
import kde.jobcontainer.dep.exchange.util.ConfigUtil;
import kde.jobcontainer.util.domain.DbConfig;

/**
 * @author xxxxxxxx
 * @date 2014-9-22, 上午10:07:40
 * 新增job操作
 * 继承InputStreamInterGatter接口是为了获取到InputStream进行数据接收流操作,见ServerThread线程
 */
public class AddConfigJobAction extends BaseAction implements InputStreamInterGatter {
	private InputStream is;
	
	public static Logger logger = LoggerFactory.getLogger(  AddConfigJobAction.class );

	@Override
	public PolicyResponse executeSome(PolicyRequest request) throws Exception {
		ConfigUtil configUtil = ConfigUtil.getInstance();
		String basePath = configUtil.getJarFileBasePath();
		String fileName = (String) request.getParamValue("fileName");
		long fileLength = (Long)request.getParamValue("fileLength");
		String filePath = basePath + File.separator + fileName;   //文件路径由根路径+文件名组成
		File file = new File(filePath);
		FileOutputStream fos = new FileOutputStream(file);
		DataInputStream dis = new DataInputStream(new BufferedInputStream(is));   //得到字节接收流，接收文件
		byte[] b = new byte[512];
		if(logger.isInfoEnabled()) {
			logger.info("开始拷贝文件...文件位置="+filePath);
		}
		long l = 0L;
		int readInt = dis.read(b);
		l = l + readInt;
		while(readInt != -1) {
			fos.write(b);
			readInt = dis.read(b);
			/*if(l < fileLength) {
				l = l+readInt;
			} else {
				break;
			}*/
		}
		fos.flush();
		logger.info("拷贝文件结束...");
		DepJobConfig jobConfig = (DepJobConfig) request.getParamValue("jobConfig");  //得到Job传输数据
		jobConfig.setJarPath(filePath);
		DbConfig dbConfig = configUtil.getDbConfig();   //得到默认的数据库配置
		//BaseAccessDao dao = ClassFactory.getDefaultAccessDao(dbConfig);
		//dao.addConfigJob(jobConfig);
		PolicyResponse response = new PolicyResponse();
		response.createASimpleSuccess();
		response.addParameter("filePath", filePath);
		return response;
	}

	public void setInputStream(InputStream is) {
		this.is = is;
	}
	
}

