/**
 * 
 */
package br.com.tarlis.mov3lets.utils;

/**
 * The Class LoggerAdapter.
 *
 * @author tarlis
 */
public abstract class LoggerAdapter {
	
	/**
	 * Trace.
	 *
	 * @param s the s
	 */
	public abstract void trace(String s);
	
	/**
	 * Prints the timer.
	 *
	 * @param timer the timer
	 * @param time the time
	 */
	public synchronized void printTimer(String timer, long time) {
		trace(timer + ": " + time + " milliseconds");
	}
	
	/**
	 * Trace W.
	 *
	 * @param s the s
	 */
	public synchronized void traceW(String s) {
		trace("Warning: " + s);
	}
	
	/**
	 * Trace E.
	 *
	 * @param s the s
	 * @param e the e
	 */
	public synchronized void traceE(String s, Exception e) {
		trace("\n[Error] " + s);
//		String stacktrace = ExceptionUtils.getStackTrace(e);
//        trace(stacktrace);
		e.printStackTrace();
	}

	/**
	 * Prints the memory.
	 */
	public void printMemory() {
		System.gc();
		Runtime rt = Runtime.getRuntime();
		double total = rt.totalMemory() / (1024.0 * 1024.0);
		double free = rt.freeMemory() / (1024.0 * 1024.0);
        double used = (total - free);
        
		trace("Memory Usage (MiB), Memory Total: "+total+". Memory Free: "+free+". Memory Used: "+used+".");
	}

}
