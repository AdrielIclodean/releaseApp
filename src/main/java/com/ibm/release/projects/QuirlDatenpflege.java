package com.ibm.release.projects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import com.ibm.release.ReleaseCreation;
import com.ibm.release.zip.ZipUtils;

public class QuirlDatenpflege extends ReleaseCreation {

	private static final String DMZ = "DMZ";
	private static final String INTRANET = "Intranet";
	private static final String MAIL_SMTP_HOST_PROP = "mail.smtp.host";
	private static final String QUIRL_PORTAL_URL_PROP = "quirl.portal.url";

	private File propertiesFileToEdit;

	public QuirlDatenpflege(String releaseNumber) {
		super(releaseNumber);

	}

	public boolean createRelease() throws Exception {
		ZipUtils zu = new ZipUtils();
		List<Path> foldersToFillIn = createDatenpflegeFolderStructure();
		try {
			createFolderStructure(foldersToFillIn, QUIRL_DATENPFLEGE_EAR);
		} catch (NoSuchFileException e) {
			System.err.println("The file " + QUIRL_DATENPFLEGE_EAR + " was not found in the current directory");
			clearRelease();
			return false;
		}

		final String q2dpFileName = "q2dp.properties";

		for (Path path : foldersToFillIn) {
			
			System.out.println("Processing release folder " + path.toString());
			
			String quirlDatenpflegeZipPath = path + PATH + QUIRL_DATENPFLEGE + SUFFIX + EAR;

			InputStream warIs = zu.getInputStreamFactory(new FileInputStream(quirlDatenpflegeZipPath), QUIRL_WAR);
			
			final String q2dpWarPath = "WEB-INF/classes/" + q2dpFileName;
			propertiesFileToEdit = new File(q2dpFileName);
			propertiesFileToEdit.deleteOnExit();

			try (InputStream q2dpInput = zu.getInputStreamFactory(warIs, q2dpWarPath)) {
				changeRequiredProperties(zu, path, q2dpInput);
			} catch (IllegalStateException e) {
				System.err.println("Problem with " + QUIRL_WAR + " file: " + e.getMessage());
				clearRelease();
				return false;
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
					
					writeBuildPropertiesToWar(newWar, zu);
				} catch (Exception e) {
					System.err.println("Something happend " + e.getMessage());
				}

			}
			warIs.close();
			
			try (ZipInputStream zis = new ZipInputStream(new FileInputStream(quirlDatenpflegeZipPath))) {
				ZipEntry zipEntry = zis.getNextEntry();

				try (ZipOutputStream newEar = new ZipOutputStream(
						new FileOutputStream(path.resolve(QUIRL_DATENPFLEGE_EAR).toFile()))) {
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

			

			deleteTempFiles(path);
		}
		return true;

	}

	private void writeBuildPropertiesToWar(ZipOutputStream zos, ZipUtils zipUtils) throws Exception {
		File q2dpBuildProperties = new File("q2dp-build.properties");
		q2dpBuildProperties.createNewFile();

		Map<String, String> properties = new LinkedHashMap<>();
		properties.put("build.number", buildNumber);
		DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("dd.MM.YYYY hh:mm:ss");
		properties.put("build.date", LocalDateTime.of(LocalDate.now(), LocalTime.now()).format(formatter));
		properties.put("build.version", buildVersion);
		
		zipUtils.addPropertiesToFile(q2dpBuildProperties, properties);
		
		FileInputStream fis = new FileInputStream(q2dpBuildProperties);
		ZipEntry zipEntry = new ZipEntry("WEB-INF\\classes\\q2dp-build.properties");
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}
		fis.close();
		zos.closeEntry();
		
		Files.delete(q2dpBuildProperties.toPath());
	}

	private void changeRequiredProperties(ZipUtils zu, Path path, InputStream q2dpInput) throws Exception {
		Map<String, String> propertiesToAdd = getPropertiesToChange(path);
		FileUtils.copyInputStreamToFile(q2dpInput, propertiesFileToEdit);
		zu.editPropertiesFile(propertiesFileToEdit, propertiesToAdd);
	}

	private Map<String, String> getPropertiesToChange(Path path) {

		Map<String, String> properties = new HashMap<>();

		if (path.endsWith(Paths.get(QS, DMZ).toString())) {
			properties.put(MAIL_SMTP_HOST_PROP, "mailgate.qs2x.vwg");
			properties.put(QUIRL_PORTAL_URL_PROP, "https://inawasp52.wob.vw.vwg/quirl/q2dp");
		}

		if (path.endsWith(Paths.get(QS, INTRANET).toString())) {
			properties.put(MAIL_SMTP_HOST_PROP, "mailgate.vw.vwg");
			properties.put(QUIRL_PORTAL_URL_PROP, "https://inawasp52.wob.vw.vwg/quirl/q2dp");
		}

		if (path.endsWith(Paths.get(PROD, DMZ).toString())) {
			properties.put(MAIL_SMTP_HOST_PROP, "mailgate.b2x.vwg");
			properties.put(QUIRL_PORTAL_URL_PROP, "https://inawl067t01.wob.vw.vwg:4432/quirl/q2dp");
		}

		if (path.endsWith(Paths.get(PROD, INTRANET).toString())) {
			properties.put(MAIL_SMTP_HOST_PROP, "mailgate.vw.vwg");
			properties.put(QUIRL_PORTAL_URL_PROP, "https://inawl067t01.wob.vw.vwg:4432/quirl/q2dp");
		}

		return properties;

	}

	private List<Path> createDatenpflegeFolderStructure() {
		List<Path> requiredFolders = new ArrayList<>();

		requiredFolders.add(releasePath.resolve(Paths.get(QS, DMZ)));
		requiredFolders.add(releasePath.resolve(Paths.get(QS, INTRANET)));
		requiredFolders.add(releasePath.resolve(Paths.get(PROD, DMZ)));
		requiredFolders.add(releasePath.resolve(Paths.get(PROD, INTRANET)));

		return requiredFolders;
	}

	public String getReleaseNumber() {
		return releaseNumber;
	}

	public File getFile() {
		return propertiesFileToEdit;
	}

}
