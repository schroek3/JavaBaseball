
public class Pitcher extends Player {
	private double gameIP;
	private int gameRuns;
	private int gameHits;
	private int gameWalks;
	private double seasonIP;
	private int seasonW;
	private int seasonD;
	private int seasonL;
	private int seasonRuns;
	private int seasonHits;
	private int seasonWalks;
	private double RAA;
	
	/*public Pitchers(){
		super();
	}
	
	public Pitchers(String n){
		super(n);
	}
	*/
	
	public String pitcherCSV(){
		return getPaddedName()+","+super.getTeam()+","+seasonW+","+seasonD+","+seasonL+","+seasonRuns+","+seasonHits+","+seasonWalks;
	}
	
	public void setRAA(){
		RAA = 9*(double)seasonRuns/seasonIP;
	}
	
	public double getRAA(){
		return RAA;
	}
	
	public void incrementWins(){
		seasonW++;
	}
	public int getWin(){
		return seasonW;
	}
	public int getDraw(){
		return seasonD;
	}
	public int getLoss(){
		return seasonL;
	}
	
	public void incrementLosses(){
		seasonL++;
	}
	
	public void incrementDraws(){
		seasonD++;
	}
	
	public void addIP(double n){
		seasonIP += n;
	}
	
	public double getIP(){
		return seasonIP;
	}
	
	public void addToSeason(){
		seasonIP += gameIP;
		seasonRuns += gameRuns;
		seasonHits += gameHits;
		seasonWalks += gameWalks;
		
	}
}
