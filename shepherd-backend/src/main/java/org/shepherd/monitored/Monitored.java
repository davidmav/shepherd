package org.shepherd.monitored;

/**
 * 
 * @author DavidM
 *
 */
public interface Monitored {

	/**
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Tests the validity of the settings, i.e. in case of JMX, if a connection can be established with the process
	 * 
	 * @since Jan 1, 2015
	 * @author davidm
	 * @return
	 * @throws MonitoredException
	 *             if a connection cannot be established
	 */
	public boolean test() throws MonitoredException;

}
