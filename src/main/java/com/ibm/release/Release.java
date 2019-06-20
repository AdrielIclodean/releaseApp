package com.ibm.release;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public abstract class Release {

	protected String releaseNumber;

	protected static final String EAR = ".ear";
	protected static final String QUIRL_DATENPFLEGE = "quirl_datenpflege";
	protected static final String QUIRL_DATENPFLEGE_EAR = QUIRL_DATENPFLEGE + EAR;
	protected static final String QUIRL_WAR = "quirl.war";

	public static final String SUFFIX = "_temp";

	protected static final String RELEASES = "Releases";
	protected static final String QS = "QS";
	protected static final String PROD = "Prod";

	protected static final String PATH = File.separator;

	protected Path releasePath;

	protected final String buildNumber;
	protected final String buildVersion;

	/**
	 * 
	 * @param releaseNumber - Should be like dd-ddd buildVersion-buildNumber
	 */
	public Release(String releaseNumber) {
		this.releaseNumber = releaseNumber;

		String currentDir = System.getProperty("user.dir");
		System.out.println("Will create the release folder here: " + currentDir);

		this.buildVersion = releaseNumber.substring(0, releaseNumber.indexOf("-"));
		this.buildNumber = releaseNumber.substring(releaseNumber.indexOf("-") + 1);
		this.releasePath = Paths.get(currentDir, RELEASES, releaseNumber);
	}

	/**
	 * Create release for different projects
	 * 
	 * @return true if everything went fine
	 * @throws Exception
	 */
	public abstract boolean execute() throws Exception;

	public void deleteTempFiles(Path folder) throws IOException {
		File files = folder.toFile();
		if (files.isDirectory()) {
			for (File f : files.listFiles()) {
				if (!(f.getName().endsWith(QUIRL_DATENPFLEGE_EAR))) {
					Files.delete(f.toPath());
				}
			}
		} else {
			files.delete();
		}
	}

	/**
	 * Will remove the created release folder
	 */
	public void clearRelease() {
		try {
			Files.walk(releasePath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createFolderStructure(List<Path> earPaths, String filename) throws IOException {
		for (Path path : earPaths) {
			Files.createDirectories(path);
			Files.copy(Paths.get(filename), Paths.get(path + "/" + getSuffixName(filename)), REPLACE_EXISTING);
		}
	}

	private String getSuffixName(String filename) {
		if (filename.endsWith("ear")) {
			return filename.substring(0, filename.length() - 4) + SUFFIX + ".ear";
		}
		return filename;
	}

}
