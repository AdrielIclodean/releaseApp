package com.ibm.release;

import java.util.Scanner;


public class Main {
	
	public static final int QUIRL_DATENPFLEGE = 1;
	public static final int QUIRL_IMPORT = 2;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Please choose project: (1 - Quirl Datenpflege; 2 - Quirl Import)");
		int projectName = scan.nextInt();
		
		switch (projectName) {
		case QUIRL_DATENPFLEGE:
			System.out.println("Please enter Quirl Datenpflege release: ");
			Scanner datenpflegeScan = new Scanner(System.in);
			String releaseNumber = datenpflegeScan.nextLine();
			createDatenpflegeFiles(releaseNumber);
			break;
		case QUIRL_IMPORT:
			System.out.println("Not implemented yet");
			break;
		default:
			System.out.println("Invalid project!");
		}

		scan.close();
		System.out.println("Well done!");
	}

	private static void createDatenpflegeFiles(String releaseNumber) throws Exception {
		QuirlDatenpflege quirlDatenpflege = new QuirlDatenpflege(releaseNumber);
		quirlDatenpflege.createDatenpflegeFolderStructure();
		quirlDatenpflege.createNewArchives("q2dp.properties");
	}


}
