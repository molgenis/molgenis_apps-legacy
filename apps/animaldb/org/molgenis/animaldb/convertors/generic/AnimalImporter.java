package org.molgenis.animaldb.convertors.generic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

	private void populateLitter(String string) {
		// TODO Auto-generated method stub

	}

	private void populateAnimal(String string) {
		// TODO Auto-generated method stub

	}

	private void populateAnimalMetaInfo(String string) {
		// TODO Auto-generated method stub

	}
}
