package org.falcsim.agentmodel.dao.jdbc.csv;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.apache.log4j.Logger;

/**
 * Encapsulates CSV transaction with set of temporary files 
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
public class CsvTrasaction {
	
	private static final Logger _log = Logger.getLogger(CsvTrasaction.class);
	
	private static ConcurrentLinkedDeque<String> transactionCsv = 
			new ConcurrentLinkedDeque<String>();

		/**
		 * This method adds a file name into the queue. The file 
		 * 
		 * the filename has to be without extension with the full path!
		 * 
		 * @param tempFileName file name is the same for temp file and csv file
		 */
		public static void addToTransaction(String tempFileName){
			_log.debug(tempFileName + " was added to transaction");
			CsvTrasaction.transactionCsv.add(tempFileName);
		}
		
		/**
		 * This method replaces the first csv file in the queue with 
		 * the temporary file
		 * @throws RuntimeException if it's not possible to delete csv file
		 */
		public static void commitFirst() throws RuntimeException {
			String fileName = CsvTrasaction.transactionCsv.pollFirst();
			if(fileName == null){
				_log.warn("Nothing else to persist...");
				return;
			}
			CsvTrasaction.doRename(fileName);
			
		}
		
		/**
		 * While queue contains entries the method commitFirst is executed
		 * @see commitFirst
		 * @throws RuntimeException
		 */
		public static void commitAll() throws RuntimeException {
			while(!CsvTrasaction.transactionCsv.isEmpty()){
				CsvTrasaction.commitFirst();
			}
		}
		
		public static void commitLast() throws RuntimeException{
			String fileName = CsvTrasaction.transactionCsv.pollLast();
			if(fileName == null){
				_log.warn("Nothing else to persist...");
				return;
			}
			CsvTrasaction.doRename(fileName);
		}
		
		private static void doRename(String fileName){
			File oldFile = new File(fileName + CsvUpdateOperations.CSV_EXTENSION);
			File newFile = new File(fileName + CsvUpdateOperations.TEMPORARY_EXTENSION);
			if(oldFile.exists()){
				oldFile.delete();
				_log.info("Deleting table " +  oldFile.toString() + " and replacing with " + newFile.toString());
			}
			if(newFile.exists())
				newFile.renameTo(oldFile);
		}
}
