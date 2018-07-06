package kde.jobcontainer.dep.job.ipmonitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import kde.jobcontainer.dep.PlatformUtil;
import kde.jobcontainer.dep.domain.CountyPlat;
import kde.jobcontainer.util.abstracts.KJob;
import kde.jobcontainer.utils.ConnectionUtils;
/**
 * 
 * @author xxxxxxxx
 *
 * @date 2014-5-20 下午2:40:20
 */
public class MonitorIpJob extends KJob{

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		List<CountyPlat> ipList = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			Properties p = PlatformUtil.getPropertites();
			//获取ip地址列表
			ipList = getIpList(p,conn,stmt,rs);
			//循环检查每个ip是否连得上，并将最新时间和状态存入数据库
			if(ipList.size()>0){
				logger.info("检查ip表");
				for(CountyPlat ip : ipList){
					String relInfo = getConnInfo(ip);
					ip.setQueryTime(new Date());
					if(relInfo.equals("连接成功")){
						ip.setConnInfo(1);					
					}else{
						ip.setConnInfo(0);
					}
					ip.setRemark(relInfo);
					logger.info("开始更新ip数据");
					updateInfo(ip,p,conn,stmt);
					logger.info("开始插入ip数据");
					insertInfo(ip,p,conn,stmt);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		}
		
	}
	/**
	 * 往历史表插入数据
	 * @param ip
	 * @param p
	 */
	private void insertInfo(CountyPlat ip, Properties p,Connection conn,PreparedStatement stmt) {
		
		try {
			ip.setId(UUID.randomUUID().toString().replace("-", ""));
			conn = ConnectionUtils.getConnection(p);
			stmt = conn.prepareStatement("insert into COUNTY_PLAT_HISTORY(id,IP,ADCD,QUERY_TIME,CONN_INFO,remark) values(?,?,?,?,?,?)");
			stmt.setString(1, ip.getId());
			stmt.setString(2, ip.getIp());
			stmt.setString(3, ip.getAdcd());
			stmt.setString(4, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ip.getQueryTime()));
			stmt.setInt(5, ip.getConnInfo());
			stmt.setString(6, ip.getRemark());
			stmt.executeUpdate();
			logger.info("插入ip成功");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}			
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}
		}
	}
	/**
	 * 更新最新状态ip表的数据
	 * @param ip
	 * @param p
	 */
	private void updateInfo(CountyPlat ip, Properties p,Connection conn,PreparedStatement stmt) {
		try {
			conn = ConnectionUtils.getConnection(p);
			stmt = conn.prepareStatement("update COUNTY_PLAT_NEWEST set IP=?,ADCD=?,QUERY_TIME=?,CONN_INFO=?,remark=? where id=?");
			stmt.setString(6, ip.getId());
			stmt.setString(1, ip.getIp());
			stmt.setString(2, ip.getAdcd());
			stmt.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ip.getQueryTime()));
			stmt.setInt(4, ip.getConnInfo());
			stmt.setString(5, ip.getRemark());
			stmt.executeUpdate();
			logger.info("更新ip成功");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}
		}
	}
	/**
	 * 根据ip地址获取连接状态
	 * @param ip
	 * @return
	 */
	private String getConnInfo(CountyPlat ip) {
		Process process;
		String con = "连接失败";
		try {
			process = Runtime.getRuntime().exec("ping "+ip.getIp()+" -n 1");
			BufferedReader br = new BufferedReader(new InputStreamReader(
				      process.getInputStream(),"gbk"));
			 String s;
			 StringBuffer sb = new StringBuffer();
				    while ((s = br.readLine()) != null) {
				     // buf.append(s + "/r/n");
				     sb.append(s);
				    }
				    if(sb.toString().contains("已接收 = 1")){
				    	con="连接成功";
				    }else if(sb.toString().contains("TTL 传输中过期")){
				    	con="TTL 传输中过期";
				    }else if(sb.toString().contains("无法访问目标主机")){
				    	con="目标主机无法访问";
				    }else if(sb.toString().contains("未知主机")){
				    	con="未知主机";
				    }else if(sb.toString().contains("传输失败")){
				    	con=sb.toString().substring(sb.toString().indexOf("传输失败"),sb.toString().indexOf("。"));
				    }else if(sb.toString().contains("请求超时")){
				    	con="请求超时";
				    }else{
				    	con="请检查路由设置、网线或者网卡";
				    }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return con;
	}
	/**
	 * 获取ip地址列表
	 * @param p
	 * @return
	 */
	private List<CountyPlat> getIpList(Properties p,Connection conn,PreparedStatement stmt,ResultSet rs) {
		List<CountyPlat> ipList = new ArrayList<CountyPlat>();
		CountyPlat ip = null;
		try {
			conn = ConnectionUtils.getConnection(p);
			stmt = conn.prepareStatement("select * from COUNTY_PLAT_NEWEST");
			rs = stmt.executeQuery();
			while(rs.next()){
				ip = new CountyPlat(rs.getString("id"), rs.getString("IP"), rs.getString("ADCD"), rs.getDate("QUERY_TIME"), rs.getInt("CONN_INFO"), rs.getString("remark"));
				ipList.add(ip);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}
		}
		return ipList;
	}
	
	public static void main(String[] args) {
		Process process;
		try {
			process = Runtime.getRuntime().exec("ping "+"192.168.1.120");
			 BufferedReader br = new BufferedReader(new InputStreamReader(
				      process.getInputStream(),"gbk"));
			 String s;
			 StringBuffer sb = new StringBuffer();
				    while ((s = br.readLine()) != null) {
				     // buf.append(s + "/r/n");
				     sb.append(s);
				    }
				    if(sb.toString().contains("已接收 = 4")){
				    	System.out.println("能连通");
				    }
				    System.out.println("PING: 传输失败，错误代码 1231。".substring("PING: 传输失败，错误代码 1231。".indexOf("传输失败"),"PING: 传输失败，错误代码 1231。".indexOf("。")));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
