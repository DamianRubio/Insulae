package model;

import java.util.List;

import model.DataVisualizing.MwpVariable;
import model.DataVisualizing.SwhVariable;
import model.DataVisualizing.WsVariable;
import utilities.MeteoUtil;

public class QueryPoint {

	private double latitude;
	private double longitude;
	private List<SwhVariable> swhData;
	private List<MwpVariable> mwpData;
	private List<WsVariable> windData;

	public QueryPoint(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Gets the latitude of the quey point.
	 * 
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Sets the latitude of the query point.
	 * 
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Gets the longitude of the query point.
	 * 
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Gets the longitude of the query point converted into a East coordinate.
	 * 
	 * @return Longitude of the query point converted into a East coordinate.
	 */
	public double getRegularLongitude() {
		return MeteoUtil.getInstance().toRegularCoordinate(getLongitude());
	}

	/**
	 * Sets the longitude of the query point.
	 * 
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Gets the SWH data of the query point.
	 * 
	 * @return SWH data of the query point.
	 */
	public List<SwhVariable> getSwhData() {
		return swhData;
	}

	/**
	 * Sets the SWH data of the query point.
	 * 
	 * @param swhData
	 *            Data of the query point.
	 */
	public void setSwhData(List<SwhVariable> swhData) {
		this.swhData = swhData;
	}

	/**
	 * Gets the MWP data of the query point.
	 * 
	 * @return MWP data of the query point.
	 */
	public List<MwpVariable> getMwpData() {
		return mwpData;
	}

	/**
	 * Sets the MWP data of the query point.
	 * 
	 * @param mwpData
	 *            data of the query point.
	 */
	public void setMwpData(List<MwpVariable> mwpData) {
		this.mwpData = mwpData;
	}

	/**
	 * Gets the WS data of the query point.
	 * 
	 * @return WS data of the query point.
	 */
	public List<WsVariable> getWindData() {
		return windData;
	}

	/**
	 * Sets the WS data of the query point.
	 * 
	 * @param windData
	 *            data of the query point.
	 */
	public void setWindData(List<WsVariable> windData) {
		this.windData = windData;
	}

	@Override
	public String toString() {
		return "(" + latitude + ", " + longitude + ")";
	}
}
