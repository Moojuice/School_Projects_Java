package lab5.model;
/** Player
 * 	Represents each player, with a name, id and score. 
 * 
 * @author Premade
 *   Used in the lab by (not edited): Ben Epstein, Jeremy Tang, Edward Xie, Kyle Liu
 *   Course: CSE 132
 *   Lab:    5
 */

public class Player extends Actor {
	private String name; 	// name of the Player
	private int ID;			// player's ID
	private int score;   	// player's score in game
	
	/** initializes players name, id, and score
	 * @param name name of the Player
	 */
	public Player(String name, int ID) {
		super(name);
		this.name = name;
		this.ID = ID;
		score = 0;
	}
	
	/**
	 * Accessor for name.
	 * @return name of the Player
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Accessor for score.
	 * @return score of the Player
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * Accessor for player's ID.
	 * @return ID
	 */
	public synchronized int getID() {
		return ID;
	}
	
	/**
	 * Mutator for score.
	 */
	public void setScore(int score) {
		this.score = score;
		status(""+score);
	}
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (ID != other.ID)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

}
