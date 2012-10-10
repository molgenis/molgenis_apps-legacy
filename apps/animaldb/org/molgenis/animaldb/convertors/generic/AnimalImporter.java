package org.molgenis.animaldb.convertors.generic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AnimalImporter {

	public void convertFromZip(String filename) throws Exception {
		// Path to store files from zip
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		String path = tmpDir.getAbsolutePath() + File.separatorChar;
		// Extract zip
		ZipFile zipFile = new ZipFile(filename);
		Enumeration<?> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			copyInputStream(
					zipFile.getInputStream(entry),
					new BufferedOutputStream(new FileOutputStream(path
							+ entry.getName())));
		}
		// Run convertor steps
		populateAnimalMetaInfo(path + "defaults");
		populateAnimal(path + "animals.csv");
		populateLitter(path + "litters.csv");
	}

	private void populateAnimalMetaInfo(String string) {
		// TODO Auto-generated method stub
		// set default settings for animals to be imported if not specified the
		// value should be given in a column in the animals.csv should be given.
		//
		// featureNameList.add("Active");
		// featureNameList.add("Species");
		// featureNameList.add("Sex");
		// featureNameList.add("AnimalType");
		// featureNameList.add("Source");
		// featureNameList.add("Background");
		// featureNameList.add("Line");
		// featureNameList.add("GeneModification");
		// featureNameList.add("GeneState");
		// featureNameList.add("DateOfBirth");
		// featureNameList.add("ResponsibleResearcher");
		// featureNameList.add("Location"); /*
	}

	private void populateLitter(String string) {

		// TODO Auto-generated method stub

	}

	private void populateAnimal(String string) {
		// TODO Auto-generated method stub

	}

	public static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

}
