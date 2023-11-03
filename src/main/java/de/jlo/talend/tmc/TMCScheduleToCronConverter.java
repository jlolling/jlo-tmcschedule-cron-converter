package de.jlo.talend.tmc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TMCScheduleToCronConverter {
	
	private ObjectNode oneTriggerNode = null;
	private final static ObjectMapper objectMapper = new ObjectMapper();
	private List<CronExp> cronExpressions = new ArrayList<CronExp>();
	private Date startDate = null;
	private TimeZone tz = null;
	
	public TMCScheduleToCronConverter(String triggerStr) throws Exception {
		if (triggerStr == null || triggerStr.trim().isEmpty()) {
			throw new IllegalArgumentException("triggerStr cannot be null or empty");
		}
		try {
			oneTriggerNode = (ObjectNode) objectMapper.readTree(triggerStr);
		} catch (Exception e) {
			throw new Exception("Error parsing trigger json. Error: " + e.getMessage() + "\njson: " + triggerStr, e);
		}
	}
	
	public TMCScheduleToCronConverter(ObjectNode oneTriggerNode) {
		if (oneTriggerNode == null) {
			throw new IllegalArgumentException("oneTriggerNode cannot be null");
		}
		this.oneTriggerNode = oneTriggerNode;
	}
	
	private String getMandatoryValueString(JsonNode node, String attribute) throws Exception {
		if (node == null || node.isNull()) {
			throw new Exception("node cannot be null");
		}
		JsonNode valueNode = node.get(attribute);
		if (valueNode == null || valueNode.isNull()) {
			throw new Exception("attribute: " + attribute + " is missing or null");
		}
		String value = valueNode.asText();
		if (value == null || value.trim().isEmpty()) {
			throw new Exception("attribute: " + attribute + " value is empty or null");
		}
		return value;
	}
	
	private int getMandatoryValueInt(JsonNode node, String attribute) throws Exception {
		try {
			String strValue = getMandatoryValueString(node, attribute);
			return Integer.parseInt(strValue);
		} catch (Exception e) {
			throw new Exception("Cannot get int value from attribute: " + attribute + ", error: " + e.getMessage(), e);
		}
	}
	
	private Date getMandatoryValueDate(JsonNode node, String attribute) throws Exception {
		try {
			String strValue = getMandatoryValueString(node, attribute);
			return new SimpleDateFormat("yyyy-MM-dd").parse(strValue);
		} catch (Exception e) {
			throw new Exception("Cannot get Date value from attribute: " + attribute + ", error: " + e.getMessage(), e);
		}
	}
	
	private int getMonth(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("date cannot be null");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}

	private int getDayOfMonth(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("date cannot be null");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	private int getDayOfWeek(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("date cannot be null");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	private int getYear(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("date cannot be null");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	public void convert() throws Exception {
		String type = getMandatoryValueString(oneTriggerNode, "type");
		ObjectNode atTimeNode = (ObjectNode) oneTriggerNode.get("atTimes");
		this.startDate = getMandatoryValueDate(oneTriggerNode, "startDate");
		if (atTimeNode == null || atTimeNode.isNull()) {
			throw new Exception("invalid trigger node: atTimes attributes is missing. Node: " + oneTriggerNode.toPrettyString());
		}
		if (type.equals("ONCE")) {
			CronExp exp = new CronExp();
			// we will have only one time and the startDate will be also set in the result cron expression
			setupDate(exp, startDate);
			JsonNode timeNode = atTimeNode.get("time");
			if (timeNode == null || timeNode.isNull()) {
				throw new Exception("invalid trigger node: time attributes is missing. Node: " + oneTriggerNode.toPrettyString());
			}
			setupTime(exp, String.valueOf(timeNode.asText()));
			cronExpressions.add(exp);
		} else if (type.equals("DAILY")) {
			
		} else if (type.equals("WEEKLY")) {
			
		} else if (type.equals("MONTHLY")) {
			
		} else if (type.equals("CRON")) {
			
		} else {
			throw new Exception("Unknown trigger type: " + type);
		}
	}
	
	private void setupTime(CronExp exp, String time) throws Exception {
		if (time == null || time.trim().isEmpty()) {
			throw new Exception("time value cannot be empty or null");
		}
		int pos = time.indexOf(':');
		if (pos == -1) {
			throw new Exception("time value: " + time + " does not contains a valid time");
		}
		String hours = time.substring(0, pos);
		String minutes = time.substring(pos + 1);
		exp.hours = hours;
		exp.minutes = minutes;
	}
	
	private void setupDate(CronExp exp, Date date) {
		exp.years = String.valueOf(getYear(date));
		exp.months = String.valueOf(getMonth(date));
		exp.daysOfMonth = String.valueOf(getDayOfMonth(date));
		exp.daysOfWeek = "?";
	}

	public List<String> getCronExpressions() {
		List<String> list = new ArrayList<String>();
		for (CronExp exp : cronExpressions) {
			list.add(exp.toString());
		}
		return list;
	}
	
}
