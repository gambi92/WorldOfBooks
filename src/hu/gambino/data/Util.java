package hu.gambino.data;

import java.io.FileInputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.net.ftp.FTPClient;

import hu.gambino.Start;

public class Util {
	public static Date date=null;

	// Checks if the given data matches the predefined circumstances
	public static boolean processData(String[] data) {
		if (data != null && data.length >= Start.minHeaderCount) {
			if (!Util.hasEmptyString(data)) {
				if (Util.isValidEmail(data[4])) {
					//Date date = null;
					boolean validDate = true;
					if (data.length == Start.minHeaderCount) {
						date = Util.getCurrentDate();
					} else {
						if (Util.isValidDate(data[Start.minHeaderCount]))
							date = Date.valueOf(data[Start.minHeaderCount]);
						else {
							Start.getFileDAO().addToResponseFile(new ResponseHeader(Long.decode(data[0]),
									ResponseStatus.ERROR, "The line has an invalid date format!"));
							validDate = false;
						}
					}
					if (validDate) {
						if (Util.isInteger(data[6])) {
							if (Util.isDecimal(data[7], 0.0) && Util.isDecimal(data[8], 1.0)) {
								if (Util.isValidStatus(data[10])) {
									// This line I had to comment because the program wouldn't work properly
									// (logically).
									// If there is more <order_item> connected into the same <order> then in the
									// inputFile it has to be records that has the same OrderId
									// if (!Start.getDatabaseDAO().orderIdExist(Long.decode(data[2]))) {
									if (!Start.getDatabaseDAO().orderItemIdExist(Long.decode(data[1]))) {
										return true;
									} else
										Start.getFileDAO().addToResponseFile(
												new ResponseHeader(Long.decode(data[0]), ResponseStatus.ERROR,
														"This orderItemId already exist in the database!"));
									/*
									 * } else Start.getFileDAO().addToResponseFile(new
									 * ResponseHeader(Long.decode(data[0]), ResponseStatus.ERROR,
									 * "This orderId already exist in the database!"));
									 */
								} else
									Start.getFileDAO().addToResponseFile(new ResponseHeader(Long.decode(data[0]),
											ResponseStatus.ERROR, "The line has an invalid status!"));
							} else
								Start.getFileDAO().addToResponseFile(new ResponseHeader(Long.decode(data[0]),
										ResponseStatus.ERROR, "The line has an invalid shipping price or sale price!"));
						} else
							Start.getFileDAO().addToResponseFile(new ResponseHeader(Long.decode(data[0]),
									ResponseStatus.ERROR, "The line has an invalid postcode!"));
					}
				} else
					Start.getFileDAO().addToResponseFile(new ResponseHeader(Long.decode(data[0]), ResponseStatus.ERROR,
							"The line has an invalid email address!"));
			} else
				Start.getFileDAO().addToResponseFile(
						new ResponseHeader(Long.decode(data[0]), ResponseStatus.ERROR, "The line has an empty field!"));
		} else
			Start.getFileDAO().addToResponseFile(new ResponseHeader(Long.decode(data[0]), ResponseStatus.ERROR,
					"The line doesn't have the required amount of data!"));
		return false;
	}

	// Checks if the given parameter has a valid email format [it's not thorough]
	public static boolean isValidEmail(String email) {
		return (email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}"));
	}

	// Checks if the given parameter (which is a splitted line from the input file)
	// has any empty values in it
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

	// checks if the given parameter is a valid Double and that it is smaller or
	// equal than the second parameter
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
