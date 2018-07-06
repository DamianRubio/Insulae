package model.DataRetrieving;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import model.QueryPoint;

public class FileReader {

	private AbstractConsumer dataConsumer;
	private File file;

	/**
	 * File reader constructor.
	 * 
	 * @param consumerMode
	 *            Mode of consumption for the data.
	 * @param file
	 *            File from which the data is going to be retrieved.
	 */
	public FileReader(int consumerMode, File file) {
		setConsumerMode(consumerMode);
		this.file = file;
	}

	/**
	 * Sets the consumer mode with the one provided.
	 * 
	 * @param consumerMode
	 *            Int representing the consume mode.
	 */
	public void setConsumerMode(int consumerMode) {
		if (consumerMode == 0)
			this.dataConsumer = new ProximityConsumer();
		else if (consumerMode == 1)
			this.dataConsumer = new PrudentConsumer();
	}

	/**
	 * Gets the list of dates of the info on a file.
	 *
	 * @return List containing the dates of the data contained in the file.
	 */
	public List<Date> getFileDates() {
		if (this.file != null)
			return this.dataConsumer.getFileDates(this.file.getAbsolutePath());
		else
			return null;
	}

	/**
	 * Gets the start date of the info on a file.
	 * 
	 * @return Date on which the data of the file starts.
	 */
	public Date getFileStartDate() {
		return this.dataConsumer.getFileStartDate(this.file);
	}

	/**
	 * Gets the latitudes contained on the data of a file.
	 * 
	 * @return List of doubles containing the latitudes stored on the file.
	 */
	public List<Double> getLatData() {
		if (this.file != null)
			return this.dataConsumer.getLatData(this.file.getAbsolutePath());
		else
			return null;
	}

	/**
	 * Gets the longitudes contained on the data of a file.
	 * 
	 * @return List of doubles containing the longitudes stored on the file.
	 */
	public List<Double> getLonData() {
		if (this.file != null)
			return this.dataConsumer.getLonData(this.file.getAbsolutePath());
		else
			return null;
	}

	/**
	 * Checks if there is significant data about a point on a file.
	 * 
	 * @param qp
	 *            Query made by the user for which it is wanted to obtain data.
	 * 
	 * @return Map containing the closest point for which the file contains data,
	 *         null if there is not such a point.
	 */
	public Map<String, Double> isSignificantDataAvailable(QueryPoint qp) {
		return this.dataConsumer.isSignificantDataAvailable(this.file.getAbsolutePath(), qp.getLatitude(),
				qp.getRegularLongitude());
	}

	/**
	 * Gets the significant wave height data of a point.
	 * 
	 * @param qp
	 *            Query made by the user for which it is wanted to obtain data.
	 */
	public void getSwhData(QueryPoint qp) {
		Map<String, Double> closestPoint = isSignificantDataAvailable(qp);
		if (closestPoint != null)
			qp.setSwhData(this.dataConsumer.getSwhData(this.file, closestPoint.get("lat"), closestPoint.get("lon")));
	}

	/**
	 * Gets the mean wave period data of a point.
	 * 
	 * @param qp
	 *            Query made by the user for which it is wanted to obtain data.
	 */
	public void getMwpData(QueryPoint qp) {
		Map<String, Double> closestPoint = isSignificantDataAvailable(qp);
		if (closestPoint != null)
			qp.setMwpData(this.dataConsumer.getMwpData(this.file, closestPoint.get("lat"), closestPoint.get("lon")));
	}

	/**
	 * Gets the significant wind speed data of a point.
	 * 
	 * @param qp
	 *            Query made by the user for which it is wanted to obtain data.
	 */
	public void getWindData(QueryPoint qp) {
		Map<String, Double> closestPoint = isSignificantDataAvailable(qp);
		if (closestPoint != null)
			qp.setWindData(this.dataConsumer.getWindData(this.file, closestPoint.get("lat"), closestPoint.get("lon")));
	}

}
