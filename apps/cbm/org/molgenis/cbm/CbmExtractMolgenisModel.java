package org.molgenis.cbm;

import org.molgenis.MolgenisOptions;
import org.molgenis.model.JDBCModelExtractor;
import org.molgenis.model.jaxb.Model;

public class CbmExtractMolgenisModel
{
	public static void main(String[] args) throws Exception
	{
		MolgenisOptions options = new MolgenisOptions();
		options.db_driver = "com.mysql.jdbc.Driver";
		options.db_user = "molgenis";
		options.db_password = "molgenis";
		options.db_uri = "jdbc:mysql://localhost/cbm?innodb_autoinc_lock_mode=2";

		JDBCModelExtractor ex = new JDBCModelExtractor();

		// extract model from jdbc
		Model model = ex.extractModel(options);

		// write model to xml
		String xml = ex.toString(model);

		// write to out
		System.out.print(xml);
	}
}
