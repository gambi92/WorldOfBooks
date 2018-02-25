package hu.gambino.data;

public class ResponseHeader {
	private Long lineNumber;
	private ResponseStatus status;
	private String message;
	
	
	public ResponseHeader(Long lineNumber, ResponseStatus status, String message) {
		this.lineNumber = lineNumber;
		this.status = status;
		this.message = message;
	}


	public Long getLineNumber() {
		return lineNumber;
	}


	public ResponseStatus getStatus() {
		return status;
	}


	public String getMessage() {
		return message;
	}
	
	

}
