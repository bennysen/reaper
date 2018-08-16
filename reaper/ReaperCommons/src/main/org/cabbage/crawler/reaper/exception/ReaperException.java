package org.cabbage.crawler.reaper.exception;

public class ReaperException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4847751169139903406L;

	private Long errorCode = -1L;

	public ReaperException() {
		super();
	}

	public ReaperException(String message) {
		super(message);
	}

	public ReaperException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReaperException(Throwable cause) {
		super(cause);
	}

	public ReaperException(Long errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public ReaperException(String message, Long errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public ReaperException(String message, Throwable cause, Long errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public ReaperException(Throwable cause, Long errorCode) {
		super(cause);
		this.errorCode = errorCode;
	}
	
	public Long getErrorCode() {
		return errorCode;
	}

}
