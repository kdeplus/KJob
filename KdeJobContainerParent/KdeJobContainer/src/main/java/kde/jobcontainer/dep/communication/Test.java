package kde.jobcontainer.dep.communication;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import kde.jobcontainer.dataSync.policy.PolicyRequest;
import kde.jobcontainer.dataSync.policy.PolicyResponse;
import kde.jobcontainer.dataSync.util.DataConstants;

/**
 * @author xxxxxxxx
 * @date 2014-9-21, 下午5:29:59
 */
public class Test {
	
	public static void main(String[] args) throws Exception {
		PolicyRequest request = new PolicyRequest(PolicyRequest.TEST_CONNECTION_SIGN);
		Socket socket = new Socket("192.168.1.155", DataConstants.DEFAULT_OPEN_PORT);
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		oos.writeObject(request);
		PolicyResponse response = (PolicyResponse) ois.readObject();
		System.out.println("the dataReturned = "+response.getState());
	}
	
}

