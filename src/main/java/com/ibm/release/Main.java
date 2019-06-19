package com.ibm.release;

import java.util.Scanner;

public class Main {

	public static final int QUIRL_DATENPFLEGE = 1;
	public static final int QUIRL_IMPORT = 2;

	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);

		System.out.println("Please choose project: (1 - Quirl Datenpflege; 2 - Quirl Import)");
		int projectName = scan.nextInt();

		String releaseName;
		while (projectName != -1) {
			switch (projectName) {
			case QUIRL_DATENPFLEGE:
				System.out.println("Please enter Quirl Datenpflege release name: ");
				Scanner datenpflegeScan = new Scanner(System.in);
				releaseName = datenpflegeScan.nextLine();
				new QuirlDatenpflege(releaseName).createRelease();
				projectName = -1;
				break;
			case QUIRL_IMPORT:
				System.out.println("Please enter Quirl Import release name: ");
				Scanner importScan = new Scanner(System.in);
				releaseName = importScan.nextLine();
				new QuirlImport(releaseName).createRelease();
				projectName = -1;
				break;
			case -1:
				break;
			default:
				System.out.println("Invalid project! Try again");
				projectName = scan.nextInt();
			}
		}

		System.out.println("Well done!");
		scan.close();

	}

}
