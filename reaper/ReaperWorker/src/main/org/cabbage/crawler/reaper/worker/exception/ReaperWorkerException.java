package org.cabbage.crawler.reaper.worker.exception;

public class ReaperWorkerException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -749772219105842290L;
	
	private Long errorCode = -1L;

	public ReaperWorkerException() {
		super();
	}

	public ReaperWorkerException(String message) {
		super(message);
	}

	public ReaperWorkerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReaperWorkerException(Throwable cause) {
		super(cause);
	}

	public ReaperWorkerException(Long errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public ReaperWorkerException(String message, Long errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public ReaperWorkerException(String message, Throwable cause, Long errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public ReaperWorkerException(Throwable cause, Long errorCode) {
		super(cause);
		this.errorCode = errorCode;
	}
	
	public Long getErrorCode() {
		return errorCode;
	}

}
