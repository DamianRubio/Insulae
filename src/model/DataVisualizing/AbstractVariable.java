package model.DataVisualizing;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractVariable {

	String acronym;
	String name;
	double value;
	Date date;
	String units;

	@Override
	public String toString() {
		DecimalFormat decf = new DecimalFormat("#.###");
		decf.setRoundingMode(RoundingMode.CEILING);
		DateFormat datef = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		StringBuilder sb = new StringBuilder();
		sb.append(datef.format(this.date) + "\n");
		sb.append(this.acronym + ": " + decf.format(this.value) + " " + this.units);
		return sb.toString();
	}

	/**
	 * Gets the value of the variable.
	 * 
	 * @return Double value of the variable.
	 */
	public double getValue() {
		return this.value;
	}

}
