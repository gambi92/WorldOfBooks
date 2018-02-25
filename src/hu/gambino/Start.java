package hu.gambino;

import hu.gambino.data.DatabaseDAO;
import hu.gambino.data.FileDAO;
import hu.gambino.data.ResponseHeader;
import hu.gambino.data.ResponseStatus;
import hu.gambino.data.Util;

public class Start {

	// Path of Database property file
	public static final String databaseConnectInfoFile = "dbinfo.cfg";

	// Files path
	public static final String inputFilePath = "testInput.csv";
	public static final String responseFilePath = "testResponse.csv";
	
	// Data separation infos
	public static final String dataSeparator = ";";
	public static final Integer minHeaderCount = 11;

	// Main File DAO
	private static FileDAO fileDAO;

	// Main Database DAO
	private static DatabaseDAO databaseDAO;

	// This function makes sure that there is only one DAO gets used (Singleton)
	public static FileDAO getFileDAO() {
		if (fileDAO == null) {
			fileDAO = new FileDAO();
		}
		return fileDAO;
	}

	// This function makes sure that there is only one DAO gets used (Singleton)
	public static DatabaseDAO getDatabaseDAO() {
		if (databaseDAO == null) {
			databaseDAO = new DatabaseDAO();
		}
		return databaseDAO;
	}
	
	

	/***** MAIN *****/
	public static void main(String[] args) {
		try {
			// Reading the input file
			getFileDAO().getInputsOneByOne();
		} catch (Exception e) {
			try {
				getFileDAO().addToResponseFile(new ResponseHeader((long) -1,
						ResponseStatus.ERROR, e.getMessage()));
			} catch (Exception e1) {
				System.out.println("An error occured while trying to write into the response file!");
			}
		}
		
		Util.uploadFileIntoFTP(responseFilePath);
	}

}
