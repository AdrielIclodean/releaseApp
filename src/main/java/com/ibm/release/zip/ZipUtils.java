package com.ibm.release.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;

public class ZipUtils {

	public InputStream getInputStreamFactory(InputStream in, String entry) throws IOException {
		ZipInputStream zis = new ZipInputStream(in, StandardCharsets.UTF_8);
		ZipEntry zipEntry = zis.getNextEntry();

		while (zipEntry != null) {
			if (zipEntry.toString().equals(entry)) {
				// quirl.war
				return zis;
			}
			zipEntry = zis.getNextEntry();
		}
		throw new IllegalStateException("No entry '" + entry + "' found");
	}

	public void editPropertiesFile(File propertiesFile, Map<String, String> properties) throws Exception {
		PropertiesConfiguration config = new PropertiesConfiguration();
		PropertiesConfigurationLayout layout = new PropertiesConfigurationLayout();
		layout.load(config, new InputStreamReader(new FileInputStream(propertiesFile)));

		for (Map.Entry<String, String> entry : properties.entrySet()) {
			config.setProperty(entry.getKey(), entry.getValue());
		}
		layout.save(config, new FileWriter(propertiesFile.getPath()));

	}


	public void addToZipFile(File file, ZipOutputStream zos, ZipEntry zipEntry) throws IOException {
		try (FileInputStream fis = new FileInputStream(file)) {
			zos.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}
			zos.closeEntry();
		}
	}

	public void addToZipFile(ZipInputStream zis, ZipOutputStream zos, ZipEntry zipEntry) throws IOException {
		ZipEntry newZipEntry = new ZipEntry(zipEntry.getName());
		zos.putNextEntry(newZipEntry);
		byte[] bytes = new byte[1024];
		int len = 0;
		while ((len = zis.read(bytes)) != -1) {
			zos.write(bytes, 0, len);
		}
		zos.closeEntry();
	}

}
