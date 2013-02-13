import org.molgenis.MolgenisOptions;
import org.molgenis.model.JDBCModelExtractor;
import org.molgenis.model.jaxb.Model;

public class jcbcImport
{
	public static void main(String[] args) throws Exception
	{
		MolgenisOptions options = new MolgenisOptions();
		options.db_driver = "com.mysql.jdbc.Driver";
		options.db_user = "root";
		options.db_password = "root";
		options.db_uri = "jdbc:mysql://localhost/bbmri?innodb_autoinc_lock_mode=2";

		JDBCModelExtractor ex = new JDBCModelExtractor();

		// extract model from jdbc
		Model model = JDBCModelExtractor.extractModel(options);

		// write model to xml
		String xml = JDBCModelExtractor.toString(model);

		// write to out
		System.out.print(xml);
	}
}