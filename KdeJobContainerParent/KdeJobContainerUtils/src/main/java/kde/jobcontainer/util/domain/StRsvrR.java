package kde.jobcontainer.util.domain;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.util.domain.DataObj;
/**
 * @author Lidong 2014-4-16
 * 从农水局项目中抽出，仅作为例子
 */
public class StRsvrR extends DataObj{
	
	private String stcd;
	private Date tm;
	private Double rz;
	private Double inq;
	private Double w;
	private Double blrz;
	private Double otq;
	private Double inqdr;
	private String rwchrcd;
	private String rwptn;
	private String msqmt;
	
	public String getStcd() {
		return stcd;
	}
	public void setStcd(String stcd) {
		this.stcd = stcd;
	}
	public Date getTm() {
		return tm;
	}
	public void setTm(Date tm) {
		this.tm = tm;
	}
	public Double getRz() {
		return rz;
	}
	public void setRz(Double rz) {
		this.rz = rz;
	}
	public Double getInq() {
		return inq;
	}
	public void setInq(Double inq) {
		this.inq = inq;
	}
	public Double getW() {
		return w;
	}
	public void setW(Double w) {
		this.w = w;
	}
	public Double getBlrz() {
		return blrz;
	}
	public void setBlrz(Double blrz) {
		this.blrz = blrz;
	}
	public Double getOtq() {
		return otq;
	}
	public void setOtq(Double otq) {
		this.otq = otq;
	}
	public Double getInqdr() {
		return inqdr;
	}
	public void setInqdr(Double inqdr) {
		this.inqdr = inqdr;
	}
	public String getRwchrcd() {
		return rwchrcd;
	}
	public void setRwchrcd(String rwchrcd) {
		this.rwchrcd = rwchrcd;
	}
	public String getRwptn() {
		return rwptn;
	}
	public void setRwptn(String rwptn) {
		this.rwptn = rwptn;
	}
	public String getMsqmt() {
		return msqmt;
	}
	public void setMsqmt(String msqmt) {
		this.msqmt = msqmt;
	}
	@Override
	public String[] getPrimaryKeys() {
		return new String[]{"stcd","tm"};
	}
	@Override
	public String[] getFileds() {
		return new String[]{"rz","inq","w","blrz","otq","inqdr","rwchrcd","rwptn","msqmt"};
	}
	@Override
	public String getTableName() {
		return "ST_RSVR_R";
	}
	@Override
	public String notInsertField() {
		return "";
	}
	private final String objName = "rsvr";
	public StRsvrR( JSONObject j ) throws Exception{
		this.setValue(j, this.getPrimaryKeys(), this.objName, this);
		this.setValue(j, this.getFileds(), this.objName, this);
	}
}
