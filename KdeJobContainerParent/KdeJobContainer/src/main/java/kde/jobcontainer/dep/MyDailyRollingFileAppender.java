package kde.jobcontainer.dep;

import java.io.IOException;

import org.apache.log4j.DailyRollingFileAppender;

import kde.jobcontainer.dep.utils.FileUtil;

public class MyDailyRollingFileAppender extends DailyRollingFileAppender
{
  public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
    throws IOException
  {
    String dirPath = FileUtil.getLogDirPath();
    if (fileName.lastIndexOf("\\") != -1)
      fileName = dirPath + fileName.substring(fileName.lastIndexOf("\\"));
    else
      fileName = dirPath + "\\" + fileName;

    System.out.println("fileName===" + fileName);
    super.setFile(fileName, append, bufferedIO, bufferSize);
  }
}