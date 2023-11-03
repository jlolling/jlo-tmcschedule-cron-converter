package de.jlo.talend.tmc;

public class CronExp {
	
	String minutes = "*";
	String hours = "*";
	String daysOfMonth = "*";
	String daysOfWeek = "*";
	String months = "*";
	String years = "*";
	
	public String toString() {
		return minutes + " " + hours + " " + daysOfMonth + " " + months + " " + daysOfWeek + " " + years;
	}
	
}
