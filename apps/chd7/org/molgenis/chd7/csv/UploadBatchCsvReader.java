/* File:        col7a1/model/UploadBatch.java
 * Copyright:   GBIC 2000-2010, all rights reserved
 * Date:        August 11, 2010
 * 
 * generator:   org.molgenis.generators.csv.CsvReaderGen 3.3.2-testing
 *
 * 
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package org.molgenis.chd7.csv;

import java.io.IOException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.apache.commons.lang.ObjectUtils;
//import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.CsvToDatabase;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Entity;
import org.molgenis.util.tuple.Tuple;

//import org.molgenis.mutation.Mutation;
//import org.molgenis.mutation.service.MutationService;
//import org.molgenis.mutation.service.UploadService;
//import org.molgenis.mutation.vo.MutationSummaryVO;
//import org.molgenis.mutation.vo.MutationUploadVO;
//import org.molgenis.mutation.vo.ObservedValueVO;
//import org.molgenis.mutation.vo.PatientSummaryVO;
//import org.molgenis.core.Publication;
//import org.molgenis.core.service.PublicationService;
//import org.molgenis.core.vo.PublicationVO;
//import col7a1.UploadBatch;
//import org.molgenis.services.PubmedService;
//import org.molgenis.services.pubmed.Author;
//import org.molgenis.services.pubmed.PubmedArticle;
//import org.molgenis.submission.Submission;
import org.springframework.stereotype.Component;

/**
 * Reads UploadBatch from a delimited (csv) file, resolving xrefs to ids where
 * needed, that is the tricky bit ;-)
 */
@Component
public class UploadBatchCsvReader extends CsvToDatabase<Entity>
{
	public static final transient Logger logger = Logger.getLogger(UploadBatchCsvReader.class);

	/**
	 * Imports UploadBatch from tab/comma delimited File
	 * 
	 * @param db
	 *            database to import into
	 * @param reader
	 *            csv reader to load data from
	 * @param defaults
	 *            to set default values for each row
	 * @param dbAction
	 *            indicating wether to add,update,remove etc
	 * @param missingValues
	 *            indicating what value in the csv is treated as 'null' (e.g. ""
	 *            or "NA")
	 * @return number of elements imported
	 */
	public int importCsv(final Database db, CsvReader reader, final Tuple defaults, final DatabaseAction dbAction,
			final String missingValues) throws DatabaseException, IOException, Exception
	{
		return 0;
	}
}
