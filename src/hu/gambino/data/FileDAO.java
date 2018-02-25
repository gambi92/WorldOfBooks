package hu.gambino.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Date;

import hu.gambino.Start;

public class FileDAO {

	public FileDAO() {
	}

	// Reads the input file lines and uploads it into the database if it is met
	// certain conditions
	public void getInputsOneByOne() throws Exception {
		FileReader fReader = new FileReader(Start.inputFilePath);
		BufferedReader bReader = new BufferedReader(fReader);

		String line;
		while ((line = bReader.readLine()) != null) {
			String[] splittedLine = line.split(Start.dataSeparator);

			if (splittedLine != null && splittedLine.length >= Start.minHeaderCount) {
				if (!Util.hasEmptyString(splittedLine)) {
					if (Util.isValidEmail(splittedLine[4])) {
						Date date = null;
						boolean validDate = true;
						if (splittedLine.length == Start.minHeaderCount) {
							date = Util.getCurrentDate();
						} else {
							if (Util.isValidDate(splittedLine[Start.minHeaderCount]))
								date = Date.valueOf(splittedLine[Start.minHeaderCount]);
							else {
								addToResponseFile(new ResponseHeader(Long.decode(splittedLine[0]), ResponseStatus.ERROR,
										"The line has an invalid date format!"));
								validDate = false;
							}
						}
						if (validDate) {
							if (Util.isInteger(splittedLine[6])) {
								if (Util.isDecimal(splittedLine[7], 0.0) && Util.isDecimal(splittedLine[8], 1.0)) {
									if (Util.isValidStatus(splittedLine[10])) {
										// This line I had to comment because the program wouldn't work properly
										// (logically).
										// If there is more <order_item> connected into the same <order> then in the
										// inputFile it has to be records that has the same OrderId
										// if (!Start.getDatabaseDAO().orderIdExist(Long.decode(splittedLine[2]))) {
										if (!Start.getDatabaseDAO().orderItemIdExist(Long.decode(splittedLine[1]))) {
											Start.getDatabaseDAO().addRecord(new InputHeader(
													Long.decode(splittedLine[0]), Long.decode(splittedLine[1]),
													Long.decode(splittedLine[2]), splittedLine[3], splittedLine[4],
													splittedLine[5], Integer.decode(splittedLine[6]),
													Double.valueOf(splittedLine[7]), Double.valueOf(splittedLine[8]),
													splittedLine[9], OrderItemStatus.valueOf(splittedLine[10]), date));
										} else
											addToResponseFile(new ResponseHeader(Long.decode(splittedLine[0]),
													ResponseStatus.ERROR,
													"This orderItemId already exist in the database!"));
										/*
										 * } else addToResponseFile(new ResponseHeader(Long.decode(splittedLine[0]),
										 * ResponseStatus.ERROR, "This orderId already exist in the database!"));
										 */
									} else
										addToResponseFile(new ResponseHeader(Long.decode(splittedLine[0]),
												ResponseStatus.ERROR, "The line has an invalid status!"));
								} else
									addToResponseFile(
											new ResponseHeader(Long.decode(splittedLine[0]), ResponseStatus.ERROR,
													"The line has an invalid shipping price or sale price!"));
							} else
								addToResponseFile(new ResponseHeader(Long.decode(splittedLine[0]), ResponseStatus.ERROR,
										"The line has an invalid postcode!"));
						}
					} else
						addToResponseFile(new ResponseHeader(Long.decode(splittedLine[0]), ResponseStatus.ERROR,
								"The line has an invalid email address!"));
				} else
					addToResponseFile(new ResponseHeader(Long.decode(splittedLine[0]), ResponseStatus.ERROR,
							"The line has an empty field!"));
			} else
				addToResponseFile(new ResponseHeader(Long.decode(splittedLine[0]), ResponseStatus.ERROR,
						"The line doesn't have the required amount of data!"));

		}

		bReader.close();
		fReader.close();
	}

	// Adds a new line into the response file
	public void addToResponseFile(ResponseHeader response) throws Exception {
		FileWriter fWriter = new FileWriter(Start.responseFilePath, true);
		BufferedWriter bWriter = new BufferedWriter(fWriter);

		bWriter.write(response.getLineNumber() + Start.dataSeparator + response.getStatus() + Start.dataSeparator
				+ response.getMessage());
		bWriter.newLine();

		bWriter.close();
		fWriter.close();
	}

}
