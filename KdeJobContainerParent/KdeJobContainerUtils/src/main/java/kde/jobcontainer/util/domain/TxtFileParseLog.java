package kde.jobcontainer.util.domain;

import java.sql.ResultSet;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxtFileParseLog extends DataObj{
	private static final Logger logger = LoggerFactory.getLogger(TxtFileParseLog.class);
	
	private Integer id;
	private String jobName;			//标识出这个job的任务,避免多任务中文件名重复
	private String fileName;		//操作过的文件名称
	private Date fileModifyDate;	//文件处理时它的修改时间
	private Date processDate;		// 处理的时间
	private String status ;			//处理状态标识
	private String info;		 	//相关信息
	
	public TxtFileParseLog(){}
	
	public TxtFileParseLog(ResultSet rs){
		try{
			this.id = rs.getInt("id");
			this.jobName = rs.getString( "jobName" );
			this.fileName = rs.getString( "fileName" );
			this.fileModifyDate = rs.getTimestamp( "info" );
			this.processDate = rs.getTimestamp( "processDate" );
			this.status = rs.getString( "status" );
			this.info = rs.getString( "info" );
		}catch(Exception e){
			logger.error( e.getMessage() , e);
		}
	}
	
	public String[] getPrimaryKeys(){
		return  new String[]{"id"};
	}
	public String notInsertField(){
		return "id";
	}
	public String[] getFileds(){
		return new String[]{"jobName","fileName","fileModifyDate","processDate","status","info"};
	}
	public String getTableName(){
		return "TXTFILEPARSELOG";
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Date getFileModifyDate() {
		return fileModifyDate;
	}
	public void setFileModifyDate(Date fileModifyDate) {
		this.fileModifyDate = fileModifyDate;
	}
	public Date getProcessDate() {
		return processDate;
	}
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	

	
	
	public static void main(String[] args){
		TxtFileParseLog t = new TxtFileParseLog();
		t.setJobName( "121323" );
		t.setProcessDate( new Date() );
		System.out.println( t.getUpdateSql( t ) );
	}
}
