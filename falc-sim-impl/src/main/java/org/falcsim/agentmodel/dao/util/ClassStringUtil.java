package org.falcsim.agentmodel.dao.util;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.dao.jdbc.ClassDescriptor;
import org.falcsim.agentmodel.dao.jdbc.FieldDescriptor;
import org.falcsim.agentmodel.distances.domain.DistanceRecord;
import org.falcsim.agentmodel.distances.domain.SimpleDistanceRecord;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.domain.Step;
import org.falcsim.agentmodel.sublocations.domain.LocationSubset;

/**
 * Encapsulates string functions for loaded CSV values according CSVT definition. 
 * Generates entities CSVT file content.
 * Generates entities headers.
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 * 
 */
public class ClassStringUtil {

	private static final Logger _log = Logger.getLogger(ClassStringUtil.class);
	
	public static final String QUOTE = "\"";
	public static final String SPACE = " ";
	public static final String EMPTY = "";
	public static final Double ZERO_D = 0D;
	public static final Integer ZERO_I = 0;
	
	private static String formatDate = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getDateFormat();
	private static String formatTimestamp = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getTimestampFormat();
	private static String decimalSeparator = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getDecimalSeparator();
	private static SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
	private static SimpleDateFormat sdf_t = new SimpleDateFormat(formatTimestamp);
	private static DecimalFormatSymbols dfs = null;
	private static DecimalFormat df = null;
	
	public static String formatDate(Date date){
		if(date != null)
			return ClassStringUtil.sdf.format(date);
		else
			return EMPTY;
	}
	
	public static String formatDouble(Double d){
		if(dfs == null){
			dfs = new DecimalFormatSymbols();
			dfs.setDecimalSeparator(decimalSeparator.charAt(0));
			df = new DecimalFormat("#.###",  dfs);
		}
		return df.format(d);
	}
	
	public static Integer parseInt(final String value){
		if(value.isEmpty())
			return null;
		else
			return Integer.parseInt(value);
	}
	
	public static Double parseDouble(final String value){
		if(value.isEmpty())
			return null;
		else
			return Double.parseDouble(value);
	}
	
	public static Timestamp parseTimestamp(String value){
		Date text = null;
		try {
			text = sdf_t.parse(value.replaceAll(QUOTE, EMPTY));
		} catch (ParseException e) {
			_log.error(e.getMessage(), e);
		}
		return new Timestamp(text.getTime());
	}
	
	public static String UnQuote(String value){
		return value.replaceAll(QUOTE, EMPTY);
	}
	
	public static String formatTimestamp(Timestamp ts){
		return sdf_t.format(ts);
	}
	
	public static Date parseDate(String value){
		value = value.replace(QUOTE, EMPTY);
		if(value.isEmpty())
			return null;
		else
			try{
				return sdf.parse(value);
			} catch (ParseException pe) {
				_log.error("Unable to parse date " + value);
				_log.error(pe.getMessage(), pe);
				return null;
			}
	}
	
	public static <T> String getHeader(Class<T> clazz, String separator){
		if(clazz == Person.class){
			return getPersonHeader(separator);
		}
		if(clazz == Location.class){
			return getLocationHeader(separator);
		}
		if(clazz == Household.class){
			return getHouseholdHeader(separator);
		}
		if(clazz == Business.class){
			return getBusinessHeader(separator); 
		}
		if(clazz == DistanceRecord.class){
			return getDistanceRecordHeader(separator);
		}
		if(clazz == SimpleDistanceRecord.class){
			return getSimpleDistanceRecordHeader(separator);
		}		
		if(clazz == LocationSubset.class){
			return getLocationSubsetHeader(separator);
		} 
		if(clazz == Step.class){
			return getStepHeader(separator);
		}
		return getGeneralHeader(clazz, separator);
	}


	private static String getStepHeader(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.QUOTE + "created" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "step" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "startyear" + ClassStringUtil.QUOTE);
		return sb.toString();
	}

	public static <T> String getColumnsTypes(Class<T> clazz, String separator){
		if(clazz == Person.class){
			return getPersonColumnTypes(separator);
		}
		if(clazz == Location.class){
			return getLocationColumnTypes(separator);
		}
		if(clazz == Household.class){
			return getHouseholdColumnTypes(separator);
		}
		if(clazz == Business.class){
			return getBusinessColumnTypes(separator);
		}
		if(clazz == DistanceRecord.class){
			return getDistanceRecordTypes(separator);
		}
		if(clazz == LocationSubset.class){
			return getLocationSubsetTypes(separator);
		}
		if(clazz == Step.class){
			return getStepTypes(separator);
		}
		return getGeneralTypes(clazz, separator);
	}

	private static String getStepTypes(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append("Date" + separator);
		sb.append("Integer" + separator);
		sb.append("Integer");
		return sb.toString();
	}

	private static String getLocationSubsetTypes(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append("Integer" + separator + SPACE);
		sb.append("String" + separator + SPACE);
		for(int i = 0; i < LocationSubset.COLUMNS_COUNT; i++){
			sb.append("String");
			sb.append(separator + SPACE);
		}
		for(int i = 0; i < LocationSubset.COLUMNS_COUNT; i++){
			sb.append("String");
			if(i < LocationSubset.COLUMNS_COUNT - 1)
				sb.append(separator + SPACE);
		}
		return sb.toString();
	}
	
	private static String getLocationSubsetHeader(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.QUOTE + "locid" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "denot" + ClassStringUtil.QUOTE + separator);
		for(int i = 0; i < LocationSubset.COLUMNS_COUNT; i++){
			sb.append(ClassStringUtil.QUOTE + String.format("locsubseth_%02d", i) + ClassStringUtil.QUOTE);
			sb.append(separator);
		}
		for(int i = 0; i < LocationSubset.COLUMNS_COUNT; i++){
			sb.append(ClassStringUtil.QUOTE + String.format("locsubsetb_%02d", i) + ClassStringUtil.QUOTE);
			if(i < LocationSubset.COLUMNS_COUNT - 1)
				sb.append(separator);
		}
		return sb.toString();
	}

	private static String getBusinessHeader(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.QUOTE + "business_id" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "location_id" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "type_1" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "type_2" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "type_3" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "income" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "run" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "dfoundation" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "dclosing" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "nr_of_jobs" + ClassStringUtil.QUOTE);
		return sb.toString();
	}
	
	private static String getBusinessColumnTypes(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append("Integer" + separator + SPACE); 
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Date" + separator + SPACE); 
		sb.append("Date" + separator + SPACE);
		sb.append("Integer");
		return sb.toString();
	}
	
	private static String getPersonHeader(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.QUOTE + "person_id" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "run" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "business_id" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "household_id" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "position_in_hh" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "position_in_bus" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "partnership_since" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "employed_since" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "workingstatus" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "sex" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "income" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "dbirth" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "status" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "ddeath" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "education" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "mother_id" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "father_id" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "decissionNumber" + ClassStringUtil.QUOTE);
		return sb.toString();
	}
	
	private static String getPersonColumnTypes(String separator){
		StringBuilder sb = new StringBuilder();
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Date" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Date" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer");
		return sb.toString();
	}

	private static String getHouseholdHeader(String separator){
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.QUOTE + "household_id" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "run" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "location_id" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "dfoundation" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "dclosing" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "type_1" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "type_2" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "type_3" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "partner_id_a" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "partner_id_b" + ClassStringUtil.QUOTE);
		return sb.toString();
	}
	
	private static String getHouseholdColumnTypes(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Date" + separator + SPACE);
		sb.append("Date" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer");
		return sb.toString();
	}

	private static String getLocationHeader(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.QUOTE + "locid" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "denot" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "subarea_nr" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "urban_centre" + ClassStringUtil.QUOTE);
		return sb.toString();
	}
	
	@SuppressWarnings("unused")
	private static String getLocationHeaderOld(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.QUOTE + "locid" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "denot" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "municipality_nr" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "subarea_nr" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "run" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "res_tot" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "emp_tot" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "settlement_area" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "flat_rent" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "urban_centre" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "diversity_sector" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "motorway_access" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "railway_access" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "resident_landprice_n" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "landuse_density_n" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "university_degree_n" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "diversity_sector_n" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "tax_holdingcomp_n" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "tax_partnership_n" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "tax_companies_n" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "accessibility_t_n" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "bus_dev_cant_n" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "accessibility_res" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "bus_dev_mun_n" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "av_1" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "av_2" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "av_3" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "av_4" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "av_5" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "av_6" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "landtype" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "maxfloorareares" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "usedfloorareares" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "maxfloorareawrk" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "usedfloorareawrk" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "maxfloorareaall" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "usedfloorareaall" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "locsubseth" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "locsubsetb" + ClassStringUtil.QUOTE);
		return sb.toString();
	}
	
	private static String getLocationColumnTypes(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append("Integer" + separator);
		sb.append("String" + separator);
		sb.append("Integer" + separator);
		sb.append("Integer");
		return sb.toString();
	}	
	
	@SuppressWarnings("unused")
	private static String getLocationColumnTypesOld(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append("Integer" + separator);
		sb.append("String" + separator);
		sb.append("Integer" + separator);
		sb.append("Integer" + separator);
		sb.append("Integer" + separator);
		sb.append("Integer" + separator);
		sb.append("Integer" + separator);
		sb.append("Integer" + separator);
		sb.append("Integer" + separator);
		sb.append("Integer" + separator);
		sb.append("Double" + separator);
		sb.append("Integer" + separator);
		sb.append("Integer" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Integer" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Integer" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("Double" + separator);
		sb.append("String" + separator);
		sb.append("String");
		return sb.toString();
	}
	
	private static String getDistanceRecordHeader(String separator){
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.QUOTE + "location_a_id" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "location_b_id" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "distance_1" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "distance_2" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "distance_3" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "distance_4" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "distance_5" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "distance_6" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "distance_7" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "distance_8" + ClassStringUtil.QUOTE);
		return sb.toString();
	}
		
	private static String getSimpleDistanceRecordHeader(String separator){
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.QUOTE + "from" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "to" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "km" + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + "min" + ClassStringUtil.QUOTE);
		return sb.toString();
	}
	
	private static String getDistanceRecordTypes(String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append("Integer" + separator + SPACE);
		sb.append("Integer" + separator + SPACE);
		sb.append("Double" + separator + SPACE);
		sb.append("Double" + separator + SPACE);
		sb.append("Double" + separator + SPACE);
		sb.append("Double" + separator + SPACE);
		sb.append("Double" + separator + SPACE);
		sb.append("Double");		
		return sb.toString();
	}

	private static <T> String getGeneralHeader(Class<T> clazz, String separator){
		StringBuilder sb = new StringBuilder();
		ClassDescriptor cd = ClassDescriptor.getClassDescriptor(clazz);
		for(int i = 0; i < cd.getFields().size(); i++){
			FieldDescriptor fd = cd.getFields().get(i);
			sb.append(ClassStringUtil.QUOTE + fd.getFieldName() + ClassStringUtil.QUOTE);
			if(i < cd.getFields().size() - 1)
				sb.append(separator);
		}
		return sb.toString();
	}
	
	private static <T> String getGeneralTypes(Class<T> clazz, String separator){
		StringBuilder sb = new StringBuilder();
		ClassDescriptor cd = ClassDescriptor.getClassDescriptor(clazz);
		for(int i = 0; i < cd.getFields().size(); i++){
			FieldDescriptor fd = cd.getFields().get(i);
			if(fd.getFieldClass() == Double.class || 
				fd.getFieldClass() == double.class ||
				fd.getFieldClass() == Float.class || 
				fd.getFieldClass() == float.class)
				sb.append("Double");
			else if(fd.getFieldClass() == Integer.class ||
				fd.getFieldClass() == int.class ||
				fd.getFieldClass() == Long.class ||
				fd.getFieldClass() == long.class)
				sb.append("Integer");
			else if(fd.getFieldClass() == Date.class)
				sb.append("Date");
			else 
				sb.append("String");
			
			if(i < cd.getFields().size() - 1)
				sb.append(separator);
		}
		return sb.toString();
	}
	
	public static String checkNull(final Object object){
		return (object == null) ? EMPTY : object.toString();
	}
}
