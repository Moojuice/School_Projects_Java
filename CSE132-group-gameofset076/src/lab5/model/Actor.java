package lab5.model;

abstract public class Actor extends StatusChanger {

	protected final String name;
	
	/**
	 * Manage reporting of actions via PCS.
	 * @param name person taking action
	 */
	public Actor(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see weasley.model.StatusChanger#getName()
	 */
	public String getName() {
		return name;
	}
	
}