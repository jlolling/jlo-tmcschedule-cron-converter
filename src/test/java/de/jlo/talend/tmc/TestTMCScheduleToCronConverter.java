package de.jlo.talend.tmc;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class TestTMCScheduleToCronConverter {

	@Test
	public void testOnce() throws Exception {
		String trigger = "	{\r\n"
		+ "		\"type\": \"ONCE\",\r\n"
		+ "		\"startDate\": \"2023-11-02\",\r\n"
		+ "		\"timeZone\": \"Europe/Berlin\",\r\n"
		+ "		\"atTimes\": {\r\n"
		+ "			\"type\": \"AT_TIME\",\r\n"
		+ "			\"time\": \"13:00\"\r\n"
		+ "		},\r\n"
		+ "		\"name\": \"daily_every_10min\"\r\n"
		+ "	}";
		String expected = "00 13 2 11 ? 2023";
		TMCScheduleToCronConverter c = new TMCScheduleToCronConverter(trigger);
		c.convert();
		List<String> resultList = c.getCronExpressions();
		assertEquals(1, resultList.size());
		assertEquals("wrong cron", expected, resultList.get(0));
	}

	@Test
	public void testDailySpecificTimes() throws Exception {
		String trigger = "	    {\r\n"
				+ "      \"type\": \"DAILY\",\r\n"
				+ "      \"interval\": 1,\r\n"
				+ "      \"startDate\": \"2023-11-02\",\r\n"
				+ "      \"timeZone\": \"Europe/Berlin\",\r\n"
				+ "      \"atTimes\": {\r\n"
				+ "        \"type\": \"AT_SPECIFIC_TIMES\",\r\n"
				+ "        \"times\": [\r\n"
				+ "          \"11:54\",\r\n"
				+ "          \"16:00\"\r\n"
				+ "        ]\r\n"
				+ "      }"
				+ "  }";
		String expected1 = "54 11 * * ? *";
		String expected2 = "00 16 * * ? *";
		TMCScheduleToCronConverter c = new TMCScheduleToCronConverter(trigger);
		c.convert();
		List<String> resultList = c.getCronExpressions();
		assertEquals(2, resultList.size());
		assertEquals("wrong cron1", expected1, resultList.get(0));
		assertEquals("wrong cron2", expected2, resultList.get(1));
	}

	@Test
	public void testDailyInterval() throws Exception {
		String trigger = "	{\r\n"
				+ "		\"type\": \"DAILY\",\r\n"
				+ "		\"interval\": 2,\r\n"
				+ "		\"startDate\": \"2023-07-18\",\r\n"
				+ "		\"timeZone\": \"Europe/Berlin\",\r\n"
				+ "		\"atTimes\": {\r\n"
				+ "			\"type\": \"AT_INTERVALS\",\r\n"
				+ "			\"startTime\": \"01:30\",\r\n"
				+ "			\"endTime\": \"20:50\",\r\n"
				+ "			\"interval\": 10\r\n"
				+ "		},\r\n"
				+ "		\"name\": \"daily_every_10min\"\r\n"
				+ "	}";
		String expected1 = "30-59/10 1 */2 * ? *";
		String expected2 = "*/10 2-19 */2 * ? *";
		String expected3 = "00-50/10 20 */2 * ? *";
		TMCScheduleToCronConverter c = new TMCScheduleToCronConverter(trigger);
		c.convert();
		List<String> resultList = c.getCronExpressions();
		assertEquals(3, resultList.size());
		assertEquals("wrong cron1", expected1, resultList.get(0));
		assertEquals("wrong cron2", expected2, resultList.get(1));
		assertEquals("wrong cron3", expected3, resultList.get(2));
	}

}
