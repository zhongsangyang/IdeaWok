package com.cn.flypay.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */

/**
 * 
 * 文件建立于2009-6-24
 */
public class WorkDay {

	/**
	 * @param args
	 */
	/*
	 * 先找时间段内包含的节日，确定节日中是否经历周未 如有周未，先计算除周未外的假日天数，最后再计算周未天数
	 */
	public int ds = 3; // 节假日休息天数
	Map map1 = new HashMap(); // 节假日名:起休日期
	Map map2 = new HashMap(); // 起休日期:止休日期
	public String st = "2009-04-12";
	public String et = "2009-06-18";
	public int jiari = 0;

	public WorkDay() throws ParseException {
		map1.put("5.1", "05-01");
		map2.put("05-01", "05-03");

		String st1 = map1.get("5.1").toString();
		String et1 = map2.get(st1).toString();

		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy");

		Date sti = sdf.parse(st.substring(5));
		Date eti = sdf.parse(et.substring(5));
		Date i = sdf.parse(st1);
		Date i2 = sdf.parse(et1);
		/*
		 * 节日日期需在起休日之后 且 节日多出起停日期的部分也需不计 外国的星期一为中国的星期天
		 */
		if (sdf3.parse(st.substring(0, 4)).compareTo(sdf3.parse(et.substring(0, 4))) == 0) {
			if (i.after(sti)) { // 如果节日起休日在任务日当中
				if (i2.before(eti)) { // 如果节日休止日在任务日当中
					/*
					 * 同年份，任务起始日和终止日如果跨一个年份的话，可能计算会出现比较大的误差 等测试
					 */

					Calendar ca = Calendar.getInstance();
					ca.setTime(sdf2.parse(st.substring(0, 5) + st1));
					for (int k = 0; k < ds - 1; k++) {
						ca.add(Calendar.DAY_OF_MONTH, k);
						if (ca.get(Calendar.DAY_OF_WEEK) == 7 || ca.get(Calendar.DAY_OF_WEEK) == 1)
							continue;
						else
							jiari++;
					}
					ca.setTime(sdf2.parse(st));
					int xinqi = ca.get(Calendar.DAY_OF_WEEK);
					int t1 = ca.get(Calendar.DAY_OF_YEAR);
					ca.setTime(sdf2.parse(et));
					int t2 = ca.get(Calendar.DAY_OF_YEAR);

					if (xinqi == 1) {
						jiari += ((t2 - t1) / 7) * 2 + 1;
					} else {
						jiari += (t2 - t1 + 1 - (7 - xinqi + 2)) * 2 / 7 + 1;
					}

				}
			}
		}
	}

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		/*
		 * Calendar ca=Calendar.getInstance(); try { ca.setTime(new
		 * SimpleDateFormat("yyyy-MM").parse("2008-02")); } catch
		 * (ParseException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * System.out.println(ca.getActualMaximum(Calendar.DATE));
		 */
		WorkDay wd = new WorkDay();

		System.out.println(wd.jiari);
	}

}
