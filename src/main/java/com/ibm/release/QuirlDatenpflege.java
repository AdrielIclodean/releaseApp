package com.ibm.release;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import com.ibm.release.zip.ZipUtils;

public class QuirlDatenpflege {

	private static final String QUIRL_DATENPFLEGE = "quirl_datenpflege";
	private static final String EAR = ".ear";
	private static final String QUIRL_DATENPFLEGE_EAR = QUIRL_DATENPFLEGE + EAR;
	private static final String QUIRL_WAR = "quirl.war";
	private String releaseNumber;
	private File propertiesFileToEdit;

	public QuirlDatenpflege(String releaseNumber) throws Exception {
		this.releaseNumber = releaseNumber;

	}

	public void createRelease() throws Exception {
		ZipUtils zu = new ZipUtils();
		List<String> foldersToFillIn = createDatenpflegeFolderStructure();
		zu.createFolderStructure(releaseNumber, foldersToFillIn, QUIRL_DATENPFLEGE_EAR);

		final String q2dpFileName = "q2dp.properties";

		for (String path : foldersToFillIn) {
			String quirlDatenpflegeZipPath = path + "/" + QUIRL_DATENPFLEGE + ZipUtils.SUFFIX + EAR;

			InputStream warIs = zu.getInputStreamFactory(new FileInputStream(quirlDatenpflegeZipPath), QUIRL_WAR);
			
			final String q2dpWarPath = "WEB-INF/classes/" + q2dpFileName;
			InputStream q2dpInput = zu.getInputStreamFactory(warIs, q2dpWarPath);
			propertiesFileToEdit = new File(q2dpFileName);
			propertiesFileToEdit.deleteOnExit();

			changeRequiredProperties(zu, path, q2dpInput);

			File quirlWar = new File(path + "/" + QUIRL_WAR);

			warIs = zu.getInputStreamFactory(new FileInputStream(quirlDatenpflegeZipPath), QUIRL_WAR);
			ZipInputStream zis = new ZipInputStream(warIs);
			ZipEntry zipEntry = zis.getNextEntry();

			ZipOutputStream newWar = new ZipOutputStream(new FileOutputStream(quirlWar));
			while (zipEntry != null) {
				if (!(zipEntry.toString().endsWith(propertiesFileToEdit.getName()))) {
					zu.addToZipFile(zis, newWar, zipEntry);
				} else {
					zu.addToZipFile(propertiesFileToEdit, newWar, new ZipEntry(q2dpWarPath));
				}
				zipEntry = zis.getNextEntry();
			}

			zis.close();

			zis = new ZipInputStream(new FileInputStream(quirlDatenpflegeZipPath));
			zipEntry = zis.getNextEntry();

			ZipOutputStream newEar = new ZipOutputStream(
					new FileOutputStream(new File(path + "/" + QUIRL_DATENPFLEGE_EAR)));
			while (zipEntry != null) {
				if (!(zipEntry.toString().equals(QUIRL_WAR))) {
					zu.addToZipFile(zis, newEar, zipEntry);
				} else {
					zu.addToZipFile(quirlWar, newEar, new ZipEntry(QUIRL_WAR));
				}
				zipEntry = zis.getNextEntry();
			}

			newWar.close();
			zis.close();
			warIs.close();
			newEar.close();
			q2dpInput.close();
			warIs.close();

			deleteTempFiles(path);
		}
	}

	private void changeRequiredProperties(ZipUtils zu, String path, InputStream q2dpInput)
			throws IOException, Exception {
		Map<String, String> propertiesToAdd = getPropertiesToChange(path);
		FileUtils.copyInputStreamToFile(q2dpInput, propertiesFileToEdit);
		zu.editPropertiesFile(propertiesFileToEdit, propertiesToAdd);
	}

	private Map<String, String> getPropertiesToChange(String path) throws IOException, Exception {

		Map<String, String> properties = new HashMap<String, String>();

		if (path.endsWith("/QS/DMZ")) {
			properties.put("mail.smtp.host", "mailgate.qs2x.vwg");
			properties.put("quirl.portal.url", "https://inawasp52.wob.vw.vwg/quirl/q2dp");
		}

		if (path.endsWith("/QS/Intranet")) {
			properties.put("mail.smtp.host", "mailgate.vw.vwg");
			properties.put("quirl.portal.url", "https://inawasp52.wob.vw.vwg/quirl/q2dp");
		}

		if (path.endsWith("/Prod/DMZ")) {
			properties.put("mail.smtp.host", "mailgate.b2x.vwg");
			properties.put("quirl.portal.url", "https://inawl067t01.wob.vw.vwg:4432/quirl/q2dp");
		}

		if (path.endsWith("/Prod/Intranet")) {
			properties.put("mail.smtp.host", "mailgate.vw.vwg");
			properties.put("quirl.portal.url", "https://inawl067t01.wob.vw.vwg:4432/quirl/q2dp");
		}

		return properties;

	}

	private void deleteTempFiles(String folder) throws IOException {
		File files = new File(folder);
		if (files.isDirectory()) {
			for (File f : files.listFiles()) {
				if (!(f.getName().endsWith(QUIRL_DATENPFLEGE_EAR))) {
					f.delete();
				}
			}
		}
	}

	private List<String> createDatenpflegeFolderStructure() throws Exception {
		String currentDir = System.getProperty("user.dir");

		List<String> requiredFolders = new ArrayList<String>();

		System.out.println("Will create the release folder here: " + currentDir);
		requiredFolders.add(currentDir + "/Releases/" + releaseNumber + "/QS/DMZ");
		requiredFolders.add(currentDir + "/Releases/" + releaseNumber + "/QS/Intranet");
		requiredFolders.add(currentDir + "/Releases/" + releaseNumber + "/Prod/DMZ");
		requiredFolders.add(currentDir + "/Releases/" + releaseNumber + "/Prod/Intranet");

		return requiredFolders;
	}

	public String getReleaseNumber() {
		return releaseNumber;
	}

	public File getFile() {
		return propertiesFileToEdit;
	}

}
