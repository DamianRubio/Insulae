package model.DataVisualizing;

import java.util.Date;

public class WsVariable extends AbstractVariable {

	private final double knotsFactor = 1.9438444924574;

	/**
	 * Constructor for WS variables.
	 * 
	 * @param date
	 *            Time stamp of the variable.
	 * @param value
	 *            Value of the variable.
	 */
	public WsVariable(Date date, double value) {
		this.acronym = "WS";
		this.name = "Wind speed";
		this.date = date;
		this.value = convertToKnots(value);
		this.units = "knots";
	}

	/**
	 * Converts a quantity to knots.
	 * 
	 * @param value
	 *            Value to be converted.
	 * @return The value transformed into knots.
	 */
	private double convertToKnots(double value) {
		return value * knotsFactor;
	}

}
