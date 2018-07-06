package kde.jobcontainer.dep.exchange.service;

import java.util.Date;

import kde.jobcontainer.dataSync.domain.DEPNode;
import kde.jobcontainer.dataSync.domain.DepJobConfig;
import kde.jobcontainer.dataSync.policy.PolicyResponse;
import kde.jobcontainer.dep.exchange.dao.base.BaseAccessDao;

/**
 * @author xxxxxxxx
 * @date 2014-9-22, 下午9:46:20
 */
public class DataDealService {
	
	private BaseAccessDao dao;
	
	public DataDealService(BaseAccessDao dao) {
		this.dao = dao;
	}
	
	/**
	 * 创建一个job
	 * @param jobConfig
	 * @return
	 * @throws Exception
	 */
	public PolicyResponse addConfigJob(DepJobConfig jobConfig) throws Exception {
		PolicyResponse response = null;
		dao.addConfigJob(jobConfig);
		response = new PolicyResponse();
		response.createASimpleSuccess();
		return response;
	}
	
	/**
	 * 修改Job内容
	 * @param jobConfig
	 * @return
	 * @throws Exception
	 */
	public PolicyResponse updateConfigJob(DepJobConfig jobConfig) throws Exception {
		PolicyResponse response = null;
		dao.updateConfigJob(jobConfig);
		response = new PolicyResponse();
		response.createASimpleSuccess();
		return response;
	}
	
	/**
	 * 删除Job
	 * @param jobConfig
	 * @return
	 * @throws Exception
	 */
	public PolicyResponse deleteConfigJob(DepJobConfig jobConfig) throws Exception {
		PolicyResponse response = null;
		dao.deleteConfigJob(jobConfig);
		response = new PolicyResponse();
		response.createASimpleSuccess();
		return response;
	}
	
	public PolicyResponse updateNodeInfo(DEPNode node,Date updateTime) throws Exception {
		PolicyResponse response = null;
		dao.updateNodeInfo(node, updateTime);
		response = new PolicyResponse();
		response.createASimpleSuccess();
		return response;
	}
}

