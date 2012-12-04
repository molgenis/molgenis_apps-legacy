package org.molgenis.omx.hl7;

import java.util.HashMap;
import java.util.Map;

import org.molgenis.omx.generated.ANY;
import org.molgenis.omx.generated.BL;
import org.molgenis.omx.generated.CD;
import org.molgenis.omx.generated.CO;
import org.molgenis.omx.generated.INT;
import org.molgenis.omx.generated.PQ;
import org.molgenis.omx.generated.REAL;
import org.molgenis.omx.generated.ST;
import org.molgenis.omx.generated.TS;

/**
 * Maps HL7v3 data types to OMX data types
 */
public class HL7DataTypeMapper
{
	private static final Map<Class<? extends ANY>, String> dataTypeMap;

	static
	{
		dataTypeMap = new HashMap<Class<? extends ANY>, String>();
		dataTypeMap.put(INT.class, "int");
		dataTypeMap.put(ST.class, "string");
		dataTypeMap.put(CO.class, "code"); // or categorical?
		dataTypeMap.put(CD.class, "code"); // or categorical?
		dataTypeMap.put(PQ.class, "decimal");
		dataTypeMap.put(TS.class, "datetime");
		dataTypeMap.put(REAL.class, "decimal");
		dataTypeMap.put(BL.class, "bool");
	}

	public static String get(Class<? extends ANY> clazz)
	{
		return dataTypeMap.get(clazz);
	}

	public static String get(ANY any)
	{
		return get(any.getClass());
	}
}
