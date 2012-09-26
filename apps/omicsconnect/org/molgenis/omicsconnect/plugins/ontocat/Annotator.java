package org.molgenis.omicsconnect.plugins.ontocat;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class Annotator {
	public static final String annotatorUrl = "http://rest.bioontology.org/obs/annotator";

	public static void main(String[] args) {
		try {
			HttpClient client = new HttpClient();
			client.getParams().setParameter(HttpMethodParams.USER_AGENT,
					"Annotator Client Example - Annotator"); // Set this string
																// for your
																// application

			String text = "Melanoma";

			PostMethod method = new PostMethod(annotatorUrl);

			// Configure the form parameters
			method.addParameter("longestOnly", "true");
			method.addParameter("wholeWordOnly", "true");
			method.addParameter("filterNumber", "true");
			// method.addParameter("stopWords","I,a,above,after,against,all,alone,always,am,amount,an,and,any,are,around,as,at,back,be,before,behind,below,between,bill,both,bottom,by,call,can,co,con,de,detail,do,done,down,due,during,each,eg,eight,eleven,empty,ever,every,few,fill,find,fire,first,five,for,former,four,from,front,full,further,get,give,go,had,has,hasnt,he,her,hers,him,his,i,ie,if,in,into,is,it,last,less,ltd,many,may,me,mill,mine,more,most,mostly,must,my,name,next,nine,no,none,nor,not,nothing,now,of,off,often,on,once,one,only,or,other,others,out,over,part,per,put,re,same,see,serious,several,she,show,side,since,six,so,some,sometimes,still,take,ten,then,third,this,thick,thin,three,through,to,together,top,toward,towards,twelve,two,un,under,until,up,upon,us,very,via,was,we,well,when,while,who,whole,will,with,within,without,you,yourself,yourselves");
			method.addParameter("withDefaultStopWords", "true");
			method.addParameter("isTopWordsCaseSensitive", "false");
			method.addParameter("mintermSize", "3");
			method.addParameter("scored", "true");
			method.addParameter("withSynonyms", "true");
			method.addParameter("ontologiesToExpand", "");
			method.addParameter("ontologiesToKeepInResult", "");
			method.addParameter("isVirtualOntologyId", "true");
			method.addParameter("semanticTypes", "");
			method.addParameter("levelMax", "0");
			method.addParameter("mappingTypes", "Automatic"); // null,
																// Automatic,
																// Manual
			method.addParameter("textToAnnotate", text); // "Melanoma is a malignant tumor of melanocytes which are found predominantly in skin but also in the bowel and the eye");
			method.addParameter("format", "text"); // Options are 'text', 'xml',
													// 'tabDelimited'
			method.addParameter("apikey",
					"eaae1357-1b7d-4c9d-98f4-2bd61c0e49fc");

			// Execute the POST method
			int statusCode = client.executeMethod(method);

			if (statusCode != -1) {
				try {
					String contents = method.getResponseBodyAsString();
					method.releaseConnection();
					System.out.println(contents);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
