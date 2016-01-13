package org.falcsim.agentmodel.dao.csv;

/**
 * CSV database access parameters
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
public class CsvProperties {
	private String path;
	private String defaultSchema;
	private String networksSchema;
	private String distancesSchema;
	private String columnsSeparator;
	private String schemaDelimiter;
	private String charset;
	
	private String tableExtension;
	private String columnDefinitionExtension;
	private String temporaryTableExtension;
	private String dateFormat;
	private String timestampFormat;
	private String decimalSeparator;
	
	public String getDefaultSchema() {
		return defaultSchema;
	}
	
	public void setDefaultSchema(String defaultSchema) {
		this.defaultSchema = defaultSchema;
	}
	
	public String getNetworksSchema() {
		return networksSchema;
	}

	public void setNetworksSchema(String networksSchema) {
		this.networksSchema = networksSchema;
	}

	public String getDistancesSchema() {
		return distancesSchema;
	}

	public void setDistancesSchema(String distancesSchema) {
		this.distancesSchema = distancesSchema;
	}
	
	public String getColumnsSeparator() {
		return columnsSeparator;
	}
	
	public void setColumnsSeparator(String columnsSeparator) {
		this.columnsSeparator = columnsSeparator;
	}
	
	public String getSchemaDelimiter() {
		return schemaDelimiter;
	}
	
	public void setSchemaDelimiter(String schemaDelimiter) {
		this.schemaDelimiter = schemaDelimiter;
	}
	
	public String getCharset() {
		return charset;
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getTableExtension() {
		return tableExtension;
	}

	public void setTableExtension(String tableExtension) {
		this.tableExtension = tableExtension;
	}

	public String getColumnDefinitionExtension() {
		return columnDefinitionExtension;
	}

	public void setColumnDefinitionExtension(String columnDefinitionExtension) {
		this.columnDefinitionExtension = columnDefinitionExtension;
	}

	public String getTemporaryTableExtension() {
		return temporaryTableExtension;
	}

	public void setTemporaryTableExtension(String temporaryTableExtension) {
		this.temporaryTableExtension = temporaryTableExtension;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getTimestampFormat() {
		return timestampFormat;
	}

	public void setTimestampFormat(String timestampFormat) {
		this.timestampFormat = timestampFormat;
	}

	public String getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(String decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}
}
