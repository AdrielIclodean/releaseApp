package com.ibm.release;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
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

	private static final String DMZ = "DMZ";
	private static final String INTRANET = "Intranet";
	private static final String RELEASES = "DatenpflegeReleases";
	private static final String MAIL_SMTP_HOST_PROP = "mail.smtp.host";
	private static final String QUIRL_PORTAL_URL_PROP = "quirl.portal.url";
	private static final String QUIRL_DATENPFLEGE = "quirl_datenpflege";
	private static final String EAR = ".ear";
	private static final String QUIRL_DATENPFLEGE_EAR = QUIRL_DATENPFLEGE + EAR;
	private static final String QUIRL_WAR = "quirl.war";

	private static final String PATH = File.separator;

	private String releaseNumber;
	private File propertiesFileToEdit;

	public QuirlDatenpflege(String releaseNumber) {
		this.releaseNumber = releaseNumber;

	}

	public void createRelease() throws Exception {
		ZipUtils zu = new ZipUtils();
		List<String> foldersToFillIn = createDatenpflegeFolderStructure();
		try {
			zu.createFolderStructure(foldersToFillIn, QUIRL_DATENPFLEGE_EAR);
		} catch (NoSuchFileException e) {
			System.err.println("The file " + QUIRL_DATENPFLEGE_EAR + " was not found in the current directory");
			return;
		}

		final String q2dpFileName = "q2dp.properties";

		for (String path : foldersToFillIn) {
			String quirlDatenpflegeZipPath = path + PATH + QUIRL_DATENPFLEGE + ZipUtils.SUFFIX + EAR;

			InputStream warIs = zu.getInputStreamFactory(new FileInputStream(quirlDatenpflegeZipPath), QUIRL_WAR);

			final String q2dpWarPath = "WEB-INF/classes/" + q2dpFileName;
			propertiesFileToEdit = new File(q2dpFileName);
			propertiesFileToEdit.deleteOnExit();

			try (InputStream q2dpInput = zu.getInputStreamFactory(warIs, q2dpWarPath)) {
				changeRequiredProperties(zu, path, q2dpInput);
			} catch (IllegalStateException e) {
				System.err.println("Problem with " + QUIRL_WAR + " file: " + e.getMessage());
				return;
			}

			File quirlWar = new File(path + PATH + QUIRL_WAR);

			warIs = zu.getInputStreamFactory(new FileInputStream(quirlDatenpflegeZipPath), QUIRL_WAR);

			try (ZipInputStream zis = new ZipInputStream(warIs)) {

				ZipEntry zipEntry = zis.getNextEntry();

				try (ZipOutputStream newWar = new ZipOutputStream(new FileOutputStream(quirlWar))) {
					while (zipEntry != null) {
						if (!(zipEntry.toString().endsWith(propertiesFileToEdit.getName()))) {
							zu.addToZipFile(zis, newWar, zipEntry);
						} else {
							zu.addToZipFile(propertiesFileToEdit, newWar, new ZipEntry(q2dpWarPath));
						}
						zipEntry = zis.getNextEntry();
					}
				}

			}

			try (ZipInputStream zis = new ZipInputStream(new FileInputStream(quirlDatenpflegeZipPath))) {
				ZipEntry zipEntry = zis.getNextEntry();

				try (ZipOutputStream newEar = new ZipOutputStream(
						new FileOutputStream(Paths.get(path, QUIRL_DATENPFLEGE_EAR).toFile()))) {
					while (zipEntry != null) {
						if (!(zipEntry.toString().equals(QUIRL_WAR))) {
							zu.addToZipFile(zis, newEar, zipEntry);
						} else {
							zu.addToZipFile(quirlWar, newEar, new ZipEntry(QUIRL_WAR));
						}
						zipEntry = zis.getNextEntry();
					}
				}

			}

			warIs.close();
			warIs.close();

			deleteTempFiles(path);
		}

	}

	private void changeRequiredProperties(ZipUtils zu, String path, InputStream q2dpInput) throws Exception {
		Map<String, String> propertiesToAdd = getPropertiesToChange(path);
		FileUtils.copyInputStreamToFile(q2dpInput, propertiesFileToEdit);
		zu.editPropertiesFile(propertiesFileToEdit, propertiesToAdd);
	}

	private Map<String, String> getPropertiesToChange(String path) {

		Map<String, String> properties = new HashMap<>();

		if (path.endsWith(Paths.get("QS", DMZ).toString())) {
			properties.put(MAIL_SMTP_HOST_PROP, "mailgate.qs2x.vwg");
			properties.put(QUIRL_PORTAL_URL_PROP, "https://inawasp52.wob.vw.vwg/quirl/q2dp");
		}

		if (path.endsWith(Paths.get("QS",INTRANET).toString())) {
			properties.put(MAIL_SMTP_HOST_PROP, "mailgate.vw.vwg");
			properties.put(QUIRL_PORTAL_URL_PROP, "https://inawasp52.wob.vw.vwg/quirl/q2dp");
		}

		if (path.endsWith(Paths.get("Prod" ,DMZ).toString())) {
			properties.put(MAIL_SMTP_HOST_PROP, "mailgate.b2x.vwg");
			properties.put(QUIRL_PORTAL_URL_PROP, "https://inawl067t01.wob.vw.vwg:4432/quirl/q2dp");
		}

		if (path.endsWith(Paths.get("Prod",INTRANET).toString())) {
			properties.put(MAIL_SMTP_HOST_PROP, "mailgate.vw.vwg");
			properties.put(QUIRL_PORTAL_URL_PROP, "https://inawl067t01.wob.vw.vwg:4432/quirl/q2dp");
		}

		return properties;

	}

	private void deleteTempFiles(String folder) throws IOException {
		File files = new File(folder);
		if (files.isDirectory()) {
			for (File f : files.listFiles()) {
				if (!(f.getName().endsWith(QUIRL_DATENPFLEGE_EAR))) {
					Files.delete(f.toPath());
				}
			}
		}
	}

	private List<String> createDatenpflegeFolderStructure() {
		String currentDir = System.getProperty("user.dir");

		List<String> requiredFolders = new ArrayList<>();

		System.out.println("Will create the release folder here: " + currentDir);

		requiredFolders.add(Paths.get(currentDir, RELEASES, releaseNumber, "QS", DMZ).toString());
		requiredFolders.add(Paths.get(currentDir, RELEASES, releaseNumber, "QS", INTRANET).toString());
		requiredFolders.add(Paths.get(currentDir, RELEASES, releaseNumber, "Prod", DMZ).toString());
		requiredFolders.add(Paths.get(currentDir, RELEASES, releaseNumber, "Prod", INTRANET).toString());

		return requiredFolders;
	}

	public String getReleaseNumber() {
		return releaseNumber;
	}

	public File getFile() {
		return propertiesFileToEdit;
	}

}
