package com.ibm.release.projects;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.ibm.release.ReleaseCreation;

public class QuirlImport extends ReleaseCreation {

	private static final String DMZ = "DMZ";
	private static final String GAZ = "GAZ";
	private static final String MALAYSIA = "Malaysia";
	private static final String INTRANET = "Intranet";

	private static final String QUIRL_IMPORT = "quirl_import";
	private static final String QUIRL_IMPORT_EAR = QUIRL_IMPORT + EAR;

	public QuirlImport(String releaseNumber) {
		super(releaseNumber);

	}

	protected List<Path> createImportFolderStructure() {

		List<Path> requiredFolders = new ArrayList<>();

		requiredFolders.add(releasePath.resolve(Paths.get(QS, DMZ, GAZ)));
		requiredFolders.add(releasePath.resolve(Paths.get(QS, DMZ, MALAYSIA)));
		requiredFolders.add(releasePath.resolve(Paths.get(QS, INTRANET)));
		requiredFolders.add(releasePath.resolve(Paths.get(PROD, DMZ, GAZ)));
		requiredFolders.add(releasePath.resolve(Paths.get(PROD, DMZ, MALAYSIA)));
		requiredFolders.add(releasePath.resolve(Paths.get(PROD, INTRANET)));

		return requiredFolders;
	}

	@Override
	public boolean createRelease() throws Exception {
		List<Path> foldersToFillIn = createImportFolderStructure();

		try {
			createFolderStructure(foldersToFillIn, QUIRL_IMPORT_EAR);
		} catch (NoSuchFileException e) {
			System.err.println("The file " + QUIRL_IMPORT_EAR + " was not found in the current directory");
			clearRelease();
			return false;
		}

		return true;

	}
}
