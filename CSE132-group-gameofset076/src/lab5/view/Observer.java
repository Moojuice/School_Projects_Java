package lab5.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import lab5.model.StatusChanger;


/**
 * An observer of a subject's status change.
 * @author cytron
 *
 */
public class Observer {

	private final String name;

	public Observer(String name) {
		this.name = name;
	}

	/**
	 * Observe the subject only when the specified event (status change) occurs,
	 *   reacting by printing out a message.
	 * @param subject person who acted
	 * @param event action
	 */
	public void addSubject(final StatusChanger subject, final String event) {

		Runnable r = new Runnable() {
			public void run() {
				System.out.println("   " 
						+ name  					// Observer's name
						+ ": I see that " 
						+ subject + " sent msg " 	// Subject's name
						+ event);					// Subject's action
			}
		};

		register(subject.getPCS(), event, r);
	}

	/**
	 * Helper method to cause the provided Runnable to execute when the
	 *   specified pcs's event occurs.
	 * @param pcs PCS object
	 * @param event new location
	 * @param r Runnable object
	 */
	private void register(PropertyChangeSupport pcs, String event, final Runnable r) {
		//
		// Use the method to add listeners for a particular event
		//
		pcs.addPropertyChangeListener(
				event,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent arg0) {
						r.run();

					}

				}
		);
	}

	/**
	 * Observe the subject for *any* status change
	 * @param subject
	 */
	public void addObsession(final StatusChanger subject) {
		//
		// Use the method to add listeners for *all* events
		//
		subject.getPCS().addPropertyChangeListener(
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						System.out.println("   " 
								+ name 
								+ ": watching over " 
								+ subject
								+ " who sent msg " 
								+ evt.getPropertyName());

					}

				}
		);
	}


}
