package org.falcsim.agentmodel.app.domain;


/** Parameters to switch on and off application addons, universe and behaviors
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5  
 */

public class RunParameters {
	
	Boolean run_synthese;
	Boolean run_universe;
	Boolean run_populateDistances;
	Boolean run_sublocations;

	Integer service_maximum_threads;
	Integer service_cycles;
	Boolean service_final_save; 
	
	Boolean universe_use_random_byID;
	Boolean universe_reference_runs;
	Integer universe_reference_runs_rg_seed;
	Integer	universe_reference_runs_cycles;	
	Integer universe_reference_runs_cycles_startfrom;
	String universe_reference_runs_schema;
	String population_sub_schema;

	Boolean	universe_reference_runs_statistics;

	Boolean	universe_reference_runs_statistics_avg;
	Boolean	universe_reference_runs_statistics_min;
	Boolean	universe_reference_runs_statistics_max;
	Boolean	universe_reference_runs_statistics_median;
	Boolean universe_reference_runs_copy_best_cycle;
	
	String universe_reference_runs_copy_best_cycle_name;
	Boolean universe_reference_runs_copy_best_cycle_clean;

	Boolean run_yearly_accessible_variables;
	
	Boolean run_scenarios;
	String run_scenarios_xml_path;
		
	String app_systemQuery;
	Integer app_theYear;
	
	String projectDir;
	String projectfilePath;
	String localProjectfilePath;
	String paramsDefPath;
	
	String distances_evolution_xml_path;

	public String getDistances_evolution_xml_path() {
		return distances_evolution_xml_path;
	}

	public void setDistances_evolution_xml_path(String distances_evolution_xml_path) {
		this.distances_evolution_xml_path = distances_evolution_xml_path;
	}

	public Boolean getRun_synthese() {
		return run_synthese;
	}

	public void setRun_synthese(Boolean run_synthese) {
		this.run_synthese = run_synthese;
	}

	public Boolean getRun_universe() {
		return run_universe;
	}

	public void setRun_universe(Boolean run_universe) {
		this.run_universe = run_universe;
	}

	public Boolean getRun_populateDistances() {
		return run_populateDistances;
	}

	public void setRun_populateDistances(Boolean run_populateDistances) {
		this.run_populateDistances = run_populateDistances;
	}

	public Integer getService_cycles() {
		return service_cycles;
	}

	public void setService_cycles(Integer service_cycles) {
		this.service_cycles = service_cycles;
	}

	public Integer getService_maximum_threads() {
		return service_maximum_threads;
	}

	public void setService_maximum_threads(Integer service_maximum_threads) {
		this.service_maximum_threads = service_maximum_threads;
	}
		
	public Boolean getService_final_save() {
		return service_final_save;
	}

	public void setService_final_save(Boolean service_final_save) {
		this.service_final_save = service_final_save;
	}
	
	public Boolean getRun_sublocations() {
		return run_sublocations;
	}

	public void setRun_sublocations(Boolean run_sublocations) {
		this.run_sublocations = run_sublocations;
	}
		
	public Boolean getRun_yearly_accessible_variables() {
		return run_yearly_accessible_variables;
	}

	public void setRun_yearly_accessible_variables(
			Boolean run_yearly_accessible_variables) {
		this.run_yearly_accessible_variables = run_yearly_accessible_variables;
	}
	
	public Boolean getUniverse_use_random_byID() {
		return universe_use_random_byID;
	}

	public void setUniverse_use_random_byID(Boolean universe_use_random_byID) {
		this.universe_use_random_byID = universe_use_random_byID;
	}	
	
	public Boolean getUniverse_reference_runs() {
		return universe_reference_runs;
	}

	public void setUniverse_reference_runs(Boolean universe_reference_runs) {
		this.universe_reference_runs = universe_reference_runs;
	}

	public Integer getUniverse_reference_runs_rg_seed() {
		return universe_reference_runs_rg_seed;
	}	
	
	public void setUniverse_reference_runs_rg_seed(
			Integer universe_reference_runs_rg_seed) {
		this.universe_reference_runs_rg_seed = universe_reference_runs_rg_seed;
	}
	
	public Integer getUniverse_reference_runs_cycles() {
		return universe_reference_runs_cycles;
	}

	public void setUniverse_reference_runs_cycles(
			Integer universe_reference_runs_cycles) {
		this.universe_reference_runs_cycles = universe_reference_runs_cycles;
	}

	public Integer getUniverse_reference_runs_cycles_startfrom() {
		//return number greather that 0
		return universe_reference_runs_cycles_startfrom == null || universe_reference_runs_cycles_startfrom < 1 ? 1 : universe_reference_runs_cycles_startfrom;
	}

	public void setUniverse_reference_runs_cycles_startfrom(
			Integer universe_reference_runs_cycles_startfrom) {
		this.universe_reference_runs_cycles_startfrom = universe_reference_runs_cycles_startfrom;
	}
	
	public Boolean getUniverse_reference_runs_statistics() {
		return universe_reference_runs_statistics;
	}

	public void setUniverse_reference_runs_statistics(
			Boolean universe_reference_runs_statistics) {
		this.universe_reference_runs_statistics = universe_reference_runs_statistics;
	}

	public Boolean getUniverse_reference_runs_statistics_avg() {
		return universe_reference_runs_statistics_avg;
	}

	public void setUniverse_reference_runs_statistics_avg(
			Boolean universe_reference_runs_statistics_avg) {
		this.universe_reference_runs_statistics_avg = universe_reference_runs_statistics_avg;
	}

	public Boolean getUniverse_reference_runs_statistics_min() {
		return universe_reference_runs_statistics_min;
	}

	public void setUniverse_reference_runs_statistics_min(
			Boolean universe_reference_runs_statistics_min) {
		this.universe_reference_runs_statistics_min = universe_reference_runs_statistics_min;
	}

	public Boolean getUniverse_reference_runs_statistics_max() {
		return universe_reference_runs_statistics_max;
	}

	public void setUniverse_reference_runs_statistics_max(
			Boolean universe_reference_runs_statistics_max) {
		this.universe_reference_runs_statistics_max = universe_reference_runs_statistics_max;
	}

	public Boolean getUniverse_reference_runs_statistics_median() {
		return universe_reference_runs_statistics_median;
	}

	public void setUniverse_reference_runs_statistics_median(
			Boolean universe_reference_runs_statistics_median) {
		this.universe_reference_runs_statistics_median = universe_reference_runs_statistics_median;
	}
	
	public String getUniverse_reference_runs_schema() {
		return universe_reference_runs_schema;
	}

	public void setUniverse_reference_runs_schema(
			String universe_reference_runs_schema) {
		this.universe_reference_runs_schema = universe_reference_runs_schema;
	}
	
	public Boolean getUniverse_reference_runs_copy_best_cycle() {
		return universe_reference_runs_copy_best_cycle;
	}

	public void setUniverse_reference_runs_copy_best_cycle(Boolean universe_reference_runs_copy_best_cycle) {
		this.universe_reference_runs_copy_best_cycle = universe_reference_runs_copy_best_cycle;
	}
	
	public String getUniverse_reference_runs_copy_best_cycle_name() {
		return universe_reference_runs_copy_best_cycle_name;
	}

	public void setUniverse_reference_runs_copy_best_cycle_name(
			String universe_reference_runs_copy_best_cycle_name) {
		this.universe_reference_runs_copy_best_cycle_name = universe_reference_runs_copy_best_cycle_name;
	}

	public Boolean getUniverse_reference_runs_copy_best_cycle_clean() {
		return universe_reference_runs_copy_best_cycle_clean;
	}

	public void setUniverse_reference_runs_copy_best_cycle_clean(
			Boolean universe_reference_runs_copy_best_cycle_clean) {
		this.universe_reference_runs_copy_best_cycle_clean = universe_reference_runs_copy_best_cycle_clean;
	}
	
	public Boolean getRun_scenarios() {
		return run_scenarios;
	}

	public void setRun_scenarios(Boolean run_scenarios) {
		this.run_scenarios = run_scenarios;
	}

	public String getRun_scenarios_xml_path() {
		return run_scenarios_xml_path;
	}

	public void setRun_scenarios_xml_path(String run_scenarios_xml_path) {
		this.run_scenarios_xml_path = run_scenarios_xml_path;
	}
	
	public String getPopulation_sub_schema() {
		return population_sub_schema;
	}

	public void setPopulation_sub_schema(String population_sub_schema) {
		this.population_sub_schema = population_sub_schema;
	}

	public String getApp_systemQuery() {
		return app_systemQuery;
	}

	public void setApp_systemQuery(String app_systemQuery) {
		this.app_systemQuery = app_systemQuery;
	}

	public Integer getApp_theYear() {
		return app_theYear;
	}

	public void setApp_theYear(Integer app_theYear) {
		this.app_theYear = app_theYear;
	}
	
	public String getProjectDir() {
		return projectDir;
	}

	public void setProjectDir(String projectDir) {
		this.projectDir = projectDir;
	}	
	
	public String getProjectfilePath() {
		return projectfilePath;
	}

	public void setProjectfilePath(String projectfilePath) {
		this.projectfilePath = projectfilePath;
	}	
	
	public String getLocalProjectfilePath() {
		return localProjectfilePath;
	}

	public void setLocalProjectfilePath(String localProjectfilePath) {
		this.localProjectfilePath = localProjectfilePath;
	}	
	
	public String getExportParameterInfoDefinitionPath() {
		return paramsDefPath;
	}

	public void setExportParameterInfoDefinitionPath(String paramsDefPath) {
		this.paramsDefPath = paramsDefPath;
	}	
	
}
