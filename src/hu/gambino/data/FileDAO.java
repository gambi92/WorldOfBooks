package hu.gambino.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import hu.gambino.Start;

public class FileDAO {

	public FileDAO() {
	}

	// Reads the input file lines and uploads it into the database if it is met
	// certain conditions
	public void getInputsOneByOne() {
		FileReader fReader = null;
		BufferedReader bReader = null;
		try {
			fReader = new FileReader(Start.inputFilePath);
			bReader = new BufferedReader(fReader);

			String line;
			while ((line = bReader.readLine()) != null) {
				String[] splittedLine = line.split(Start.dataSeparator);

				if (Util.processData(splittedLine))
					Start.getDatabaseDAO()
							.addRecord(new InputHeader(Long.decode(splittedLine[0]), Long.decode(splittedLine[1]),
									Long.decode(splittedLine[2]), splittedLine[3], splittedLine[4], splittedLine[5],
									Integer.decode(splittedLine[6]), Double.valueOf(splittedLine[7]),
									Double.valueOf(splittedLine[8]), splittedLine[9],
									OrderItemStatus.valueOf(splittedLine[10]), Util.date));

			}
		} catch (Exception e) {
			System.out.println("An error has occured while trying to read the input file!");
			e.printStackTrace();
		} finally {
			if (bReader != null)
				try {
					bReader.close();
				} catch (IOException e) {
					System.out.println("An error has occured while trying to close the input file!");
				}
			if (fReader != null)
				try {
					fReader.close();
				} catch (IOException e) {
					System.out.println("An error has occured while trying to close the input file!");
				}
		}

	}

	// Adds a new line into the response file
	public void addToResponseFile(ResponseHeader response) {
		FileWriter fWriter = null;
		BufferedWriter bWriter = null;
		try {
			fWriter = new FileWriter(Start.responseFilePath, true);
			bWriter = new BufferedWriter(fWriter);
			bWriter.write(response.getLineNumber() + Start.dataSeparator + response.getStatus() + Start.dataSeparator
					+ response.getMessage());
			bWriter.newLine();
		} catch (IOException e) {
			System.out.println("An error has occured while trying to write to the response file!");
		} finally {
			if (bWriter != null)
				try {
					bWriter.close();
				} catch (IOException e) {
					System.out.println("An error has occured while trying to close the buffered output stream!");
				}
			if (fWriter != null)
				try {
					fWriter.close();
				} catch (IOException e) {
					System.out.println("An error has occured while trying to close the output stream!");
				}
		}

	}

}
