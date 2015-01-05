package org.shepherd.monitored;

/**
 * Monitored interface, implementation must have at least 1 constructor with (String id) annotated with @UICreationPoint, id must
 * be first arg. //TODO - we need to get rid of this constraint - will be redesigned.
 * 
 * @author DavidM
 *
 */
public interface Monitored {

	/**
	 * A unique identifier for the Monitored Application
	 * 
	 * @since Jan 4, 2015
	 * @author davidm
	 * @return
	 */
	public String getId();

	/**
	 * A name for the Monitored Application, not unique
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
