package com.ibm.release;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

import com.ibm.release.zip.ZipUtils;

public class QuirlImport {
	
	private String releaseNumber;
	
	private static final String PATH = File.separator;
	private static final String DMZ = "DMZ";
	private static final String GAZ = "GAZ";
	private static final String MALAYSIA = "Malaysia";
	private static final String INTRANET = "Intranet";
	private static final String RELEASES = "ImportReleases";
	
	private static final String QUIRL_IMPORT = "quirl_import";
	private static final String EAR = ".ear";
	private static final String QUIRL_IMPORT_EAR = QUIRL_IMPORT + EAR;
	
	public QuirlImport(String releaseNumber) {
		this.releaseNumber = releaseNumber;

	}
	
	protected List<String> createImportFolderStructure() {
		String currentDir = System.getProperty("user.dir");

		List<String> requiredFolders = new ArrayList<>();

		System.out.println("Will create the release folder here: " + currentDir);
		
		requiredFolders.add(currentDir + PATH + RELEASES + PATH + releaseNumber + PATH + "QS" + PATH + DMZ + PATH + GAZ);
		requiredFolders.add(currentDir + PATH + RELEASES + PATH + releaseNumber + PATH + "QS" + PATH + DMZ + PATH + MALAYSIA);
		requiredFolders.add(currentDir + PATH + RELEASES + PATH + releaseNumber + PATH + "QS" + PATH + INTRANET);
		requiredFolders.add(currentDir + PATH + RELEASES + PATH + releaseNumber + PATH + "Prod" + PATH + DMZ + PATH + GAZ);
		requiredFolders.add(currentDir + PATH + RELEASES + PATH + releaseNumber + PATH + "Prod" + PATH + DMZ + PATH + MALAYSIA);
		requiredFolders.add(currentDir + PATH + RELEASES + PATH + releaseNumber + PATH + "Prod" + PATH + INTRANET);

		return requiredFolders;
	}
	
	public void createRelease() throws Exception {
		ZipUtils zu = new ZipUtils();
		List<String> foldersToFillIn = createImportFolderStructure();
		
		try {
			zu.createFolderStructure(foldersToFillIn, QUIRL_IMPORT_EAR);
		} catch (NoSuchFileException e) {
			System.err.println("The file " + QUIRL_IMPORT_EAR + " was not found in the current directory");
			return;
		}
		
	}
}
