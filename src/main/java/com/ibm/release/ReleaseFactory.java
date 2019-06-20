package com.ibm.release;

import com.ibm.release.projects.QuirlDatenpflege;
import com.ibm.release.projects.QuirlImport;

public class ReleaseFactory {
		
	public Release createRelease(ProjectType project, String releaseName) {
	    if(project == null){
	         return null;
	      }		
	      if(project == ProjectType.QUIRL_DATENPFLEGE){
	         return new QuirlDatenpflege(releaseName);
	         
	      } else if(project == ProjectType.QUIRL_IMPORT){
	         return new QuirlImport(releaseName);
	      }
	      return null;
	}
}
