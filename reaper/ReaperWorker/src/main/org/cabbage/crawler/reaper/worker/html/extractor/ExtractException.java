package org.cabbage.crawler.reaper.worker.html.extractor;

/**
 * 内容抽取中可能出现的异常信息
 * @author Stream
 *
 */
public class ExtractException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2489905499773738484L;

	public ExtractException() {
	}

	public ExtractException(String arg0) {
		super(arg0);
	}

	public ExtractException(Throwable arg0) {
		super(arg0);
	}

	public ExtractException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
