package common.doer;

public class StopIteration extends RuntimeException {
	
	private Object lastly = null;

	public StopIteration() {
		super();
	}
	
	public StopIteration(Object lastResult) {
		lastly = lastResult;
	}

	public StopIteration(String message, Throwable cause) {
		super(message, cause);
	}

	public StopIteration(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	
	public Object lastResult() {
		return lastly;
	}

}
