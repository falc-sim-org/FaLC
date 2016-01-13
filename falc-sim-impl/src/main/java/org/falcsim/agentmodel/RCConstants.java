package org.falcsim.agentmodel;

/** 
* Constant values used throughout the package
*
* @author regioConcept AG
* @version 1.0
* @since   0.5
* 
*/

public class RCConstants {
	
	public static final String HISTORY_OPTION = "'HIST'";
	public static final String APP_CONFIG_PATH="classpath:/META-INF/spring-config/app-config.xml";
	public static final String LOG4J_PROP_FILE="log4j.properties";
	public static final String LOCID_STR = "locid";
	public static final String SEP="-";
	public static final String PG_RAND= " 1=1 ORDER BY RANDOM() ";
	public static final Integer NO_ID=-4;
	public static final Integer SAVE=1;
	public static final Integer UPDATE=0;
	public static final Integer MALE=1;
	public static final Integer FEMALE=0;
	public static final Double INFTY=10000000000000000000000000000000000000000000.0;
	public static final Integer STATUS_IMMIGRANT = 7;
	public static final String F_DB_SEP = "','";
	public static final String F_DB_M = "'";
	public static final String F_DB_F = ",";
	public static final String HISTORY_SUFFIX = "_h";
	
	public static final String  WGS_84_CRS = "EPSG:4326" ;
	public static final String  CH1903_CRS ="EPSG:21781";
	/** 
	* Resident type by age: WorkAge, Elder
	*/	
	public enum AgeRange {
		WORKAGE, OLDER
	}
	public  static final int KID = 3;
	//public static final int F_P = 1; //female Partner
	//public static final int M_P = 2; //male Partner
	public static final int H_Partner = 5; //just parent without partner
	
	public static final String  SCHEMA_PUBLIC ="assumptions";
	public static final String  SCHEMA_SYTNTHESE ="assumptions";
	
	public static final int LANDTYPE_LARGECITY = 91;
	public static final int LANDTYPE_AGGLOMERATION = 92;
	public static final int LANDTYPE_COUNTYSIDE = 93;
	public static final int LANDTYPE_UNKNOWN = -1;
	
	public static final int CEO = 1;
	public static final int EMPLOYEE = 2;
	
	
	public static final int HH_TYPE_UNKNOWN = 0;
	public static final int HH_TYPE_SINGLE = 1;
	public static final int HH_TYPE_PAIR = 2;
	public static final int HH_TYPE_WITH_CHILDREN = 3;	
	public static final int HH_TYPE_ELDER = 4;
}


