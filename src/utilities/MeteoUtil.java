package utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class MeteoUtil {

	private MeteoUtil() {
	}

	static MeteoUtil instance = new MeteoUtil();

	/**
	 * Retrieves the instance for the MeteoUtil singleton.
	 * 
	 * @return The current instance of MeteoUtil.
	 */
	public static MeteoUtil getInstance() {
		return instance;
	}

	/**
	 * Allows to get the date since when the time has been recorded.
	 * 
	 * @param filename
	 *            Name of the file that we want to analyze.
	 * @return Date since when the time has been recorded.
	 */
	public Date getBaseDate(String filename) {
		NetcdfFile ncfile = null;
		Date dat = null;
		try {
			ncfile = NetcdfFile.open(filename);
			Variable v = ncfile.findVariable("time");
			String[] time_unit = v.getAttributes().get(0).getStringValue().split(" ");
			String date_str = time_unit[2] + " " + time_unit[3];
			DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:s");
			date_format.setTimeZone(TimeZone.getTimeZone("UTC"));
			dat = date_format.parse(date_str);
			ncfile.close();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return dat;
	}

	/**
	 * Allows to get the time unit on which the time changes.
	 * 
	 * @param filename
	 *            Name of the file that we want to analyze.
	 * @return Unit on which the time changes.
	 */
	public String getTimeUnit(String filename) {
		NetcdfFile ncfile = null;
		String unit = "";
		try {
			ncfile = NetcdfFile.open(filename);
			Variable v = ncfile.findVariable("time");
			String[] time_unit = v.getAttributes().get(0).getStringValue().split(" ");
			unit = time_unit[0];
			ncfile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return unit;
	}

	/**
	 * Reads the input of the user and retrieves it as a string.
	 * 
	 * @param msg
	 *            Message to inform the user what is expected from him.
	 * @return Input of the user.
	 */
	public String readInput(String msg) {
		System.out.println(msg);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String aux = "";
		try {
			aux = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return aux;
	}

	/**
	 * Receives a distance in kilometers and converts it to nautical miles.
	 * 
	 * @param kilometers
	 *            Distance in kilometers.
	 * @return Distance in nautical miles.
	 */
	public double convertKmToNauticalMile(double kilometers) {
		return kilometers * 0.539957;
	}

	/**
	 * Receives a distance in nautical miles and converts it to kilometers.
	 * 
	 * @param nautical_miles
	 *            Distance in nautical miles.
	 * @return Distance in kilometers.
	 */
	public double convertNauticalMileToKm(double nautical_miles) {
		return nautical_miles * 1.852;
	}

	/**
	 * Calculates the corresponding date of a week ago.
	 * 
	 * @return Date one week ago.
	 */
	public Date getDateOneWeekAgo() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		return cal.getTime();
	}

	/**
	 * Calculates if the date is today's date.
	 * 
	 * @param date
	 *            The date we want to know if it is today.
	 * @return True if the date is today's date, false otherwise.
	 */
	public boolean isDateToday(Date date) {
		Calendar calDate = Calendar.getInstance();
		calDate.setTime(date);

		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		if (calDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
				&& calDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)
				&& calDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH))
			return true;
		return false;
	}

	/**
	 * Calculates the euclidean distance between two points.
	 * 
	 * @param y1
	 *            Y coordinate of the first point.
	 * @param x1
	 *            X coordinate of the first point.
	 * @param y2
	 *            Y coordinate of the second point.
	 * @param x2
	 *            X coordinate of the second point.
	 * @return Euclidean distance between the provided points.
	 */
	public double calculateEuclideanDistance(double y1, double x1, double y2, double x2) {
		double xDistace = (x2 - x1);
		double yDistace = (y2 - y1);
		return Math.sqrt(Math.pow(xDistace, 2) + Math.pow(yDistace, 2));
	}

	/**
	 * Calculates the distance between two points.
	 * 
	 * @param latitude1
	 *            Latitude of the first point.
	 * @param longitude1
	 *            Longitude of the first point.
	 * @param latitude2
	 *            Latitude of the second point.
	 * @param longitude2
	 *            Longitude of the second point.
	 * @return Distance between the provided points.
	 */
	public double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
		double earth_r_km = 6378.137;
		latitude1 = (Math.PI / 180) * latitude1;
		longitude1 = (Math.PI / 180) * longitude1;
		latitude2 = (Math.PI / 180) * latitude2;
		longitude2 = (Math.PI / 180) * longitude2;

		double distance_km = earth_r_km
				* Math.acos(Math.cos(latitude1) * Math.cos(latitude2) * Math.cos(longitude2 - longitude1)
						+ Math.sin(latitude1) * Math.sin(latitude2));
		return distance_km;
	}

	/**
	 * Transforms one given coordinate to the same coordinate in the negative format
	 * regarding the Greenwhich meridian.
	 * 
	 * @param longitude
	 *            The longitude coordinate that wants to be transformed.
	 * @return The equivalent coordinate from 0º to -180º.
	 */
	public Double toWestCoordinate(Double longitude) {
		if (longitude > 0)
			return longitude - 360;
		return longitude;
	}

	/**
	 * Transforms one coordiate at west of Greenwhich to the same coordinate in the
	 * internatioal format.
	 * 
	 * @param longitude
	 *            The longitude coordinate that wants to be transformed.
	 * @return The equivalent coordinate from 0º to 180º.
	 */
	public Double toRegularCoordinate(Double longitude) {
		if (longitude <= 0)
			return 360 + longitude;
		return longitude;
	}

	/**
	 * Rounds a given double value to the given decimal places.
	 * 
	 * @param value
	 *            The decimal that is wanted to be rounded.
	 * @param places
	 *            The number of decimal places that the output is wanted to have.
	 * @return The rounded value with the specific decimal places. If the number of
	 *         places is not a valid one it will return the original value.
	 */
	public double round(double value, int places) {
		if (places >= 0) {
			BigDecimal bd = new BigDecimal(value);
			bd = bd.setScale(places, RoundingMode.HALF_UP);
			return bd.doubleValue();
		}
		return value;
	}

	/**
	 * Checks of a date is today or in a week before today.
	 * 
	 * @param fileDate
	 *            The date that wants to be checked.
	 * @return True if the data is today or in the previous week, false otherwise.
	 */
	public boolean isDateTodayOrInThePreviousWeek(Date fileDate) {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, 0);
		return isDateToday(fileDate) || (fileDate.after(getDateOneWeekAgo()) && fileDate.before(today.getTime()));
	}

	/**
	 * Checks if a date is later than the current date.
	 * 
	 * @param date
	 *            The date that wants to be checked.
	 * @return True if the date is later than the current date, false otherwise.
	 */
	public boolean isDateLaterThanToday(Date date) {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, 0);
		return !isDateToday(date) && date.after(today.getTime());
	}
}
