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

public class QuirlDatenpflege {
	
	private String releaseNumber;
	private File propertiesFileToEdit;
	private ZipUtils zu;
	private List<String> earPaths;
	
	public QuirlDatenpflege(String releaseNumber) {
		super();
		this.releaseNumber = releaseNumber;
		zu = new ZipUtils();
		earPaths = new ArrayList<String>();
	}

	public String getReleaseNumber() {
		return releaseNumber;
	}
	
	public File getFile() {
		return propertiesFileToEdit;
	}

	public List<String> getEarPaths() {
		return earPaths;
	}

	public void createDatenpflegeFolderStructure() throws Exception{
		earPaths.add("C:/CatalinVladu/QuirlDatenpflege/Releases/" + releaseNumber + "/QS/DMZ");
		earPaths.add("C:/CatalinVladu/QuirlDatenpflege/Releases/" + releaseNumber + "/QS/Intranet");
		earPaths.add("C:/CatalinVladu/QuirlDatenpflege/Releases/" + releaseNumber + "/Prod/DMZ");
		earPaths.add("C:/CatalinVladu/QuirlDatenpflege/Releases/" + releaseNumber + "/Prod/Intranet");

		zu.createFolderStructure(releaseNumber, earPaths, "quirl_datenpflege.ear");
	}

	public void createNewArchives(String filename) throws Exception {
		for (String path: earPaths) {
			InputStream warIs = zu.getInputStreamFactory(new FileInputStream(path + "/quirl_datenpflege" + ZipUtils.SUFFIX + ".ear"), "quirl.war");
			InputStream q2dpInput = zu.getInputStreamFactory(warIs, "WEB-INF/classes/q2dp.properties");		
			propertiesFileToEdit = new File(filename);
			propertiesFileToEdit.deleteOnExit();	
			editPropertiesFile(path, q2dpInput);
			
			File quirlWar = new File (path + "/quirl.war");		
			
			warIs = zu.getInputStreamFactory(new FileInputStream(path + "/quirl_datenpflege" + ZipUtils.SUFFIX + ".ear"), "quirl.war");
			ZipInputStream zis = new ZipInputStream(warIs);
			ZipEntry zipEntry = zis.getNextEntry();
			
			ZipOutputStream newWar = new ZipOutputStream(new FileOutputStream(quirlWar));
			while (zipEntry != null) {
				if (!(zipEntry.toString().endsWith(propertiesFileToEdit.getName()))) {
					zu.addToZipFile(zis, newWar, zipEntry);
				}	else {				
					zu.addToZipFile(propertiesFileToEdit, newWar, new ZipEntry("WEB-INF/classes/q2dp.properties"));
				}
				zipEntry = zis.getNextEntry();
			}
			
			zis.close();
			
			zis = new ZipInputStream(new FileInputStream(path + "/quirl_datenpflege" + ZipUtils.SUFFIX + ".ear"));
			zipEntry = zis.getNextEntry();
			
			ZipOutputStream newEar = new ZipOutputStream(new FileOutputStream(new File (path + "/quirl_datenpflege.ear")));
			while (zipEntry != null) {
				if (!(zipEntry.toString().equals("quirl.war"))) {
					zu.addToZipFile(zis, newEar, zipEntry);
				} else {
					zu.addToZipFile(quirlWar, newEar, new ZipEntry("quirl.war"));
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

	private void editPropertiesFile(String path, InputStream q2dpInput) throws IOException, Exception {
		FileUtils.copyInputStreamToFile(q2dpInput, propertiesFileToEdit);	
		
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
		
		zu.editPropertiesFile(propertiesFileToEdit, properties);
	}
	
	private void deleteTempFiles(String folder) throws IOException {
		File files = new File(folder);
		if (files.isDirectory()) {
			for(File f: files.listFiles()) {
				if (!(f.getName().endsWith("quirl_datenpflege.ear"))) {
					f.delete();
				}
			}
		}
	}
}
