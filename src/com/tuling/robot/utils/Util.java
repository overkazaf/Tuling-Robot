package com.tuling.robot.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	public Util(){};
	public static String FormatDate (Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.format(date);
	} 
}
