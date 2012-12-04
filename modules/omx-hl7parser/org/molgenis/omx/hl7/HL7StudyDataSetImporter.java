package org.molgenis.omx.hl7;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.observ.DataSet;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.ObservationSet;
import org.molgenis.observ.ObservationTarget;
import org.molgenis.observ.ObservedValue;
import org.molgenis.observ.Protocol;
import org.molgenis.observ.target.OntologyTerm;
import org.molgenis.omx.generated.REPCMT000100UV01Component3;
import org.molgenis.omx.generated.REPCMT000100UV01Observation;
import org.molgenis.omx.generated.REPCMT000100UV01Organizer;
import org.molgenis.omx.generated.REPCMT000100UV01RecordTarget;
import org.molgenis.omx.generated.REPCMT000400UV01ActCategory;
import org.molgenis.omx.generated.REPCMT000400UV01Component4;

import app.DatabaseFactory;

public class HL7StudyDataSetImporter
{
	private Database db;

	public HL7StudyDataSetImporter(Database db)
	{
		if (db == null) throw new IllegalArgumentException();
		this.db = db;
	}

	/**
	 * Import a HL7 study dataset describing a OMX dataset into an empty
	 * database
	 * 
	 * @param xmlStream
	 * @throws DatabaseException
	 */
	public void importData(InputStream xmlStream) throws DatabaseException
	{
		REPCMT000400UV01ActCategory actCategory = JAXB.unmarshal(xmlStream, REPCMT000400UV01ActCategory.class);

		Map<String, OntologyTerm> ontologyTerms = new HashMap<String, OntologyTerm>();
		Map<String, ObservableFeature> features = new LinkedHashMap<String, ObservableFeature>();
		List<ObservationTarget> targets = new ArrayList<ObservationTarget>();
		List<ObservedValue> values = new ArrayList<ObservedValue>();
		List<ObservationSet> observationSets = new ArrayList<ObservationSet>();

		DataSet dataSet = new DataSet();
		dataSet.setIdentifier(DataSet.class.getSimpleName());
		dataSet.setName("Dataset");

		Protocol protocol = null;

		for (REPCMT000400UV01Component4 rootComponent : actCategory.getComponent())
		{
			REPCMT000100UV01Organizer organizer = rootComponent.getOrganizer().getValue();

			if (protocol == null) protocol = HL7OrganizerConvertor.toProtocol(organizer);

			// create target
			REPCMT000100UV01RecordTarget recordTarget = organizer.getRecordTarget().getValue();
			ObservationTarget target = HL7RecordTargetConvertor.toObservationTarget(recordTarget);
			targets.add(target);

			// create observation set
			ObservationSet observationSet = new ObservationSet();
			observationSet.setTarget(target);
			observationSet.setPartOfDataSet(dataSet);
			observationSets.add(observationSet);

			// create features and values
			for (REPCMT000100UV01Component3 organizerComponent : organizer.getComponent())
			{
				JAXBElement<REPCMT000100UV01Observation> jaxbObservation = organizerComponent.getObservation();
				if (jaxbObservation == null) continue;

				REPCMT000100UV01Observation observation = jaxbObservation.getValue();

				// create feature
				String featureId = HL7ObservationConvertor.toObservableFeatureIdentifier(observation);
				ObservableFeature feature = features.get(featureId);
				if (feature == null)
				{
					feature = HL7ObservationConvertor.toObservableFeature(observation);
					features.put(featureId, feature);

					// create ontology term
					String ontologyTermId = HL7ObservationConvertor.toOntologyTermIdentifier(observation);
					OntologyTerm ontologyTerm = ontologyTerms.get(ontologyTermId);
					if (ontologyTerm == null)
					{
						ontologyTerm = HL7ObservationConvertor.toOntologyTerm(observation);
						ontologyTerms.put(ontologyTermId, ontologyTerm);
					}

					if (ontologyTerm != null) feature.setUnit(ontologyTerm);
				}

				// create value
				ObservedValue value = HL7ObservationConvertor.toObservedValue(observation, feature, observationSet);
				values.add(value);
			}
		}

		List<ObservableFeature> featureList = new ArrayList<ObservableFeature>(features.values());
		List<OntologyTerm> ontologyTermsList = new ArrayList<OntologyTerm>(ontologyTerms.values());

		// update protocol
		if (protocol != null)
		{
			protocol.setFeatures(featureList);
			dataSet.setProtocolUsed(protocol);
		}

		// update db
		try
		{
			db.beginTx();

			db.add(targets);
			db.add(ontologyTermsList);
			db.add(featureList);
			if (protocol != null) db.add(protocol);
			db.add(dataSet);
			db.add(observationSets);
			db.add(values);

			db.commitTx();
		}
		catch (DatabaseException e)
		{
			db.rollbackTx();
			throw e;
		}
	}

	public static void main(String[] args) throws DatabaseException, IOException
	{
		if (args.length != 1)
		{
			System.err.println("Usage: java " + HL7StudyDataSetImporter.class.getSimpleName() + " xmlfile");
			return;
		}

		InputStream xmlStream = new FileInputStream(args[0]);
		try
		{
			HL7StudyDataSetImporter importer = new HL7StudyDataSetImporter(DatabaseFactory.create());
			importer.importData(xmlStream);
		}
		finally
		{
			xmlStream.close();
		}
	}
}
