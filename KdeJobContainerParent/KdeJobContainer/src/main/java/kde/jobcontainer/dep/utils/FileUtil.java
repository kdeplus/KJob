package kde.jobcontainer.dep.utils;

import java.io.File;
import java.util.Date;

import kde.jobcontainer.util.utils.DateUtil;

public class FileUtil {
	public static boolean isDirExist(String dirPath)
	  {
	    File dir = new File(dirPath);
	    if (!(dir.exists())) {
	      dir.mkdir();
	      return false;
	    }
	    return true; }

	  public static String getLogDirPath() {
	    String yearDirPath = System.getProperty("user.dir") + "\\log\\" + DateUtil.DateTimeToString(new Date(), DateUtil.yyyy);
	    isDirExist(yearDirPath);
	    String YMDirPath = yearDirPath + "\\" + DateUtil.DateTimeToString(new Date(), DateUtil.MM);
	    isDirExist(YMDirPath);
	    return YMDirPath;
	  }
}
