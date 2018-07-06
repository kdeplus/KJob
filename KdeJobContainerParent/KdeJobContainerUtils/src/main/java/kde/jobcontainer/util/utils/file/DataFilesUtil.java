package kde.jobcontainer.util.utils.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lidong 处理数据文件相关的工作
 */
public class DataFilesUtil {

	private static Logger logger = LoggerFactory.getLogger(DataFilesUtil.class);

	/**
	 * @author Lidong
	 * @param dictory
	 * @return 该文件夹下面所有的文件,如果文件夹不存在,则返回null,如文件夹下文件不存在,返回null
	 */
	public static File[] getFilesInDataDictory(File dictory, FileFilter filter) {
		return dictory.listFiles(filter);
	}

	/**
	 * @author lidong 2014年12月23日
	 * @param source 源文件
	 * @param target 目标文件
	 * <pre>
	 * 将源文件复制到目标文件
	 * </pre>
	 */
	public void fileChannelCopy(File source, File target) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fi = new FileInputStream(source);
			//如果目标文件不存在，先判断文件夹，进行创建，再创建出来这个文件
			if(!target.exists()){
				if(!target.getParentFile().exists()){
					target.getParentFile().mkdirs();
					target.createNewFile();
				}
			}
			fo = new FileOutputStream(target);
			in = fi.getChannel();// 得到对应的文件通道
			out = fo.getChannel();// 得到对应的文件通道
			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {
			logger.error("复制文件中异常", e);
		} finally {
			try {
				fi.close();
				in.close();
				fo.close();
				out.close();
			} catch (IOException e) {
				logger.error("复制文件中，关闭流时出现异常", e);
			}
		}
	}
}
