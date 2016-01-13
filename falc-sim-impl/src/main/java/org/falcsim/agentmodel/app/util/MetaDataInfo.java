package org.falcsim.agentmodel.app.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.dao.GeneralDao;

public class MetaDataInfo {
	private static Logger logger =  Logger.getLogger(MetaDataInfo.class);
	

	public static void createMetadataInfoFileGeneral(String content, String filename){
		try{
			saveFile(content, new File(filename));
		}
		catch(Exception e){
			logger.error(e);
			e.printStackTrace();
		}		
	}	
	
	public static String getTableDirectory(String table){
		GeneralDao generalDao = AppCtxProvider.getApplicationContext().getBean(GeneralDao.class);
		String locationCSVfile = generalDao.getTableFilePath(table);
		return new File(locationCSVfile).getParent();
	}
	
	public static void createMetadataInfoFileSynthese(String content, String table){
		try{
			GeneralDao generalDao = AppCtxProvider.getApplicationContext().getBean(GeneralDao.class);
			String locationCSVfile = generalDao.getTableFilePath(table);
			storeContent(content, locationCSVfile,".info.txt");
		}
		catch(Exception e){
			logger.error(e);
			e.printStackTrace();
		}		
	}

	@SuppressWarnings("rawtypes")
	public static String getTableDirectory(Class clazz){
		GeneralDao generalDao = AppCtxProvider.getApplicationContext().getBean(GeneralDao.class);
		@SuppressWarnings("unchecked")
		String locationCSVfile = generalDao.getTableFilePath(clazz);
		return new File(locationCSVfile).getParent();
	}	
	
	@SuppressWarnings("rawtypes")
	public static void createMetadataInfoFileSynthese(String content, Class clazz){
		try{
			GeneralDao generalDao = AppCtxProvider.getApplicationContext().getBean(GeneralDao.class);			
			@SuppressWarnings("unchecked")
			String locationCSVfile = generalDao.getTableFilePath(clazz);
			storeContent(content, locationCSVfile, ".info.txt");
		}
		catch(Exception e){
			logger.error(e);
			e.printStackTrace();
		}		
	}
	
	private static void storeContent(String content, String locationCSVfile, String filename) throws IOException{
		logger.debug(locationCSVfile);
		File f = new File(locationCSVfile);
		if(f.exists()){
			File infof = new File(f.getParent(), filename);
			saveFile(content, infof);
		}
	}
	
	private static void saveFile(String content, File infof) throws IOException{
		Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(infof, false), "UTF-8"));
		try{
			bw.append(content);
		}
		finally{
			bw.flush();
			bw.close();
		}
		logger.info(infof.getAbsolutePath() + " written to storage");
	}	
}
