package org.falcsim.agentmodel.service.methods.util.domain;

import java.util.ArrayList;
import java.util.List;

public class RunIndicatorsParameters {
	public static int run_indicators_export_level_FULL = 4;
	public static int run_indicators_export_level_EXTENDED = 3;
	public static int run_indicators_export_level_CALIBRATION = 2;
	public static int run_indicators_export_level_MINIMAL = 1;

	private Boolean run_indicators_active;
	private Integer run_indicators_export_level;
	private String run_indicators_folder;
	private String run_indicators_description;
	private String run_indicators_extra_description;

	private Boolean run_indicators_export_ripis_business;
	private Boolean run_indicators_export_ripis_households;
	private String run_indicators_export_ripis_locations;
		
	private Boolean run_indicators_print_header_rows;
	
	//New Export Files settings
	private Boolean run_indicators_Overview_active;
	private String run_indicators_Overview_runs_filter;
	private String run_indicators_Overview_years_filter;
	private Boolean run_indicators_Overview_locations;
	private String run_indicators_Overview_location_filter;
	private Boolean run_indicators_Overview_municipalities;
	private String run_indicators_Overview_municipalities_filter;
	private Boolean run_indicators_Overview_cantons;
	private String run_indicators_Overview_cantons_filter;
	private Boolean run_indicators_Overview_all_year_single_file;

	private Boolean run_indicators_GIS_active;
	private String run_indicators_GIS_runs_filter;
	private String run_indicators_GIS_years_filter;
	private Boolean run_indicators_GIS_locations;
	private String run_indicators_GIS_location_filter;
	private Boolean run_indicators_GIS_municipalities;
	private String run_indicators_GIS_municipalities_filter;
	private Boolean run_indicators_GIS_cantons;
	private String run_indicators_GIS_cantons_filter;
	private Boolean run_indicators_GIS_all_year_single_file;

	private Boolean run_indicators_commuters_active;
	private String run_indicators_commuters_runs_filter;
	private String run_indicators_commuters_years_filter;
	private Boolean run_indicators_commuters_locations;
	private String run_indicators_commuters_location_filter;
	private Boolean run_indicators_commuters_municipalities;
	private String run_indicators_commuters_municipalities_filter;
	private Boolean run_indicators_commuters_cantons;
	private String run_indicators_commuters_cantons_filter;
	private Boolean run_indicators_commuters_all_year_single_file;
	
	private Boolean run_indicators_Households_active;
	private String run_indicators_Households_runs_filter;
	private String run_indicators_Households_years_filter;
	private Boolean run_indicators_Households_locations;
	private String run_indicators_Households_location_filter;
	private Boolean run_indicators_Households_municipalities;
	private String run_indicators_Households_municipalities_filter;
	private Boolean run_indicators_Households_cantons;
	private String run_indicators_Households_cantons_filter;
	private Boolean run_indicators_Households_all_year_single_file;

	private Boolean run_indicators_Businesses_active;
	private String run_indicators_Businesses_runs_filter;
	private String run_indicators_Businesses_years_filter;
	private Boolean run_indicators_Businesses_locations;
	private String run_indicators_Businesses_location_filter;
	private Boolean run_indicators_Businesses_municipalities;
	private String run_indicators_Businesses_municipalities_filter;
	private Boolean run_indicators_Businesses_cantons;
	private String run_indicators_Businesses_cantons_filter;
	private Boolean run_indicators_Businesses_all_year_single_file;
	

	private Boolean run_indicators_BusinessesTypes_active;
	private String run_indicators_BusinessesTypes_runs_filter;
	private String run_indicators_BusinessesTypes_years_filter;
	private Boolean run_indicators_BusinessesTypes_locations;
	private String run_indicators_BusinessesTypes_location_filter;
	private Boolean run_indicators_BusinessesTypes_municipalities;
	private String run_indicators_BusinessesTypes_municipalities_filter;
	private Boolean run_indicators_BusinessesTypes_cantons;
	private String run_indicators_BusinessesTypes_cantons_filter;
	private Boolean run_indicators_BusinessesTypes_all_year_single_file;
	
	private Boolean run_indicators_Persons_active;
	private String run_indicators_Persons_runs_filter;
	private String run_indicators_Persons_years_filter;
	private Boolean run_indicators_Persons_locations;
	private String run_indicators_Persons_location_filter;
	private Boolean run_indicators_Persons_municipalities;
	private String run_indicators_Persons_municipalities_filter;
	private Boolean run_indicators_Persons_cantons;
	private String run_indicators_Persons_cantons_filter;
	private Boolean run_indicators_Persons_all_year_single_file;	
	
	private Boolean run_indicators_Relocation_active;
	private Boolean run_indicators_Relocation_cumulated;	
	private String run_indicators_Relocation_runs_filter;
	private String run_indicators_Relocation_years_filter;
	private Boolean run_indicators_Relocation_locations;
	private String run_indicators_Relocation_location_filter;
	private Boolean run_indicators_Relocation_municipalities;
	private String run_indicators_Relocation_municipalities_filter;
	private Boolean run_indicators_Relocation_cantons;
	private String run_indicators_Relocation_cantons_filter;
	private Boolean run_indicators_Relocation_all_year_single_file;
	
	private Boolean run_indicators_LocationsZones_active;
	private String run_indicators_LocationsZones_runs_filter;
	private String run_indicators_LocationsZones_years_filter;
	private Boolean run_indicators_LocationsZones_locations;
	private String run_indicators_LocationsZones_location_filter;
	private Boolean run_indicators_LocationsZones_all_year_single_file;
	
	private Boolean run_indicators_LocationsZones_municipalities = false;
	private String run_indicators_LocationsZones_municipalities_filter = "";
	private Boolean run_indicators_LocationsZones_cantons = false;
	private String run_indicators_LocationsZones_cantons_filter = "";
	
	private Boolean run_indicators_RunInfo_active;
	private String run_indicators_RunInfo_runs_filter;
	private String run_indicators_RunInfo_years_filter;
	private Boolean run_indicators_RunInfo_all_year_single_file;
	
	private Boolean run_indicators_RunInfo_locations = false;
	private String run_indicators_RunInfo_location_filter = "";
	private Boolean run_indicators_RunInfo_municipalities = false;
	private String run_indicators_RunInfo_municipalities_filter = "";
	private Boolean run_indicators_RunInfo_cantons = false;
	private String run_indicators_RunInfo_cantons_filter = "";
		
	public Boolean getRun_indicators_print_header_rows() {
		return run_indicators_print_header_rows;
	}

	public void setRun_indicators_print_header_rows(Boolean run_indicators_print_header_rows) {
		this.run_indicators_print_header_rows = run_indicators_print_header_rows;
	}
		
	public String getRun_indicators_folder() {
		return run_indicators_folder;
	}

	public void setRun_indicators_folder(String run_indicators_folder) {
		this.run_indicators_folder = run_indicators_folder;
	}
	
	public String getRun_indicators_description() {
		return run_indicators_description;
	}

	public void setRun_indicators_description(String run_indicators_description) {
		this.run_indicators_description = run_indicators_description;
	}
	
	public String getRun_indicators_extra_description() {
		return run_indicators_extra_description;
	}

	public void setRun_indicators_extra_description(String run_indicators_extra_description) {
		this.run_indicators_extra_description = run_indicators_extra_description;
	}
	
	public Boolean getRun_indicators_active() {
		return run_indicators_active;
	}

	public void setRun_indicators_active(Boolean run_indicators_active) {
		this.run_indicators_active = run_indicators_active;
	}
	
	public Integer getRun_indicators_export_level() {
		if(run_indicators_export_level == null || run_indicators_export_level > 4 || run_indicators_export_level < 1) run_indicators_export_level = run_indicators_export_level_CALIBRATION;
		return run_indicators_export_level;
	}

	public void setRun_indicators_export_level(Integer run_indicators_export_level) {
		if(run_indicators_export_level == null || run_indicators_export_level > 4 || run_indicators_export_level < 1) run_indicators_export_level = run_indicators_export_level_CALIBRATION;
		this.run_indicators_export_level = run_indicators_export_level;
	}
	
	public Boolean getRun_indicators_export_ripis_business() {
		return run_indicators_export_ripis_business;
	}

	public void setRun_indicators_export_ripis_business(
			Boolean run_indicators_export_ripis_business) {
		this.run_indicators_export_ripis_business = run_indicators_export_ripis_business;
	}

	public Boolean getRun_indicators_export_ripis_households() {
		return run_indicators_export_ripis_households;
	}

	public void setRun_indicators_export_ripis_households(
			Boolean run_indicators_export_ripis_households) {
		this.run_indicators_export_ripis_households = run_indicators_export_ripis_households;
	}

	public String getRun_indicators_export_ripis_locations() {
		return run_indicators_export_ripis_locations;
	}

	public void setRun_indicators_export_ripis_locations(
			String run_indicators_export_ripis_locations) {
		this.run_indicators_export_ripis_locations = run_indicators_export_ripis_locations;
	}
	
	public List<Integer> getExportRipisLocationsSet(){
		List<Integer> expLocList = new ArrayList<Integer>();
		String[] locations = run_indicators_export_ripis_locations.split(",");
		if(locations.length > 0){
			for(String sid : locations){
				try{
					expLocList.add(Integer.parseInt(sid));
				}catch(Exception ex){}
			}
		}
		return expLocList;
	}
	
	
	public Boolean getRun_indicators_Overview_active() {
		return run_indicators_Overview_active;
	}

	public void setRun_indicators_Overview_active(
			Boolean run_indicators_Overview_active) {
		this.run_indicators_Overview_active = run_indicators_Overview_active;
	}

	public String getRun_indicators_Overview_runs_filter() {
		return run_indicators_Overview_runs_filter;
	}

	public void setRun_indicators_Overview_runs_filter(
			String run_indicators_Overview_runs_filter) {
		this.run_indicators_Overview_runs_filter = run_indicators_Overview_runs_filter;
	}

	public String getRun_indicators_Overview_years_filter() {
		return run_indicators_Overview_years_filter;
	}

	public void setRun_indicators_Overview_years_filter(
			String run_indicators_Overview_years_filter) {
		this.run_indicators_Overview_years_filter = run_indicators_Overview_years_filter;
	}

	public Boolean getRun_indicators_Overview_locations() {
		return run_indicators_Overview_locations;
	}

	public void setRun_indicators_Overview_locations(
			Boolean run_indicators_Overview_locations) {
		this.run_indicators_Overview_locations = run_indicators_Overview_locations;
	}

	public String getRun_indicators_Overview_location_filter() {
		return run_indicators_Overview_location_filter;
	}

	public void setRun_indicators_Overview_location_filter(
			String run_indicators_Overview_location_filter) {
		this.run_indicators_Overview_location_filter = run_indicators_Overview_location_filter;
	}

	public Boolean getRun_indicators_Overview_municipalities() {
		return run_indicators_Overview_municipalities;
	}

	public void setRun_indicators_Overview_municipalities(
			Boolean run_indicators_Overview_municipalities) {
		this.run_indicators_Overview_municipalities = run_indicators_Overview_municipalities;
	}

	public String getRun_indicators_Overview_municipalities_filter() {
		return run_indicators_Overview_municipalities_filter;
	}

	public void setRun_indicators_Overview_municipalities_filter(
			String run_indicators_Overview_municipalities_filter) {
		this.run_indicators_Overview_municipalities_filter = run_indicators_Overview_municipalities_filter;
	}

	public Boolean getRun_indicators_Overview_cantons() {
		return run_indicators_Overview_cantons;
	}

	public void setRun_indicators_Overview_cantons(
			Boolean run_indicators_Overview_cantons) {
		this.run_indicators_Overview_cantons = run_indicators_Overview_cantons;
	}

	public String getRun_indicators_Overview_cantons_filter() {
		return run_indicators_Overview_cantons_filter;
	}

	public void setRun_indicators_Overview_cantons_filter(
			String run_indicators_Overview_cantons_filter) {
		this.run_indicators_Overview_cantons_filter = run_indicators_Overview_cantons_filter;
	}

	public Boolean getRun_indicators_Overview_all_year_single_file() {
		return run_indicators_Overview_all_year_single_file;
	}

	public void setRun_indicators_Overview_all_year_single_file(
			Boolean run_indicators_Overview_all_year_single_file) {
		this.run_indicators_Overview_all_year_single_file = run_indicators_Overview_all_year_single_file;
	}

	public Boolean getRun_indicators_GIS_active() {
		return run_indicators_GIS_active;
	}

	public void setRun_indicators_GIS_active(Boolean run_indicators_GIS_active) {
		this.run_indicators_GIS_active = run_indicators_GIS_active;
	}

	public String getRun_indicators_GIS_runs_filter() {
		return run_indicators_GIS_runs_filter;
	}

	public void setRun_indicators_GIS_runs_filter(
			String run_indicators_GIS_runs_filter) {
		this.run_indicators_GIS_runs_filter = run_indicators_GIS_runs_filter;
	}

	public String getRun_indicators_GIS_years_filter() {
		return run_indicators_GIS_years_filter;
	}

	public void setRun_indicators_GIS_years_filter(
			String run_indicators_GIS_years_filter) {
		this.run_indicators_GIS_years_filter = run_indicators_GIS_years_filter;
	}

	public Boolean getRun_indicators_GIS_locations() {
		return run_indicators_GIS_locations;
	}

	public void setRun_indicators_GIS_locations(Boolean run_indicators_GIS_locations) {
		this.run_indicators_GIS_locations = run_indicators_GIS_locations;
	}

	public String getRun_indicators_GIS_location_filter() {
		return run_indicators_GIS_location_filter;
	}

	public void setRun_indicators_GIS_location_filter(
			String run_indicators_GIS_location_filter) {
		this.run_indicators_GIS_location_filter = run_indicators_GIS_location_filter;
	}

	public Boolean getRun_indicators_GIS_municipalities() {
		return run_indicators_GIS_municipalities;
	}

	public void setRun_indicators_GIS_municipalities(
			Boolean run_indicators_GIS_municipalities) {
		this.run_indicators_GIS_municipalities = run_indicators_GIS_municipalities;
	}

	public String getRun_indicators_GIS_municipalities_filter() {
		return run_indicators_GIS_municipalities_filter;
	}

	public void setRun_indicators_GIS_municipalities_filter(
			String run_indicators_GIS_municipalities_filter) {
		this.run_indicators_GIS_municipalities_filter = run_indicators_GIS_municipalities_filter;
	}

	public Boolean getRun_indicators_GIS_cantons() {
		return run_indicators_GIS_cantons;
	}

	public void setRun_indicators_GIS_cantons(Boolean run_indicators_GIS_cantons) {
		this.run_indicators_GIS_cantons = run_indicators_GIS_cantons;
	}

	public String getRun_indicators_GIS_cantons_filter() {
		return run_indicators_GIS_cantons_filter;
	}

	public void setRun_indicators_GIS_cantons_filter(
			String run_indicators_GIS_cantons_filter) {
		this.run_indicators_GIS_cantons_filter = run_indicators_GIS_cantons_filter;
	}

	public Boolean getRun_indicators_GIS_all_year_single_file() {
		return run_indicators_GIS_all_year_single_file;
	}

	public void setRun_indicators_GIS_all_year_single_file(
			Boolean run_indicators_GIS_all_year_single_file) {
		this.run_indicators_GIS_all_year_single_file = run_indicators_GIS_all_year_single_file;
	}

	public Boolean getRun_indicators_Households_active() {
		return run_indicators_Households_active;
	}

	public void setRun_indicators_Households_active(
			Boolean run_indicators_Households_active) {
		this.run_indicators_Households_active = run_indicators_Households_active;
	}

	public String getRun_indicators_Households_runs_filter() {
		return run_indicators_Households_runs_filter;
	}

	public void setRun_indicators_Households_runs_filter(
			String run_indicators_Households_runs_filter) {
		this.run_indicators_Households_runs_filter = run_indicators_Households_runs_filter;
	}

	public String getRun_indicators_Households_years_filter() {
		return run_indicators_Households_years_filter;
	}

	public void setRun_indicators_Households_years_filter(
			String run_indicators_Households_years_filter) {
		this.run_indicators_Households_years_filter = run_indicators_Households_years_filter;
	}

	public Boolean getRun_indicators_Households_locations() {
		return run_indicators_Households_locations;
	}

	public void setRun_indicators_Households_locations(
			Boolean run_indicators_Households_locations) {
		this.run_indicators_Households_locations = run_indicators_Households_locations;
	}

	public String getRun_indicators_Households_location_filter() {
		return run_indicators_Households_location_filter;
	}

	public void setRun_indicators_Households_location_filter(
			String run_indicators_Households_location_filter) {
		this.run_indicators_Households_location_filter = run_indicators_Households_location_filter;
	}

	public Boolean getRun_indicators_Households_municipalities() {
		return run_indicators_Households_municipalities;
	}

	public void setRun_indicators_Households_municipalities(
			Boolean run_indicators_Households_municipalities) {
		this.run_indicators_Households_municipalities = run_indicators_Households_municipalities;
	}

	public String getRun_indicators_Households_municipalities_filter() {
		return run_indicators_Households_municipalities_filter;
	}

	public void setRun_indicators_Households_municipalities_filter(
			String run_indicators_Households_municipalities_filter) {
		this.run_indicators_Households_municipalities_filter = run_indicators_Households_municipalities_filter;
	}

	public Boolean getRun_indicators_Households_cantons() {
		return run_indicators_Households_cantons;
	}

	public void setRun_indicators_Households_cantons(
			Boolean run_indicators_Households_cantons) {
		this.run_indicators_Households_cantons = run_indicators_Households_cantons;
	}

	public String getRun_indicators_Households_cantons_filter() {
		return run_indicators_Households_cantons_filter;
	}

	public void setRun_indicators_Households_cantons_filter(
			String run_indicators_Households_cantons_filter) {
		this.run_indicators_Households_cantons_filter = run_indicators_Households_cantons_filter;
	}

	public Boolean getRun_indicators_Households_all_year_single_file() {
		return run_indicators_Households_all_year_single_file;
	}

	public void setRun_indicators_Households_all_year_single_file(
			Boolean run_indicators_Households_all_year_single_file) {
		this.run_indicators_Households_all_year_single_file = run_indicators_Households_all_year_single_file;
	}

	public Boolean getRun_indicators_Businesses_active() {
		return run_indicators_Businesses_active;
	}

	public void setRun_indicators_Businesses_active(
			Boolean run_indicators_Businesses_active) {
		this.run_indicators_Businesses_active = run_indicators_Businesses_active;
	}

	public String getRun_indicators_Businesses_runs_filter() {
		return run_indicators_Businesses_runs_filter;
	}

	public void setRun_indicators_Businesses_runs_filter(
			String run_indicators_Businesses_runs_filter) {
		this.run_indicators_Businesses_runs_filter = run_indicators_Businesses_runs_filter;
	}

	public String getRun_indicators_Businesses_years_filter() {
		return run_indicators_Businesses_years_filter;
	}

	public void setRun_indicators_Businesses_years_filter(
			String run_indicators_Businesses_years_filter) {
		this.run_indicators_Businesses_years_filter = run_indicators_Businesses_years_filter;
	}

	public Boolean getRun_indicators_Businesses_locations() {
		return run_indicators_Businesses_locations;
	}

	public void setRun_indicators_Businesses_locations(
			Boolean run_indicators_Businesses_locations) {
		this.run_indicators_Businesses_locations = run_indicators_Businesses_locations;
	}

	public String getRun_indicators_Businesses_location_filter() {
		return run_indicators_Businesses_location_filter;
	}

	public void setRun_indicators_Businesses_location_filter(
			String run_indicators_Businesses_location_filter) {
		this.run_indicators_Businesses_location_filter = run_indicators_Businesses_location_filter;
	}

	public Boolean getRun_indicators_Businesses_municipalities() {
		return run_indicators_Businesses_municipalities;
	}

	public void setRun_indicators_Businesses_municipalities(
			Boolean run_indicators_Businesses_municipalities) {
		this.run_indicators_Businesses_municipalities = run_indicators_Businesses_municipalities;
	}

	public String getRun_indicators_Businesses_municipalities_filter() {
		return run_indicators_Businesses_municipalities_filter;
	}

	public void setRun_indicators_Businesses_municipalities_filter(
			String run_indicators_Businesses_municipalities_filter) {
		this.run_indicators_Businesses_municipalities_filter = run_indicators_Businesses_municipalities_filter;
	}

	public Boolean getRun_indicators_Businesses_cantons() {
		return run_indicators_Businesses_cantons;
	}

	public void setRun_indicators_Businesses_cantons(
			Boolean run_indicators_Businesses_cantons) {
		this.run_indicators_Businesses_cantons = run_indicators_Businesses_cantons;
	}

	public String getRun_indicators_Businesses_cantons_filter() {
		return run_indicators_Businesses_cantons_filter;
	}

	public void setRun_indicators_Businesses_cantons_filter(
			String run_indicators_Businesses_cantons_filter) {
		this.run_indicators_Businesses_cantons_filter = run_indicators_Businesses_cantons_filter;
	}

	public Boolean getRun_indicators_Businesses_all_year_single_file() {
		return run_indicators_Businesses_all_year_single_file;
	}

	public void setRun_indicators_Businesses_all_year_single_file(
			Boolean run_indicators_Businesses_all_year_single_file) {
		this.run_indicators_Businesses_all_year_single_file = run_indicators_Businesses_all_year_single_file;
	}

	public Boolean getRun_indicators_Persons_active() {
		return run_indicators_Persons_active;
	}

	public void setRun_indicators_Persons_active(
			Boolean run_indicators_Persons_active) {
		this.run_indicators_Persons_active = run_indicators_Persons_active;
	}

	public String getRun_indicators_Persons_runs_filter() {
		return run_indicators_Persons_runs_filter;
	}

	public void setRun_indicators_Persons_runs_filter(
			String run_indicators_Persons_runs_filter) {
		this.run_indicators_Persons_runs_filter = run_indicators_Persons_runs_filter;
	}

	public String getRun_indicators_Persons_years_filter() {
		return run_indicators_Persons_years_filter;
	}

	public void setRun_indicators_Persons_years_filter(
			String run_indicators_Persons_years_filter) {
		this.run_indicators_Persons_years_filter = run_indicators_Persons_years_filter;
	}

	public Boolean getRun_indicators_Persons_locations() {
		return run_indicators_Persons_locations;
	}

	public void setRun_indicators_Persons_locations(
			Boolean run_indicators_Persons_locations) {
		this.run_indicators_Persons_locations = run_indicators_Persons_locations;
	}

	public String getRun_indicators_Persons_location_filter() {
		return run_indicators_Persons_location_filter;
	}

	public void setRun_indicators_Persons_location_filter(
			String run_indicators_Persons_location_filter) {
		this.run_indicators_Persons_location_filter = run_indicators_Persons_location_filter;
	}

	public Boolean getRun_indicators_Persons_municipalities() {
		return run_indicators_Persons_municipalities;
	}

	public void setRun_indicators_Persons_municipalities(
			Boolean run_indicators_Persons_municipalities) {
		this.run_indicators_Persons_municipalities = run_indicators_Persons_municipalities;
	}

	public String getRun_indicators_Persons_municipalities_filter() {
		return run_indicators_Persons_municipalities_filter;
	}

	public void setRun_indicators_Persons_municipalities_filter(
			String run_indicators_Persons_municipalities_filter) {
		this.run_indicators_Persons_municipalities_filter = run_indicators_Persons_municipalities_filter;
	}

	public Boolean getRun_indicators_Persons_cantons() {
		return run_indicators_Persons_cantons;
	}

	public void setRun_indicators_Persons_cantons(
			Boolean run_indicators_Persons_cantons) {
		this.run_indicators_Persons_cantons = run_indicators_Persons_cantons;
	}

	public String getRun_indicators_Persons_cantons_filter() {
		return run_indicators_Persons_cantons_filter;
	}

	public void setRun_indicators_Persons_cantons_filter(
			String run_indicators_Persons_cantons_filter) {
		this.run_indicators_Persons_cantons_filter = run_indicators_Persons_cantons_filter;
	}

	public Boolean getRun_indicators_Persons_all_year_single_file() {
		return run_indicators_Persons_all_year_single_file;
	}

	public void setRun_indicators_Persons_all_year_single_file(
			Boolean run_indicators_Persons_all_year_single_file) {
		this.run_indicators_Persons_all_year_single_file = run_indicators_Persons_all_year_single_file;
	}

	public Boolean getRun_indicators_Relocation_active() {
		return run_indicators_Relocation_active;
	}

	public void setRun_indicators_Relocation_active(
			Boolean run_indicators_Relocation_active) {
		this.run_indicators_Relocation_active = run_indicators_Relocation_active;
	}

	public Boolean getRun_indicators_Relocation_cumulated() {
		return run_indicators_Relocation_cumulated;
	}

	public void setRun_indicators_Relocation_cumulated(
			Boolean run_indicators_Relocation_cumulated) {
		this.run_indicators_Relocation_cumulated = run_indicators_Relocation_cumulated;
	}
		
	public String getRun_indicators_Relocation_runs_filter() {
		return run_indicators_Relocation_runs_filter;
	}

	public void setRun_indicators_Relocation_runs_filter(
			String run_indicators_Relocation_runs_filter) {
		this.run_indicators_Relocation_runs_filter = run_indicators_Relocation_runs_filter;
	}

	public String getRun_indicators_Relocation_years_filter() {
		return run_indicators_Relocation_years_filter;
	}

	public void setRun_indicators_Relocation_years_filter(
			String run_indicators_Relocation_years_filter) {
		this.run_indicators_Relocation_years_filter = run_indicators_Relocation_years_filter;
	}

	public Boolean getRun_indicators_Relocation_locations() {
		return run_indicators_Relocation_locations;
	}

	public void setRun_indicators_Relocation_locations(
			Boolean run_indicators_Relocation_locations) {
		this.run_indicators_Relocation_locations = run_indicators_Relocation_locations;
	}

	public String getRun_indicators_Relocation_location_filter() {
		return run_indicators_Relocation_location_filter;
	}

	public void setRun_indicators_Relocation_location_filter(
			String run_indicators_Relocation_location_filter) {
		this.run_indicators_Relocation_location_filter = run_indicators_Relocation_location_filter;
	}

	public Boolean getRun_indicators_Relocation_municipalities() {
		return run_indicators_Relocation_municipalities;
	}

	public void setRun_indicators_Relocation_municipalities(
			Boolean run_indicators_Relocation_municipalities) {
		this.run_indicators_Relocation_municipalities = run_indicators_Relocation_municipalities;
	}

	public String getRun_indicators_Relocation_municipalities_filter() {
		return run_indicators_Relocation_municipalities_filter;
	}

	public void setRun_indicators_Relocation_municipalities_filter(
			String run_indicators_Relocation_municipalities_filter) {
		this.run_indicators_Relocation_municipalities_filter = run_indicators_Relocation_municipalities_filter;
	}

	public Boolean getRun_indicators_Relocation_cantons() {
		return run_indicators_Relocation_cantons;
	}

	public void setRun_indicators_Relocation_cantons(
			Boolean run_indicators_Relocation_cantons) {
		this.run_indicators_Relocation_cantons = run_indicators_Relocation_cantons;
	}

	public String getRun_indicators_Relocation_cantons_filter() {
		return run_indicators_Relocation_cantons_filter;
	}

	public void setRun_indicators_Relocation_cantons_filter(
			String run_indicators_Relocation_cantons_filter) {
		this.run_indicators_Relocation_cantons_filter = run_indicators_Relocation_cantons_filter;
	}

	public Boolean getRun_indicators_Relocation_all_year_single_file() {
		return run_indicators_Relocation_all_year_single_file;
	}

	public void setRun_indicators_Relocation_all_year_single_file(
			Boolean run_indicators_Relocation_all_year_single_file) {
		this.run_indicators_Relocation_all_year_single_file = run_indicators_Relocation_all_year_single_file;
	}

	public Boolean getRun_indicators_LocationsZones_active() {
		return run_indicators_LocationsZones_active;
	}

	public void setRun_indicators_LocationsZones_active(
			Boolean run_indicators_LocationsZones_active) {
		this.run_indicators_LocationsZones_active = run_indicators_LocationsZones_active;
	}

	public String getRun_indicators_LocationsZones_runs_filter() {
		return run_indicators_LocationsZones_runs_filter;
	}

	public void setRun_indicators_LocationsZones_runs_filter(
			String run_indicators_LocationsZones_runs_filter) {
		this.run_indicators_LocationsZones_runs_filter = run_indicators_LocationsZones_runs_filter;
	}

	public String getRun_indicators_LocationsZones_years_filter() {
		return run_indicators_LocationsZones_years_filter;
	}

	public void setRun_indicators_LocationsZones_years_filter(
			String run_indicators_LocationsZones_years_filter) {
		this.run_indicators_LocationsZones_years_filter = run_indicators_LocationsZones_years_filter;
	}

	public Boolean getRun_indicators_LocationsZones_locations() {
		return run_indicators_LocationsZones_locations;
	}

	public void setRun_indicators_LocationsZones_locations(
			Boolean run_indicators_LocationsZones_locations) {
		this.run_indicators_LocationsZones_locations = run_indicators_LocationsZones_locations;
	}

	public String getRun_indicators_LocationsZones_location_filter() {
		return run_indicators_LocationsZones_location_filter;
	}

	public void setRun_indicators_LocationsZones_location_filter(
			String run_indicators_LocationsZones_location_filter) {
		this.run_indicators_LocationsZones_location_filter = run_indicators_LocationsZones_location_filter;
	}

	public Boolean getRun_indicators_LocationsZones_municipalities() {
		return run_indicators_LocationsZones_municipalities;
	}

//	public void setRun_indicators_LocationsZones_municipalities(
//			Boolean run_indicators_LocationsZones_municipalities) {
//		this.run_indicators_LocationsZones_municipalities = run_indicators_LocationsZones_municipalities;
//	}

	public String getRun_indicators_LocationsZones_municipalities_filter() {
		return run_indicators_LocationsZones_municipalities_filter;
	}

//	public void setRun_indicators_LocationsZones_municipalities_filter(
//			String run_indicators_LocationsZones_municipalities_filter) {
//		this.run_indicators_LocationsZones_municipalities_filter = run_indicators_LocationsZones_municipalities_filter;
//	}

	public Boolean getRun_indicators_LocationsZones_cantons() {
		return run_indicators_LocationsZones_cantons;
	}

//	public void setRun_indicators_LocationsZones_cantons(
//			Boolean run_indicators_LocationsZones_cantons) {
//		this.run_indicators_LocationsZones_cantons = run_indicators_LocationsZones_cantons;
//	}

	public String getRun_indicators_LocationsZones_cantons_filter() {
		return run_indicators_LocationsZones_cantons_filter;
	}

//	public void setRun_indicators_LocationsZones_cantons_filter(
//			String run_indicators_LocationsZones_cantons_filter) {
//		this.run_indicators_LocationsZones_cantons_filter = run_indicators_LocationsZones_cantons_filter;
//	}

	public Boolean getRun_indicators_LocationsZones_all_year_single_file() {
		return run_indicators_LocationsZones_all_year_single_file;
	}

	public void setRun_indicators_LocationsZones_all_year_single_file(
			Boolean run_indicators_LocationsZones_all_year_single_file) {
		this.run_indicators_LocationsZones_all_year_single_file = run_indicators_LocationsZones_all_year_single_file;
	}

	public Boolean getRun_indicators_RunInfo_active() {
		return run_indicators_RunInfo_active;
	}

	public void setRun_indicators_RunInfo_active(
			Boolean run_indicators_RunInfo_active) {
		this.run_indicators_RunInfo_active = run_indicators_RunInfo_active;
	}

	public String getRun_indicators_RunInfo_runs_filter() {
		return run_indicators_RunInfo_runs_filter;
	}

	public void setRun_indicators_RunInfo_runs_filter(
			String run_indicators_RunInfo_runs_filter) {
		this.run_indicators_RunInfo_runs_filter = run_indicators_RunInfo_runs_filter;
	}

	public String getRun_indicators_RunInfo_years_filter() {
		return run_indicators_RunInfo_years_filter;
	}

	public void setRun_indicators_RunInfo_years_filter(
			String run_indicators_RunInfo_years_filter) {
		this.run_indicators_RunInfo_years_filter = run_indicators_RunInfo_years_filter;
	}

	public Boolean getRun_indicators_RunInfo_locations() {
		return run_indicators_RunInfo_locations;
	}

//	public void setRun_indicators_RunInfo_locations(
//			Boolean run_indicators_RunInfo_locations) {
//		this.run_indicators_RunInfo_locations = run_indicators_RunInfo_locations;
//	}

	public String getRun_indicators_RunInfo_location_filter() {
		return run_indicators_RunInfo_location_filter;
	}

//	public void setRun_indicators_RunInfo_location_filter(
//			String run_indicators_RunInfo_location_filter) {
//		this.run_indicators_RunInfo_location_filter = run_indicators_RunInfo_location_filter;
//	}

	public Boolean getRun_indicators_RunInfo_municipalities() {
		return run_indicators_RunInfo_municipalities;
	}

//	public void setRun_indicators_RunInfo_municipalities(
//			Boolean run_indicators_RunInfo_municipalities) {
//		this.run_indicators_RunInfo_municipalities = run_indicators_RunInfo_municipalities;
//	}

	public String getRun_indicators_RunInfo_municipalities_filter() {
		return run_indicators_RunInfo_municipalities_filter;
	}

//	public void setRun_indicators_RunInfo_municipalities_filter(
//			String run_indicators_RunInfo_municipalities_filter) {
//		this.run_indicators_RunInfo_municipalities_filter = run_indicators_RunInfo_municipalities_filter;
//	}

	public Boolean getRun_indicators_RunInfo_cantons() {
		return run_indicators_RunInfo_cantons;
	}

//	public void setRun_indicators_RunInfo_cantons(
//			Boolean run_indicators_RunInfo_cantons) {
//		this.run_indicators_RunInfo_cantons = run_indicators_RunInfo_cantons;
//	}

	public String getRun_indicators_RunInfo_cantons_filter() {
		return run_indicators_RunInfo_cantons_filter;
	}

//	public void setRun_indicators_RunInfo_cantons_filter(
//			String run_indicators_RunInfo_cantons_filter) {
//		this.run_indicators_RunInfo_cantons_filter = run_indicators_RunInfo_cantons_filter;
//	}

	public Boolean getRun_indicators_RunInfo_all_year_single_file() {
		return run_indicators_RunInfo_all_year_single_file;
	}

	public void setRun_indicators_RunInfo_all_year_single_file(
			Boolean run_indicators_RunInfo_all_year_single_file) {
		this.run_indicators_RunInfo_all_year_single_file = run_indicators_RunInfo_all_year_single_file;
	}

	public Boolean getRun_indicators_BusinessesTypes_active() {
		return run_indicators_BusinessesTypes_active;
	}

	public void setRun_indicators_BusinessesTypes_active(
			Boolean run_indicators_BusinessesTypes_active) {
		this.run_indicators_BusinessesTypes_active = run_indicators_BusinessesTypes_active;
	}

	public String getRun_indicators_BusinessesTypes_runs_filter() {
		return run_indicators_BusinessesTypes_runs_filter;
	}

	public void setRun_indicators_BusinessesTypes_runs_filter(
			String run_indicators_BusinessesTypes_runs_filter) {
		this.run_indicators_BusinessesTypes_runs_filter = run_indicators_BusinessesTypes_runs_filter;
	}

	public String getRun_indicators_BusinessesTypes_years_filter() {
		return run_indicators_BusinessesTypes_years_filter;
	}

	public void setRun_indicators_BusinessesTypes_years_filter(
			String run_indicators_BusinessesTypes_years_filter) {
		this.run_indicators_BusinessesTypes_years_filter = run_indicators_BusinessesTypes_years_filter;
	}

	public Boolean getRun_indicators_BusinessesTypes_locations() {
		return run_indicators_BusinessesTypes_locations;
	}

	public void setRun_indicators_BusinessesTypes_locations(
			Boolean run_indicators_BusinessesTypes_locations) {
		this.run_indicators_BusinessesTypes_locations = run_indicators_BusinessesTypes_locations;
	}

	public String getRun_indicators_BusinessesTypes_location_filter() {
		return run_indicators_BusinessesTypes_location_filter;
	}

	public void setRun_indicators_BusinessesTypes_location_filter(
			String run_indicators_BusinessesTypes_location_filter) {
		this.run_indicators_BusinessesTypes_location_filter = run_indicators_BusinessesTypes_location_filter;
	}

	public Boolean getRun_indicators_BusinessesTypes_municipalities() {
		return run_indicators_BusinessesTypes_municipalities;
	}

	public void setRun_indicators_BusinessesTypes_municipalities(
			Boolean run_indicators_BusinessesTypes_municipalities) {
		this.run_indicators_BusinessesTypes_municipalities = run_indicators_BusinessesTypes_municipalities;
	}

	public String getRun_indicators_BusinessesTypes_municipalities_filter() {
		return run_indicators_BusinessesTypes_municipalities_filter;
	}

	public void setRun_indicators_BusinessesTypes_municipalities_filter(
			String run_indicators_BusinessesTypes_municipalities_filter) {
		this.run_indicators_BusinessesTypes_municipalities_filter = run_indicators_BusinessesTypes_municipalities_filter;
	}

	public Boolean getRun_indicators_BusinessesTypes_cantons() {
		return run_indicators_BusinessesTypes_cantons;
	}

	public void setRun_indicators_BusinessesTypes_cantons(
			Boolean run_indicators_BusinessesTypes_cantons) {
		this.run_indicators_BusinessesTypes_cantons = run_indicators_BusinessesTypes_cantons;
	}

	public String getRun_indicators_BusinessesTypes_cantons_filter() {
		return run_indicators_BusinessesTypes_cantons_filter;
	}

	public void setRun_indicators_BusinessesTypes_cantons_filter(
			String run_indicators_BusinessesTypes_cantons_filter) {
		this.run_indicators_BusinessesTypes_cantons_filter = run_indicators_BusinessesTypes_cantons_filter;
	}

	public Boolean getRun_indicators_BusinessesTypes_all_year_single_file() {
		return run_indicators_BusinessesTypes_all_year_single_file;
	}

	public void setRun_indicators_BusinessesTypes_all_year_single_file(
			Boolean run_indicators_BusinessesTypes_all_year_single_file) {
		this.run_indicators_BusinessesTypes_all_year_single_file = run_indicators_BusinessesTypes_all_year_single_file;
	}
	
	
	public Boolean getRun_indicators_commuters_active() {
		return run_indicators_commuters_active;
	}

	public void setRun_indicators_commuters_active(
			Boolean run_indicators_commuters_active) {
		this.run_indicators_commuters_active = run_indicators_commuters_active;
	}

	public String getRun_indicators_commuters_runs_filter() {
		return run_indicators_commuters_runs_filter;
	}

	public void setRun_indicators_commuters_runs_filter(
			String run_indicators_commuters_runs_filter) {
		this.run_indicators_commuters_runs_filter = run_indicators_commuters_runs_filter;
	}

	public String getRun_indicators_commuters_years_filter() {
		return run_indicators_commuters_years_filter;
	}

	public void setRun_indicators_commuters_years_filter(
			String run_indicators_commuters_years_filter) {
		this.run_indicators_commuters_years_filter = run_indicators_commuters_years_filter;
	}

	public Boolean getRun_indicators_commuters_locations() {
		return run_indicators_commuters_locations;
	}

	public void setRun_indicators_commuters_locations(
			Boolean run_indicators_commuters_locations) {
		this.run_indicators_commuters_locations = run_indicators_commuters_locations;
	}

	public String getRun_indicators_commuters_location_filter() {
		return run_indicators_commuters_location_filter;
	}

	public void setRun_indicators_commuters_location_filter(
			String run_indicators_commuters_location_filter) {
		this.run_indicators_commuters_location_filter = run_indicators_commuters_location_filter;
	}

	public Boolean getRun_indicators_commuters_municipalities() {
		return run_indicators_commuters_municipalities;
	}

	public void setRun_indicators_commuters_municipalities(
			Boolean run_indicators_commuters_municipalities) {
		this.run_indicators_commuters_municipalities = run_indicators_commuters_municipalities;
	}

	public String getRun_indicators_commuters_municipalities_filter() {
		return run_indicators_commuters_municipalities_filter;
	}

	public void setRun_indicators_commuters_municipalities_filter(
			String run_indicators_commuters_municipalities_filter) {
		this.run_indicators_commuters_municipalities_filter = run_indicators_commuters_municipalities_filter;
	}

	public Boolean getRun_indicators_commuters_cantons() {
		return run_indicators_commuters_cantons;
	}

	public void setRun_indicators_commuters_cantons(
			Boolean run_indicators_commuters_cantons) {
		this.run_indicators_commuters_cantons = run_indicators_commuters_cantons;
	}

	public String getRun_indicators_commuters_cantons_filter() {
		return run_indicators_commuters_cantons_filter;
	}

	public void setRun_indicators_commuters_cantons_filter(
			String run_indicators_commuters_cantons_filter) {
		this.run_indicators_commuters_cantons_filter = run_indicators_commuters_cantons_filter;
	}

	public Boolean getRun_indicators_commuters_all_year_single_file() {
		return run_indicators_commuters_all_year_single_file;
	}

	public void setRun_indicators_commuters_all_year_single_file(
			Boolean run_indicators_commuters_all_year_single_file) {
		this.run_indicators_commuters_all_year_single_file = run_indicators_commuters_all_year_single_file;
	}	
}
