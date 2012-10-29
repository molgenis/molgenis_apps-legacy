package matrix.test.implementations.memory;

import java.util.ArrayList;
import java.util.List;

import matrix.implementations.memory.MemoryDataMatrixInstance;
import matrix.test.implementations.general.Helper;
import matrix.test.implementations.general.Params;
import matrix.test.implementations.general.TestingMethods;

import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.testng.Assert;

public class TestMemoryMatrix
{
	List<String> uniqueNames = new ArrayList<String>();
	Logger logger = Logger.getLogger(getClass().getSimpleName());

	/**
	 * WARNING: running this test will empty the contents of your selected
	 * database and possibly overwrite matrix backend data! Consider carefully
	 * before running!
	 * 
	 * @param matrixDimension1
	 * @param matrixDimension2
	 * @param maxTextLength
	 * @param fixedTextLength
	 * @param sparse
	 * @throws Exception
	 */
	public TestMemoryMatrix(Database db, Params params) throws Exception
	{
		/**
		 * Assumption: the list of the traits/subjects that are created of size
		 * N match the corresponding size N of totalCols/totalRows of the
		 * randomized matrices and are therefore used 1:1 as row/colnames
		 */

		String storage = "Memory";

		logger.info("Creating database instance and erasing all existing data..");
		Helper h = new Helper(db);
		h.printSettings(storage, params);

		// set storage of elements to 'Database' because there is no 'Memory'
		// we never write the matrices to this storage though :)
		h.prepareDatabaseAndFiles("Database", params);
		List<MemoryDataMatrixInstance<Object>> mmList = new ArrayList<MemoryDataMatrixInstance<Object>>();

		for (Data data : h.getDataList())
		{
			mmList.add(h.createAndWriteRandomMemoryMatrix(h.getInputFilesDir(), data, db, params.matrixDimension2,
					params.matrixDimension1, params.maxTextLength, params.sparse, params.fixedTextLength));
		}

		logger.info("Regression tests..");
		String[] methods = new String[]
		{ "elementbyindex", "elementbyname", "rowbyindex", "rowbyname", "colbyindex", "colbyname",
				"submatrixbyindexlist", "submatrixbynamelist", "submatrixbyindexoffset", "submatrixbynameoffset" };

		if (params.skipPerElement)
		{
			methods = new String[]
			{ "rowbyindex", "rowbyname", "colbyindex", "colbyname", "submatrixbyindexlist", "submatrixbynamelist",
					"submatrixbyindexoffset", "submatrixbynameoffset" };

		}
		for (MemoryDataMatrixInstance<Object> mm : mmList)
		{
			for (String method : methods)
			{
				Assert.assertTrue(TestingMethods.parseToPlainAndCompare(logger, mm, mm.getData(), h.getInputFilesDir(),
						method, true, true));
			}
		}
	}
}
