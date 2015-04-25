package util;

public class StopWatch {
	
	public StopWatch() {
		
	}
	
	public void start() {
		startTime = System.currentTimeMillis();
		isRunning = true;
	}
	
	public long stop() {
		stopTime = System.currentTimeMillis();
		isRunning = false;
		
		return stopTime - startTime;
	}
	
	public long getElapsedMillis() {
		if(isRunning)
			return System.currentTimeMillis() - startTime;
		return stopTime - startTime;
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	private boolean isRunning = false;
	private long startTime;
	private long stopTime;

}
