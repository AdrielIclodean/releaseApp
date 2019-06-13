package com.ibm.release;

import java.util.Scanner;


public class Main {
	
	public static final int QUIRL_DATENPFLEGE = 1;
	public static final int QUIRL_IMPORT = 2;
	
	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Please choose project: (1 - Quirl Datenpflege; 2 - Quirl Import)");
		int projectName = scan.nextInt();
		
		switch (projectName) {
		case QUIRL_DATENPFLEGE:
			System.out.println("Please enter Quirl Datenpflege release name: ");
			Scanner datenpflegeScan = new Scanner(System.in);
			String releaseName = datenpflegeScan.nextLine();
			new QuirlDatenpflege(releaseName).createRelease();
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


}
