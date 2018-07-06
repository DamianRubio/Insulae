package model.DataRetrieving;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.DataVisualizing.MwpVariable;
import model.DataVisualizing.SwhVariable;
import model.DataVisualizing.WsVariable;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.ma2.Range;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;
import utilities.MeteoUtil;

public class PrudentConsumer extends AbstractConsumer {

	@Override
	public Array getData(String fileName, String variableName, double userLatitude, double userLongitude) {
		Array aux = null;

		if (isExactPoint(fileName, userLatitude, userLongitude))
			aux = getDataFromExactPoint(fileName, variableName, userLatitude, userLongitude);
		else
			aux = getDataPrudent(fileName, variableName, userLatitude, userLongitude);
		return aux;
	}

	/**
	 * Retrieves the data in prudent mode if the point asked by the user is one of
	 * the exact ones contained in the file.
	 * 
	 * @param fileName
	 *            File from which the data is going to be read.
	 * @param variableName
	 *            Variable we want to retrieve from the file.
	 * @param userLatitude
	 *            Latitude of the user point.
	 * @param userLongitude
	 *            Longitude of the user point.
	 * @return Array containing the data regarding the variable and point that the
	 *         user asked.
	 */
	private Array getDataFromExactPoint(String fileName, String variableName, double userLatitude,
			double userLongitude) {
		NetcdfFile ncfile = null;
		Array aux = null;

		int pos_lat = getFileLatitudes(fileName).indexOf(userLatitude);
		int pos_long = getFileLongitudes(fileName).indexOf(userLongitude);

		try {
			ncfile = NetcdfDataset.openDataset(fileName);
			Variable v = ncfile.findVariable(variableName);

			List<Range> ranges = new ArrayList<Range>();
			ranges.add(null);
			ranges.add(new Range(pos_lat, pos_lat));
			ranges.add(new Range(pos_long, pos_long));
			aux = v.read(ranges).reduce();
		} catch (IOException | InvalidRangeException e) {
			e.printStackTrace();
		}
		return aux;
	}

	/**
	 * Retrieves the data from the file for those points that are not exactly the
	 * ones contained in the file.
	 * 
	 * @param fileName
	 *            File from which the data is going to be read.
	 * @param variableName
	 *            Variable we want to retrieve from the file.
	 * @param userLatitude
	 *            Latitude of the user point.
	 * @param userLongitude
	 *            Longitude of the user point.
	 * @return Array containing the data regarding the variable and point that the
	 *         user asked.
	 */
	private Array getDataPrudent(String fileName, String variableName, double userLatitude, double userLongitude) {
		// Calcula los cuatro puntos más cercanos al punto pedido por el usuario.
		List<HashMap<String, Double>> nearestPoints = calculateNearestPoints(fileName, userLatitude, userLongitude);

		// Comprueba que todos los puntos tienen datos, descarta los que no
		List<HashMap<String, Double>> nearestPointsNotEarth = new ArrayList<>();
		double distance = 0;
		for (int i = 0; i < nearestPoints.size(); i++) {
			if (!isItEarth(fileName, variableName, nearestPoints.get(i).get("lat"), nearestPoints.get(i).get("lon"))) {
				nearestPointsNotEarth.add(nearestPoints.get(i));
				distance += nearestPoints.get(i).get("distance");
			}
		}

		// Decide el factor de ponderación de cada uno de los puntos y lo aplica a sus
		// datos
		List<Double> result = new ArrayList<>();
		for (int i = 0; i < nearestPointsNotEarth.size(); i++) {
			double prudentFactor = nearestPointsNotEarth.get(i).get("distance") / distance;
			Array auxResult = this.getData(fileName, variableName, nearestPointsNotEarth.get(i).get("lat"),
					nearestPointsNotEarth.get(i).get("lon"));
			for (int j = 0; j < auxResult.getSize(); j++)
				if (result.size() == j)
					result.add(auxResult.getDouble(j) * prudentFactor);
				else
					result.set(j, ((auxResult.getDouble(j) * prudentFactor) + result.get(j)));
		}

		// Devuelve el resultado
		List<String> stringResult = new ArrayList<>();
		for (Double aux : result)
			stringResult.add(aux.toString());

		return Array.makeArray(DataType.DOUBLE, stringResult);
	}

	/**
	 * Checks if a given point is an earth point.
	 * 
	 * @param fileName
	 *            Name of the file on which the data is going to be checked.
	 * @param variableName
	 *            Name of one of the variables of the data.
	 * @param latitude
	 *            Latitude of the point that is going to be checked.
	 * @param longitude
	 *            Latitude of the point that is going to be checked.
	 * @return True if the point is an Earth point, false otherwise.
	 */
	private boolean isItEarth(String fileName, String variableName, double latitude, double longitude) {
		Array data = this.getData(fileName, variableName, latitude, longitude);
		if (!String.valueOf(data.getDouble(0)).equals("NaN"))
			return false;
		return true;
	}

	/**
	 * Calculates the four nearest points to one provided by the user.
	 * 
	 * @param fileName
	 *            Name of the file on which you want to search the points.
	 * @param userLatitude
	 *            Latitude of the point given by the user.
	 * @param userLongitude
	 *            Longitude of the point given by the user.
	 * @return List containing the four nearest points to the one provided by the
	 *         user.
	 */
	private List<HashMap<String, Double>> calculateNearestPoints(String fileName, double userLatitude,
			double userLongitude) {
		List<Double> lats = getFileLatitudes(fileName);
		List<Double> longs = getFileLongitudes(fileName);
		List<HashMap<String, Double>> nearestPoints = new ArrayList<HashMap<String, Double>>();

		// Calculo las distancias de todos los puntos al punto que me interesa
		List<HashMap<String, Double>> distances = new ArrayList<HashMap<String, Double>>();
		for (int i = 0; i < lats.size(); i++)
			for (int j = 0; j < longs.size(); j++) {
				Map<String, Double> aux = new HashMap<String, Double>();
				aux.put("lat", lats.get(i));
				aux.put("lon", longs.get(j));
				aux.put("distance", MeteoUtil.getInstance().calculateDistance(userLatitude, userLongitude, lats.get(i),
						longs.get(j)));
				distances.add((HashMap<String, Double>) aux);
			}

		// Selecciono el mas pequeño, lo guardo en la solución y lo borro, cuatro veces
		nearestPoints = selectFourWithLessDistance(distances);

		return nearestPoints;
	}

	/**
	 * Select the for elements of the list with has the lowest values for key
	 * "distance".
	 * 
	 * @param distances
	 *            List of the points and its distances.
	 * @return List containing the four points with less distance.
	 */
	private List<HashMap<String, Double>> selectFourWithLessDistance(List<HashMap<String, Double>> distances) {
		List<HashMap<String, Double>> fourNearestPoints = new ArrayList<HashMap<String, Double>>();
		for (int counter = 0; counter < 4; counter++) {
			double minDistance = Double.MAX_VALUE;
			int selected = -1;
			for (int i = 0; i < distances.size(); i++) {
				if (distances.get(i).get("distance") < minDistance) {
					selected = i;
					minDistance = distances.get(i).get("distance");
				}
			}
			fourNearestPoints.add(distances.get(selected));
			distances.remove(selected);
		}
		return fourNearestPoints;
	}

	@Override
	public Map<String, Double> isSignificantDataAvailable(String absolutePath, double latPos, double lonPos) {
		// Creo la respuesta
		Map<String, Double> solution = new HashMap<String, Double>();
		solution.put("lat", latPos);
		solution.put("lon", lonPos);

		// Si el punto es un punto exacto lo devuelvo
		if (isExactPoint(absolutePath, latPos, lonPos))
			return solution;

		// Si el punto no es exacto pero está dentro de los límites lo devuelvo
		else if (isWithinTheLimit(absolutePath, latPos, lonPos))
			return solution;

		return null;
	}

	/**
	 * Calculates if a given point is exactly one of the contained in the data.
	 * 
	 * @param fileName
	 *            File containing the data from which the coordinates are going to
	 *            be extracted.
	 * @param latitude
	 *            Latitude coordinate of the provided point.
	 * @param longitude
	 *            Longitude coordinate of the provided point.
	 * @return boolean True if it is one of the points of the data, false otherwise.
	 */
	private boolean isExactPoint(String fileName, double latitude, double longitude) {
		List<Double> lats = getFileLatitudes(fileName);
		List<Double> longs = getFileLongitudes(fileName);

		if (lats.contains(latitude) && longs.contains(longitude))
			return true;
		return false;
	}

	/**
	 * Calculates if a given point is within the limits contained in the file.
	 * 
	 * @param fileName
	 *            File containing the data from which the coordinates are going to
	 *            be extracted.
	 * @param latitude
	 *            Latitude coordinate of the provided point.
	 * @param longitude
	 *            Longitude coordinate of the provided point.
	 * @return boolean True if it is within the limit, false otherwise.
	 */
	private boolean isWithinTheLimit(String fileName, double latitude, double longitude) {
		List<Double> lats = getFileLatitudes(fileName);
		List<Double> longs = getFileLongitudes(fileName);

		double lat_reference = Math.abs(lats.get(0) - lats.get(1));
		double lon_reference = Math.abs(longs.get(0) - longs.get(1));

		double max_lat = lats.get(0) + lat_reference;
		double min_lat = lats.get(lats.size() - 1) - lat_reference;
		double max_lon = longs.get(longs.size() - 1) + lon_reference;
		double min_lon = longs.get(0) - lon_reference;

		if (latitude <= max_lat && latitude >= min_lat)
			if (longitude <= max_lon && longitude >= min_lon)
				return true;

		return false;
	}

	@Override
	public List<SwhVariable> getSwhData(File file, double latitude, double longitude) {
		Array aux = this.getData(file.getAbsolutePath(), "swh", latitude, longitude);
		List<Double> values = this.transformToListDouble(aux);
		return createListSwhData(values, getFileDates(file.getAbsolutePath()));
	}

	@Override
	public List<MwpVariable> getMwpData(File file, double latitude, double longitude) {
		Array aux = this.getData(file.getAbsolutePath(), "mwp", latitude, longitude);
		List<Double> values = this.transformToListDouble(aux);
		return createListMwpData(values, getFileDates(file.getAbsolutePath()));
	}

	@Override
	public List<WsVariable> getWindData(File file, double latitude, double longitude) {
		Array aux = this.getData(file.getAbsolutePath(), "wind", latitude, longitude);
		List<Double> values = this.transformToListDouble(aux);
		return createListWindData(values, getFileDates(file.getAbsolutePath()));
	}
}
