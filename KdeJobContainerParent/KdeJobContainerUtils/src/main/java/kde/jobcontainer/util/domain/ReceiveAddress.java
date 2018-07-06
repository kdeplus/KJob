package kde.jobcontainer.util.domain;

/**
 * ReceiveAddress entity. @author MyEclipse Persistence Tools
 */

public class ReceiveAddress implements java.io.Serializable {

	// Fields

	private Integer id;
	private String ip;
	private String destination;

	// Constructors

	/** default constructor */
	public ReceiveAddress() {
	}

	/** full constructor */
	public ReceiveAddress(String ip, String destination) {
		this.ip = ip;
		this.destination = destination;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDestination() {
		return this.destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

}