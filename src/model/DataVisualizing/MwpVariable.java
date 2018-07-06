package model.DataVisualizing;

import java.util.Date;

public class MwpVariable extends AbstractVariable {

	/**
	 * Constructor for MWP variables.
	 * 
	 * @param date
	 *            Time stamp of the variable.
	 * @param value
	 *            Value of the variable.
	 */
	public MwpVariable(Date date, double value) {
		this.acronym = "MWP";
		this.name = "Mean wave period";
		this.date = date;
		this.value = value;
		this.units = "s";
	}

}
