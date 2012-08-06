package org.molgenis.omicsconnect.plugins.eutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/*
 * @author Rob Hastings date July 2012 
 * Fetches data using the NCBI eutils service
 */

public class Efetch {

	public static String getHttpUrl(String urlStr) {
		// Data obtained from service, to be returned
		String retVal = null;
		// Get data using HTTP GET
		try {
			URL url = new URL(urlStr);
			BufferedReader inBuf = new BufferedReader(new InputStreamReader(
					url.openStream()));
			StringBuffer strBuf = new StringBuffer();
			while (inBuf.ready()) {
				strBuf.append(inBuf.readLine()
						+ System.getProperty("line.separator"));
			}
			retVal = strBuf.toString();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		// Return the response data
		return retVal;
	}

	public static String constructURL(String baseURL, String db, String format,
			String id) {

		String returnUrl = baseURL + "db=" + db + "&" + "id=" + id + "&"
				+ "rettype=" + format;

		return returnUrl;

	}

}
