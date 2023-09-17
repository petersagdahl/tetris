package game;


//highscorer is a users chosen name and his/her score
public class HighScorer implements Comparable<HighScorer> {
	
	//the two variables, which cannot be accessed directly
	private int score;
	private String name;
	
	//the constructor requires the score and a name
	public HighScorer(int score, String name)
	{
		setScore(score);
		setName(name);
	}

	//the two public setters
	public void setName(String name) {

		//throws an illegalargumentexception if the name is too long			
		if (name == null || name.isEmpty()) 
			throw new IllegalArgumentException("The name cannot be empty or null.");
		else if (name.length()>20)
			throw new IllegalArgumentException("The name is too long");
		this.name = name;
	}
	public void setScore(int score) {
		if(score<0)
			throw new IllegalArgumentException("The score cannot be less than 0");
		this.score = score;
	}
	//the two public getters
	public String getName() {
		return name;
	}
	public int getScore() {
		return score;
	}

	//formating
	 @Override
	public String toString() {

		return name+":   "+ score;
	}
	 
	//compareTo is part of the Comparable interface, and makes it possible to sort the Highscorers
	//here HighScorers will be sorted by score first, and name if the scores are equal.
	@Override
	public int compareTo(HighScorer o) {
		if (o.getScore()-this.getScore() == 0)
			return this.getName().compareTo(o.getName());
		
		return o.getScore()-this.getScore();
	}

}
