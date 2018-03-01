package hu.gambino.data;

import java.sql.PreparedStatement;

public class PreparedString {

	private String preparedStatementString;
	private PreparedStatement preparedStatement;
	
	public PreparedString(String preparedStatementString) {
		this.preparedStatementString=preparedStatementString;
		preparedStatement=null;
	}

	public String getPreparedStatementString() {
		return preparedStatementString;
	}

	public PreparedStatement getPreparedStatement() {
		return preparedStatement;
	}

	public void setPreparedStatement(PreparedStatement preparedStatement) {
		this.preparedStatement = preparedStatement;
	}
	
	
}
