import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;


public class Team implements Comparable<Team>{
	private String name;
	private int wins;
	private int draws;
	private int losses;
	private int runsFor;
	private int runsAllowed;
	private LinkedList<Player> lineup = new LinkedList<Player>();
	private LinkedList<Player> rotation = new LinkedList<Player>();
	private Player pitcher;
	private Player leadoff;
	private Player ace;
	
	public Team (){
		name = "";
		wins = 0;
		losses = 0;
		draws = 0;
		runsFor = 0;
		runsAllowed = 0;
	}
	
	public Team(String n){
		name = n;
	}
	
	public Team(int w, int l){
		name = "";
		wins = w;
		losses = l;
	}
	
	public Team (int w, int d, int l){
		name = "";
		wins = w;
		draws = d;
		losses = l;
	}
	
	public Team(String n, int w, int d, int l){
		name = n;
		wins = w;
		draws = d;
		losses = l;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public boolean readTeam(Scanner infile){		
		if (infile.hasNext()){
			//name = infile.nextLine().trim();
			setName(infile.nextLine().trim());
			return true;
		}
		return false;
		}
	
	public void addPlayer(Player p){
		p.setTeam(name);
		lineup.add(p);
		
		}
	
	public void printAtBat(){
		System.out.println(lineup.peek());
	}
	
	public void setLeadoff(){
		leadoff = lineup.peek();
	}
	
	public Player getLeadoff(){
		return leadoff;
	}
	
	public void setAce(){
		ace = rotation.peek();
	}
	
	public void resetRotation(){
		while (rotation.peekFirst() != ace){
			Player dummy = rotation.pop();
			rotation.add(dummy);
		}
	}
	
	public void printLineup(){
		System.out.println("********************************");
		System.out.println("The Line-Up for " + name + ": ");
		System.out.println("********************************");
		Iterator<Player> line = lineup.iterator();
		while(line.hasNext()){
			System.out.println(line.next());
		}
	}
	
	public void clearGameStats(){
		Player dummy = lineup.pop();
		dummy.clearGameStats();
		lineup.add(dummy);
		while (lineup.peekFirst() != leadoff){
			dummy = lineup.pop();
			dummy.clearGameStats();
			lineup.add(dummy);
		}
	}
	
	public void setLineup(){
		while (lineup.peekFirst()!= leadoff){
			Player dummy = lineup.pop();
			lineup.add(dummy);
		}
		clearGameStats();
	}
	
	public Player nextBatter(){
		Player batter = lineup.pop();
		lineup.add(batter);
		return batter;
	}
	
	public Player getBatter(){
		return lineup.peek();
	}
	
	public void setRotation(){
		for (int i = 0; i < 4; i++){
			Player pitch = lineup.pop();
			rotation.add(pitch);
		}
	}
	public void setPitcher(){
		pitcher =  rotation.pop();
		rotation.add(pitcher);
	}
	
	public Player getPitcher(){
		return pitcher;
	}
	
	public int getWins(){
		return wins;
	}
	
	public void setWins(int w){
		wins = w;
	}
	
	public void incrementWins(){
		wins ++;
	}
	
	public int getDraws(){
		return draws;
	}
	
	public void setDraws(int w){
		draws = w;
	}
	
	public void incrementDraws(){
		draws ++;
	}
	
	public int getLosses(){
		return losses;
	}
	
	public void setLosses(int w){
		losses = w;
	}
	
	public void incrementLosses(){
		losses ++;
	}
	
	public int getPoints(){
		return 2*wins+draws;
	}
	public void addRunsFor(int r){
		runsFor += r;
	}
	
	public void addRunsAllowed(int r){
		runsAllowed += r;
	}
	
	public String toString(){
		return name + " " + wins +"-"+draws+"-"+losses+", "+ getPoints() +" points";
	}
	
	public String toStandingsString(){
		String winString;
		String drawString;
		String lossString;
		String pointString;
		String runsForString;
		String runsAllString;
		if (wins < 10){
			winString = "   " + wins;
		}else if (wins < 100){
			winString = "  " + wins;
		}
		else{
			winString =  " " + wins;
		}
		
		if (draws <10){
			drawString = "   " + draws;
		}
		else{
			drawString =  "  " + draws;
		}
		
		if (losses < 10){ 
			lossString = "   " + losses;
		}
		else if (losses < 100){
			lossString = "  " + losses;
		}
		else{
			lossString = " " + losses;
		}
		
		if (getPoints() < 10){
			pointString = "   " + getPoints();
		}
		else if (getPoints() < 100){
			pointString = "  " + getPoints();
		}
		else{
			pointString = " " + getPoints();
		}
		
		if (runsFor < 10){
			runsForString = "  " + runsFor;
		}
		else if (runsFor < 100){
			runsForString = " " + runsFor;
		}
		else{
			runsForString = "" + runsFor;
		}
		
		if (runsAllowed < 10){
			runsAllString = "  " + runsAllowed;
		}
		else if (runsAllowed < 100){
			runsAllString = " " + runsAllowed;
		}
		else{
			runsAllString = "" + runsAllowed;
		}
		
		String spaces = "";
		String plusMinus;
		for (int i = 0; i < 14-name.length(); i++){
			spaces += " ";
		}
		if (runsFor-runsAllowed  >= 0){
			plusMinus = "";
		}else{
			plusMinus = "-";
		}
		return name + spaces + winString + drawString + lossString + " " + pointString +" "+ runsForString +" "+runsAllString + " " + plusMinus + (Math.abs(runsFor-runsAllowed));
	}
	
	public double getWinPerc(){
		if (wins + draws + losses == 0)
			return .000;
		else
		return (wins+.5*draws)/(wins+draws+losses);
	}
	
	public int compareTo(Team other) {
		if (this.getWinPerc()>other.getWinPerc()) {
			return -1;
		}
		else if (this.getWinPerc()<other.getWinPerc()) {
			return 1;
		}
		else if (this.wins != other.wins){
			return other.wins - this.wins;
		}
		else{
			return (other.runsFor-other.runsAllowed)-(this.runsFor-this.runsAllowed);
		}
	}
	
	public void addToSeasonStats(){
		while (lineup.peekFirst()!= leadoff){
			Player dummy = lineup.pop();
			lineup.add(dummy);
		}
		Player lead = lineup.pop();
		lead.addToSeason();
		lineup.add(lead);
		while(lineup.peekFirst() != leadoff){
			Player a = lineup.pop();
			a.addToSeason();
			lineup.add(a);
		}
	}
	
	public void printBoxScore(){
		while (lineup.peekFirst()!= leadoff){
			Player dummy = lineup.pop();
			lineup.add(dummy);
		}
		System.out.println("Name\tPA\tBB\tH\t2B\t3B\tHR\tR\tBI");
		Player lead = lineup.pop();
		System.out.println(lead.boxString());
		lineup.add(lead);
		while (lineup.peekFirst() != leadoff){
			Player a = lineup.pop();
			lineup.add(a);
			System.out.println(a.boxString());
		}
	}
	

	
	
	

	
	
	
	
}

