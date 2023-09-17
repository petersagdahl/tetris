package helpers;

public enum FileNames {
	
	
	//the two files used should always be called this.
	//if you would want to change the filename, you do it here.
	HS_FILE {
		public String toString()
		{
			return "Highscore.txt";
		}
	},
	
	SAVE_FILE {
		public String toString()
		{
			return "Last_Game.txt";
		}
	}
	
}
