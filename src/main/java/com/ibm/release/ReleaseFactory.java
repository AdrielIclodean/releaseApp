package com.ibm.release;

import com.ibm.release.projects.QuirlDatenpflege;
import com.ibm.release.projects.QuirlImport;
import com.ibm.swing.MainApplicationWindow;

public class ReleaseFactory {
		
	public Release createRelease(String project) {
	    if(project == null){
	         return null;
	      }		
	      if(project.equalsIgnoreCase(MainApplicationWindow.Q_DATENPFLEGE)){
	         return new QuirlDatenpflege(project);
	         
	      } else if(project.equalsIgnoreCase(MainApplicationWindow.Q_IMPORT)){
	         return new QuirlImport(project);
	      }
	      return null;
	}
}
