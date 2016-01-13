package org.falcsim.agentmodel.utility.domain;

import java.util.Comparator;

public class RipisExportInfoComparator implements Comparator<RipisEntityExportInfo>{
	@Override
    public int compare(RipisEntityExportInfo object1, RipisEntityExportInfo object2) {
    	int cmpVal = 0;
    	if(object1.sector < object2.sector) cmpVal = -1;
    	if(object1.sector > object2.sector) cmpVal = 1;
    	if(cmpVal == 0){
    		int object1_type = object1.type > 100 ? object1.type- 100 : object1.type;
    		int object2_type = object1.type > 100 ? object1.type- 100 : object1.type;
        	if(object1_type < object2_type) cmpVal = -1;
        	if(object1_type > object2_type) cmpVal = 1;
        	if(cmpVal == 0){
            	if(object1.id < object2.id) cmpVal = -1;
            	if(object1.id > object2.id) cmpVal = 1;
        	}
    	}
        return cmpVal;
    }
}
