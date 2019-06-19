package com.ibm.release;

import java.util.Scanner;

import com.ibm.release.projects.QuirlDatenpflege;
import com.ibm.release.projects.QuirlImport;

public class Main {

	public static final int QUIRL_DATENPFLEGE = 1;
	public static final int QUIRL_IMPORT = 2;

	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);

		System.out.println("Please choose project: (1 - Quirl Datenpflege; 2 - Quirl Import)");
		int projectName = scan.nextInt();

		String releaseName;
		boolean withSuccess = false;
		while (projectName != -1) {
			switch (projectName) {
			case QUIRL_DATENPFLEGE:
				System.out.println("Please enter Quirl Datenpflege release name: ");
				Scanner datenpflegeScan = new Scanner(System.in);
				releaseName = datenpflegeScan.nextLine();
				withSuccess = new QuirlDatenpflege(releaseName).createRelease();
				projectName = -1;
				break;
			case QUIRL_IMPORT:
				System.out.println("Please enter Quirl Import release name: ");
				Scanner importScan = new Scanner(System.in);
				releaseName = importScan.nextLine();
				withSuccess = new QuirlImport(releaseName).createRelease();
				projectName = -1;
				break;
			case -1:
				break;
			default:
				System.out.println("Invalid project! Try again");
				projectName = scan.nextInt();
			}
		}

		if (withSuccess)
			System.out.println("Well done!");
		else {
			System.out.println("Something went a little ... mnaah. Please check and fix");
		}
		scan.close();

	}

}
