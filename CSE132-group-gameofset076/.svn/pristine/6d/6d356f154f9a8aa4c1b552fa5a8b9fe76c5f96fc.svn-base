package lab5.model;

import java.beans.PropertyChangeSupport;

/**
 * A class to propagate status changes via firePropertyChange.
 * @author cytron
 */

abstract public class StatusChanger {

	public static boolean logging = false;
	final protected PropertyChangeSupport pcs;
	
	/**
	 * Allocate new PropertyChangeSupport object.
	 */
	public StatusChanger() {
		pcs = new PropertyChangeSupport(this);
	}
	
	/**
	 * Determine the name of the player.
	 * @return name of player
	 */
	abstract public String getName();

	/**
	 * Accessor for PCS object.
	 * @return PCS object
	 */
	public PropertyChangeSupport getPCS() {
		return pcs;
	}

	/**
	 * Announce the message.
	 * @param message new location
	 */
	public void status(String message) {
		if (logging) {
			System.out.println(getName() + " sent msg " + message);
		}
		//
		// The use of null below pushes out the message no matter what
		//   You could use the second and third parameters to
		//   cause a message to be pushed only if something has
		//   changed.
		//
		pcs.firePropertyChange(message, null, null);

	}

}