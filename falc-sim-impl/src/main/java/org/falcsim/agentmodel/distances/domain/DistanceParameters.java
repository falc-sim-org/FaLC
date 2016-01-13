package org.falcsim.agentmodel.distances.domain;

/**
 * Parameters for the distances module.
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
public class DistanceParameters {

	/**
	 * file with definition of populating years and source files
	 */
	private String sourceFile;

	/**
	 * max speed of bicycle
	 */
	private int bicycleMaxSpeed;
	/**
	 * filter of network links for bicycle
	 */
	private String bicycleLinkFilter;
	/**
	 * replace database NaN with this value, this will be more realistic for calculations
	 */
	private double defaultDistance;

	/**
	 * default time value
	 */
	private double defaultTime;
	
	/**
	 * save evaluated routes to CSV file
	 */
	private boolean saveRoute = false;

	/**
	 * save evaluated routes to CSV file
	 */
	private String saveRouteDelimiter = ";";	

	/**
	 * save evaluated routes to SHP file
	 */
	private boolean saveRouteAsShp = false;
	private boolean saveUsedNetwork = false;
	private boolean saveSpiderWebAsShp = false;

	private String distanceUnit = "meters";
	private String timeUnit = "minutes";
	private int saveSpiderWebDecimalPlaces = 2;
	private int saveSpiderWebMaxValue = 1000000;
	
	/**
	 * path to save route information
	 */
	private String saveRoutePath;
	/**
	 * limit saving only for this locations
	 */
	private String saveRouteFromIdLoc;
	

	private String carDiiIntervals;
	private String carTiiIntervals;
	private String publicDiiIntervals;
	private String publicTiiIntervals;
	private String bicycleDiiIntervals;
	private String bicycleTiiIntervals;
	
	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * get value
	 * 
	 * @return value
	 */
	public int getBicycleMaxSpeed() {
		return bicycleMaxSpeed;
	}

	/**
	 * set value
	 * 
	 * @param bicycleMaxSpeed
	 */
	public void setBicycleMaxSpeed(int bicycleMaxSpeed) {
		this.bicycleMaxSpeed = bicycleMaxSpeed;
	}

	/**
	 * get value
	 * 
	 * @return value
	 */
	public String getBicycleLinkFilter() {
		return bicycleLinkFilter;
	}

	/**
	 * set value
	 * 
	 * @param bicycleLinkFilter
	 */
	public void setBicycleLinkFilter(String bicycleLinkFilter) {
		this.bicycleLinkFilter = bicycleLinkFilter;
	}

	/**
	 * get value
	 * 
	 * @return value
	 */
	public boolean isSaveRoute() {
		return saveRoute;
	}

	/**
	 * set value
	 * 
	 * @param saveRoute
	 */
	public void setSaveRoute(boolean saveRoute) {
		this.saveRoute = saveRoute;
	}
	
	/**
	 * get value
	 * 
	 * @return value
	 */
	public String getSaveRoutePath() {
		return saveRoutePath;
	}

	/**
	 * set value
	 * 
	 * @param saveRoutePath
	 */
	public void setSaveRoutePath(String saveRoutePath) {
		this.saveRoutePath = saveRoutePath;
	}

	/**
	 * get value
	 * 
	 * @return value
	 */
	public String getSaveRouteFromIdLoc() {
		return saveRouteFromIdLoc;
	}

	/**
	 * set value
	 * 
	 * @param saveRouteFromIdLoc
	 */
	public void setSaveRouteFromIdLoc(String saveRouteFromIdLoc) {
		this.saveRouteFromIdLoc = saveRouteFromIdLoc;
	}

	public double getDefaultDistance() {
		return defaultDistance;
	}

	public void setDefaultDistance(double defaultDistance) {
		this.defaultDistance = defaultDistance;
	}
	
	public double getDefaultTime() {
		return defaultTime;
	}

	public void setDefaultTime(double defaultTime) {
		this.defaultTime = defaultTime;
	}
	
	public String getCarDiiIntervals() {
		return carDiiIntervals;
	}
	
	public void setCarDiiIntervals(String carDiiIntervals) {
		this.carDiiIntervals = carDiiIntervals;
	}

	public String getCarTiiIntervals() {
		return carTiiIntervals;
	}

	public void setCarTiiIntervals(String carTiiIntervals) {
		this.carTiiIntervals = carTiiIntervals;
	}	
	
	public String getPublicDiiIntervals() {
		return publicDiiIntervals;
	}

	public void setPublicDiiIntervals(String publicDiiIntervals) {
		this.publicDiiIntervals = publicDiiIntervals;
	}

	public String getPublicTiiIntervals() {
		return publicTiiIntervals;
	}

	public void setPublicTiiIntervals(String publicTiiIntervals) {
		this.publicTiiIntervals = publicTiiIntervals;
	}

	public String getBicycleDiiIntervals() {
		return bicycleDiiIntervals;
	}

	public void setBicycleDiiIntervals(String bicycleDiiIntervals) {
		this.bicycleDiiIntervals = bicycleDiiIntervals;
	}

	public String getBicycleTiiIntervals() {
		return bicycleTiiIntervals;
	}

	public void setBicycleTiiIntervals(String bicycleTiiIntervals) {
		this.bicycleTiiIntervals = bicycleTiiIntervals;
	}
	
	public boolean isSaveRouteAsShp() {
		return saveRouteAsShp;
	}

	public void setSaveRouteAsShp(boolean saveRouteAsShp) {
		this.saveRouteAsShp = saveRouteAsShp;
	}

	public boolean getSaveSpiderWebAsShp() {
		return saveSpiderWebAsShp;
	}

	public void setSaveSpiderWebAsShp(boolean saveSpiderWebAsShp) {
		this.saveSpiderWebAsShp = saveSpiderWebAsShp;
	}	
	
	
	public String getSaveRouteDelimiter() {
		return saveRouteDelimiter;
	}

	public void setSaveRouteDelimiter(String saveRouteDelimiter) {
		this.saveRouteDelimiter = saveRouteDelimiter;
	}
	
	public boolean getSaveSpiderWebUseKilometers() {
		return "kilometer".equals(distanceUnit.toLowerCase());
	}

	public boolean getSaveSpiderWebUseHours() {
		return "hour".equals(timeUnit.toLowerCase());
	}

	public int getSaveSpiderWebDecimalPlaces() {
		return saveSpiderWebDecimalPlaces;
	}

	public void setSaveSpiderWebDecimalPlaces(int saveSpiderWebDecimalPlaces) {
		this.saveSpiderWebDecimalPlaces = saveSpiderWebDecimalPlaces;
	}	
	
	public int getSaveSpiderWebMaxValue() {
		return saveSpiderWebMaxValue;
	}

	public void setSaveSpiderWebMaxValue(int saveSpiderWebMaxValue) {
		this.saveSpiderWebMaxValue = saveSpiderWebMaxValue;
	}	
	
	public String getDistanceUnit() {
		return distanceUnit;
	}

	public void setDistanceUnit(String distanceUnit) {
		this.distanceUnit = distanceUnit;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}
	
	public boolean isSaveUsedNetwork() {
		return saveUsedNetwork;
	}

	public void setSaveUsedNetwork(boolean saveUsedNetwork) {
		this.saveUsedNetwork = saveUsedNetwork;
	}	
}
