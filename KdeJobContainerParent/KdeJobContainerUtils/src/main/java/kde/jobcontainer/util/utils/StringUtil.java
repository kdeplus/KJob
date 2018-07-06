package kde.jobcontainer.util.utils;

import java.text.DecimalFormat;

import org.apache.commons.lang.StringEscapeUtils;

public class StringUtil {

	/**
	 * @param oldValue
	 *            要转换的数字
	 * @param dotlen
	 *            保留的小数点后位数
	 * @param addZero
	 *            是否在后面补0
	 * @return 格式化输出后的数字，如果oldValue为null，则返回“”
	 */
	public static String viewNumFormat(Double oldValue, int dotlen,
			boolean addZero, String emptyStr) {
		if (oldValue == null) {
			return emptyStr;
		} else {
			return viewNumFormat(oldValue.doubleValue(), dotlen, addZero);
		}
	}

	/**
	 * @param oldValue
	 *            要转换的数字
	 * @param dotlen
	 *            保留的小数点后位数
	 * @param addZero
	 *            是否在后面补0
	 * @return 格式化输出后的数字，如果oldValue为null，则返回“”
	 */
	public static String viewNumFormat(Double oldValue, int dotlen,
			boolean addZero) {
		if (oldValue == null) {
			return "";
		} else {
			return viewNumFormat(oldValue.doubleValue(), dotlen, addZero);
		}
	}

	public static String viewNumFormat(double v, int dotlen) {
		if (dotlen != 0)
			return viewNumFormat(v, dotlen, true);
		else
			return viewNumFormat(v, dotlen);
	}

	/**
	 * @param oldValue
	 *            要转换的数字
	 * @param dotlen
	 *            保留的小数点后位数
	 * @param addZero
	 *            如原数值小数后位数不足，是否在后面补0
	 * @return 格式化输出后的数字，如果oldValue为null，则返回“”
	 */
	public static String viewNumFormat(double oldValue, int dotlen,
			boolean addZero) {
		if (oldValue == 0) {
			return "0";
		} else if (dotlen == 0) {
			return String.valueOf((int) (oldValue + 0.5));
		} else {
			String formatStr = "####.";

			if (addZero) {
				for (int i = 0; i < dotlen; i++)
					formatStr = formatStr + "0";
			} else {
				for (int i = 0; i < dotlen; i++)
					formatStr = formatStr + "#";
			}
			DecimalFormat df1 = new DecimalFormat(formatStr);
			String str = df1.format(oldValue);
			if (str.charAt(0) == '.') {
				str = "0" + str;
			}
			return str;
		}
	}

	public static String isNull(Object obj) {
		return obj == null ? "" : obj.toString();
	}

	public static String encodeForJSON(String text) {
		String result = "";
		if (text == null)
			return "";
		else {
			result = text.replaceAll("\\\\", "\\\\\\\\");
			result = result.replaceAll("'", "\\\\'");
			// result = result.replaceAll("\"", "\\\\\"");
		}
		return result;
	}

	/**
	 * @return 获取一个随机的数字范围应该在 0到999
	 */
	public static String getRandomStr() {
		return String.valueOf((int) (Math.random() * 1000));
	}

	/**
	 * @param str
	 * @return 将科学计数字符串转换成正常数字
	 */
	public static Double ConversionFromString(String str) {
		try {
			if (str == null || str == "")
				return null;
			if (str.indexOf("e-") == -1) {
				return Double.valueOf(str);
			} else {
				String[] s = str.split("e-");
				double num1 = Double.parseDouble(s[0]);
				double num2 = Double.parseDouble(s[1]);
				return new Double(num1 * Math.pow(10d, num2));
			}
		} catch (Exception e) {
			System.out
					.println("**********************StringUtil.ConversionFromString:"
							+ str);
			return null;
		}
	}

	public static String toTrim(String str) {
		if (str == null)
			return "";

		if (str.trim().equalsIgnoreCase("null"))
			return "";
		return str.trim();
	}

	/**
	 * @param oldStr
	 *            原字符串
	 * @param len
	 *            要求的长度
	 * @param fileWith
	 *            使用什么字符补长
	 * @return
	 */
	public static String getStrToLength(String oldStr, int len,
			Character fileWith) {
		StringBuffer sb = new StringBuffer(oldStr.trim());
		if (fileWith == null) {
			fileWith = new Character(' ');
		}
		while (sb.length() < len) {
			sb.append(fileWith);
		}
		return sb.toString();
	}
	public static String escapeSql(String str){
		return StringEscapeUtils.escapeSql(str);
	}
}
