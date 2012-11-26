package org.molgenis.mutation.dto;

import java.io.Serializable;

public class CafeVariomeDTO implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = -1446453529670281789L;

	/* Fields, see: http://www.cafevariome.org/docs/cafevariome_tab_delimited.txt */

	/* required fields */
	private String name;
	private String refnum;
	private String symbol;
	private String phenotype;
	private String policy;
	
	/* optional fields */
	private String positionGdna;
	private String variantIdentifier;
	private String patientIdentifier;
	private String gender;
	private String ethnicity;
	private String patientType;
	private String zygosity;
	private String germline;
	private String origin;
	private String patientPathogenicity;
	private String populationPathogenicity;
	private String pathogenicityType;
	private String detection;
	private String reference;
	private String molgenisUrl;

	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getRefnum()
	{
		return refnum;
	}
	public void setRefnum(String refnum)
	{
		this.refnum = refnum;
	}
	public String getSymbol()
	{
		return symbol;
	}
	public void setSymbol(String symbol)
	{
		this.symbol = symbol;
	}
	public String getPhenotype()
	{
		return phenotype;
	}
	public void setPhenotype(String phenotype)
	{
		this.phenotype = phenotype;
	}
	public String getPolicy()
	{
		return policy;
	}
	public void setPolicy(String policy)
	{
		this.policy = policy;
	}
	public String getPositionGdna()
	{
		return positionGdna;
	}
	public void setPositionGdna(String positionGdna)
	{
		this.positionGdna = positionGdna;
	}
	public String getVariantIdentifier()
	{
		return variantIdentifier;
	}
	public void setVariantIdentifier(String variantIdentifier)
	{
		this.variantIdentifier = variantIdentifier;
	}
	public String getPatientIdentifier()
	{
		return patientIdentifier;
	}
	public void setPatientIdentifier(String patientIdentifier)
	{
		this.patientIdentifier = patientIdentifier;
	}
	public String getGender()
	{
		return gender;
	}
	public void setGender(String gender)
	{
		this.gender = gender;
	}
	public String getEthnicity()
	{
		return ethnicity;
	}
	public void setEthnicity(String ethnicity)
	{
		this.ethnicity = ethnicity;
	}
	public String getPatientType()
	{
		return patientType;
	}
	public void setPatientType(String patientType)
	{
		this.patientType = patientType;
	}
	public String getZygosity()
	{
		return zygosity;
	}
	public void setZygosity(String zygosity)
	{
		this.zygosity = zygosity;
	}
	public String getGermline()
	{
		return germline;
	}
	public void setGermline(String germline)
	{
		this.germline = germline;
	}
	public String getOrigin()
	{
		return origin;
	}
	public void setOrigin(String origin)
	{
		this.origin = origin;
	}
	public String getPatientPathogenicity()
	{
		return patientPathogenicity;
	}
	public void setPatientPathogenicity(String patientPathogenicity)
	{
		this.patientPathogenicity = patientPathogenicity;
	}
	public String getPopulationPathogenicity()
	{
		return populationPathogenicity;
	}
	public void setPopulationPathogenicity(String populationPathogenicity)
	{
		this.populationPathogenicity = populationPathogenicity;
	}
	public String getPathogenicityType()
	{
		return pathogenicityType;
	}
	public void setPathogenicityType(String pathogenicityType)
	{
		this.pathogenicityType = pathogenicityType;
	}
	public String getDetection()
	{
		return detection;
	}
	public void setDetection(String detection)
	{
		this.detection = detection;
	}
	public String getReference()
	{
		return reference;
	}
	public void setReference(String reference)
	{
		this.reference = reference;
	}
	public String getMolgenisUrl()
	{
		return molgenisUrl;
	}
	public void setMolgenisUrl(String molgenisUrl)
	{
		this.molgenisUrl = molgenisUrl;
	}
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append(this.name);
		buf.append('\t' + this.refnum);
		buf.append('\t' + this.symbol);
		buf.append('\t' + this.phenotype);
		buf.append('\t' + this.policy);
		buf.append('\t' + this.positionGdna);
		buf.append('\t' + this.variantIdentifier);
		buf.append('\t' + this.patientIdentifier);
		buf.append('\t' + this.gender);
		buf.append('\t' + this.ethnicity);
		buf.append('\t' + this.zygosity);
		buf.append('\t' + this.germline);
		buf.append('\t' + this.patientPathogenicity);
		buf.append('\t' + this.detection);
		buf.append('\t' + this.reference);
		buf.append('\t' + this.molgenisUrl);

		return buf.toString();
	}
}
