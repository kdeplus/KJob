package kde.jobcontainer.util.domain;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.util.domain.DataObj;

/**
 * @author Lidong 2014-4-16
 * 从农水局项目中抽出，仅作为例子
 */
public class StPptnR extends DataObj {
	private String stcd;
	private Date tm;
	private Double drp;
	private Double intv;
	private Double pdr;
	private Double dyp;
	private String wth;
	
	@Override
	public String[] getPrimaryKeys() {
		return new String[]{"stcd","tm"};
	}

	@Override
	public String[] getFileds() {
		return new String[]{"drp","dyp","intv","pdr","dyp","wth"};
	}

	@Override
	public String getTableName() {
		return "ST_PPTN_R";
	}

	@Override
	public String notInsertField() {
		return "";
	}

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

	public Double getDrp() {
		return drp;
	}

	public void setDrp(Double drp) {
		this.drp = drp;
	}

	public Double getIntv() {
		return intv;
	}

	public void setIntv(Double intv) {
		this.intv = intv;
	}

	public Double getPdr() {
		return pdr;
	}

	public void setPdr(Double pdr) {
		this.pdr = pdr;
	}

	public Double getDyp() {
		return dyp;
	}

	public void setDyp(Double dyp) {
		this.dyp = dyp;
	}

	public String getWth() {
		return wth;
	}

	public void setWth(String wth) {
		this.wth = wth;
	}
	private final String objName = "pptn";
	public StPptnR( JSONObject j ) throws Exception{
		this.setValue(j, this.getPrimaryKeys(), this.objName, this);
		this.setValue(j, this.getFileds(), this.objName, this);
	}
}
