package model.DataVisualizing;

import java.util.Date;

public class SwhVariable extends AbstractVariable{
	
	/**
	 * Constructor for SWH variables.
	 * 
	 * @param date
	 *            Time stamp of the variable.
	 * @param value
	 *            Value of the variable.
	 */
	public SwhVariable(Date date, double value) {
		this.acronym = "SWH";
		this.name = "Significant wave height";
		this.date = date;
		this.value = value;
		this.units = "m";
	}
}
