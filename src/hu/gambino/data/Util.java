package hu.gambino.data;

import java.io.FileInputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.net.ftp.FTPClient;

import hu.gambino.Start;

public class Util {

	// Checks if the given parameter has a valid email format [it's not thorough]
	public static boolean isValidEmail(String email) {
		return (email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}"));
	}

	// Checks if the given parameter (which is a splitted line from the input file) has any empty values in it 
	public static boolean hasEmptyString(String[] textArray) {
		for (int i = 0; i < textArray.length; i++) {
			if (textArray[i] == null || textArray[i].equals(""))
				return true;
		}
		return false;
	}

	// Gets the current date and converts it into java.sql.Date format
	public static Date getCurrentDate() {
		java.util.Date currentDate = new java.util.Date();
		return new Date(currentDate.getTime());
	}

	// Checks if the given parameter is a valid date
	public static boolean isValidDate(String date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			simpleDateFormat.parse(date);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	// Checks if the given parameter is a valid integer
	public static boolean isInteger(String text) {
		try {
			Integer.parseInt(text);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	// checks if the given parameter is a valid Double and that it is smaller or equal than the second parameter
	public static boolean isDecimal(String text, Double minValue) {
		Double tmp;
		try {
			tmp = Double.parseDouble(text);
		} catch (Exception e) {
			return false;
		}

		if (tmp >= minValue)
			return true;
		else
			return false;
	}

	// Check the given parameter if it can be a valid Enumerated type
	public static boolean isValidStatus(String text) {
		try {
			OrderItemStatus.valueOf(text);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	// Uploads the given file to an FTP server
	public static void uploadFileIntoFTP(String file) {
		FTPClient ftpClient = new FTPClient();
		FileInputStream fileInputStream = null;

		try {
			fileInputStream = new FileInputStream(file);
			ftpClient.connect("127.0.0.1");
			ftpClient.login("admin", "006554");
			ftpClient.storeFile(file, fileInputStream);
			ftpClient.logout();
		} catch (Exception e) {
			try {
				Start.getFileDAO().addToResponseFile(new ResponseHeader((long) -1, ResponseStatus.ERROR,
						"The response file couldn't be uploaded to the given FTP server!"));
			} catch (Exception e1) {
				System.out.println("An error occured while trying to write in to the response file!");
			}
		}
		
		try {
			fileInputStream.close();
			ftpClient.disconnect();
		} catch (Exception e) {
		}
	}

}
