/**
 * @author DOETTLINGER
 */

package com.example.obd2.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Utilities {

	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}

	public static String timestampToDate(long timestamp) {
		Date date = new Date(timestamp);
		Calendar cal = new GregorianCalendar(TimeZone.getDefault());
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		sdf.setCalendar(cal);
		cal.setTime(date);
		String t = sdf.format(date);
		String result = t.substring(0, 26) + ":" + t.substring(26);
		return result;
	}

	public static String timestamp2Date(long timestamp) {
		Date date = new Date(timestamp);
		Calendar cal = new GregorianCalendar(TimeZone.getDefault());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMM/dd hh:mm:ss");
		sdf.setCalendar(cal);
		cal.setTime(date);
		// String t = sdf.format(date);
		// String result = t.substring(0, 26) + ":" + t.substring(26);
		return sdf.format(date);
	}

	public static String setOpenRouteServiceOrientationPoint(double longitude,
			double latitude) {
		return "http://openrouteservice.org/index.php?position=" + longitude
				+ "," + latitude + "&zoom=16";

	}
}
