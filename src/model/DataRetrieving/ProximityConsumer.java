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
import ucar.ma2.InvalidRangeException;
import ucar.ma2.Range;
import ucar.nc2.NetcdfFile;
import ucar.nc2.dataset.NetcdfDataset;
import utilities.MeteoUtil;
import ucar.nc2.Variable;

public class ProximityConsumer extends AbstractConsumer {

	@Override
	public Array getData(String fileName, String variableName, double userLatitude, double userLongitude) {
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

	@Override
	public Map<String, Double> isSignificantDataAvailable(String absolutePath, double latPos, double lonPos) {
		return this.calculateNearestPoint(absolutePath, latPos, lonPos);
	}

	/**
	 * Calculates the nearest coordinates for which it is available data, to the one
	 * given by its parameters.
	 * 
	 * @param fileName
	 *            File containing the data from which the coordinates are going to
	 *            be extracted.
	 * @param latitude
	 *            Latitude coordinate of the provided point.
	 * @param longitude
	 *            Longitude coordinate of the provided point.
	 * @return Map containing the latitude and longitude of the nearest point to the
	 *         one provided by its parameters. Also contains the calculated
	 *         distance.
	 */
	private Map<String, Double> calculateNearestPoint(String fileName, double latitude, double longitude) {
		List<Double> lats = getFileLatitudes(fileName);
		List<Double> longs = getFileLongitudes(fileName);
		Double min_distance = Double.MAX_VALUE;
		Map<String, Double> solution = new HashMap<String, Double>();

		// Como es un cuadrado puedo asumir esto.
		double reference = Math.abs(lats.get(0) - lats.get(1));
		reference = (Math.sqrt(Math.pow(reference, 2) + Math.pow(reference, 2))) / 2;

		for (int i = 0; i < lats.size(); i++)
			for (int j = 0; j < longs.size(); j++) {
				if (latitude == lats.get(i) && longitude == longs.get(j)) {
					solution.put("lat", lats.get(i));
					solution.put("lon", longs.get(j));
					solution.put("distance", 0.0);
					return solution;
				}
				if (min_distance > MeteoUtil.getInstance().calculateDistance(latitude, longitude, lats.get(i),
						longs.get(j))) {
					min_distance = MeteoUtil.getInstance().calculateDistance(latitude, longitude, lats.get(i),
							longs.get(j));
					solution.put("lat", lats.get(i));
					solution.put("lon", longs.get(j));
					solution.put("distance", min_distance);
				}
			}
		if (MeteoUtil.getInstance().calculateEuclideanDistance(latitude, longitude, solution.get("lat"),
				solution.get("lon")) > reference)
			return null;
		else
			return solution;
	}

	@Override
	public List<MwpVariable> getMwpData(File file, double latitude, double longitude) {
		NetcdfFile ncfile = null;
		Array aux = null;
		List<Double> mwpValues = null;
		int pos_lat = getFileLatitudes(file.getAbsolutePath()).indexOf(latitude);
		int pos_long = getFileLongitudes(file.getAbsolutePath()).indexOf(longitude);

		try {
			ncfile = NetcdfDataset.openDataset(file.getAbsolutePath());
			Variable v = ncfile.findVariable("mwp");

			List<Range> ranges = new ArrayList<Range>();
			ranges.add(null);
			ranges.add(new Range(pos_lat, pos_lat));
			ranges.add(new Range(pos_long, pos_long));
			aux = v.read(ranges).reduce();
		} catch (IOException | InvalidRangeException e) {
			e.printStackTrace();
		}
		mwpValues = transformToListDouble(aux);
		return createListMwpData(mwpValues, getFileDates(file.getAbsolutePath()));
	}

	@Override
	public List<WsVariable> getWindData(File file, double latitude, double longitude) {
		{
			NetcdfFile ncfile = null;
			Array aux = null;
			List<Double> windValues = null;
			int pos_lat = getFileLatitudes(file.getAbsolutePath()).indexOf(latitude);
			int pos_long = getFileLongitudes(file.getAbsolutePath()).indexOf(longitude);

			try {
				ncfile = NetcdfDataset.openDataset(file.getAbsolutePath());
				Variable v = ncfile.findVariable("wind");

				List<Range> ranges = new ArrayList<Range>();
				ranges.add(null);
				ranges.add(new Range(pos_lat, pos_lat));
				ranges.add(new Range(pos_long, pos_long));
				aux = v.read(ranges).reduce();
			} catch (IOException | InvalidRangeException e) {
				e.printStackTrace();
			}
			windValues = transformToListDouble(aux);
			return createListWindData(windValues, getFileDates(file.getAbsolutePath()));
		}
	}

	@Override
	public List<SwhVariable> getSwhData(File file, double latitude, double longitude)  {		
		NetcdfFile ncfile = null;
		Array aux = null;
		List<Double> swhValues = null;
		int pos_lat = getFileLatitudes(file.getAbsolutePath()).indexOf(latitude);
		int pos_long = getFileLongitudes(file.getAbsolutePath()).indexOf(longitude);

		try {
			ncfile = NetcdfDataset.openDataset(file.getAbsolutePath());
			Variable v = ncfile.findVariable("swh");

			List<Range> ranges = new ArrayList<Range>();
			ranges.add(null);
			ranges.add(new Range(pos_lat, pos_lat));
			ranges.add(new Range(pos_long, pos_long));
			aux = v.read(ranges).reduce();
		} catch (IOException | InvalidRangeException e) {
			e.printStackTrace();
		}
		swhValues = transformToListDouble(aux);
		return createListSwhData(swhValues, getFileDates(file.getAbsolutePath()));
	}

}
