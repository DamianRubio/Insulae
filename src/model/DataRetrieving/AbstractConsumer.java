package model.DataRetrieving;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import model.DataVisualizing.MwpVariable;
import model.DataVisualizing.SwhVariable;
import model.DataVisualizing.WsVariable;
import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.units.DateUnit;
import utilities.MeteoUtil;

public abstract class AbstractConsumer {

	/**
	 * Transforms an array of data into a list of doubles.
	 * 
	 * @param data
	 *            NetCDF array of data.
	 * @return List of doubles containing the transformed data.
	 */
	protected List<Double> transformToListDouble(Array data) {
		List<Double> aux = new ArrayList<Double>();
		for (int i = 0; i < data.getSize(); i++)
			if (!String.valueOf(data.getDouble(i)).equals("NaN"))
				aux.add(data.getDouble(i));
			else {
				return null;
			}
		return aux;
	}

	/**
	 * Gets all the data of the given coordinates regarding the variable provided.
	 * 
	 * @param fileName
	 *            Name of the file from which the data needs to be extracted.
	 * @param variableName
	 *            Name of the variable that wants to be consulted.
	 * @param userLatitude
	 *            Latitude for which the data is going to be extracted.
	 * @param userLongitude
	 *            Longitude for which the data is going to be extracted.
	 * @return Netcdf array containing the data relative to the variable and
	 *         coordinate provided.
	 */
	public abstract Array getData(String fileName, String variableName, double userLatitude, double userLongitude);

	/**
	 * Gets all the dates of the data contained in the provided file.
	 * 
	 * @param fileName
	 *            File from which we want to extract the dates.
	 * @return List of dates extracted from the provided file.
	 */
	public List<Date> getFileDates(String fileName) {
		List<Date> dateUnitList = new ArrayList<Date>();
		try (NetcdfFile ncfile = NetcdfFile.open(fileName);) {

			DateUnit date_unit;
			Date base_date = MeteoUtil.getInstance().getBaseDate(fileName);
			String time_unit = MeteoUtil.getInstance().getTimeUnit(fileName);
			Variable v = ncfile.findVariable("time");
			Array times = v.read();
			for (int i = 0; i < times.getSize(); i++) {
				date_unit = new DateUnit(times.getDouble(i), time_unit, base_date);
				dateUnitList.add(date_unit.getDate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateUnitList;
	}

	/**
	 * Gets all the latitudes contained in the provided file.
	 * 
	 * @param fileName
	 *            Name of the file of which we want to extract the latitudes.
	 * @return List containing all the latitudes contained in the data of the
	 *         provided file.
	 */
	protected List<Double> getFileLatitudes(String fileName) {
		NetcdfFile ncfile = null;
		List<Double> latitudesList = new ArrayList<Double>();
		try {
			ncfile = NetcdfFile.open(fileName);
			Variable v = ncfile.findVariable("latitude");
			String[] latitudes = v.read().toString().split(" ");
			for (String str : latitudes)
				latitudesList.add(Double.valueOf(str));
			ncfile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return latitudesList;
	}

	/**
	 * Gets all the longitudes contained in the provided file.
	 * 
	 * @param fileName
	 *            Name of the file of which we want to extract the longitudes.
	 * @return List containing all the longitudes contained in the data of the
	 *         provided file.
	 */
	protected List<Double> getFileLongitudes(String fileName) {
		NetcdfFile ncfile = null;
		List<Double> longitudesList = new ArrayList<Double>();
		try {
			ncfile = NetcdfFile.open(fileName);
			Variable v = ncfile.findVariable("longitude");
			String[] longitudes = v.read().toString().split(" ");
			for (String str : longitudes)
				longitudesList.add(Double.valueOf(str));
			ncfile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return longitudesList;
	}

	/**
	 * Gets the starting date of the data contained in the provided file.
	 * 
	 * @param file
	 *            File from which we want to know its starting date.
	 * @return The starting date of the data in the file.
	 */
	public Date getFileStartDate(File file) {
		String filename = file.getAbsolutePath();
		return getFileDates(filename).get(0);
	}

	/**
	 * Gets all the latitudes contained in the data of a file calling an internal
	 * method with the needed implementation.
	 * 
	 * @param fileName
	 *            File for which the latitudes are wanted.
	 * @return List containing all the latitudes in the file.
	 */
	public List<Double> getLatData(String fileName) {
		return this.getFileLatitudes(fileName);
	}

	/**
	 * Gets all the longitudes contained in the data of a file calling an internal
	 * method with the needed implementation.
	 * 
	 * @param fileName
	 *            File for which the longitudes are wanted.
	 * @return List containing all the latitudes in the file.
	 */
	public List<Double> getLonData(String fileName) {
		return this.getFileLongitudes(fileName);
	}

	/**
	 * Checks if there is significant data available for a given point.
	 * 
	 * @param absolutePath
	 *            Path of the file on which the data is going to be checked.
	 * @param latPos
	 *            Latitude of the point that is going to be checked.
	 * @param lonPos
	 *            Longitude of the point that is going to be checked.
	 * @return Map containing the point if there is significant data, null
	 *         otherwise.
	 */
	public abstract Map<String, Double> isSignificantDataAvailable(String absolutePath, double latPos, double lonPos);

	/**
	 * Gets the list containing all the SWH related data from the file and the
	 * specified point.
	 * 
	 * @param file
	 *            File from which the data is going to be retrieved.
	 * @param latitude
	 *            Latitude of the point for which the that is going to be retrieved.
	 * @param longitude
	 *            Longitude of the point for which the that is going to be
	 *            retrieved.
	 * @return List containing all the SWH related info for the point contained in
	 *         the file.
	 */
	public abstract List<SwhVariable> getSwhData(File file, double latitude, double longitude);

	/**
	 * Transforms a list with double values and a list with dates into a list of SWH
	 * variables.
	 * 
	 * @param swhValues
	 *            Values of data for the SWH variable.
	 * @param fileDates
	 *            Time stamps for the SWH variables.
	 * @return List containing the SWH variables created with their correspondent
	 *         time and value.
	 */
	protected List<SwhVariable> createListSwhData(List<Double> swhValues, List<Date> fileDates) {
		List<SwhVariable> vars = new ArrayList<>();
		int counter = 0;
		for (Double value : swhValues) {
			SwhVariable var = new SwhVariable(fileDates.get(counter), value);
			vars.add(var);
			counter++;
		}
		return vars;
	}

	/**
	 * Gets the list containing all the MWP related data from the file and the
	 * specified point.
	 * 
	 * @param file
	 *            File from which the data is going to be retrieved.
	 * @param latitude
	 *            Latitude of the point for which the that is going to be retrieved.
	 * @param longitude
	 *            Longitude of the point for which the that is going to be
	 *            retrieved.
	 * @return List containing all the MWP related info for the point contained in
	 *         the file.
	 */
	public abstract List<MwpVariable> getMwpData(File file, double latitude, double longitude);

	/**
	 * Transforms a list with double values and a list with dates into a list of MWP
	 * variables.
	 * 
	 * @param mwpValues
	 *            Values of data for the MWP variable.
	 * @param fileDates
	 *            Time stamps for the MWP variables.
	 * @return List containing the MWP variables created with their correspondent
	 *         time and value.
	 */
	protected List<MwpVariable> createListMwpData(List<Double> mwpValues, List<Date> fileDates) {
		List<MwpVariable> vars = new ArrayList<>();
		int counter = 0;
		for (Double value : mwpValues) {
			MwpVariable var = new MwpVariable(fileDates.get(counter), value);
			vars.add(var);
			counter++;
		}
		return vars;
	}

	/**
	 * Gets the list containing all the WS related data from the file and the
	 * specified point.
	 * 
	 * @param file
	 *            File from which the data is going to be retrieved.
	 * @param latitude
	 *            Latitude of the point for which the that is going to be retrieved.
	 * @param longitude
	 *            Longitude of the point for which the that is going to be
	 *            retrieved.
	 * @return List containing all the WS related info for the point contained in
	 *         the file.
	 */
	public abstract List<WsVariable> getWindData(File file, double latitude, double longitude);

	/**
	 * Transforms a list with double values and a list with dates into a list of WS
	 * variables.
	 * 
	 * @param windValues
	 *            Values of data for the WS variable.
	 * @param fileDates
	 *            Time stamps for the WS variables.
	 * @return List containing the WS variables created with their correspondent
	 *         time and value.
	 */
	protected List<WsVariable> createListWindData(List<Double> windValues, List<Date> fileDates) {
		List<WsVariable> vars = new ArrayList<>();
		int counter = 0;
		for (Double value : windValues) {
			WsVariable var = new WsVariable(fileDates.get(counter), value);
			vars.add(var);
			counter++;
		}
		return vars;
	}

}
