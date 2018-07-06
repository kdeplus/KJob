package kde.jobcontainer.dep.communication.action.base;

import kde.jobcontainer.dataSync.policy.PolicyRequest;
import kde.jobcontainer.dataSync.policy.PolicyResponse;

/**
 * @author xxxxxxxx
 * @date 2014-9-21, 下午4:52:33
 * 处理内容基本父类对象
 */
public abstract class BaseAction {
	/**
	 * 父类进行方法，附加验证和子类处理方法调用
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public PolicyResponse execute(PolicyRequest request) throws Exception {
		validation();
		return executeSome(request);
	}
	
	/**
	 * 子类继承处理方法
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public abstract PolicyResponse executeSome(PolicyRequest request) throws Exception;
	
	/**
	 * 验证方法，用于处理内部验证逻辑，父类方法中明确调用，子类进行查看
	 * @throws Exception
	 */
	public void validation() throws Exception {
		
	}
}

