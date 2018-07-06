package kde.jobcontainer.dep.communication.interfaces;

import java.io.InputStream;

/**
 * @author xxxxxxxx
 * @date 2014-9-21, 下午5:10:50
 * 由于有可能涉及到文件传输和其他的数据流操作，所以要将socket的inputStream传入使用
 */
public interface InputStreamInterGatter {
	/**
	 * 在线程分析调用时，如果有类继承此接口，要将socket输入流传入给继承类
	 * @param is
	 */
	public void setInputStream(InputStream is);
}

